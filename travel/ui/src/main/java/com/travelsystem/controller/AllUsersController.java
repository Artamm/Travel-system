package com.travelsystem.controller;

import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.logic.services.UserService;
import com.travelsystem.model.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AllUsersController {

    private final UserService userService;
private final JourneyService journeyService;


    @Autowired
    public AllUsersController(UserService userService, JourneyService journeyService) {
        this.userService = userService;
        this.journeyService = journeyService;
    }

    @GetMapping("allUsers")
    public String profilePage(Model model, @AuthenticationPrincipal User id){
User user = userService.userById(id.getId());
        model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator",journeyService.userOrganizator(user));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user",user);
        model.addAttribute("person", new User());
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("upcoming",journeyService.upcomingJourneys(user));

        return  "allUsers";
    }

    @RequestMapping( value = "/allUsers/user",method = RequestMethod.GET)
    public String findPersonByAnyParameter(@AuthenticationPrincipal User user,@ModelAttribute User person, Model model, final BindingResult bindingResult){

        if (person.getUsername()!=null && !person.getUsername().isEmpty()){
            person.setSurname(person.getUsername());
            person.setName(person.getUsername());
            try {
                person.setId(Long.parseLong(person.getUsername()));
            }
            catch (Exception ignored){
                person.setId(null);
            }

            List<User> userList = userService.findUserByAnyParameter(person.getId(),person.getUsername(),person.getSurname(),person.getName());
            model.addAttribute("users", userList);
            model.addAttribute("person", new User());
        }
        else{
            model.addAttribute("users", userService.findAll());
            model.addAttribute("person", new User());

        }
        model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator",journeyService.userOrganizator(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("upcoming",journeyService.upcomingJourneys(user));


        return "allUsers";
    }

    @RequestMapping(value = "/giveRights/{id}", params = {"giveRights"},method = RequestMethod.PUT)
    public String giveRights(@PathVariable("id") Long id){
        userService.giveAdminRights(id);
        return  "redirect:/allUsers";
    }

    @RequestMapping(value = "/removeRights/{id}", params = {"removeRights"},method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public String removeRights(@PathVariable("id") Long id, @AuthenticationPrincipal User user){
        userService.removeAdminRights(id);

        if (user.getId() == id){
            Authentication authentication = new UsernamePasswordAuthenticationToken(userService.findByUsername(user.getUsername()), user.getPassword(), userService.findByUsername(user.getUsername()).getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return  "redirect:/allUsers";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value="/del/{id}",method = RequestMethod.DELETE)
    public String deleteUserById(@PathVariable("id") Long id){
        userService.deleteUserById(id);
        return  "redirect:/";
    }
}

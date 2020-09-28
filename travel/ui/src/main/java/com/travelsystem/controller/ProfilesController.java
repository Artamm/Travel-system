package com.travelsystem.controller;

import com.travelsystem.logic.SendMailSender;
import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.logic.services.UserService;
import com.travelsystem.model.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class ProfilesController {

private final UserService userService;
private final JourneyService journeyService;



    @Autowired
    public ProfilesController(UserService userService, JourneyService journeyService) {
        this.userService = userService;
        this.journeyService = journeyService;

    }

    @GetMapping("profile")
    public String profilePage(Model model, @AuthenticationPrincipal User user){

        User nameChange = user;
        User preferences = user;
        User userProfile = user;

        model.addAttribute("user", userService.userById(user.getId()));
        model.addAttribute("userProfile", userProfile);
        model.addAttribute("passwordChange", new User());
        model.addAttribute("preferences",preferences);
        model.addAttribute("nameChange", nameChange);

        model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator",journeyService.userOrganizator(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("user",userService.userById(user.getId()));


        return  "profile";
    }



    @RequestMapping(method = RequestMethod.POST,value="/updateImage")
    public String updateProfileImage(@AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file,
            @ModelAttribute User userProfile){

        if(!file.isEmpty()) {
            try {
                userProfile.setUsername(user.getUsername());
                userProfile.setThumbnail(file.getBytes());
                user.setThumbnail(file.getBytes());
                userService.updateImage(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userService.findByUsername(user.getUsername()), user.getPassword(), user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

       return "redirect:/profile" ;
    }

    @RequestMapping(method = RequestMethod.POST,value="/changePassword")
    public String changePassword(@AuthenticationPrincipal User user, @ModelAttribute("passwordChange") User passwordChange){
        userService.changePassword(user, passwordChange.getPassword());
        return "redirect:/profile";
    }

    //for development purposes only
    @GetMapping("/make_quickly_admin/")
    public String quicklyAdmin(@AuthenticationPrincipal User user){
        userService.giveAdminRights(user.getId());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userService.findByUsername(user.getUsername()), user.getPassword(), userService.findByUsername(user.getUsername()).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/";
    }


    @RequestMapping(method = RequestMethod.POST,value="/changeContacts")
    public String changeContacts(@AuthenticationPrincipal User user, @ModelAttribute User userContacts){
        user.setMail(userContacts.getMail());
        user.setPhone(userContacts.getPhone());
        if (!userService.isUsedByOtherUser(user.getMail(),user.getUsername()))
            userService.changeContacts(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userService.findByUsername(user.getUsername()), user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/profile";
    }

    @RequestMapping(method = RequestMethod.POST,value="/changeName")
    public String changeName(@AuthenticationPrincipal User user, @ModelAttribute User userContacts){
        user.setName(userContacts.getName());
        user.setSurname(userContacts.getSurname());
        userService.changeContacts(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userService.findByUsername(user.getUsername()), user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/profile";
    }





    @GetMapping(value = "/profileImage/", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] loadProfileImage(@AuthenticationPrincipal User user) {
        return userService.getFullImageById(user.getId());
    }


    @GetMapping("/profileUser/{username}")
    public String profileUser(Model model, @PathVariable ("username") String username, @AuthenticationPrincipal User user){
        if (!user.getUsername().equals(username)){
            model.addAttribute("user",userService.findByUsername(username));
            User profile = userService.findByUsername(username);
            model.addAttribute("userJourneys",journeyService.activeJourneyByOrganizator(profile));
            model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
            model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
            model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
            model.addAttribute("organizator",journeyService.userOrganizator(user));
            model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
            model.addAttribute("upcoming",journeyService.upcomingJourneys(user));
            model.addAttribute("userYou",userService.userById(user.getId()));


            return "profileUser";
        }else{
            return "redirect:/profile";
        }

    }

    @GetMapping(value = "/profileImage/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] loadProfileUserImage(@PathVariable("id") Long id) {
        return userService.getFullImageById(id);
    }

    @RequestMapping(method = RequestMethod.POST,value="/changePreferences")
    public String changePreferences(@AuthenticationPrincipal User user, @ModelAttribute("preferences") User preferences, final BindingResult bindingResult, final HttpServletRequest httpServletRequest){
        userService.setSettings(user, preferences);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userService.findByUsername(user.getUsername()), user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/profile";
    }

}

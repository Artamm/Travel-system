package com.travelsystem.controller;

import com.travelsystem.logic.services.DestinationService;
import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.logic.services.UserService;
import com.travelsystem.model.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class MainController {

    private final JourneyService journeyService;
    private  final UserService userService;
    private  final DestinationService destinationService;

    @Autowired
    public MainController(JourneyService journeyService, UserService userService, DestinationService destinationService) {
        this.journeyService = journeyService;
        this.userService = userService;
        this.destinationService = destinationService;
    }


    @GetMapping("journeyLink")
    public String redirectJourney() {

        return "redirect:journey";
    }

    @GetMapping("help")
    public String documentation (Model model, @AuthenticationPrincipal User user){
        model.addAttribute("user",user);
        return "help";
    }


    @GetMapping("/")
    public String index(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator",journeyService.userOrganizator(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("upcoming",journeyService.upcomingJourneys(user));
        model.addAttribute("user",userService.userById(user.getId()));
        model.addAttribute("locationsNew",destinationService.newLocations());
        return "index";

    }

    @GetMapping("findJourney")
    public String findJourney(Model model){
        return "find_journey";
    }


     @GetMapping("/admin")
     @PreAuthorize("hasAnyAuthority('ADMIN')")
     public String adminPanel(Model model,@AuthenticationPrincipal User user){
         model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
         model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
         model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
         model.addAttribute("organizator",journeyService.userOrganizator(user));
         model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
         model.addAttribute("upcoming",journeyService.upcomingJourneys(user));
         model.addAttribute("user",userService.userById(user.getId()));
        return "adminpage";
     }
}
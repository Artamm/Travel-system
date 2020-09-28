package com.travelsystem.controller;


import com.travelsystem.logic.SendMailSender;
import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.logic.services.UserService;
import com.travelsystem.model.dao.Journey;
import com.travelsystem.model.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class JourneyController {

    private final JourneyService journeyService;
    private final UserService userService;
    private final SendMailSender sendMailSender;
    private final MailContentBuilder mailContentBuilder;


    @Autowired
    public JourneyController(JourneyService journeyService, UserService userService, SendMailSender sendMailSender, MailContentBuilder mailContentBuilder) {
        this.journeyService = journeyService;
        this.userService = userService;
        this.sendMailSender = sendMailSender;
        this.mailContentBuilder = mailContentBuilder;
    }

    @GetMapping(value = "journey")
    public String journey(Model model, @AuthenticationPrincipal User user) {
        List<Journey> journeyList= journeyService.premoderatedRequests(user);
        model.addAttribute("oldJourney",journeyService.userPastJourneys(user));
        model.addAttribute("journeys",journeyService.userActiveJourneys(user));
        model.addAttribute("requests",journeyList);
        model.addAttribute("invited",journeyService.userInvitedInJourneys(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("user",user);
        model.addAttribute("search", new Journey());
        model.addAttribute("organizator",journeyService.userOrganizator(user));


        return "journey";
    }

    @GetMapping("/search/journey/mine")
    public String search(@ModelAttribute Journey parameter, Model model, @AuthenticationPrincipal User user){

        modelAttributes(model, user);
        if(parameter.getTitle().equals(""))
            return "journey";

        model.addAttribute("oldJourneys",journeyService.searchByActive(parameter.getTitle(),journeyService.userPastJourneys(user))) ;
        model.addAttribute("journeys",journeyService.searchByActive(parameter.getTitle(),journeyService.userActiveJourneys(user))) ;
        return "journey";
    }

    @Async
    public void modelAttributes(Model model, User user){
        List<Journey> journeyList= journeyService.premoderatedRequests(user);
        model.addAttribute("oldJourney",journeyService.userPastJourneys(user));
        model.addAttribute("journeys",journeyService.userActiveJourneys(user));
        model.addAttribute("requests",journeyList);
        model.addAttribute("invited",journeyService.userInvitedInJourneys(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("user",user);
        model.addAttribute("search", new Journey());
        model.addAttribute("organizator",journeyService.userOrganizator(user));

    }



    @RequestMapping(value = "acceptRequest/{userId}/{journeyId}",method = RequestMethod.POST)
    public String acceptRequest(@PathVariable ("userId") Long userId, @PathVariable("journeyId") Long journeyId){
        journeyService.confirmJourneyForUser(userId,journeyId);
        if (userService.userById(userId).isSendNotifications())
            sendAccepted(userId,journeyId);
        return "redirect:/journey";
    }


    @RequestMapping(value = "declineRequest/{userId}/{journeyId}",method = RequestMethod.POST)
    public String declineRequest(@PathVariable ("userId") Long userId, @PathVariable("journeyId") Long journeyId){
        journeyService.declineJourneyForUser(userId,journeyId);
        if (userService.userById(userId).isSendNotifications())
            sendDeclined(userId,journeyId);
        return "redirect:/journey";
    }

    @Async
    void sendDeclined(Long userId,Long journeyId){
        Journey journey = journeyService.findJourneyById(journeyId);
        String message = mailContentBuilder.buildGeneral("Your request has been declined. Details about journey below:",journey);
        sendMailSender.prepareAndSend(userService.userById(userId).getMail(),message,"Declined request");
    }

    @Async
    void sendAccepted(Long userId,Long journeyId){
        Journey journey = journeyService.findJourneyById(journeyId);
        String message = mailContentBuilder.buildGeneral("Your request has been accepted. Details about journey below:",journey);
        sendMailSender.prepareAndSend(userService.userById(userId).getMail(),message,"Accepted request");
    }



    @RequestMapping(value = "acceptInviteRequest/{journeyId}",method = RequestMethod.POST)
    public String acceptInviteRequest(@AuthenticationPrincipal User user, @PathVariable("journeyId") Long journeyId){
        journeyService.confirmJourneyForUser(user.getId(),journeyId);
        return "redirect:/journey";
    }


    @RequestMapping(value = "declineInviteRequest/{journeyId}",method = RequestMethod.POST)
    public String declineInviteRequest(@AuthenticationPrincipal User user, @PathVariable("journeyId") Long journeyId){
        journeyService.declineJourneyForUser(user.getId(),journeyId);
        return "redirect:/journey";
    }
}

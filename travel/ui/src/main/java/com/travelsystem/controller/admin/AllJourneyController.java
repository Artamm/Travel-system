package com.travelsystem.controller.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.logic.services.SearchService;
import com.travelsystem.model.dao.Journey;
import com.travelsystem.model.dao.User;
import com.travelsystem.model.dao.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class AllJourneyController {

    private final
    JourneyService journeyService;

    private final
    SearchService searchService;


    @Autowired
    public AllJourneyController(JourneyService journeyService, SearchService searchService) {
        this.journeyService = journeyService;
        this.searchService = searchService;
    }


    @GetMapping("/AllJourney")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String allJourneyList(@AuthenticationPrincipal User user, Model model, @RequestParam("page") Optional<Integer> page,
                                 @RequestParam("size") Optional<Integer> size) {
        List<Journey> journeyList = journeyService.allJourneys();
//        Page<Journey> journeyPage = new PageImpl<Journey>(journeyList, new PageRequest(page, size, sort), journeyList.size());

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(8);
        Page<Journey> journeyPage = journeyService.findPaginated(PageRequest.of(currentPage - 1, pageSize), journeyService.allJourneys());

        int totalPages = journeyPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }


        model.addAttribute("personJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator", journeyService.userOrganizator(user));
        model.addAttribute("requestNumber", journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("upcoming", journeyService.upcomingJourneys(user));
        model.addAttribute("user", user);
        model.addAttribute("journeys", journeyPage);
        model.addAttribute("journey", new Journey());
        return "secure/adminJourney";
    }

    @GetMapping("/AllJourney/find/")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String certainJourney(@ModelAttribute Journey journey, Model model, final BindingResult bindingResult, @RequestParam("page") Optional<Integer> page,
                                 @RequestParam("size") Optional<Integer> size, @AuthenticationPrincipal User user) {


        List<Journey> journeyList= new ArrayList<>();
        if (!journey.getTitle().equals("")) {
            List<String> tags = new ArrayList<>();
            tags.add(journey.getTitle());
            journeyList.addAll(searchService.findByTags(tags));
            journeyList.addAll(searchService.findByTitle(journey.getTitle()));
        } else {
            journeyList = journeyService.allJourneys();
        }
//        journeyList.addAll(searchService.findByOrganizator(journey.getTitle()));
//        Page<Journey> journeyPage = new PageImpl<Journey>(journeyList, new PageRequest(page, size, sort), journeyList.size());

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(15);
        Page<Journey> journeyPage = journeyService.findPaginated(PageRequest.of(currentPage - 1, pageSize), journeyList);

        int totalPages = journeyPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }


        modelAttributes(model, user);
        model.addAttribute("journeys", journeyPage);
        model.addAttribute("journey", new Journey());
        return "secure/adminJourney";
    }


    private void modelAttributes(Model model, User user) {
        model.addAttribute("personJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator", journeyService.userOrganizator(user));
        model.addAttribute("requestNumber", journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("upcoming", journeyService.upcomingJourneys(user));
        model.addAttribute("user", user);
    }

    @GetMapping(value = "/json/journey", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @JsonView(Views.idName.class)
    public List<Journey> journeysLots() {
        return journeyService.allJourneys();
    }


    @PostMapping("/deleteJourney/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String dd(@PathVariable("id") Long id, Model model) {
        journeyService.deleteJourneyById(id);
        return "redirect:/AllJourney";
    }
}

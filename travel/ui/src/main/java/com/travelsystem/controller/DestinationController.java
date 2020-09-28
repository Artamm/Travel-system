package com.travelsystem.controller;

import com.travelsystem.SupportMethods;
import com.travelsystem.logic.OpenStreetMapUtils;
import com.travelsystem.logic.services.DestinationService;
import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.model.dao.Destination;
import com.travelsystem.model.dao.User;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class DestinationController {


    private final DestinationService destinationService;
    private final JourneyService journeyService;
    private SupportMethods supportMethods = new SupportMethods();

    @Autowired
    public DestinationController(DestinationService destinationService, JourneyService journeyService) {
        this.destinationService = destinationService;
        this.journeyService = journeyService;
    }

    @GetMapping("/locations")
    public String getLocations( @AuthenticationPrincipal User user, Model model,@RequestParam("page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<Destination> destinationPage = destinationService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

        int totalPages = destinationPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }


        model.addAttribute("location", new Destination());
        model.addAttribute("locations",destinationPage /*destinationService.destinationListUsers()*/);

        model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator",journeyService.userOrganizator(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
model.addAttribute("user",user);
        return "locations";
    }



    @GetMapping("/user/find/location/")
    public String findLocationUser(@ModelAttribute Destination parameter, Model model,@RequestParam("page") Optional<Integer> page,
                                   @RequestParam("size") Optional<Integer> size, @AuthenticationPrincipal User user) {

        model.addAttribute("location", parameter);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        if (parameter.getName().equals("")) {
            return "redirect:/locations";
        }

        try {
            parameter.setId(Long.parseLong(parameter.getName()));
            List<Destination> capsule = new ArrayList<>();
            capsule.add(destinationService.destinationbyId(parameter.getId()));
            Page<Destination> destinationPageId = destinationService.findPaginatedList(PageRequest.of(currentPage - 1, pageSize),capsule);
            model.addAttribute("locations", destinationPageId);

            return "locations";

        } catch (Exception ignored) {

        }
        Page<Destination> destinationPage = destinationService.findPaginatedList(PageRequest.of(currentPage - 1, pageSize),destinationService.findAllByCountryOrNameOrCoordinatesUserFalse(parameter.getName()));
        int totalPages = destinationPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("locations",destinationPage );




        model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator",journeyService.userOrganizator(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("user",user);
        return "locations";
    }




    @GetMapping("/browseLocation/{id}")
    public String browseLocation(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal User user) throws IOException, JSONException {
        Destination destination = destinationService.destinationbyId(id);
        supportMethods.setCountry(destination);
        String inform = OpenStreetMapUtils.getInstance().CoronavirusMap(destination.getCountry());
        model.addAttribute("location", destination);
        model.addAttribute("locationIMG", destination);
        model.addAttribute("inform", inform);

        model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator",journeyService.userOrganizator(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("user", user);

        return "location";
    }


    @GetMapping(value = "/getDestinationImage/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] loadDestinationImage(@PathVariable("id") Long id) {
        return destinationService.getDestinationImageById(id);
    }

}

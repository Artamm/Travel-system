package com.travelsystem.controller;

import com.travelsystem.logic.services.DestinationService;
import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.logic.services.SearchService;
import com.travelsystem.logic.services.UserService;
import com.travelsystem.model.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller

public class SearchController {

    private final SearchService searchService;

    private final JourneyService journeyService;

    private final DestinationService destinationService;
    private final UserService userService;

    @Autowired
    public SearchController(SearchService searchService, JourneyService journeyService, DestinationService destinationService, UserService userService) {
        this.searchService = searchService;
        this.journeyService = journeyService;
        this.destinationService = destinationService;
        this.userService = userService;
    }

    @GetMapping("/find")
    public String SearchPage(Model model, @AuthenticationPrincipal User id) {
        modelAttributes(model, id);
        model.addAttribute("parameters", new Journey());
        model.addAttribute("parametersD", new Destination());

        return "advancedSearch";
    }

    @GetMapping(value = {"/search/{location}", "search/location/{location}"})
    public String redirectSearch(@PathVariable("location") String location, Model model, @AuthenticationPrincipal User id) {
        List<Destination> destinations = new ArrayList<>();
        modelAttributes(model, id);

        checkDestination(destinations, location);
        Journey parameter = new Journey();
        parameter.setTitle(location);
        model.addAttribute("freeSearch", parameter);
        model.addAttribute("destinationList", destinations);
        model.addAttribute("journeyList", searchService.findByDestinationName(location));

        model.addAttribute("parameters", new Journey());
        model.addAttribute("parametersD", new Destination());


        return "advancedSearch";
    }

    @GetMapping(value = "/search", params = {"free"})
    public String FreeSearch(@ModelAttribute Journey parameter, Model model, @AuthenticationPrincipal User id, final BindingResult bindingResult, @RequestParam(value = "choice", required = false) boolean choice) {
        if (parameter.getTitle().isEmpty() | parameter.getTitle() == null) {
            return "redirect:/find";
        }
        List<Journey> journeys = new ArrayList<>();
        List<Destination> destinations = new ArrayList<>();

        //------------------Check  Journeys---------------------
        modelAttributes(model, id);

        checkJourney(journeys, parameter.getTitle());
        checkDestination(destinations, parameter.getTitle());
        model.addAttribute("freeSearch", parameter);
        model.addAttribute("journeyList", journeys);
        model.addAttribute("destinationList", destinations);
        model.addAttribute("parameters", new Journey());
        model.addAttribute("parametersD", new Destination());

        return "advancedSearch";

    }

    private void checkJourney(List<Journey> journeys, String parameter) {
        if (searchService.findByTitle(parameter) != null) {
            journeys.addAll(searchService.findByTitle(parameter));
        }

        if (searchService.findByOrganizator(parameter) != null) {
            List<Journey> tempList = searchService.findByOrganizator(parameter);
            for (Journey journey : tempList) {
                if (!journeys.contains(journey)) {
                    journeys.add(journey);
                }
            }
        }
    }

    private void checkDestination(List<Destination> destinations, String parameter) {
        if (searchService.findByName(parameter) != null) {
            destinations.add(searchService.findByName(parameter));
        }
        if (searchService.findByCountry(parameter) != null) {
            List<Destination> tempList = searchService.findByCountry(parameter);

            for (Destination destination : tempList) {
                if (!destinations.contains(destination)) {
                    destinations.add(destination);
                }
            }
        }
    }


    @RequestMapping(method = RequestMethod.GET, value = "find/journey", params = {"AddTag"})
    public String addTag(@ModelAttribute("parameters") Journey journey, @AuthenticationPrincipal User id, final BindingResult bindingResult, @RequestParam(value = "choice", required = false) boolean choice, Model model) {
        journey.getTags().add(new Tag());
        modelAttributes(model, id);
        model.addAttribute("parameters", journey);
        model.addAttribute("parametersD", new Destination());


        return "advancedSearch";
    }

    @RequestMapping(method = RequestMethod.GET, value = "find/journey", params = {"DeleteTag"})
    public String DeleteTag(@ModelAttribute("parameters") Journey journey, @AuthenticationPrincipal User id, final BindingResult bindingResult, final HttpServletRequest req, @RequestParam(value = "choice", required = false) boolean choice, Model model) {
        final Integer rowId = Integer.valueOf(req.getParameter("DeleteTag"));
        journey.getTags().remove(rowId.intValue());
        modelAttributes(model, id);
        model.addAttribute("parameters", journey);
        model.addAttribute("parametersD", new Destination());
        return "advancedSearch";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/find/location", params = {"AddCategory"})
    public String addCategory(@ModelAttribute("parametersD") Destination destination, @AuthenticationPrincipal User id, final BindingResult bindingResult, Model model) {
        destination.getCategories().add(Category.OTHERS);
        modelAttributes(model, id);
        model.addAttribute("parameters", new Journey());
        model.addAttribute("parametersD", destination);
        return "advancedSearch";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/find/location", params = {"DeleteCategory"})
    public String DeleteCategory(@ModelAttribute("parametersD") Destination destination, @AuthenticationPrincipal User id, final BindingResult bindingResult, final HttpServletRequest req, Model model) {
        final Integer rowId = Integer.valueOf(req.getParameter("DeleteCategory"));
        destination.getCategories().remove(rowId.intValue());
        modelAttributes(model, id);

        model.addAttribute("parameters", new Journey());
        model.addAttribute("parametersD", destination);
        return "advancedSearch";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/find/journey")
    public String showJourneys(@ModelAttribute("parameters") Journey journey, @AuthenticationPrincipal User id, final BindingResult bindingResult, final HttpServletRequest req, Model model) {

        modelAttributes(model, id);
        model.addAttribute("parameters", journey);
        model.addAttribute("parametersD", new Destination());
        model.addAttribute("journeyList", checkJourneys(journey));
        model.addAttribute("destinationList", new ArrayList<Destination>());
        return "advancedSearch";
    }


    private boolean emptyEntry(Journey journey) {
        return journey.getTitle().equals("") & journey.getOrganizator().getUsername().equals("")
                & !journey.isActive() & !journey.isByInvitation() & journey.getStart_position().getName().equals("") & journey.getTags().isEmpty() & journey.getStart_date() == null;
    }

    private List<Journey> checkJourneys(Journey journey) {

        List<Journey> journeyList = new ArrayList<>();

        //in case of empty entry
        if (emptyEntry(journey)){
            return journeyService.allJourneys();

        }

        // finds if at least 2 parameters match
        if (journey.isActive()) {
            journeyList.addAll(searchService.findActiveJourneysByParameters(journey.getTitle(), journey.getStart_position().getName(), new Integer(journey.getPeopleNumber()), journey.getStart_date(),journey.getStart_date(), journey.isActive()));
        }
        else{
            journeyList.addAll(searchService.findAllJourneysByParameters(journey.getTitle(), journey.getStart_position().getName(), new Integer(journey.getPeopleNumber()), journey.getStart_date(),journey.getStart_date(), journey.isActive()));
        }
        //same with organizator active/not
        if (!journey.getOrganizator().getUsername().equals("")) {
            if (searchService.findByOrganizator(journey.getOrganizator().getUsername()) != null)
                if (journey.isActive())
                    searchService.findByOrganizator(journey.getOrganizator().getUsername()).stream().filter(Journey::isActive).forEach(journeyList::add);
                else
                    journeyList.addAll(searchService.findByOrganizator(journey.getOrganizator().getUsername()));
        }
        //cause in thymeleaf page it's = open
        if(journey.isByInvitation()){
            List<Journey> filtered = new ArrayList<>();
            try {
                journeyList.stream().filter(x -> !x.isByInvitation()).forEach(filtered::add);
                return filtered;
            }
            catch (NullPointerException ex){
                return new ArrayList<Journey>();
            }

        }
            else{
            return journeyList;
            }
    }


    @RequestMapping(method = RequestMethod.GET, value = "/find/location")
    public String showDestinations(@ModelAttribute("parametersD") Destination destination, @AuthenticationPrincipal User id, final BindingResult bindingResult, final HttpServletRequest req, Model model) {

        modelAttributes(model, id);
        model.addAttribute("parameters", new Journey());
        model.addAttribute("parametersD", destination);
        model.addAttribute("destinationList", checkDestinations(destination));
        model.addAttribute("journeyList", new ArrayList<Journey>());

        return "advancedSearch";
    }

    @GetMapping("/search/tag/{name}")
    public String findAllByTag(@PathVariable("name") String tagName, Model model, @AuthenticationPrincipal User id) {
        List<String> tags = new ArrayList<>();
        tags.add(tagName);

        modelAttributes(model, id);
        model.addAttribute("parameters", new Journey());
        model.addAttribute("parametersD", new Destination());
        model.addAttribute("destinationList", new ArrayList<Destination>());
        List<Journey> journeys = searchService.findByTags(tags);
        model.addAttribute("journeyList", journeys);
        return "advancedSearch";
    }

    @GetMapping("/search/category/{name}")
    public String findAllByCategory(@PathVariable("name") String tagName, Model model, @AuthenticationPrincipal User id) {
        List<String> categories = new ArrayList<>();
        categories.add(tagName);

        modelAttributes(model, id);
        model.addAttribute("parameters", new Journey());
        List<Journey> journeys = searchService.findJourneyByCategories(categories);
        model.addAttribute("parametersD", new Destination());
        model.addAttribute("destinationList", searchService.findDestinationByStringCategory(tagName));
        model.addAttribute("journeyList", journeys);
        return "advancedSearch";
    }


    private void modelAttributes(Model model, @AuthenticationPrincipal User id) {

        User user = userService.userById(id.getId());
        model.addAttribute("personJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator", journeyService.userOrganizator(user));
        model.addAttribute("freeSearch", new Journey());
        model.addAttribute("journeyList", journeyService.allJourneys());
        model.addAttribute("destinationList", destinationService.destinationListUsers());
        model.addAttribute("user", user);
        model.addAttribute("users", userService.findAll());

        model.addAttribute("requestNumber", journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("upcoming", journeyService.upcomingJourneys(user));
    }

    private List<Destination> checkDestinations(Destination destination) {
        List<Destination> destinations = new ArrayList<>();
        if (destination == null)
            return destinationService.destinationListUsers();
        else {
            destinations.addAll(searchService.findbyAllParametersNotStrict(destination));
        }

        if (destination.getCategories() != null) {
            destinations.addAll(searchService.findDestinationByCategories(destination.getCategories()));
        }

        return destinations;
    }

}

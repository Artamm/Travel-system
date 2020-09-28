package com.travelsystem.controller.admin;

import com.travelsystem.SupportMethods;
import com.travelsystem.logic.OpenStreetMapUtils;
import com.travelsystem.logic.services.DestinationService;
import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.logic.services.SearchService;
import com.travelsystem.model.dao.Category;
import com.travelsystem.model.dao.Destination;
import com.travelsystem.model.dao.User;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class AdminDestinationController {

    private final DestinationService destinationService;
    private final JourneyService journeyService;
    private SupportMethods supportMethods = new SupportMethods();


    @Autowired
    public AdminDestinationController(DestinationService destinationService, JourneyService journeyService) {
        this.destinationService = destinationService;
        this.journeyService = journeyService;
    }

    @GetMapping("/adminDestination")
    public String getAdminLocations(Model model) {
        model.addAttribute("location", new Destination());
        model.addAttribute("locations", destinationService.destinationListAdmin());
        return "secure/adminDestination";
    }


    @GetMapping("/editLocation/{id}")
    public String editLocation(@PathVariable("id") Long id, Model model) {
        model.addAttribute("location", destinationService.destinationbyId(id));
        model.addAttribute("locationIMG", destinationService.destinationbyId(id));
        return "secure/editLocation";
    }

    @RequestMapping(value = "/editLocation/{id}", params = {"calc"}, method = RequestMethod.POST)
    public String getCoordinates(Model model, @PathVariable("id") Long id, @ModelAttribute Destination destination, final HttpServletRequest req, final BindingResult bindingResult) throws IOException, JSONException {
        supportMethods.setCoordinates(destination);
        supportMethods.setCountry(destination);
        model.addAttribute("location", destination);
        model.addAttribute("locationIMG", destinationService.destinationbyId(id));

        return "secure/editLocation";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/editLocation/{id}", params = {"addCategory"})
    public String addEditCategory(Model model, @PathVariable("id") Long id, @ModelAttribute Destination destination, final BindingResult bindingResult) {
        destination.getCategories().add(Category.OTHERS);
        model.addAttribute("location", destination);
        model.addAttribute("locationIMG", destinationService.destinationbyId(id));
        return "secure/editLocation";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/editLocation/{id}", params = {"DeleteCategory"})
    public String DeleteEditCategory(Model model, @PathVariable("id") Long id, @ModelAttribute Destination destination, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer rowId = Integer.valueOf(req.getParameter("DeleteCategory"));
        destination.getCategories().remove(rowId.intValue());
        model.addAttribute("location", destination);
        model.addAttribute("locationIMG", destinationService.destinationbyId(id));

        return "secure/editLocation";
    }

    @RequestMapping(value = "/editLocation/{id}", params = {"Save"}, method = RequestMethod.POST)
    public String SaveChanges(Model model, @PathVariable("id") Long id, @ModelAttribute Destination destination, final HttpServletRequest req, final BindingResult bindingResult) {
        destination.setId(id);
        destinationService.saveChanges(destination);
        return "redirect:/adminDestination";
    }

    @RequestMapping(value = "/DestinationImage/{id}", params = {"Save"}, method = RequestMethod.POST)
    public String EditImage(@RequestParam("file") MultipartFile file, Model model, @PathVariable("id") Long id, @ModelAttribute Destination destination, final HttpServletRequest req, final BindingResult bindingResult) {
        destination.setId(id);
        try {
            destination.setThumbnail(file.getBytes());
            destinationService.saveImage(destination);

        } catch (IOException ignored) {
        }
        return "redirect:/adminDestination";
    }


    @GetMapping("/addDestination")
    public String addDestinationPage(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("destination", new Destination());
        model.addAttribute("damn", "Corona");

        model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator",journeyService.userOrganizator(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("upcoming",journeyService.upcomingJourneys(user));
        model.addAttribute("user",user);

        return "secure/create_destination";
    }


    private  void menuAttributes(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator",journeyService.userOrganizator(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("upcoming",journeyService.upcomingJourneys(user));
        model.addAttribute("user",user);


    }


    @RequestMapping(value = "/addDestination", params = {"calc"}, method = RequestMethod.POST)
    public String getCordinatesByAddress(Model model,@AuthenticationPrincipal User user, @ModelAttribute Destination destination, final HttpServletRequest req, final BindingResult bindingResult) throws IOException, JSONException {
        supportMethods.setCoordinates(destination);
        supportMethods.setCountry(destination);
        String g = OpenStreetMapUtils.getInstance().CoronavirusMap(destination.getCountry());
//     supportMethods.setCountry(destination);
//        supportMethods.setCountry(destination);
        model.addAttribute("destination", destination);
        model.addAttribute("damn", g);
        model.addAttribute("full_address", supportMethods.setDisplayName(destination));
        menuAttributes(model,user);
        return "secure/create_destination";
    }



    @RequestMapping(method = RequestMethod.POST, value = "/addDestination", params = {"addCategory"})
    public String addCategory(Model model, @AuthenticationPrincipal User user,@ModelAttribute Destination destination, final BindingResult bindingResult) {
        destination.getCategories().add(Category.OTHERS);
        menuAttributes(model,user);
        return "secure/create_destination";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/addDestination", params = {"DeleteCategory"})
    public String DeleteCategory(Model model, @AuthenticationPrincipal User user,@ModelAttribute Destination destination, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer rowId = Integer.valueOf(req.getParameter("DeleteCategory"));
        destination.getCategories().remove(rowId.intValue());
        menuAttributes(model,user);
        return "secure/create_destination";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/addDestination", params = {"Save"})
    public String SaveDestination(@ModelAttribute Destination destination, final BindingResult bindingResult) {
       if( destinationService.NotexistsInDB(destination.getName()))
            destinationService.destinationAdd(destination);
        return "redirect:/adminDestination";
    }


    @GetMapping("/admin/find/location/")
    public String findLocationAdmin(@ModelAttribute Destination parameter, Model model) {

        model.addAttribute("location", parameter);

        if (parameter.getName().equals("")) {
            model.addAttribute("locations", destinationService.destinationListAdmin());
            return "secure/adminDestination";
        }

        try {
            parameter.setId(Long.parseLong(parameter.getName()));
            List<Destination> capsule = new ArrayList<>();
            capsule.add(destinationService.destinationbyId(parameter.getId()));
            model.addAttribute("locations", capsule);
            return "secure/adminDestination";

        } catch (Exception ignored) {

        }
        model.addAttribute("locations", destinationService.findAllByCountryOrNameOrCoordinates(parameter.getName()));

        return "secure/adminDestination";
    }



    @RequestMapping(value = "/AdminDestination/{id}", params = {"delete"}, method = RequestMethod.POST)
    public String deleteDestination(@PathVariable("id") Long id) {
        destinationService.destinationDelete(id);
        return "redirect:/adminDestination";
    }


}

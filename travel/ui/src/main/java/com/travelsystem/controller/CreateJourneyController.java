package com.travelsystem.controller;

import com.travelsystem.SupportMethods;
import com.travelsystem.logic.SendMailSender;
import com.travelsystem.logic.services.DestinationService;
import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.logic.services.UserService;
import com.travelsystem.model.dao.*;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
/// Привязать биндингом картинку и файл

@Controller
public class CreateJourneyController {

    private final
    JourneyService journeyService;
    private final DestinationService destinationService;
private final UserService userService;
    private SupportMethods supportMethods = new SupportMethods();
    private final MailContentBuilder mailContentBuilder;
    private final SendMailSender mailSender;


    @Autowired
    public CreateJourneyController(JourneyService journeyService, DestinationService destinationService, UserService userService, MailContentBuilder mailContentBuilder, SendMailSender mailSender) {
        this.journeyService = journeyService;
        this.destinationService = destinationService;
        this.userService = userService;
        this.mailContentBuilder = mailContentBuilder;
        this.mailSender = mailSender;
    }


    @GetMapping("/coordinates")
    public String getCoordinates(Model model) {
        model.addAttribute("coordinates", new Destination());
        return "coordinates";
    }

    @GetMapping("/journey/new")
    public String getCreate(Model model,@AuthenticationPrincipal User user) {
        model.addAttribute("locations", destinationService.destinationListUsers());
        model.addAttribute("newJourney", new Journey());
        List<User> userList = userService.findAll();
        userList.remove(user);
        model.addAttribute("users", userList);
        return "Addjourney";
    }

    @PostMapping(value = "/AddJourney", params = {"addDestination"})
    public String addDestination(@ModelAttribute("newJourney") Journey journey,Model model, final BindingResult bindingResult) {


        Destination destination = new Destination();
        destination.setName("destination");
        destination.setCoordinates("destination");

        journey.getRoute().add(destination);
        model.addAttribute("locations", destinationService.destinationListUsers());
        model.addAttribute("users", userService.findAll());

        return "Addjourney";
    }

    @PostMapping(value = "/AddJourney", params = {"coordinates"})
    public String Coordinates(@ModelAttribute("newJourney") Journey journey,Model model, final BindingResult bindingResult, final HttpServletRequest req) throws IOException, JSONException {
        SupportMethods supportMethods = new SupportMethods();
//        supportMethods.setDisplayName(journey.getRoute().get(Integer.valueOf(req.getParameter("coordinates"))));
        supportMethods.setCoordinates(journey.getRoute().get(Integer.valueOf(req.getParameter("coordinates"))));
        supportMethods.setCountry(journey.getRoute().get(Integer.valueOf(req.getParameter("coordinates"))));
        model.addAttribute("locations", destinationService.destinationListUsers());
        model.addAttribute("users", userService.findAll());

        return "Addjourney";
    }


    @PostMapping(value = "/AddJourney", params = {"coordinatesStartPosition"})
    public String CoordinatesStartPosition(@ModelAttribute("newJourney") Journey journey,Model model, final BindingResult bindingResult, final HttpServletRequest req) throws IOException, JSONException {
        journey.getStart_position().setCoordinates(journey.getStart_position().getName());
        SupportMethods supportMethods = new SupportMethods();
//        supportMethods.setDisplayName(journey.getStart_position());
        supportMethods.setCoordinates(journey.getStart_position());
        supportMethods.setCountry(journey.getStart_position());
        model.addAttribute("locations", destinationService.destinationListUsers());
        model.addAttribute("users", userService.findAll());
        journey.getStart_position().setCoordinates(journey.getStart_position().getName());

        return "Addjourney";
    }

    @PostMapping(value = "/AddJourney", params = {"removeAll"})
    public String clearRoute(@ModelAttribute("newJourney") Journey journey, final BindingResult bindingResult, final HttpServletRequest req,Model model) {
        journey.getRoute().clear();
        model.addAttribute("users", userService.findAll());
        model.addAttribute("locations", destinationService.destinationListUsers());

        return "Addjourney";
    }
///Добавь везде модель и метод         model.addAttribute("locations", destinationService.destinationListUsers());

    @PostMapping(value = "/AddJourney", params = {"saveChanges"})
    public String saveChanges(@ModelAttribute("newJourney") Journey journey, final BindingResult bindingResult, final HttpServletRequest req,Model model) {
        String destination = journey.getRoute().get(Integer.valueOf(req.getParameter("saveChanges"))).getCoordinates();
        Destination destination1 =journey.getRoute().get(Integer.valueOf(req.getParameter("saveChanges")));
        //проверить фикс
        if((destination1.getCoordinates()!=null & destination1.getName() ==null)|(!destination1.getCoordinates().equals("") & destination1.equals(""))){
            journey.getRoute().get(Integer.valueOf(req.getParameter("saveChanges"))).setName(destination);
        }
        if(destination1.getName() !=null & (destination1.getCoordinates()==null |destination1.getCoordinates().equals("destination")||destination1.getCoordinates().equals("")) ){
            journey.getRoute().get(Integer.valueOf(req.getParameter("saveChanges"))).setCoordinates(destination1.getName());
            Destination destinationOriginal = destinationService.findByCoordinates(destination1.getName());
            journey.getRoute().get(Integer.valueOf(req.getParameter("saveChanges"))).setLatlngX(destinationOriginal.getLatlngX());
            journey.getRoute().get(Integer.valueOf(req.getParameter("saveChanges"))).setLatlngY(destinationOriginal.getLatlngY());

        }


        model.addAttribute("users", userService.findAll());
        model.addAttribute("locations", destinationService.destinationListUsers());

        return "Addjourney";
    }

    @PostMapping(value = "/AddJourney", params = {"saveChangesDefault"})
    public String saveChangesDefault(@ModelAttribute("newJourney") Journey journey, final BindingResult bindingResult, final HttpServletRequest req,Model model) {

      //если на выбор
        if (journey.getStart_position().getName().equals("") & !journey.getStart_position().getCoordinates().equals("")){
            journey.getStart_position().setName(journey.getStart_position().getCoordinates());
Destination destination = destinationService.findByCoordinates(journey.getStart_position().getCoordinates());
journey.getStart_position().setLatlngX(destination.getLatlngX());
            journey.getStart_position().setLatlngY(destination.getLatlngY());

        }

        //если кастомно
        if(journey.getStart_position().getCoordinates().equals("")){
            journey.getStart_position().setCoordinates(journey.getStart_position().getName());

        }

        model.addAttribute("users", userService.findAll());
        model.addAttribute("locations", destinationService.destinationListUsers());

        return "Addjourney";
    }


    @RequestMapping(method = RequestMethod.POST, value = "/AddJourney", params = {"AddOption"})
    public String addOption(@ModelAttribute("newJourney") Journey journey, final BindingResult bindingResult, final HttpServletRequest req,Model model) {
        journey.getSuggestedPeople().add(new User());
        model.addAttribute("locations", destinationService.destinationListUsers());
        model.addAttribute("users", userService.findAll());

        return "Addjourney";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/AddJourney", params = {"DeleteOption"})
    public String DeleteOption(@ModelAttribute("newJourney") Journey journey, final BindingResult bindingResult, final HttpServletRequest req,Model model) {
        final Integer rowId = Integer.valueOf(req.getParameter("DeleteOption"));
        journey.getSuggestedPeople().remove(rowId.intValue());
        model.addAttribute("locations", destinationService.destinationListUsers());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("users", userService.findAll());
        return "Addjourney";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/AddJourney", params = {"deleteUser"})
    public String DeleteUser(@ModelAttribute("newJourney") Journey journey, final BindingResult bindingResult, final HttpServletRequest req,Model model) {
        final Integer rowId = Integer.valueOf(req.getParameter("deleteUser"));
        journey.getPeople().remove(rowId.intValue());
        model.addAttribute("locations", destinationService.destinationListUsers());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("users", userService.findAll());
        return "Addjourney";
    }



    @RequestMapping(method = RequestMethod.POST, value = "/addJourney", params = {"AddLocation"})
    public String addLocation(@ModelAttribute Journey journey, final BindingResult bindingResult,final HttpServletRequest req,Model model) {
        journey.getRoute().add(new Destination());
        model.addAttribute("users", userService.findAll());
        return "create_journey";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/AddJourney", params = {"DeleteLocation"})
    public String DeleteLocation(@ModelAttribute("newJourney") Journey journey, final BindingResult bindingResult, final HttpServletRequest req,Model model) {
        final Integer rowId = Integer.valueOf(req.getParameter("DeleteLocation"));
        journey.getRoute().remove(rowId.intValue());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("locations", destinationService.destinationListUsers());

        return "Addjourney";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/AddJourney", params = {"AddTag"})
    public String addTag(@ModelAttribute("newJourney") Journey journey, final BindingResult bindingResult, @RequestParam(value = "choice", required = false) boolean choice,Model model) {
        journey.getTags().add(new Tag());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("locations", destinationService.destinationListUsers());
        return "Addjourney";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/AddJourney", params = {"DeleteTag"})
    public String DeleteTag(@ModelAttribute("newJourney") Journey journey, final BindingResult bindingResult, final HttpServletRequest req, @RequestParam(value = "choice", required = false) boolean choice,Model model) {
        final Integer rowId = Integer.valueOf(req.getParameter("DeleteTag"));
        journey.getTags().remove(rowId.intValue());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("locations", destinationService.destinationListUsers());
        return "Addjourney";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/AddJourney", params = {"AddCategory"})
    public String addCategory(@ModelAttribute("newJourney") Journey journey, final BindingResult bindingResult,Model model) {
        journey.getCategories().add(Category.OTHERS);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("locations", destinationService.destinationListUsers());
        return "Addjourney";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/AddJourney", params = {"DeleteCategory"})
    public String DeleteCategory(@ModelAttribute("newJourney") Journey journey, final BindingResult bindingResult, final HttpServletRequest req,Model model) {
        final Integer rowId = Integer.valueOf(req.getParameter("DeleteCategory"));
        journey.getCategories().remove(rowId.intValue());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("locations", destinationService.destinationListUsers());

        return "Addjourney";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/AddJourney", params = {"Save"})
    public String SaveJourney(@AuthenticationPrincipal User user, @ModelAttribute("newJourney") Journey journey, @RequestParam("files") MultipartFile file, @RequestParam("file1") MultipartFile file1, final BindingResult bindingResult, final ModelMap model) {

        if (journey.getStart_position().getName().equals("") & !journey.getStart_position().getCoordinates().equals(""))
            journey.getStart_position().setName(journey.getStart_position().getCoordinates());

        if(journey.getStart_position().getCoordinates().equals(""))
            journey.getStart_position().setCoordinates(journey.getStart_position().getName());


        if (!file.isEmpty()) {
            try {
                journey.setThumbnail(file.getBytes());


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!file1.isEmpty()) {
            try {
                journey.setFile(file1.getBytes());
                journey.setFilename(file1.getOriginalFilename());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(journey.getId()!=null){
            journeyService.editJourney(journey);
            return "redirect:/journey";

        }

        else{
            journey.setOrganizator(user);

            journeyService.saveJourney(journey);

        }
        sendInvitations(journey);
model.addAttribute("success","Success");
        return "Addjourney";
    }


    @Async
    public void sendInvitations(Journey journey){
        String message = mailContentBuilder.build(journey);
        mailSender.sendInvitation(journey,message);
    }

    @RequestMapping(value = "searchDestination", method = RequestMethod.GET)
    @ResponseBody
    public List<String> availableDestinations(HttpServletRequest request) {
        return destinationService.search(request.getParameter("term"));
    }

    @RequestMapping(value = "/edit/trip/{id}", method = RequestMethod.POST)
    public String editJourney(Model model, @PathVariable("id") Long id, @AuthenticationPrincipal User user) {

        Journey journey = journeyService.findJourneyById(id);

        if (!user.getUsername().equals(journey.getOrganizator().getUsername()) & !user.getRoles().contains(Role.ADMIN))
            return "redirect:/";
        model.addAttribute("newJourney", journey);
        model.addAttribute("user", user);
        model.addAttribute("participants",journey.getPeople());
        List<User> userList = userService.findAll();
        userList.remove(user);
        model.addAttribute("users", userList);
        model.addAttribute("locations", destinationService.destinationListUsers());

        return "Addjourney";
    }

    @RequestMapping(value = "/repeat/trip/{id}", method = RequestMethod.POST)
    public String repeatJourney(Model model, @PathVariable("id") Long id, @AuthenticationPrincipal User user) {
        Journey journey = journeyService.findJourneyById(id);
        Journey newJourney = new Journey();
        setRepeat(newJourney,journey);
        model.addAttribute("newJourney", newJourney);
        model.addAttribute("user", user);
        List<User> userList = userService.findAll();
        userList.remove(user);
        model.addAttribute("users", userList);
        return "Addjourney";
    }

    //because Hibernate doesn't allow me to just set id on null -__-
    private void setRepeat(Journey New, Journey old){
        New.setTags(old.getTags());
        New.setCategories(old.getCategories());
        New.setFile(old.getFile());
        New.setThumbnail(old.getThumbnail());
        New.setTitle(old.getTitle()+" (Repeat)");
        New.setRoute(old.getRoute());
        New.setStart_position(old.getStart_position());
        New.setPlaces(old.getPlaces());
        New.setByInvitation(old.isByInvitation());
        New.setPremoderated(old.isPremoderated());
        New.setDescription(old.getDescription());
    }

}


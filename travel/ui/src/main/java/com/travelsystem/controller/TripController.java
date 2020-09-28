package com.travelsystem.controller;

import com.travelsystem.SupportMethods;
import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.logic.services.ReviewService;
import com.travelsystem.logic.services.UserService;
import com.travelsystem.model.dao.Destination;
import com.travelsystem.model.dao.Journey;
import com.travelsystem.model.dao.Review;
import com.travelsystem.model.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TripController {


    private final
    JourneyService journeyService;

    private  final UserService userService;

    private SupportMethods supportMethods = new SupportMethods();
    private final ReviewService reviewService;


    @Autowired
    public TripController(JourneyService journeyService, UserService userService, ReviewService reviewService) {
        this.journeyService = journeyService;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("trip/{id}")
    public String tripPage(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal User user) {
        Journey journey = journeyService.findJourneyById(id);
        User user1= userService.findByUsername(user.getUsername());
        model.addAttribute("trip", journey);
        model.addAttribute("user", user1);
        model.addAttribute("journey", journey);
        model.addAttribute("review", new Review());
        model.addAttribute("comments",journeyService.commentsById(id));
        model.addAttribute("estimated",String.valueOf(journey.getStart_date().getTime()-journey.getEnd_date().getTime()));
        model.addAttribute("connectedReviews",reviewService.reviewsByJourney(id));
        model.addAttribute("endpoint",endDestination(id));
        return "trip";
    }

    private String endDestination(Long id){
        List<String> address= new ArrayList<>();
        List<Destination> destinations = journeyService.findJourneyById(id).getRoute();

        if(destinations.size()==0)
            return  journeyService.findJourneyById(id).getStart_position().getCoordinates();
        else
        return destinations.get(destinations.size()-1).getCoordinates();
    }


    @GetMapping(value = "/file/{id}",produces = {MediaType.APPLICATION_PDF_VALUE, MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE,MediaType.IMAGE_JPEG_VALUE})
    @ResponseBody
    public ResponseEntity<byte[]> includedFile(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        Journey journey = journeyService.findJourneyById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + journey.getFilename() + "\"")
                .body(journey.getFile());



         // return   journeyService.includedFile(id);

    }

    @GetMapping(value = "/tripImage/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] loadProfileImage(@PathVariable("id") Long id) {
        return journeyService.getFullImageById(id);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/signOff/{id}")
    public String signOffJourney(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
        journeyService.signOffJourney(user, id);
        return "redirect:/trip/"+id;
    }

    @RequestMapping(value = "/closeJourney/{id}", method = RequestMethod.PUT)
    public String closeJourney(@PathVariable("id") Long id, @AuthenticationPrincipal User user){
        journeyService.closeJourney(id,user);
        return"redirect:/trip/"+id;
    }

    @RequestMapping(value ="/applyForTrip/{id}",method = RequestMethod.POST)
    public String applyForJourney(@PathVariable ("id") Long id, @AuthenticationPrincipal User user){
        journeyService.applyForJourney(user, journeyService.findJourneyById(id));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userService.findByUsername(user.getUsername()), user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/trip/"+id;
    }

    @RequestMapping(value ="/sendRequest/{id}",method = RequestMethod.POST)
    public String sendRequest(@PathVariable ("id") Long id, @AuthenticationPrincipal User user){
        Journey journey = journeyService.findJourneyById(id);
        if (journey.getOrganizator() == user)
            journeyService.simpleSignAgain(user.getId(),journey.getId());
        else if(journey.getSuggestedPeople().contains(user)){
            journeyService.confirmJourneyForUser(user.getId(),journey.getId());

        }else
            journeyService.applyForModeratedJourney(user, journeyService.findJourneyById(id));
        return "redirect:/trip/"+id;
    }
    @RequestMapping(value = "/addComment/", method = RequestMethod.POST,params = {"addComment"})
    public String addComment(@AuthenticationPrincipal User user, @ModelAttribute Review review, final BindingResult bindingResult, final HttpServletRequest req, @RequestParam("file") MultipartFile file) throws IOException {
        if(!file.isEmpty()){
            review.setImage(file.getBytes());
            review.setFilename(file.getOriginalFilename());
        }
        review.setAuthor(user);
        journeyService.addComment(review,Long.valueOf(req.getParameter("addComment")));
        return "redirect:/trip/"+Long.valueOf(req.getParameter("addComment"));
    }

    @GetMapping(value ="/openFull/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public ResponseEntity<byte[]> openFullCommentImage(@PathVariable("id") Long id){

        Review review = reviewService.findById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + review.getFilename() + "\"")
                .body(review.getImage());

    }


    ///добавь функцию чтобы типа показывало картинку если картинка или скачать файл если файл

    @RequestMapping(value = "/removeComment/{id}", method = RequestMethod.DELETE,params = {"removeComment"})
    public String removeComment(@PathVariable("id")Long id,@ModelAttribute Review review, final BindingResult bindingResult, final HttpServletRequest req ){
        journeyService.removeComment(id,Long.valueOf(req.getParameter("removeComment")));
        return "redirect:/trip/"+Long.valueOf(req.getParameter("removeComment"));
    }

    @PutMapping("/undoRequest/{id}")
    public String undoRequest(@PathVariable("id")Long id, @AuthenticationPrincipal User user){
        journeyService.declineJourneyForUser(user.getId(),id);
        return "redirect:/trip/"+id;
    }


}

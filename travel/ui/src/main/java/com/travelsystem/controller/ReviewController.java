package com.travelsystem.controller;

import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.logic.services.ReviewService;
import com.travelsystem.model.dao.Journey;
import com.travelsystem.model.dao.Review;
import com.travelsystem.model.dao.Tag;
import com.travelsystem.model.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ReviewController {

    private final
    ReviewService reviewService;

    private final JourneyService journeyService;

    @Autowired
    public ReviewController(ReviewService reviewService, JourneyService journeyService) {
        this.reviewService = reviewService;
        this.journeyService = journeyService;
    }

    @GetMapping("/reviews")
    public String ReviewsPage(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("reviews",reviewService.findReviewsbyAuthor(user));
        model.addAttribute("journeys",journeyService.userPastJourneysReview(user));
        model.addAttribute("user",user);
        model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator",journeyService.userOrganizator(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());

        model.addAttribute("review",new Review());
        return "review";
    }



    @GetMapping("/single/review/{id}")
    public String singleReview(@PathVariable ("id")Long id,Model model,@AuthenticationPrincipal User user){
        model.addAttribute("review",reviewService.findById(id));
        model.addAttribute("journeys",journeyService.userPastJourneysReview(user));
        model.addAttribute("user",user);
        model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator",journeyService.userOrganizator(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());

return "singleReview";
    }

    @RequestMapping(value = "/review/new", method = RequestMethod.POST)
public String addReview(@ModelAttribute("review") Review review, @AuthenticationPrincipal User user, @RequestAttribute("file") MultipartFile file, final BindingResult bindingResult, final ModelMap modelMap){
        review.setAuthor(user);
        if (file != null & !file.isEmpty() ) {

            try {
                review.setImage(file.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Review forImage;
        if (review.getId()!=null){
            forImage = reviewService.findById(review.getId());

            if (review.getImage() == null)
                review.setImage(forImage.getImage());
        }



            reviewService.saveReview(review);

        return "redirect:/reviews";
    }


    @GetMapping(value ="/review/image/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] ReviewImage(@PathVariable ("id") Long id){
        return reviewService.getImageById(id);
    }

    @DeleteMapping("delete/review/{id}")
public String deleteReview(@PathVariable("id")Long id){
        reviewService.deleteReview(id);
        return "redirect:/reviews";
    }

    @PostMapping("/review/edit/{id}")
    public String editReview(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal User user){

        Review review= reviewService.findById(id);
        review.setId(id);
        model.addAttribute("review",review);
        model.addAttribute("reviews",reviewService.findReviewsbyAuthor(user));
        Journey journey = journeyService.findJourneyById(review.getJourney().getId());
        List<Journey> journeyList = new ArrayList<>();
        journeyList.add(journey);
        ModelAttributes(user, model);

        model.addAttribute("journeys",journeyList);
        return "review";
    }



    @RequestMapping(method = RequestMethod.POST, value = "/review/new", params = {"AddTag"})
    public String addTag(@ModelAttribute("review") Review review,@AuthenticationPrincipal User user, final BindingResult bindingResult, Model model) {
        review.getTag().add(new Tag());
        ModelAttributes(user, model);

        model.addAttribute("reviews",reviewService.findReviewsbyAuthor(user));
        model.addAttribute("journeys",journeyService.userPastJourneysReview(user));
        return "review";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/review/new", params = {"DeleteTag"})
    public String DeleteTag(@ModelAttribute("review") Review review,@AuthenticationPrincipal User user, final BindingResult bindingResult, final HttpServletRequest req, Model model) {
        final Integer rowId = Integer.valueOf(req.getParameter("DeleteTag"));
        review.getTag().remove(rowId.intValue());
        ModelAttributes(user, model);
        model.addAttribute("reviews",reviewService.findReviewsbyAuthor(user));
        model.addAttribute("journeys",journeyService.userPastJourneysReview(user));
        return "review";
    }

    private  void ModelAttributes(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("reviews",reviewService.findReviewsbyAuthor(user));
        model.addAttribute("journeys",journeyService.userPastJourneysReview(user));
        model.addAttribute("user",user);
        model.addAttribute("personJourney",journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator",journeyService.userOrganizator(user));
        model.addAttribute("requestNumber",journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
    }
}

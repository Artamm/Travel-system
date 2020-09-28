package com.travelsystem.controller.admin;

import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.logic.services.ReviewService;
import com.travelsystem.logic.services.TagCategoryService;
import com.travelsystem.model.dao.Review;
import com.travelsystem.model.dao.Tag;
import com.travelsystem.model.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class TagCategoryController {

    private final TagCategoryService tagCategoryService;
    private final JourneyService journeyService;
    private final ReviewService reviewService;


    @Autowired
    public TagCategoryController(TagCategoryService tagCategoryService, JourneyService journeyService, ReviewService reviewService) {
        this.tagCategoryService = tagCategoryService;
        this.journeyService = journeyService;
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews/tags/")
    public String getTagCategoryPage(Model model, @AuthenticationPrincipal User user) {

        modelAttributes(model, user);
        model.addAttribute("review", new Review());
        model.addAttribute("tags", tagCategoryService.getAll());

//        model.addAttribute("categories",Category.values() );

        return "secure/AllCategories";
    }


    private void modelAttributes(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute("personJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personActiveJourney", journeyService.userActiveJourneys(user));
        model.addAttribute("personPastJourney", journeyService.userPastJourneys(user));
        model.addAttribute("organizator", journeyService.userOrganizator(user));
        model.addAttribute("requestNumber", journeyService.premoderatedRequests(user).size() + journeyService.userInvitedInJourneys(user).size());
        model.addAttribute("upcoming", journeyService.upcomingJourneys(user));
        model.addAttribute("user", user);
        model.addAttribute("tags", tagCategoryService.getAll());
        model.addAttribute("tagsEdit", tagCategoryService.getAll());

        model.addAttribute("reviews", reviewService.findAll());
    }

    @GetMapping("admin/find/review/")
    public String findReviewByTag(Model model, @ModelAttribute("review") Review review, @AuthenticationPrincipal User user) {
        modelAttributes(model, user);
        if (review.getText().equals("") || review.getText() == null)
            return "redirect:/reviews/tags/";


        model.addAttribute("reviews",reviewService.reviewsByTag(review.getText().toLowerCase()));


        return "secure/AllCategories";
    }

    @PutMapping(value = "/edit/tag/{id}", params = {"saveChange"})
    public String editTag(@PathVariable("id")Long id, @ModelAttribute("tag") String tag, @AuthenticationPrincipal User user, Model model){

        String message = tagCategoryService.editTag(id,tag);
        modelAttributes(model,user);
        model.addAttribute("message",message);
        model.addAttribute("review", new Review());

        return "secure/AllCategories";
    }

    @DeleteMapping("/del/tag/{id}")
    public String deleteTag(@PathVariable("id")Long id){
        tagCategoryService.deleteTagById(id);
        return "redirect:/reviews/tags/";
    }

    @PutMapping(value = "/edit/review/{id}",params = {"saveChange"})
    public String editReviewText(@PathVariable("id")Long id, @ModelAttribute("review") String review, @AuthenticationPrincipal User user, Model model){

        String message = reviewService.editReviewText(id,review);
        modelAttributes(model,user);
        model.addAttribute("message",message);
        model.addAttribute("review", new Review());
        return "secure/AllCategories";
    }



}

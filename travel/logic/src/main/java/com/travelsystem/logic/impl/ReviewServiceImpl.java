package com.travelsystem.logic.impl;

import com.travelsystem.logic.Repository.JourneyRepository;
import com.travelsystem.logic.Repository.ReviewRepository;
import com.travelsystem.logic.Repository.TagRepository;
import com.travelsystem.logic.services.ReviewService;
import com.travelsystem.model.dao.Journey;
import com.travelsystem.model.dao.Review;
import com.travelsystem.model.dao.Tag;
import com.travelsystem.model.dao.User;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@Data
@CommonsLog
@Service
public class ReviewServiceImpl implements ReviewService {

    final
    ReviewRepository reviewRepository;
    final JourneyRepository journeyRepository;

    final TagRepository tagRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, JourneyRepository journeyRepository, TagRepository tagRepository) {
        this.reviewRepository = reviewRepository;
        this.journeyRepository = journeyRepository;
        this.tagRepository = tagRepository;
    }

    public byte[] openFullImageById(Long id){
        return reviewRepository.findReviewById(id).getImage();
    }

    @Override
    public List<Review> findALlbyAuthor(User user) {
        return reviewRepository.findAllByAuthor(user);
    }

    @Override
    public List<Review> findReviewsbyAuthor(User user) {
        return reviewRepository.findAllByAuthorAndIsReviewIsTrue(user);
    }

    public void saveReview(Review review){
        checkRating(review);
        review.setCreate_date(new Date());
        review.setReview(true);
        review.setJourney(journeyRepository.findJourneyById(review.getJourney().getId()));
        reviewRepository.save(review);
    }

    private  void checkRating(Review review){
        if (review.getRate() >=3){
            review.setPositive(true);
        }else{
            review.setPositive(false);
        }

    }

    public  void deleteReview(Long id){
        reviewRepository.deleteById(id);
    }

    public void editReview(Review review){
        Review edited = reviewRepository.findReviewById(review.getId());
        edited.setCreate_date(new Date());
        reviewRepository.save(edited);
    }

    public byte[] getImageById(Long id) {

        Optional<Review> ob = reviewRepository.findById(id);
        if (ob.isPresent()) {
            if (ob.get().getImage() != null)
                return ob.get().getImage();
            else {
                return null;
            }
        } else {
            // make default picture
            // return pictureRepository.findFirstByFilename("unknown.png").getFullImage();
            return null;
        }
    }

    @Override
    public List<Review> reviewsByTag(String tag) {
        Tag original = tagRepository.findByTag(tag);
        if (original != null)
            return reviewRepository.findAllByTagContaining(tagRepository.findByTag(tag));
        else
            return null;
    }

    @Override
    public Review findById(Long id) {
        return reviewRepository.findReviewById(id);
    }

    @Override
    public List<Review> reviewsByJourney(Long id) {

        Journey journey = journeyRepository.findJourneyById(id);
      return   reviewRepository.findAllByJourney(journey);
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAllByReviewIsTrue();
    }

    @Override
    public String editReviewText(Long id, String review) {

        Review original = reviewRepository.findReviewById(id);
        original.setText(review);
        reviewRepository.save(original);
        return "Review text successfuly changed";
    }


}

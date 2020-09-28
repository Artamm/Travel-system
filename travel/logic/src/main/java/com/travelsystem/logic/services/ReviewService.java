package com.travelsystem.logic.services;

import com.travelsystem.model.dao.Review;
import com.travelsystem.model.dao.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface ReviewService {
    byte[] openFullImageById(Long id);

    List<Review> findALlbyAuthor(User user);
    List<Review> findReviewsbyAuthor(User user);
    void saveReview(Review review);
    void deleteReview(Long id);
    byte[] getImageById(Long id);

    List<Review>reviewsByTag(String tag);

    Review findById(Long id);
    List<Review> reviewsByJourney(Long id);

    List<Review> findAll();

    String editReviewText(Long id, String review);
}

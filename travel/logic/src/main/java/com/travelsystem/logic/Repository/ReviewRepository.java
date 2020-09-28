package com.travelsystem.logic.Repository;

import com.travelsystem.model.dao.Journey;
import com.travelsystem.model.dao.Review;
import com.travelsystem.model.dao.Tag;
import com.travelsystem.model.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    Review findReviewById(Long id);
    List<Review> findAllByAuthor(User user);
    List<Review> findAllByAuthorAndIsReviewIsTrue(User user);
List<Review> findAllByJourney(Journey journey);
    List<Review> findReviewByJourneyAndAuthor(Journey journey, User user);
    @Query("FROM Review  where isReview = true")
    List<Review> findAllByReviewIsTrue();

    List<Review>findAllByTagContaining(Tag tag);
}

package com.travelsystem.logic.services;

import com.travelsystem.model.dao.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public interface JourneyService {

    void editJourney(Journey journey);
    void saveJourney(Journey journey);
    void save(Journey journey);
    Journey findJourneyById(Long id);
    List<Journey> userActiveJourneys(User user);
    List <Journey> userPastJourneys(User user);
    List<Journey> userPastJourneysReview(User user);
    List<Journey> userAllJourneys(User user);
    List<Journey> userOrganizator(User user);
    List<Journey> activeJourneyByOrganizator(User user);
    List <Journey> premoderatedRequests(User user);
    String applyForJourney(User user, Journey journey);
    String applyForModeratedJourney (User user, Journey journey);
    void declineJourneyForUser(Long userId, Long journeyId);
    void confirmJourneyForUser(Long userId, Long journeyId);

    byte[] getFullImageById(Long id);
    void signOffJourney(User user, Long id);
    void simpleSignAgain(Long userId, Long journeyId);

    void closeJourney(Long id,User user);

    List <Journey> allJourneys();
    List <Journey> upcomingJourneys(User user);
    List<Journey> userInvitedInJourneys(User user);
    List<Journey> userRequestedInJourneys(User user);
    void deleteJourneyById(Long id);
    List<Journey> openedAvailableJourneys();

    void addComment(Review review, Long journeyId);
    void removeComment(Long id, Long journeyId);
    List<Review>commentsById(Long id);
    byte[] includedFile(Long id) throws IOException;

    Page<Journey> findPaginated(Pageable pageable, List<Journey> journeys);



    List<Journey> searchByActive(String parameter, List<Journey> active);

//    List<Journey> searchByPast(Long id,String title,List<Category>categories,List<Tag>tags,User user);
}

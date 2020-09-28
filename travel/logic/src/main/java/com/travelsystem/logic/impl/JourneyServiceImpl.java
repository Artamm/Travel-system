package com.travelsystem.logic.impl;

import com.travelsystem.logic.Repository.*;
import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.model.dao.*;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static com.travelsystem.logic.CONSTANTS.*;

@Data
@CommonsLog
@Service
@Transactional
public class JourneyServiceImpl implements JourneyService {


    private final JourneyRepository journeyRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final DestinationRepository destinationRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public JourneyServiceImpl(JourneyRepository journeyRepository, UserRepository userRepository, TagRepository tagRepository, DestinationRepository destinationRepository, ReviewRepository reviewRepository) {
        this.journeyRepository = journeyRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.destinationRepository = destinationRepository;
        this.reviewRepository = reviewRepository;
    }


    public void saveJourney(Journey journey) {
        journey.setActive(true);
        defaultTags(journey);
        findTags(journey);
        findUsers(journey);
        defineRoute(journey);
        byInvitation(journey);
        journey.setCreate_date(new Date());
        journey.getPeople().add(journey.getOrganizator());
        if (journey.getThumbnail() != null) {
            resizeImage(journey);
        }
        journeyRepository.save(journey);


    }

    private User findOrganizator(String username) {

        return userRepository.findByUsername(username);
    }

    public void editJourney(Journey journey) {
        journey.setActive(true);
        journey.setOrganizator(findOrganizator(journey.getOrganizator().getUsername()));
        defaultTags(journey);
        findTags(journey);
        byInvitation(journey);
        Journey journeyOriginal = journeyRepository.findJourneyById(journey.getId());
        if (journey.getThumbnail() == null)
            journey.setThumbnail(journeyOriginal.getThumbnail());
        if (journey.getFile() == null)
            journey.setFile(journeyOriginal.getFile());

        defineRoute(journey);
        checkUsers(journey); //in case if new users added
        findUsers(journey);
        // journey.getPeople().add(journey.getOrganizator());

        journey.setComments(journeyOriginal.getComments());

        journey.setCreate_date(new Date());
        journeyRepository.save(journey);
    }


    private void checkUsers(Journey journey) {

        List<User> participants = new ArrayList<>();
        for (User user : journey.getPeople())
            if (userRepository.findByUsername(user.getUsername()) != null)
                participants.add(userRepository.findByUsername(user.getUsername()));

        journey.setPeople(participants);
    }


    //Set journey by invitation
    private void byInvitation(Journey journey) {
        if (journey.isByInvitation() & journey.getId() == null) {
            journey.setPeopleNumber(1 + journey.getSuggestedPeople().size() + journey.getPeople().size());

        } else if (journey.isByInvitation() & journey.getId() != null) {
            journey.setPeopleNumber(journey.getSuggestedPeople().size() + journey.getPeople().size());
        } else {
//            journey.getPeople().clear(); зачем я это добавила? -_-
            journey.getSuggestedPeople().clear();
        }
    }

    //    find Destination in repository. if doesn't exist - let user create its own
    private void defineRoute(Journey journey) {
        List<Destination> route = new ArrayList<>();
        Destination check = destinationRepository.findByCoordinates(journey.getStart_position().getCoordinates());
        if (check != null) {
            if (check.getLatlngX() == journey.getStart_position().getLatlngX() & check.getLatlngY() == journey.getStart_position().getLatlngY()) {
                Destination start_position = check;
                journey.setStart_position(start_position);
            } else {
                journey.getStart_position().setCoordinates(journey.getStart_position().getCoordinates() + "_");
                journey.getStart_position().setCreatedByUser(true);
                journey.getStart_position().setName(journey.getStart_position().getCoordinates());
                journey.getStart_position().setCreated(new Date());
            }

        } else {
            journey.getStart_position().setCreatedByUser(true);
            journey.getStart_position().setName(journey.getStart_position().getCoordinates());
            journey.getStart_position().setCreated(new Date());

        }
        for (Destination destination : journey.getRoute()) {
            if (destinationRepository.findByCoordinates(destination.getCoordinates()) != null) {
                Destination checkRouteDestination = destinationRepository.findByCoordinates(destination.getCoordinates());
                if (checkRouteDestination.getLatlngY() == destination.getLatlngY() & checkRouteDestination.getLatlngX() == destination.getLatlngX())
                    route.add(destinationRepository.findByCoordinates(destination.getCoordinates()));
                else {
                    destination.setCoordinates(destination.getCoordinates() + "_");
                    destination.setCreatedByUser(true);
                    destination.setName(destination.getCoordinates());
                    destination.setCreated(new Date());
                    route.add(destination);
                }
            } else {
                destination.setCreatedByUser(true);
                destination.setName(destination.getCoordinates());
                destination.setCreated(new Date());
                route.add(destination);
            }
        }
        journey.setRoute(route);

    }

    //    Find tags in repository
    private void findTags(Journey journey) {

        List<Tag> tags = new ArrayList<>();

        for (Tag tag : journey.getTags()) {
            if (tagRepository.findByTag(tag.getTag()) != null) {
                Tag correct_tag = tagRepository.findByTag(tag.getTag());
                tags.add(correct_tag);
            } else {
                tags.add(tag);
            }
            journey.setTags(tags);
        }
    }

    //Make tags general
    private void defaultTags(Journey journey) {
        for (Tag tag : journey.getTags()) {
            tag.setTag(tag.getTag().toLowerCase());
        }
    }

    //    find users from repository
    private void findUsers(Journey journey) {

        List<User> participants = new ArrayList<>();

        for (User user : journey.getSuggestedPeople()) {

            if (userRepository.findByUsername(user.getUsername()) != null) {
                participants.add(userRepository.findByUsername(user.getUsername()));
            }

        }
        journey.setSuggestedPeople(participants);
    }

    //resize image
    private void resizeImage(Journey journey) {
        try (InputStream in = new ByteArrayInputStream(journey.getThumbnail())) {
            BufferedImage img = ImageIO.read(in);
            BufferedImage thumbnailImage = Scalr.resize(img, Scalr.Mode.FIT_EXACT, WIDTH, HEIGHT);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                //Need to choose formats by itself
                ImageIO.write(thumbnailImage, "png", baos);
                baos.flush();
                journey.setThumbnail(baos.toByteArray());
            }
        } catch (IOException e) {
            log.error("From bytes to bufferImage in  thumbnail()  -> ");
        }
    }

    public Journey findJourneyById(Long id) {
        return journeyRepository.findJourneyById(id);

    }

    public void save(Journey journey) {
        journeyRepository.save(journey);
    }

    public List<Journey> userOrganizator(User user) {
        return journeyRepository.findAllByOrganizator(user);
    }

    public List<Journey> activeJourneyByOrganizator(User user) {
        return journeyRepository.findAllByOrganizatorAndIsActiveIsTrue(user);
    }

    public List<Journey> userAllJourneys(User user) {
        return journeyRepository.findAllByPeopleContaining(user);
    }

    public List<Journey> userActiveJourneys(User user) {
        return journeyRepository.findAllByIsActiveIsTrueAndPeopleContaining(user);
    }

    public List<Journey> userPastJourneys(User user) {
        return journeyRepository.findAllByIsActiveIsFalseAndPeopleContaining(user);
    }

    @Override
    public List<Journey> userPastJourneysReview(User user) {
        List<Journey> allJourneys = journeyRepository.findAllByIsActiveIsFalseAndPeopleContaining(user);
        List<Journey> selected = new ArrayList<>();
        for (Journey journey : allJourneys) {
            if (reviewRepository.findReviewByJourneyAndAuthor(journey, user).isEmpty())
                selected.add(journey);
        }
        return selected;
    }

    ///TB CHECKED
    public List<Journey> premoderatedRequests(User user) {
        return journeyRepository.findAllByOrganizatorAndRequestedInvitationIsNotNull(user);
    }


    public String applyForJourney(User user, Journey journey) {
        User original = userRepository.findUserById(user.getId());
        if (journey.getPeople().size() < journey.getPeopleNumber()) {
            journey.getPeople().add(original);
            journeyRepository.save(journey);
            return SUCCESSFUL_ADDITION;
        } else {
            return FAILED_ADDITION;
        }

    }

    public String applyForModeratedJourney(User user, Journey journey) {

        if (journey.getOrganizator().equals(user)) {
            simpleSignAgain(user.getId(), journey.getId());
            return SUCCESSFUL_ADDITION;
        } else {
            if (journey.getPeople().size() < journey.getPeopleNumber() | journey.isByInvitation()) {
                journey.getRequestedInvitation().add(user);
                journeyRepository.save(journey);
                return SUCCESSFUL_MODERATED_ADDITION;
            } else {
                return FAILED_ADDITION;
            }
        }
    }

    public void confirmJourneyForUser(Long userId, Long journeyId) {
        Journey journey = journeyRepository.findJourneyById(journeyId);
        User user = userRepository.findUserById(userId);

        if (journey.getPeople().size() < journey.getPeopleNumber() | journey.isByInvitation()) {
            journey.getPeople().add(user);
            journey.getRequestedInvitation().remove(user);
            journey.getSuggestedPeople().remove(user);
            journeyRepository.save(journey);
        }
    }

    @Override
    public byte[] getFullImageById(Long id) {

        Optional<Journey> ob = journeyRepository.findById(id);
        if (ob.isPresent()) {
            if (ob.get().getThumbnail() != null)
                return ob.get().getThumbnail();
            else {
                return new byte[0];
            }
        } else {
            // make default picture
            // return pictureRepository.findFirstByFilename("unknown.png").getFullImage();
            return new byte[0];
        }
    }

    public void declineJourneyForUser(Long userId, Long journeyId) {

        Journey journey = journeyRepository.findJourneyById(journeyId);
        User user = userRepository.findUserById(userId);

        if (journey.isByInvitation()) {
            journey.getSuggestedPeople().remove(user);
            journey.getRequestedInvitation().remove(user);
        }

        if (journey.getPeople().size() < journey.getPeopleNumber()) {
            journey.getSuggestedPeople().remove(user);
            journey.getRequestedInvitation().remove(user);

        }
        journeyRepository.save(journey);

    }

    public void signOffJourney(User user, Long id) {
        Journey journey = journeyRepository.findJourneyById(id);
        journey.getPeople().remove(user);
        journeyRepository.save(journey);
    }

    public void simpleSignAgain(Long userId, Long journeyId) {

        User user1 = userRepository.findUserById(userId);
        Journey journey1 = journeyRepository.findJourneyById(journeyId);
        if (journey1.getOrganizator().equals(user1))
            journey1.getPeople().add(user1);
        journeyRepository.save(journey1);
    }

    @Override
    public void closeJourney(Long id, User user) {
        Journey journey = journeyRepository.findJourneyById(id);
        if (journey.getOrganizator().equals(user))
            journey.setActive(false);
        journeyRepository.save(journey);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public List<Journey> allJourneys() {
        return journeyRepository.findAll();
    }


    public List<Journey> upcomingJourneys(User user) {
        List<Journey> journeys = journeyRepository.findAllByIsActiveIsTrueAndPeopleContaining(user);
        List<Journey> upcomingJourneys = new ArrayList<>();

        for (Journey journey : journeys) {
            if (journey.getStart_date().compareTo(new Date()) > 0) {
                upcomingJourneys.add(journey);
            }
        }
        return upcomingJourneys;
    }

    public List<Journey> userInvitedInJourneys(User user) {
        return journeyRepository.findAllByByInvitationIsTrueAndSuggestedPeopleContaining(user);
    }

    public List<Journey> userRequestedInJourneys(User user) {
        return journeyRepository.findAllByByInvitationIsTrueAndRequestedInvitationContaining(user);
    }

    public void deleteJourneyById(Long id) {
        Journey journey = journeyRepository.findJourneyById(id);
        journey.setRoute(null);
        journey.setStart_position(new Destination());
        journey.setOrganizator(null);
        journeyRepository.save(journey);
        deleteConnectedReviews(id);
        journeyRepository.deleteById(id);
    }


    private void deleteConnectedReviews(Long id) {

        List<Review> reviews = reviewRepository.findAllByJourney(journeyRepository.findJourneyById(id));

        for (Review review : reviews) {
            reviewRepository.deleteById(review.getId());
        }
    }

    @Override
    public List<Journey> openedAvailableJourneys() {
        List<Journey> journeys = journeyRepository.findAllByByInvitationIsFalseAndIsActiveTrue();
        List<Journey> openAvailable = new ArrayList<>();
        for (Journey journey : journeys) {

            if (journey.getPeopleNumber() > journey.getPeople().size()) {
                openAvailable.add(journey);
            }
        }
        return openAvailable;
    }


    public void addComment(Review review, Long journeyId) {

        Journey journey = journeyRepository.findJourneyById(journeyId);

        review.setCreate_date(new Date());
        journey.getComments().add(review);
        journeyRepository.save(journey);
    }

    @Override
    public void removeComment(Long id, Long journeyId) {
        Journey journey = journeyRepository.findJourneyById(journeyId);
        Review review = reviewRepository.findReviewById(id);
        journey.getComments().remove(review);
        journeyRepository.save(journey);


    }


    public List<Review> commentsById(Long id) {
        Journey journey = journeyRepository.findJourneyById(id);
        return journey.getComments();
    }

    public byte[] includedFile(Long id) throws IOException {
        Journey journey = journeyRepository.findJourneyById(id);
        return journey.getFile();
    }


    public Page<Journey> findPaginated(Pageable pageable, List<Journey> alls) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Journey> list;

        if (alls.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, alls.size());
            list = alls.subList(startItem, toIndex);
        }

        return new PageImpl<Journey>(list, PageRequest.of(currentPage, pageSize), alls.size());
    }

    @Override
    public List<Journey> searchByActive(String parameter, List<Journey> active) {


        List<Journey> journeys = new ArrayList<>();
        if (tagRepository.findByTag(parameter) != null)
            for (Journey journey : active) {
                journey.getTags().stream()
                        .filter(o -> journey.getTags().contains(tagRepository.findByTag(parameter)))
                        .forEach(tagTrue -> journeys.add(journey));
            }
        Category category = Existing(parameter);
        if (category != null)
            for (Journey journey : active) {
                journey.getCategories().stream()
                        .filter(o -> journey.getCategories().contains(category))
                        .forEach(categoryTrue -> journeys.add(journey));
            }

        return journeys;
    }


    public Category Existing(String parameter) {
        for (Category category : Category.values()) {
            if (category.name().equals(parameter.toUpperCase()))
                return category;
        }
        return null;
    }
//
//    public List<Journey> searchByActive(String parameter,User user){
//        Tag tag = tagRepository.findByTag(parameter);
//        List<Tag> tags = new ArrayList<>();
//        tags.add(tag);
//        List<Journey> journeyList = new ArrayList<>(journeyRepository.findDistinctByTagsInAndPeopleInAndIsActiveTrueOrderByCreate_dateDesc(tags, Collections.singletonList(user)));
//        if(Existing(parameter)){
//
//            List<Category>categoryList = Collections.singletonList(Category.valueOf(parameter));
//            journeyList.addAll(journeyRepository.findAllByCategoriesInAndPeopleContainingAndIsActiveFalseOrderByCreate_dateDesc(categoryList,user));
//
//        }
//        return  journeyList;
//
//
//    }
//    public List<Journey> searchByPast(Long id,String title,List<Category>categories,List<Tag>tags,User user){
//        return journeyRepository
//                .findAllByIdOrByTitleOrByCategoriesInOrByTagsInAndIsActiveFalseAndPeopleContainingOrderByCreate_dateDescIgnoreCase(id,title,categories,tags,user);
//
//    }

}

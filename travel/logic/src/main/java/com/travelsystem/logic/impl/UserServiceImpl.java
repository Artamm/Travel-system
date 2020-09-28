package com.travelsystem.logic.impl;

import com.travelsystem.logic.Repository.UserRepository;
import com.travelsystem.logic.services.JourneyService;
import com.travelsystem.logic.services.ReviewService;
import com.travelsystem.logic.services.UserService;
import com.travelsystem.model.dao.Journey;
import com.travelsystem.model.dao.Review;
import com.travelsystem.model.dao.Role;
import com.travelsystem.model.dao.User;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static com.travelsystem.logic.CONSTANTS.*;


@Data
@CommonsLog
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final JourneyService journeyService;

    private  final ReviewService reviewService;

    PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, JourneyService journeyService, ReviewService reviewService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.journeyService = journeyService;
        this.reviewService = reviewService;
        this.passwordEncoder = passwordEncoder;

    }


    public void updateImage(User profile) {
        resizeImage(profile);
        User user = userRepository.findByUsername(profile.getUsername());
        user.setThumbnail(profile.getThumbnail());
        userRepository.save(user);
    }

    private void resizeImage(User user) {

        try (InputStream in = new ByteArrayInputStream(user.getThumbnail())) {
            BufferedImage img = ImageIO.read(in);
//            BufferedImage thumbnailImage = Scalr.crop(img, 300,300,Scalr.OP_ANTIALIAS);
            BufferedImage thumbnailImage = Scalr.resize(img, Scalr.Mode.FIT_EXACT, 300, 300);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                //Need to choose formats by itself
                ImageIO.write(thumbnailImage, "png", baos);
                baos.flush();
                user.setThumbnail(baos.toByteArray());
            }
        } catch (IOException e) {
            log.error("From bytes to bufferImage in  thumbnail()  -> ");
        }
    }






    public byte[] getFullImageById(Long id) {
        byte[] extra = Integer.toString(232).getBytes();
        Optional<User> ob = userRepository.findById(id);
        if (ob.isPresent()) {
            if (ob.get().getThumbnail() != null)
                return ob.get().getThumbnail();
            else {
                return extra;
            }
        } else {
            // make default picture
            // return pictureRepository.findFirstByFilename("unknown.png").getFullImage();
            return extra;
        }
    }


    public void changePassword(User user, String password) {
        User original = userRepository.findUserById(user.getId());
        user.setPassword(passwordEncoder.encode(password));
        original.setPassword(user.getPassword());
        userRepository.save(original);
    }

    public void changeContacts(User user) {
        userRepository.save(user);
    }

    public void changeNS(User user) {

        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findUser(String user) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(user));
        return userOptional.orElse(null);
    }

    @Override
    public List<User> findUserByAnyParameter(Long id, String username, String surname, String name) {
        return userRepository.findUserByIdOrUsernameOrSurnameOrName(id, username, surname, name);
    }

    @Override
    public void deleteUser(Long deleteUser) {
        userRepository.deleteById(deleteUser);
    }

    @Override
    public void banUser(Long id) {
        User user = userRepository.findUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    public User userById(Long id) {
        return userRepository.findUserById(id);
    }

    @Override
    public void giveAdminRights(Long id) {
        User user = userRepository.findUserById(id);
        if (!user.getRoles().contains(Role.ADMIN)) {
            user.getRoles().add(Role.ADMIN);
            userRepository.save(user);
        }
    }

    @Override
    public void removeAdminRights(Long id) {
        User user = userRepository.findUserById(id);
        if (user.getRoles().contains(Role.ADMIN)) {
            user.getRoles().remove(Role.ADMIN);
            userRepository.save(user);
        }
    }


    /* Registration tools */
    public String RegisterUser(User user) {

        if (checkMail(user.getMail()) & checkUsername(user.getUsername())) {
            return null;
        } else {

            user.setActive(true);
            user.getRoles().add(Role.USER);
            userRepository.save(user);
            return SUCCESSFUL_SIGN_UP;
        }

    }

    @Override
    public void renameUser(User user, Long id) {
        User userDB = userRepository.findUserById(id);

        if (userRepository.findByUsername(user.getUsername()) == null | user.getUsername().equals(userDB.getUsername())) {
            userDB.setUsername(user.getUsername());
            userDB.setName(user.getName());
            userDB.setSurname(user.getSurname());
            userRepository.save(userDB);
        }

    }

    private boolean checkUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    private boolean checkMail(String mail) {
        return userRepository.findByMail(mail) != null;
    }

    public boolean isUsedByOtherUser(String mail, String username) {

        User user = userRepository.findByMail(mail);
        if (user != null)
            return !user.getUsername().equals(username);
        else
            return false;
    }
    //////////////////////////

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void deleteUserById(Long id) {
        removeDelUserFromLists(id);
        userRepository.deleteById(id);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN')")

    private void removeDelUserFromLists(Long id) {

        User user = userRepository.findUserById(id);
        List<Journey> invitedIn = journeyService.userInvitedInJourneys(user); //from lists where invited
        List<Journey> userOrganizator = journeyService.userOrganizator(user);// from lists where organizator
        List<Journey> participant = journeyService.userAllJourneys(user);//from list where participant
        List<Review> reviews = reviewService.findReviewsbyAuthor(user);
        for (Journey journey : invitedIn) {
            journey.getSuggestedPeople().remove(user);
            journey.getRequestedInvitation().remove(user);
            journeyService.save(journey);
        }
        for (Journey journey : userOrganizator) {
            journey.setOrganizator(null);
            journeyService.save(journey);
        }
        for (Journey journey : participant) {
            journey.getPeople().remove(user);
            journeyService.save(journey);
        }
        for (Review review: reviews){
            reviewService.deleteReview(review.getId());
        }



    }

    @Override
    public void setSettings(User user, User preferences) {
        User original = userRepository.findUserById(user.getId());
        original.setSendNotifications(preferences.isSendNotifications());
        original.setHideContacts(preferences.isHideContacts());
        userRepository.save(original);
    }


}

package com.travelsystem.logic.services;

import com.travelsystem.model.dao.Category;
import com.travelsystem.model.dao.Destination;
import com.travelsystem.model.dao.Journey;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional

public interface SearchService {

    //--------------------------------------Journey search------------------------------------------

    //    List<Journey> findByStartDate(Date start);
//    List<Journey> findByStartPosition(String destination);
    List<Journey> findOpenJourney();
    List<Journey> findByTitle(String title);
    List<Journey> findByStartDateAndPosition(Date date, String postion);
//    List<Journey> findByStartPosition(Destination destination);
    List<Journey> findByOrganizator(String organizator);
    List<Journey> findJourneyByCategories (List<String> categories);
    List<Journey> findByTags(List<String> tags);
    List<Journey> findByCategoriesAndTags(List<String> tags, List<String>categories);

    List<Journey>findByDestinationName(String name);

    //--------------------------------------Destination search------------------------------------------

    Destination findByName(String name);
    Destination findByCoordinates(String address);
    List<Destination> findByCountry(String country);
    List<Destination> findbyAllParametersNotStrict(Destination destination);
    List<Destination> findDestinationByCategories(List<Category>categories);
    List<Destination> findDestinationByStringCategory(String category);
    List<Destination> searchLocations(String keyword);

    List<Journey> findActiveJourneysByParameters(String title, String start,Integer people, Date keyword, Date date, boolean active);
    List<Journey> findAllJourneysByParameters(String title, String start,Integer people, Date keyword, Date date, boolean active);




}

package com.travelsystem.logic.impl;

import com.travelsystem.logic.Repository.DestinationRepository;
import com.travelsystem.logic.Repository.JourneyRepository;
import com.travelsystem.logic.Repository.TagRepository;
import com.travelsystem.logic.Repository.UserRepository;
import com.travelsystem.logic.services.SearchService;
import com.travelsystem.model.dao.*;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
@CommonsLog
@Service
@Transactional
public class SearchServiceImpl implements SearchService {

    private final JourneyRepository journeyRepository;
    private final DestinationRepository destinationRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Autowired
    public SearchServiceImpl(JourneyRepository journeyRepository, DestinationRepository destinationRepository, UserRepository userRepository, TagRepository tagRepository) {
        this.journeyRepository = journeyRepository;
        this.destinationRepository = destinationRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    /*Search functions */
//    public List<Journey> findByStartDate(Date start){
//        return journeyRepository.findAllByStart_dateOrderByCreate_dateDesc(start);
//    }

//    @Override
//    public List<Journey> findByStartPosition(String input) {
//        Optional <Destination> destination = destinationRepository.findDestinationByCoordinates(input);
//        return destination.map(journeyRepository::findAllByStart_position).orElse(null);
//    }


    //--------------------------------------Journey search---------------------------------------------
    @Override
    public List<Journey> findOpenJourney() {
        return journeyRepository.findAllByByInvitationIsFalse();
    }

    @Override
    public List<Journey> findByTitle(String title) {
        return journeyRepository.findAllByTitle(title);
    }

    @Override
    public List<Journey> findByStartDateAndPosition(Date date, String postion) {
        return null;
    }

//    @Override
//    public List<Journey> findByStartPosition(Destination destination) {
//        return journeyRepository.findByStart_position(destination);
//    }

    @Override
    public List<Journey> findByOrganizator(String organizator) {
        Optional <User> user = Optional.ofNullable(userRepository.findByUsername(organizator));
        return user.map(journeyRepository::findAllByOrganizator).orElse(null);

    }

    @Override
    public List<Journey> findJourneyByCategories(List<String> categories) {

        List<Category> categoryList = new ArrayList<>();

        for(String category : categories){

            for( Category category1 : Category.values()){
                if(category1.toString().equals(category))
                    categoryList.add(category1);
            }
        }
        return journeyRepository.findAllByCategoriesIn(categoryList);
    }

    @Override
    public List<Journey> findByTags(List<String> tags) {
        List <Tag> tagList = new ArrayList<>();
        for(String tag : tags){
            Optional<Tag> tagOne = Optional.ofNullable(tagRepository.findByTag(tag));
            tagOne.ifPresent(tagList::add);
        }
        return journeyRepository.findDistinctByTagsIn(tagList);
    }

    @Override
    public List<Journey> findByCategoriesAndTags(List<String> tags, List<String> categories) {
        return null;
    }

    @Override
    public List<Journey> findByDestinationName(String name) {

        Destination destination = destinationRepository.findByName(name);
        List<Journey> journeys= new ArrayList<>();
        journeys.addAll(journeyRepository.findByRouteContaining(destination));
        journeys.addAll(journeyRepository.findAllByStart_position(destination.getName()));
        return journeys;
    }


    //--------------------------------------Journey search---------------------------------------------

    //--------------------------------------Destination search------------------------------------------
    @Override
    public Destination findByName(String name) {
        return destinationRepository.findByName(name);
    }

    @Override
    public Destination findByCoordinates(String address) {
        return destinationRepository.findByCoordinates(address);
    }

    @Override
    public List<Destination> findByCountry(String country) {
        return destinationRepository.findByCountry(country);
    }

    @Override
    public List<Destination> findbyAllParametersNotStrict(Destination destination) {
        return destinationRepository.findAllByNameOrAndCountryOrCoordinatesIgnoreCase(destination.getName(),destination.getCountry(),destination.getCoordinates());
    }

    @Override
    public List<Destination> findDestinationByCategories(List<Category>categories) {
        return destinationRepository.findAllByCategoriesIn(categories);
    }

    @Override
    public List<Destination> findDestinationByStringCategory(String category) {

        List<Category>categoryList = new ArrayList<>();
        for( Category category1 : Category.values()){
            if(category1.toString().equals(category))
                categoryList.add(category1);
        }

        return destinationRepository.findAllByCategoriesIn(categoryList);
    }

    @Override
    public List<Destination> searchLocations(String keyword) {
        return destinationRepository.searchLocations(keyword);
    }

    @Override
    public List<Journey> findActiveJourneysByParameters(String title, String start,Integer people,  Date keyword, Date date, boolean active) {
        return journeyRepository.findByAllParametersStrictCase(title,start,date,people);
    }

    @Override
    public List<Journey> findAllJourneysByParameters(String title, String start,Integer people,  Date keyword, Date date, boolean active) {
        return journeyRepository.findAllByAllParametersStrictCase(title,start,date,people);
    }
    //--------------------------------------Destination search------------------------------------------

    //--------------------------------------User search------------------------------------------

    //--------------------------------------User search------------------------------------------


    //--------------------------------------Review search------------------------------------------
    //--------------------------------------Review search------------------------------------------




}

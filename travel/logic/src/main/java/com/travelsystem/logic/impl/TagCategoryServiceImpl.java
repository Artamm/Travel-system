package com.travelsystem.logic.impl;

import com.travelsystem.logic.Repository.JourneyRepository;
import com.travelsystem.logic.Repository.ReviewRepository;
import com.travelsystem.logic.Repository.TagRepository;
import com.travelsystem.logic.services.TagCategoryService;
import com.travelsystem.model.dao.Journey;
import com.travelsystem.model.dao.Review;
import com.travelsystem.model.dao.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TagCategoryServiceImpl implements TagCategoryService {

    private final TagRepository tagRepository;
    private final JourneyRepository journeyRepository;
    private final ReviewRepository reviewRepository;


    @Autowired
    public TagCategoryServiceImpl(TagRepository tagRepository, JourneyRepository journeyRepository, ReviewRepository reviewRepository) {
        this.tagRepository = tagRepository;
        this.journeyRepository = journeyRepository;
        this.reviewRepository = reviewRepository;
    }



    public List<Tag> getAll(){
       return tagRepository.findAll();
    }
    public void deleteTagById(Long id){

        Tag tag = tagRepository.findTagById(id);
        List<Tag> tags = new ArrayList<>();
        tags.add(tag);
        List<Journey> journeys = journeyRepository.findDistinctByTagsIn(tags);
        List<Review> reviews = reviewRepository.findAllByTagContaining(tag);

        journeys.forEach(x -> {x.getTags().remove(tag); journeyRepository.save(x);});
        reviews.forEach(x -> {x.getTag().remove(tag); reviewRepository.save(x);});
        tagRepository.deleteById(id);
    }

    @Override
    public String editTag(Long id, String saveChange) {
        Tag tag = tagRepository.findTagById(id);
        if(tagRepository.findByTag(saveChange.toLowerCase())== null){
            tag.setTag(saveChange.toLowerCase());
            tagRepository.save(tag);
            return "Successfuly edited tag";
        }else
        {
            return "Tag hasn't been updated. Possible duplicate exist";
        }

    }
}

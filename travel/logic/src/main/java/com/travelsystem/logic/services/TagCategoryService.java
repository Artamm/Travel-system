package com.travelsystem.logic.services;

import com.travelsystem.model.dao.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface TagCategoryService {
    List<Tag> getAll();
    void deleteTagById(Long id);

    String editTag(Long id, String saveChange);
}

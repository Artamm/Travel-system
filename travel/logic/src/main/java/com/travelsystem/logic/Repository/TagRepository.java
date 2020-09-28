package com.travelsystem.logic.Repository;

import com.travelsystem.model.dao.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findTagById(Long id);
    Tag findByTag(String tag);
}

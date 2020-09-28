package com.travelsystem.model.dao;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Transactional
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String text;

    private int rate;
    private boolean isPositive;

    @Lob
    private byte[] image;
    private String filename;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.REFRESH,CascadeType.MERGE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Journey journey;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tag = new ArrayList<>();

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.REFRESH})
    @OnDelete(action = OnDeleteAction.CASCADE)

    private User author;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date create_date;

    private boolean isReview;


}

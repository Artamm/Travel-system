package com.travelsystem.model.dao;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JsonView(Views.idName.class)
    private String name;
    @JsonView(Views.idName.class)
    private String coordinates;
    @JsonView(Views.idName.class)
    private String country;
    private boolean createdByUser;
    @ElementCollection(targetClass = Category.class)
//    @CollectionTable(name = "journey_category", joinColumns = @JoinColumn(name = "journey_id"))
    @Enumerated(EnumType.STRING)
    private List<Category> categories = new ArrayList<>(); //поменять на сет????

    @Lob
    private byte[] thumbnail;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date created;

    private double latlngX = 0;
    private double latlngY = 0;
}

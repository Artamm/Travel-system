package com.travelsystem.model.dao;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Transactional
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.idName.class)
    private Long id; // for Hibernate bullshit
    private boolean isActive;


    @ManyToOne(cascade={CascadeType.DETACH,CascadeType.REFRESH,CascadeType.REMOVE},fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    private User organizator;

    @JsonView(Views.idName.class)
    private String title;

    @JsonView(Views.idName.class)
    private boolean isPremoderated; //to control participants / mixed choice
    @JsonView(Views.idName.class)
    private boolean byInvitation; // for closed journeys
    @JsonView(Views.idName.class)
    private int peopleNumber = 1; // for opened journeys
    @Lob
    private byte[] thumbnail;

    @Lob
    private byte[] file;
    private String filename;



    @JsonView(Views.idName.class)
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH,

    },
            fetch = FetchType.EAGER)
    @JoinTable(name = "road",
            joinColumns = @JoinColumn(name = "journey_id"),
            inverseJoinColumns = @JoinColumn(name = "destination_id")
    )
    private List<Destination> route = new ArrayList<>();

    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE,CascadeType.REFRESH})
    private List<User> people = new ArrayList<>();

    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE,CascadeType.REFRESH})
    private List<User>suggestedPeople = new ArrayList<>();

    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE,CascadeType.REFRESH})
    private List <User> requestedInvitation =new ArrayList<>();

    @JsonView(Views.idName.class)
    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    private List<Tag> tags = new ArrayList<>();


    private int places;

    @JsonView(Views.idName.class)
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    private Destination start_position;

    @JsonView(Views.idName.class)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date start_date;
    @JsonView(Views.idName.class)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date end_date;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonView(Views.idName.class)
    private Date create_date;
    @JsonView(Views.idName.class)
    private String description;

    @OneToMany(cascade=CascadeType.ALL,orphanRemoval = true)
    private List<Review> comments = new ArrayList<>();

    @JsonView(Views.idName.class)
    @ElementCollection(targetClass = Category.class)
//    @CollectionTable(name = "journey_category", joinColumns = @JoinColumn(name = "journey_id"))
    @Enumerated(EnumType.STRING)
    private List<Category> categories = new ArrayList<>(); //поменять на сет????


}

package com.travelsystem.model.dao;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Data
@NoArgsConstructor
@Entity
@Table(name = "\"User\"")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.idUsername.class)

    private Long id; // for Hibernate bullshit

    @Column(unique=true)
    @JsonView(Views.idUsername.class)

    public String username;  // for others users to find you

    private String password;
    private boolean isActive ;

    @Lob
    private byte[] thumbnail;

    /*Contact information */
    @JsonView(Views.idUsername.class)
    private String name;
    @JsonView(Views.idUsername.class)
    private String surname;
    @JsonView(Views.idUsername.class)
    private String phone;
    @Column(unique=true)
    @JsonView(Views.idUsername.class)
    private String mail;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))

    @Enumerated(EnumType.STRING)
    private Set<Role> roles;


    private boolean  hideContacts;
    private boolean sendNotifications;

    public User(User user){
        this.name=user.getName();
        this.surname=user.getSurname();
        this.mail=user.getMail();
        this.phone=user.getPhone();
        this.username=user.getUsername();
        this.isActive=true;
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        this.roles=roles;

    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
         return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}

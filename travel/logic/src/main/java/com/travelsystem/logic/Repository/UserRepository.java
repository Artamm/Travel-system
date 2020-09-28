package com.travelsystem.logic.Repository;

import com.travelsystem.model.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {


    User findByUsername(String username);

    Optional<User> findById(Long id);

   List<User>  findUserByIdOrUsernameOrSurnameOrName( Long id, String username,String surname, String name);

    User findUserById(Long id);

    User findByMail(String mail);

    List<User> findAll();
}

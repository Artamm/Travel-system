package com.travelsystem.logic.services;

import com.travelsystem.model.dao.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public interface UserService {

   void updateImage(User user);

   byte[] getFullImageById(Long id);

   void changePassword(User user, String password);

    void changeContacts(User user);

 boolean isUsedByOtherUser(String mail, String username);

 User  findByUsername(String username);
    List<User> findAll();
    User findUser(String user);
    List<User> findUserByAnyParameter( Long id, String username,String surname, String name);

    void deleteUser(Long deleteUser);

    void banUser(Long deleteUser);

    void giveAdminRights(Long id);

    void removeAdminRights(Long id);

    String RegisterUser(User user);

    User userById(Long id);

    void renameUser(User user, Long id);
    void  deleteUserById(Long id);

    void setSettings(User user, User preferences);
}

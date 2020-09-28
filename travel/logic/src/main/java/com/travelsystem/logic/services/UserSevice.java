package com.travelsystem.logic.services;

import com.travelsystem.logic.Repository.UserRepository;
import com.travelsystem.model.dao.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserSevice implements UserDetailsService {

    private final UserRepository userRepository;

    public UserSevice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(userRepository.findByUsername(username).isEnabled())
            return userRepository.findByUsername(username);
        else
            return null;
    }

    public User findbyUsername(String username){
        return  userRepository.findByUsername(username);
    }


}

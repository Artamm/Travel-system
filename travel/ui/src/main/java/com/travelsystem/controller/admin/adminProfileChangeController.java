package com.travelsystem.controller.admin;

import com.travelsystem.logic.services.UserService;
import com.travelsystem.model.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class adminProfileChangeController {


    private final
    UserService userService;

    @Autowired
    public adminProfileChangeController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/adminProfileChange/{username}")
    public String profileAdminPage(Model model, @PathVariable("username") String username){
        model.addAttribute("user", userService.findByUsername(username));
        model.addAttribute("userProfile", new User());
        model.addAttribute("passwordChange", new User());
        return  "secure/adminProfileChange";
    }


    @RequestMapping(method = RequestMethod.POST,value="/updateImage/{username}")
    public String updateProfileImageAdmin(@PathVariable ("username") String username,
                                     @RequestParam("file") MultipartFile file,
                                     @ModelAttribute User userProfile){

        if(!file.isEmpty()) {
            try {
                User user = userService.findByUsername(username);
                userProfile.setUsername(user.getUsername());
                userProfile.setThumbnail(file.getBytes());
                userService.updateImage(userProfile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "redirect:/profileUser/"+username ;
    }

    @PostMapping("/renameUser/{id}")
    public String renameUser(@ModelAttribute User user, @PathVariable("id") Long id){

        userService.renameUser(user, id);
        return "redirect:/adminProfileChange/"+userService.userById(id).getUsername();
    }

    @RequestMapping(method = RequestMethod.POST,value="/changePasswordAdmin/{id}")
    public String changePassword(@ModelAttribute("passwordChange") User passwordChange,@PathVariable("id") Long id ){
        User user =userService.userById(id);
        userService.changePassword(user, passwordChange.getPassword());
        return "redirect:/adminProfileChange/"+user.getUsername();
    }

    @RequestMapping(method = RequestMethod.POST,value="/changeContactsAdmin/{username}")
    public String changeContacts(@ModelAttribute User userContacts, @PathVariable ("username") String username){
        User user = userService.findByUsername(username);
        user.setMail(userContacts.getMail());
        user.setPhone(userContacts.getPhone());
        userService.changeContacts(user);
        return "redirect:/adminProfileChange/"+username;
    }



}

package com.travelsystem.controller;

import com.travelsystem.logic.Repository.UserRepository;
import com.travelsystem.logic.SendMailSender;
import com.travelsystem.logic.services.UserService;
import com.travelsystem.model.dao.Role;
import com.travelsystem.model.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.Random;

@Controller
public class BrowsePageController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private  final MailContentBuilder mailContentBuilder;
    private final SendMailSender sendMailSender;

    @Autowired
    public BrowsePageController(UserService userService, PasswordEncoder passwordEncoder, UserRepository userRepository, MailContentBuilder mailContentBuilder, SendMailSender sendMailSender) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.mailContentBuilder = mailContentBuilder;
        this.sendMailSender = sendMailSender;
    }


    @GetMapping("/login")
    public String main(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("main_user", new User());


        return "browse_page";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }



    @RequestMapping(value = "/forgot", params ={"forgot"}, method = RequestMethod.POST)
    public String forgotPassword(@ModelAttribute User user,Model model){

        if (user.getUsername() !=null ){

            try {
                User current = userService.findByUsername(user.getUsername());

                String pass = passGen();
                current.setPassword(passwordEncoder.encode(pass));
                userRepository.save(current);
                sendPass(user.getUsername(), current.getMail(), pass);
                model.addAttribute("message", "A new password has been sent on your mail address");
            }
            catch (Exception e){
                model.addAttribute("message","Either user does not exists, or field 'username' is not filled");
            }
        }else{
            model.addAttribute("message","Either user does not exists, or field 'username' is not filled");

        }
        return "browse_page";
    }

    private String passGen(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Async
    public void sendPass(String username,String mail, String pass){

        String message = mailContentBuilder.buildPassword(username,pass);
        sendMailSender.prepareAndSend(mail,message,"Reset password");

    }

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute User user,Model model) {
        User newUser = new User(user);
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(newUser);
        String message = userService.RegisterUser(user);

        model.addAttribute("message",message);
        sendHelloMail(user);

        return "browse_page";
    }


    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public String addProfile(@ModelAttribute("user") User user, final BindingResult bindingResult, Model model) {

        try {
            User newUser = new User(user);
            newUser.getRoles().add(Role.USER);
            userRepository.save(newUser);
            model.addAttribute("success", " New user has been added.");

            sendHelloMail(newUser);
        }
        catch (Exception e){
            model.addAttribute("error","User is not registered. Check for duplicates");
        }
        return "register";
    }

    @Async
    public void sendHelloMail(User user){
       String message = mailContentBuilder.buildHello(user);
        sendMailSender.prepareAndSend(user.getMail(),message,"Travelsystem registration");
    }

}

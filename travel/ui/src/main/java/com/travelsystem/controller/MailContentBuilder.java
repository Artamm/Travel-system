package com.travelsystem.controller;

import com.travelsystem.model.dao.Journey;
import com.travelsystem.model.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;

@Service
public class MailContentBuilder {

    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    public String build( Journey journey) {
        Context context = new Context();
        context.setVariable("author", journey.getOrganizator().getUsername());
        context.setVariable("journey",journey.getTitle());
        context.setVariable("date",journey.getStart_date());
        context.setVariable("start",journey.getStart_position().getCoordinates());

        return templateEngine.process("mailTemplate", context);
    }

    public String buildHello(User user){
        Context context = new Context();
        context.setVariable("username",user.getUsername() );
        context.setVariable("mail",user.getMail());
        context.setVariable("password",user.getPassword());
        context.setVariable("registered",new Date());

        return templateEngine.process("registerHelloTemplate", context);

    }

    public String buildGeneral(String message, Journey journey){
        Context context = new Context();
        context.setVariable("message",message);
        context.setVariable("author", journey.getOrganizator().getUsername());
        context.setVariable("journey",journey.getTitle());
        return templateEngine.process("generalMailTemplate",context);
    }

    public String buildPassword(String username, String password){
        Context context = new Context();
        context.setVariable("username",username );
        context.setVariable("password",password);
        context.setVariable("registered",new Date());

        return templateEngine.process("forgotPassTemplate", context);

    }

}
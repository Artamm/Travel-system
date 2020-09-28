package com.travelsystem.logic;

import com.travelsystem.model.dao.Journey;
import com.travelsystem.model.dao.User;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Data
@CommonsLog
@Service
public class SendMailSender {

    private final JavaMailSender javaMailSender;

    @Autowired
    public SendMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public void sendEmail() {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("wanifeta@gmail.com");

        msg.setSubject("Show Must Go On");
        msg.setText("Show Must gooooooooo ooooooooooon");

        javaMailSender.send(msg);

    }

    @Async
    public void prepareAndSend(String recipient, String message, String subject) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("travelsystem247@yahoo.com");
            messageHelper.setTo(recipient);
            messageHelper.setSubject(subject);
            messageHelper.setText(message,true);
        };

        try {
            javaMailSender.send(messagePreparator);
        } catch (MailException e) {
            // runtime exception; compiler will not force you to handle it
        }
    }

    @Async
    public  void sendInvitation(Journey journey, String message){
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

            for (User recipient :journey.getSuggestedPeople()){

                if(recipient.isSendNotifications()){
                    messageHelper.setFrom("travelsystem247@yahoo.com");
                    messageHelper.setTo(recipient.getMail());
                    messageHelper.setSubject("Invitation to "+journey.getTitle());
                    messageHelper.setText(message,true);
                }

            }

        };

        try {
            javaMailSender.send(messagePreparator);
        } catch (MailException e) {
            // runtime exception; compiler will not force you to handle it
        }

    }


}

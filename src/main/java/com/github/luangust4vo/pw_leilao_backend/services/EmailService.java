package com.github.luangust4vo.pw_leilao_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender sender;
    @Autowired
    private TemplateEngine templateEngine;

    @Async
    public void sendSimpleMail(String to, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(message);
        sender.send(mail);
    }

    @Async
    public void emailTemplate(String to, String subject, String templateName, Context context) {
        String process = templateEngine.process(templateName, context);
        MimeMessage message = sender.createMimeMessage();

        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(process, true);
            sender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

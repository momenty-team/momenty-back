package com.momenty.util.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;

    public void sendEmail(
            String toEmail,
            String title,
            String text
    ) {
        SimpleMailMessage emailForm = createMailForm(toEmail, title, text);
        emailSender.send(emailForm);
    }

    private SimpleMailMessage createMailForm(
            String toEmail,
            String title,
            String text
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);
        return message;
    }
}

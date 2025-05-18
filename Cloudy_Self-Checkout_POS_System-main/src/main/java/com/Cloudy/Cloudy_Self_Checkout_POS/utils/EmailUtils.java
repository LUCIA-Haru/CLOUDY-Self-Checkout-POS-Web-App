package com.Cloudy.Cloudy_Self_Checkout_POS.utils;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {

    private static final Logger log = LoggerFactory.getLogger(EmailUtils.class);
    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMsg(String to, String subject, String text, List<String> list){
    SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("khinezardev121st@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if (list != null && list.size() > 0) message.setCc(getCCArray(list));
        emailSender.send(message);
        log.info("✅ Mail Sent Successfully");
}
private String[] getCCArray(List<String> ccList){
    String[] cc = new String[ccList.size()];
    for (int i = 0; i < ccList.size(); i++) {
        cc[i] = ccList.get(i);
    }
    return cc;
}
//register
    public void otpEmail(String to, String subject, String otp) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("khinezardev121st@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        // HTML content with hyperlink for the reset link
        String htmlMsg = "<p><b>Your Register Details for Cloudy</b></p>" +
                "<p><b>Email: </b>" + to + "</p>" +
                "<p><b>OTP: </b>" + otp + "</p>";

        message.setContent(htmlMsg, "text/html");
        emailSender.send(message);
    }
//forgot
public void forgotEmail(String to, String subject, String otp) throws MessagingException {
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setFrom("khinezardev121st@gmail.com");
    helper.setTo(to);
    helper.setSubject(subject);

    // HTML content with hyperlink for the reset link
    String htmlMsg = "<p><b>Your Login Details for Cafe Management System</b></p>" +
            "<p><b>Email: </b>" + to + "</p>" +
            "<p><b>OTP: </b>" + otp + "</p>" +
            "<p>Click the following link to reset your password: " ;

//            "<a href='" + resetLink + "'>Reset Link</a></p>";

    message.setContent(htmlMsg, "text/html");
    emailSender.send(message);
    log.info("✅ Mail Sent Successfully");
}
}

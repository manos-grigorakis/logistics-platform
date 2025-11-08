package com.manosgrigorakis.logisticsplatform.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Year;

@Service
public class MailService {
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.mail.no_reply}")
    private String noReplyMail;

    @Value("${app.mail.support}")
    private String supportMail;

    @Value("classpath:templates/mails/reset-password/index.html")
    private Resource resetPasswordHtmlTemplate;

    @Value("${app.mail.displayName}")
    private String displayName;

    @Value("${app.reset_password.expires}")
    private String resetPasswordTokenExpiresIn;

    private final JavaMailSender mailSender;


    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetPasswordEmail(String name, String email, String token) {
        try {
            String subject = "Request for Password Reset";
            String resetUrl = frontendUrl + "/reset-password?token=" + token;

            // Format template
            String htmlTemplate = new String(
                    resetPasswordHtmlTemplate.getInputStream().readAllBytes(), StandardCharsets.UTF_8
            );

            // Format reset password expiration time (from 30m to 30)
            String formattedTokenExpiration = resetPasswordTokenExpiresIn.
                    substring(0, resetPasswordTokenExpiresIn.length() - 1);

            // Replace placeholders
            htmlTemplate = htmlTemplate
                    .replace("${name}", name)
                    .replace("${resetUrl}", resetUrl)
                    .replace("${displayName}", displayName)
                    .replace("${supportMail}", supportMail)
                    .replace("${resetPasswordTokenExpiresIn}", formattedTokenExpiration)
                    .replace("${currentYear}", String.valueOf(Year.now().getValue()));

            MimeMessage mail = buildHtmlMail(email, subject, htmlTemplate);
            mailSender.send(mail);
        } catch (IOException e) {
            throw new RuntimeException("Failed to build or process email template", e);
        }
    }

    private MimeMessage buildHtmlMail(String mailTo, String subject, String htmlTemplate) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(noReplyMail);
            helper.setReplyTo(supportMail);
            helper.setTo(mailTo);
            helper.setSubject(subject);
            helper.setText(htmlTemplate, true);

            return message;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}


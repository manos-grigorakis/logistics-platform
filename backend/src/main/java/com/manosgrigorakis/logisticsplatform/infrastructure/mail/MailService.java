package com.manosgrigorakis.logisticsplatform.infrastructure.mail;

import com.manosgrigorakis.logisticsplatform.users.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final JavaMailSender mailSender;
    private final Logger log = LoggerFactory.getLogger(MailService.class);

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.mail.no_reply}")
    private String noReplyMail;

    @Value("${app.mail.support}")
    private String supportMail;

    @Value("classpath:templates/mails/reset-password/index.html")
    private Resource resetPasswordHtmlTemplate;

    @Value("classpath:templates/mails/setup-password/index.html")
    private Resource setupPasswordHtmlTemplate;

    @Value("${app.mail.displayName}")
    private String displayName;

    @Value("${app.reset_password.expires}")
    private String resetPasswordTokenExpiresIn;
    
    @Value("${app.setup_password.expires}")
    private String setupPasswordTokenExpiresIn;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSetupPasswordMail(User user, String token) {
        log.info("Preparing password setup email for user {}", user.getEmail());
        try {
            String subject = "Complete your account setup";
            String url = frontendUrl + "/setup-password?token=" + token;
            String role = user.getRole().getName();
            String formatedRole = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();

            // Format template
            String htmlTemplate = new String(
                    setupPasswordHtmlTemplate.getInputStream().readAllBytes(), StandardCharsets.UTF_8
            );

            // Format reset password expiration time (from 30m to 30)
            String formattedTokenExpiration = setupPasswordTokenExpiresIn.
                    substring(0, setupPasswordTokenExpiresIn.length() - 1);

            // Replace placeholders
            htmlTemplate = htmlTemplate
                    .replace("${name}", user.getFirstName())
                    .replace("${email}", user.getEmail())
                    .replace("${role}", formatedRole)
                    .replace("${setPasswordUrl}", url)
                    .replace("${displayName}", displayName)
                    .replace("${supportMail}", supportMail)
                    .replace("${tokenExpiresIn}", formattedTokenExpiration)
                    .replace("${currentYear}", String.valueOf(Year.now().getValue()));

            MimeMessage mail = buildHtmlMail(user.getEmail(), subject, htmlTemplate);
            mailSender.send(mail);
            log.info("Password setup email sent successfully to {}", user.getEmail());
        } catch (IOException e) {
            log.error("Failed to build or send password setup email for user {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to build or process email template", e);
        }
    }

    public void sendResetPasswordEmail(String name, String email, String token) {
        log.info("Preparing password reset email for user {}", email);

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
            log.info("Password reset email sent successfully to {}", email);
        } catch (IOException e) {
            log.error("Failed to build or send password reset email for user {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to build or process email template", e);
        }
    }

    private MimeMessage buildHtmlMail(String mailTo, String subject, String htmlTemplate) {
        log.debug("Building HTML mail to {}", mailTo);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(noReplyMail);
            helper.setReplyTo(supportMail);
            helper.setTo(mailTo);
            helper.setSubject(subject);
            helper.setText(htmlTemplate, true);
            log.info("HTML email built successfully for {}", mailTo);

            return message;
        } catch (MessagingException e) {
            log.error("Failed to build HTML email for {}: {}", mailTo, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}


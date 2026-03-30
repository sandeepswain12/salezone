package com.ecom.salezone.services.impl;

import com.ecom.salezone.enums.OtpType;
import com.ecom.salezone.exceptions.EmailException;
import com.ecom.salezone.services.EmailService;
import com.ecom.salezone.util.EmailTemplates;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Autowired
    private EmailTemplates emailTemplate;

    @Async
    @Override
    public void sendEmail(String to, String subject, String body, String logKey) {

        log.info("LogKey: {} - Sending email | to={} subject={}",logKey, to, subject);

        Email from = new Email(fromEmail, "SaleZone");
        Email toEmail = new Email(to);

        Content content = new Content("text/html", body);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sendGrid = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            int statusCode = response.getStatusCode();

            if (statusCode >= 200 && statusCode < 300) {
                log.info("LogKey: {} - Email sent successfully | to={} status={}",logKey, to, statusCode);
            } else {
                log.error("LogKey: {} - Email failed | to={} status={} responseBody={}",
                        logKey, to, statusCode, response.getBody());

                throw new EmailException(
                        "Email sending failed with status: " + statusCode
                );
            }

        } catch (Exception e) {
            log.error("LogKey: {} - Exception while sending email | to={} error={}",
                    logKey, to, e.getMessage(), e);

            throw new EmailException("Failed to send email", e);
        }
    }

    @Async
    @Override
    public void sendOtpEmail(String to, String userName, String otp,
                             OtpType type, String logKey) {

        log.info("LogKey: {} - Sending OTP email | to={} type={}", logKey, to, type);

        String subject = (type == OtpType.REGISTRATION)
                ? "Verify your SaleZone account"
                : "Your SaleZone login OTP";

        String body = emailTemplate.getOtpTemplate(userName, otp, type);

        sendEmail(to, subject, body, logKey);
    }

    @Async
    @Override
    public void sendWelcomeEmail(String to, String userName, String logKey) {

        log.info("LogKey: {} - Sending welcome email | to={}", logKey, to);

        String subject = "Welcome back to SaleZone! 👋";
        String body = emailTemplate.getWelcomeBackTemplate(userName);

        sendEmail(to, subject, body, logKey);

        log.info("LogKey: {} - Welcome email sent | to={}", logKey, to);
    }

    @Async
    @Override
    public void sendPasswordResetOtpEmail(String to, String userName,
                                          String otp, String logKey) {
        log.info("LogKey: {} - Sending password reset OTP email | to={}", logKey, to);
        String body = emailTemplate.getPasswordResetOtpTemplate(userName, otp);
        sendEmail(to, "Reset your SaleZone password", body, logKey);
        log.info("LogKey: {} - Password reset OTP email sent | to={}", logKey, to);
    }

    @Async
    @Override
    public void sendPasswordResetLinkEmail(String to, String userName,
                                           String resetLink, String logKey) {
        log.info("LogKey: {} - Sending password reset link email | to={}", logKey, to);
        String body = emailTemplate.getPasswordResetLinkTemplate(userName, resetLink);
        sendEmail(to, "Reset your SaleZone password", body, logKey);
        log.info("LogKey: {} - Password reset link email sent | to={}", logKey, to);
    }

    @Async
    @Override
    public void sendPasswordChangedEmail(String to, String userName, String logKey) {
        log.info("LogKey: {} - Sending password changed confirmation email | to={}", logKey, to);
        String body = emailTemplate.getPasswordChangedTemplate(userName);
        sendEmail(to, "Your SaleZone password was changed", body, logKey);
        log.info("LogKey: {} - Password changed email sent | to={}", logKey, to);
    }
}
package com.ecom.salezone.services.impl;

import com.ecom.salezone.exceptions.EmailException;
import com.ecom.salezone.services.EmailService;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Async
    @Override
    public void sendEmail(String to, String subject, String body) {

        log.info("Sending email | to={} subject={}", to, subject);

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
                log.info("Email sent successfully | to={} status={}", to, statusCode);
            } else {
                log.error("Email failed | to={} status={} responseBody={}",
                        to, statusCode, response.getBody());

                throw new EmailException(
                        "Email sending failed with status: " + statusCode
                );
            }

        } catch (Exception e) {
            log.error("Exception while sending email | to={} error={}",
                    to, e.getMessage(), e);

            throw new EmailException("Failed to send email", e);
        }
    }
}
package com.ecom.salezone.services.impl;

import com.ecom.salezone.exceptions.EmailException;
import com.ecom.salezone.util.EmailTemplates;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private EmailTemplates emailTemplates;

    @Test
    void sendEmail_shouldCompleteWhenSendGridReturnsSuccess() throws Exception {
        EmailServiceImpl emailService = new EmailServiceImpl();
        ReflectionTestUtils.setField(emailService, "apiKey", "test-key");
        ReflectionTestUtils.setField(emailService, "fromEmail", "from@salezone.com");
        ReflectionTestUtils.setField(emailService, "emailTemplate", emailTemplates);

        Response response = mock(Response.class);
        when(response.getStatusCode()).thenReturn(202);

        try (MockedConstruction<SendGrid> mocked = mockConstruction(SendGrid.class,
                (mock, context) -> when(mock.api(any(Request.class))).thenReturn(response))) {
            emailService.sendEmail("to@salezone.com", "Subject", "<p>Hello</p>", "log-1");
            assertEquals(1, mocked.constructed().size());
        }
    }

    @Test
    void sendEmail_shouldThrowWhenSendGridReturnsFailureStatus() throws Exception {
        EmailServiceImpl emailService = new EmailServiceImpl();
        ReflectionTestUtils.setField(emailService, "apiKey", "test-key");
        ReflectionTestUtils.setField(emailService, "fromEmail", "from@salezone.com");
        ReflectionTestUtils.setField(emailService, "emailTemplate", emailTemplates);

        Response response = mock(Response.class);
        when(response.getStatusCode()).thenReturn(500);
        when(response.getBody()).thenReturn("failure");

        try (MockedConstruction<SendGrid> mocked = mockConstruction(SendGrid.class,
                (mock, context) -> when(mock.api(any(Request.class))).thenReturn(response))) {
            EmailException exception = assertThrows(
                    EmailException.class,
                    () -> emailService.sendEmail("to@salezone.com", "Subject", "<p>Hello</p>", "log-1")
            );

            assertEquals("Failed to send email", exception.getMessage());
        }
    }
}

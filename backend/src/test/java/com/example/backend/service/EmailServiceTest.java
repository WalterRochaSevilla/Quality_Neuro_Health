package com.example.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    // Test Case 1: CORREGIDO - Camino I→1→2→3→4→F (Éxito - email enviado correctamente)
    @Test
    void sendEmail_WhenValidParameters_ShouldSendEmailSuccessfully() throws Exception {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "<p>This is a test email</p>";
        
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act - SOLO UNA VEZ
        emailService.sendEmail(to, subject, body);

        // Assert & Verify - Verificar que se siguieron todos los pasos del flujo
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
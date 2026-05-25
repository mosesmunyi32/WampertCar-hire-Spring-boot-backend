package com.wampart.wampart.service;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String toEmail, String firstName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Wampert - Password Reset OTP");
            helper.setText(
                    "Hello " + firstName + "\n\n" +
                            "Your OTP for password reset is: " + otp + "\n\n" +
                            "This otp is valid for 10 minutes. \n\n" +
                            "Thank you, \n" +
                            "Wampert team", false



            );

            mailSender.send(message);
            log.info("Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to: " + toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

}

package com.gayrimenkul.system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    public void sendPasswordResetMail(String toEmail, String token) {
        try {
            if (mailUsername == null || mailUsername.isBlank() || mailPassword == null || mailPassword.isBlank()) {
                throw new RuntimeException("Mail ayarlari eksik. Render'da SPRING_MAIL_USERNAME/SPRING_MAIL_PASSWORD veya localde MAIL_USERNAME/MAIL_PASSWORD tanimlanmali.");
            }

            String resetLink = frontendUrl + "/reset-password.html?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailUsername);
            message.setTo(toEmail);
            message.setSubject("Gayrimenkul Sistemi - Sifre Sifirlama Talebi");
            message.setText("Merhaba,\n\n"
                    + "Sifrenizi sifirlamak icin asagidaki linke tiklayin:\n"
                    + resetLink
                    + "\n\nBu linkin suresi 1 saat sonra dolacaktir.");

            mailSender.send(message);
            log.info("Sifre sifirlama maili basariyla gonderildi: {}", toEmail);
        } catch (Exception e) {
            log.error("Mail gonderimi sirasinda hata olustu: {}", e.getMessage());
            throw new RuntimeException("Mail gonderilemedi, lutfen daha sonra tekrar deneyin.");
        }
    }
}

package com.gayrimenkul.system.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetMail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("senin.mailin@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Gayrimenkul Sistemi - Şifre Sıfırlama Talebi");
            
            // Linki kendi front-end veya api adresine göre ayarlayabilirsin
            String resetLink = "http://localhost:8080/api/users/password-reset?token=" + token;
            
            message.setText("Merhaba,\n\nŞifrenizi sıfırlamak için aşağıdaki linke tıklayın:\n" + resetLink + 
                            "\n\nBu linkin süresi 1 saat sonra dolacaktır.");
            
            mailSender.send(message);
            log.info("Şifre sıfırlama maili başarıyla gönderildi: {}", toEmail);
        } catch (Exception e) {
            log.error("Mail gönderimi sırasında hata oluştu: {}", e.getMessage());
            throw new RuntimeException("Mail gönderilemedi, lütfen daha sonra tekrar deneyin.");
        }
    }
}
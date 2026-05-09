package com.gayrimenkul.system.security;

import com.gayrimenkul.system.entity.Role;
import com.gayrimenkul.system.entity.User;
import com.gayrimenkul.system.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager; // Rolleri güvenli çekmek/oluşturmak için

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Veritabanında "admin" adında biri var mı kontrol et
        if (!userRepository.existsByUsername("admin")) {
            log.info("Sistemde admin bulunamadı, otomatik oluşturuluyor...");

            // 2. ADMIN rolü var mı kontrol et, yoksa anında oluştur
            Role adminRole;
            try {
                adminRole = entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                        .setParameter("name", "ADMIN")
                        .getSingleResult();
            } catch (Exception e) {
                adminRole = new Role();
                adminRole.setName("ADMIN");
                entityManager.persist(adminRole);
            }

            // 3. Kullanıcıyı oluştur ve şifreyi BCRYPT ile kodlayarak kaydet
            User adminUser = new User();
            adminUser.setUsername("admin");
            // DİKKAT: Şifreyi Spring kendi kendine şifreleyecek, karakter kırpılma derdi bitti!
            adminUser.setPassword(passwordEncoder.encode("admin123")); 
            adminUser.setFullName("Sistem Yöneticisi");
            adminUser.setEmail("admin@gayrimenkul.com");
            
            // Rolü atıyoruz
            adminUser.getRoles().add(adminRole);

            // Veritabanına kaydediyoruz
            userRepository.save(adminUser);
            log.info("✅ BAŞARILI: Admin kullanıcısı oluşturuldu! (Kullanıcı Adı: admin | Şifre: admin123)");
        } else {
            log.info("Admin kullanıcısı zaten sistemde mevcut.");
        }
    }
}
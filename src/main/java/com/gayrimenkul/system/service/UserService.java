package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.Role;
import com.gayrimenkul.system.entity.User;
import com.gayrimenkul.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService; // Rol ataması için diğer servisimizi çağırıyoruz

    // 1. Yeni Kullanıcı Kaydı (Register)
    @Transactional
    public User createUser(User user) {
        // İleride Spring Security eklediğinizde şifreyi burada kriptolayacaksınız (BCrypt)
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Email ve Username daha önce alınmış mı kontrolü (Profesyonel yaklaşım)
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten alınmış!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Bu email adresi ile zaten kayıt olunmuş!");
        }

        // Yeni kayıt olan herkese otomatik "USER" rolü verelim
        try {
            Role defaultRole = roleService.getRoleByName("USER");
            user.getRoles().add(defaultRole);
        } catch (RuntimeException e) {
            // Veritabanında henüz "USER" rolü yoksa hata fırlatmak yerine log düşebiliriz
            System.err.println("Sistemde 'USER' rolü bulunamadı, kullanıcı rolsüz kaydediliyor.");
        }

        return userRepository.save(user);
    }

    // 2. ID'ye Göre Kullanıcı Getir
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı! ID: " + id));
    }

    // 3. Username'e Göre Kullanıcı Getir (Giriş yaparken -Login- çok işine yarayacak)
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı! Username: " + username));
    }

    // 4. Tüm Kullanıcıları Listele (İleride sadece ADMIN'ler görebilmeli)
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 5. Kullanıcı Bilgilerini Güncelle
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);

        // Sadece değiştirilmesine izin verdiğimiz alanları güncelliyoruz
        existingUser.setFullName(userDetails.getFullName());
        existingUser.setPhone(userDetails.getPhone());
        
        // Not: Email ve Şifre güncellemeleri genellikle ekstra doğrulama (Aktivasyon kodu, eski şifre onayı) gerektirdiği için buraya koymadık.

        return userRepository.save(existingUser);
    }

    // 6. Kullanıcıyı Sil
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
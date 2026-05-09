package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.Role;
import com.gayrimenkul.system.entity.User;
import com.gayrimenkul.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder; // Şifreleme için eklendi

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Bu kullanıcı adı zaten alınmış!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Bu email adresi ile zaten kayıt olunmuş!");
        }

        // Şifreyi BCrypt ile güvenli hale getiriyoruz
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            Role defaultRole = roleService.getRoleByName("USER");
            user.getRoles().add(defaultRole);
        } catch (RuntimeException e) {
            log.warn("Sistemde 'USER' rolü bulunamadı, kullanıcı rolsüz kaydediliyor.");
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı! ID: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı! Username: " + username));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);
        existingUser.setFullName(userDetails.getFullName());
        existingUser.setPhone(userDetails.getPhone());
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
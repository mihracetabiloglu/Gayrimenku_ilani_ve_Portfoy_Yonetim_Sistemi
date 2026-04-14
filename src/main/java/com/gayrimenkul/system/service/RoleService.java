package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.Role;
import com.gayrimenkul.system.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    // Yeni rol oluştur (Örn: ADMIN, USER, AGENT)
    @Transactional
    public Role createRole(Role role) {
        // İsteğe bağlı: Aynı isimde rol var mı diye kontrol eklenebilir
        return roleRepository.save(role);
    }

    // Tüm rolleri listele
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // ID'ye göre rol getir
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı! ID: " + id));
    }

    // İsme göre rol getir (Kullanıcı kayıt olurken rol atamak için çok işine yarayacak)
    @Transactional(readOnly = true)
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name) // Bu metod repository'de tanımlanmalı
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı! İsim: " + name));
    }

    // Rol güncelle
    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        Role existingRole = getRoleById(id);
        
        // Sadece değiştirilebilir alanları güncelliyoruz
        existingRole.setName(roleDetails.getName());
        
        return roleRepository.save(existingRole);
    }

    // Rol sil
    @Transactional
    public void deleteRole(Long id) {
        Role role = getRoleById(id);
        roleRepository.delete(role);
    }
}
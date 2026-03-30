package com.gayrimenkul.system.repository;

import com.gayrimenkul.system.entity.Message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Bir kullanıcının aldığı tüm mesajlar (Gelen Kutusu)
    List<Message> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    // Bir kullanıcının gönderdiği tüm mesajlar (Giden Kutusu)
    List<Message> findBySenderIdOrderByCreatedAtDesc(Long senderId);

    // İki kullanıcı arasındaki mesaj geçmişi (Chat akışı)
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtAsc(
            Long senderId1, Long receiverId1, Long senderId2, Long receiverId2);

    // Belirli bir ilan (Property) altındaki mesajlaşmalar
    List<Message> findByPropertyIdOrderByCreatedAtAsc(Long propertyId);
}

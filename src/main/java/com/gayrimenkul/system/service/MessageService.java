package com.gayrimenkul.system.service;

import com.gayrimenkul.system.entity.Message;
import com.gayrimenkul.system.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;

    // Mesaj Gönder
    @Transactional
    public Message sendMessage(Message message) {
        // İsteğe bağlı: Gönderici ve alıcı aynı kişi mi kontrolü yapılabilir
        if (message.getSender().getId().equals(message.getReceiver().getId())) {
            throw new RuntimeException("Kendinize mesaj gönderemezsiniz.");
        }
        return messageRepository.save(message);
    }

    // Gelen Kutusu
    public List<Message> getInbox(Long userId) {
        return messageRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
    }

    // Giden Kutusu
    public List<Message> getSentMessages(Long userId) {
        return messageRepository.findBySenderIdOrderByCreatedAtDesc(userId);
    }

    // İki kullanıcı arasındaki sohbet geçmişi
    public List<Message> getChatHistory(Long user1Id, Long user2Id) {
        return messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtAsc(
                user1Id, user2Id, user2Id, user1Id);
    }

    // Mesaj Sil (Sadece veritabanından tamamen siler)
    @Transactional
    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }
}
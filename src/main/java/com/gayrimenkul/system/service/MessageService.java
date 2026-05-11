package com.gayrimenkul.system.service;

import com.gayrimenkul.system.dto.MessageRequest;
import com.gayrimenkul.system.dto.MessageResponse;
import com.gayrimenkul.system.entity.Message;
import com.gayrimenkul.system.entity.Property;
import com.gayrimenkul.system.entity.User;
import com.gayrimenkul.system.repository.MessageRepository;
import com.gayrimenkul.system.repository.PropertyRepository;
import com.gayrimenkul.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    @Transactional
    public MessageResponse sendMessage(MessageRequest request, String username) {
        User sender = getUser(username);
        Property property = null;
        if (request.getListingId() != null) {
            property = propertyRepository.findById(request.getListingId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ilan bulunamadi."));
        }

        User receiver = request.getReceiverId() != null
                ? userRepository.findById(request.getReceiverId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alici bulunamadi."))
                : resolvePropertyOwner(property);
        if (receiver == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ilan sahibi bulunamadi.");
        }
        if (sender.getId().equals(receiver.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kendi ilaniniza mesaj gonderemezsiniz.");
        }

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .property(property)
                .content(request.getContent().trim())
                .read(false)
                .createdBy(sender.getId())
                .build();

        return MessageResponse.from(messageRepository.save(message));
    }

    public List<MessageResponse> getInbox(String username) {
        User user = getUser(username);
        return messageRepository.findByReceiverIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(MessageResponse::from)
                .toList();
    }

    public List<MessageResponse> getSentMessages(String username) {
        User user = getUser(username);
        return messageRepository.findBySenderIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(MessageResponse::from)
                .toList();
    }

    public MessageResponse getMessage(Long messageId, String username) {
        User user = getUser(username);
        Message message = findVisibleMessage(messageId, user);
        return MessageResponse.from(message);
    }

    @Transactional
    public MessageResponse markAsRead(Long messageId, String username) {
        User user = getUser(username);
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesaj bulunamadi."));
        if (!message.getReceiver().getId().equals(user.getId())) {
            throw new AccessDeniedException("Sadece alici mesaji okundu olarak isaretleyebilir.");
        }
        message.setRead(true);
        return MessageResponse.from(messageRepository.save(message));
    }

    public List<MessageResponse> getChatHistory(Long user1Id, Long user2Id, Long propertyId, String username) {
        User current = getUser(username);
        if (!current.getId().equals(user1Id) && !current.getId().equals(user2Id)) {
            throw new AccessDeniedException("Bu konusmayi goruntuleme yetkiniz yok.");
        }
        return messageRepository.findThread(user1Id, user2Id, propertyId)
                .stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Transactional
    public void deleteMessage(Long messageId, String username) {
        User user = getUser(username);
        Message message = findVisibleMessage(messageId, user);
        messageRepository.delete(message);
    }

    private Message findVisibleMessage(Long messageId, User user) {
        return messageRepository.findByIdAndSenderIdOrIdAndReceiverId(messageId, user.getId(), messageId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesaj bulunamadi."));
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Oturum kullanicisi bulunamadi."));
    }

    private User resolvePropertyOwner(Property property) {
        if (property == null) {
            return null;
        }
        if (property.getUser() != null) {
            return property.getUser();
        }
        Long createdBy = property.getCreatedBy();
        return createdBy == null
                ? null
                : userRepository.findById(createdBy).orElse(null);
    }
}

package com.gayrimenkul.system.Controller;

import com.gayrimenkul.system.dto.MessageRequest;
import com.gayrimenkul.system.dto.MessageResponse;
import com.gayrimenkul.system.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request,
                                                       Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.sendMessage(request, authentication.getName()));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponse>> getMessages(Authentication authentication) {
        return ResponseEntity.ok(messageService.getMessages(authentication.getName()));
    }

    @GetMapping("/inbox")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponse>> getInbox(Authentication authentication) {
        return ResponseEntity.ok(messageService.getInbox(authentication.getName()));
    }

    @GetMapping("/sent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponse>> getSentMessages(Authentication authentication) {
        return ResponseEntity.ok(messageService.getSentMessages(authentication.getName()));
    }

    @GetMapping("/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> getMessage(@PathVariable Long messageId,
                                                      Authentication authentication) {
        return ResponseEntity.ok(messageService.getMessage(messageId, authentication.getName()));
    }

    @GetMapping("/thread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponse>> getThread(@RequestParam Long user1Id,
                                                           @RequestParam Long user2Id,
                                                           @RequestParam(required = false) Long propertyId,
                                                           Authentication authentication) {
        return ResponseEntity.ok(messageService.getChatHistory(
                user1Id, user2Id, propertyId, authentication.getName()));
    }

    @GetMapping("/chat")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponse>> getChatHistory(@RequestParam Long user1Id,
                                                                @RequestParam Long user2Id,
                                                                @RequestParam(required = false) Long propertyId,
                                                                Authentication authentication) {
        return getThread(user1Id, user2Id, propertyId, authentication);
    }

    @PutMapping("/{messageId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> markAsRead(@PathVariable Long messageId,
                                                      Authentication authentication) {
        return ResponseEntity.ok(messageService.markAsRead(messageId, authentication.getName()));
    }

    @PatchMapping("/{messageId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> patchAsRead(@PathVariable Long messageId,
                                                       Authentication authentication) {
        return markAsRead(messageId, authentication);
    }

    @DeleteMapping("/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId,
                                              Authentication authentication) {
        messageService.deleteMessage(messageId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}

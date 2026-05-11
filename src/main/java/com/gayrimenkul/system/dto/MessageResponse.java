package com.gayrimenkul.system.dto;

import com.gayrimenkul.system.entity.Message;
import com.gayrimenkul.system.entity.Property;
import com.gayrimenkul.system.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Long id;
    private UserSummary sender;
    private UserSummary receiver;
    private ListingSummary property;
    private Long listingId;
    private String listingTitle;
    private String content;
    private LocalDateTime createdAt;
    private Boolean read;

    public static MessageResponse from(Message message) {
        Property property = message.getProperty();
        return MessageResponse.builder()
                .id(message.getId())
                .sender(UserSummary.from(message.getSender()))
                .receiver(UserSummary.from(message.getReceiver()))
                .property(ListingSummary.from(property))
                .listingId(property == null ? null : property.getId())
                .listingTitle(property == null ? null : property.getTitle())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .read(Boolean.TRUE.equals(message.getRead()))
                .build();
    }

    @Data
    @Builder
    public static class UserSummary {
        private Long id;
        private String username;
        private String name;
        private String email;

        public static UserSummary from(User user) {
            if (user == null) {
                return null;
            }
            String displayName = user.getFullName() != null && !user.getFullName().isBlank()
                    ? user.getFullName()
                    : user.getUsername();
            return UserSummary.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .name(displayName)
                    .email(user.getEmail())
                    .build();
        }
    }

    @Data
    @Builder
    public static class ListingSummary {
        private Long id;
        private String title;

        public static ListingSummary from(Property property) {
            if (property == null) {
                return null;
            }
            return ListingSummary.builder()
                    .id(property.getId())
                    .title(property.getTitle())
                    .build();
        }
    }
}

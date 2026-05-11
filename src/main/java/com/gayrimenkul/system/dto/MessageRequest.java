package com.gayrimenkul.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageRequest {
    private Long listingId;
    private Long receiverId;

    @NotBlank(message = "Mesaj bos birakilamaz.")
    private String content;
}

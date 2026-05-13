package com.gayrimenkul.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "property_images")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    public String getImageUrl() {
        if (imageUrl == null || imageUrl.isBlank()) {
            return imageUrl;
        }

        String normalized = imageUrl.trim();
        if (normalized.startsWith("http://")
                || normalized.startsWith("https://")
                || normalized.startsWith("/")
                || normalized.startsWith("data:")
                || normalized.startsWith("blob:")) {
            return normalized;
        }

        if (normalized.startsWith("uploads/")) {
            return "/" + normalized;
        }

        return "/uploads/" + normalized;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    @JsonIgnore
    private Property property;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;
}

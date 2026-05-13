package com.gayrimenkul.system.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gayrimenkul.system.entity.Property;
import com.gayrimenkul.system.entity.PropertyImage;
import com.gayrimenkul.system.repository.PropertyImageRepository;
import com.gayrimenkul.system.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;
    private final PropertyRepository propertyRepository;
    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name:dummy_cloud}")
    private String cloudName;

    @Value("${cloudinary.api-key:dummy_key}")
    private String apiKey;

    @Value("${cloudinary.api-secret:dummy_secret}")
    private String apiSecret;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public List<PropertyImage> savePropertyImages(Long propertyId, List<MultipartFile> images) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Ilan bulunamadi: " + propertyId));

        for (MultipartFile image : images) {
            try {
                String imageUrl = uploadToCloudinary(image);

                PropertyImage propertyImage = PropertyImage.builder()
                        .imageUrl(imageUrl)
                        .property(property)
                        .build();

                propertyImageRepository.save(propertyImage);
            } catch (IOException e) {
                throw new RuntimeException("Resim yukleme hatasi: " + image.getOriginalFilename(), e);
            }
        }

        return propertyImageRepository.findByPropertyId(propertyId);
    }

    @Transactional
    public void deletePropertyImage(Long propertyId, Long imageId) {
        PropertyImage image = propertyImageRepository.findByIdAndPropertyId(imageId, propertyId)
                .orElseThrow(() -> new RuntimeException("Resim bulunamadi: " + imageId));
        propertyImageRepository.delete(image);
    }

    private String uploadToCloudinary(MultipartFile image) throws IOException {
        if (!isCloudinaryConfigured()) {
            return saveImageLocally(image);
        }

        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl != null) {
                return secureUrl.toString();
            }
            return uploadResult.get("url").toString();
        } catch (Exception e) {
            log.warn("Cloudinary upload failed, saving image locally instead", e);
            return saveImageLocally(image);
        }
    }

    private boolean isCloudinaryConfigured() {
        return hasRealValue(cloudName, "dummy_cloud")
                && hasRealValue(apiKey, "dummy_key")
                && hasRealValue(apiSecret, "dummy_secret");
    }

    private boolean hasRealValue(String value, String dummyValue) {
        return value != null && !value.isBlank() && !dummyValue.equalsIgnoreCase(value.trim());
    }

    private String saveImageLocally(MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            throw new IOException("Bos dosya yuklenemez.");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new IOException("Sadece gorsel dosyalari yuklenebilir.");
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String extension = resolveExtension(image.getOriginalFilename(), contentType);
        String fileName = UUID.randomUUID() + extension;
        Path targetPath = uploadPath.resolve(fileName).normalize();

        if (!targetPath.startsWith(uploadPath)) {
            throw new IOException("Gecersiz dosya yolu.");
        }

        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("Cloudinary bilgileri tanimli olmadigi icin gorsel lokale kaydedildi: {}", targetPath);
        return "/uploads/" + fileName;
    }

    private String resolveExtension(String originalFilename, String contentType) {
        if (originalFilename != null) {
            String cleanName = Paths.get(originalFilename).getFileName().toString();
            int dotIndex = cleanName.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < cleanName.length() - 1) {
                String extension = cleanName.substring(dotIndex).toLowerCase();
                if (extension.matches("\\.[a-z0-9]{1,8}")) {
                    return extension;
                }
            }
        }

        return switch (contentType.toLowerCase()) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}

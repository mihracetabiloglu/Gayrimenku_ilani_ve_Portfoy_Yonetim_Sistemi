package com.gayrimenkul.system.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gayrimenkul.system.entity.PropertyImage;
import com.gayrimenkul.system.repository.PropertyImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;
    private final Cloudinary cloudinary; // Config dosyasından otomatik gelecek

    @Transactional
    public void savePropertyImages(Long propertyId, List<MultipartFile> images) {
        // Her bir resmi sırayla işleyelim
        for (MultipartFile image : images) {
            try {
                // 1. Gerçekten Cloudinary'ye yükle
                String imageUrl = uploadToCloudinary(image); // Cloudinary'den dönen gerçek URL

                // 2. Entity nesnesini oluştur (Lombok builder ile)
                PropertyImage propertyImage = PropertyImage.builder()
                        .imageUrl(imageUrl) // Gerçek Cloudinary linki
                        // .property(propertyRepository.findById(propertyId).get()) // İlişki için bu lazım
                        .build();

                propertyImageRepository.save(propertyImage); // Veritabanına kaydet
            } catch (IOException e) { // Yükleme sırasında hata olursa, bunu loglayabilir veya özel bir istisna fırlatabilirsiniz
                // Hata durumunda, örneğin loglama yapabilir veya özel bir istisna fırlatabilirsiniz
                throw new RuntimeException("Resim yükleme hatası: " + image.getOriginalFilename());
            }
        }
    }

    private String uploadToCloudinary(MultipartFile image) throws IOException {
        // Cloudinary kütüphanesini kullanarak gerçek yükleme yapıyoruz
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
        // Cloudinary'nin bize döndürdüğü sonuçtan gerçek URL'yi alıyoruz
        return uploadResult.get("url").toString(); // Cloudinary'nin verdiği gerçek link
    }
}
# Spring Security Hızlı Başlangıç Rehberi

## 🚀 İlk Adımlar

### 1. **Projeyi Build Etme**
```bash
mvn clean install
```

### 2. **Uygulamayı Başlatma**
```bash
mvn spring-boot:run
```

## 🔐 Test Etme (Postman/curl)

### 1. **Login İsteği**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUluIl0sInN1YiI6ImFkbWluIiwiaWF0IjoxNzE2Njk3NjU0LCJleHAiOjE3MTY3ODQwNTR9...",
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

### 2. **Token ile Protekted Endpoint'e Erişim**
```bash
curl -X GET http://localhost:8080/api/roles \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 3. **Public Endpoint (Token Gereksiz)**
```bash
curl -X GET http://localhost:8080/api/properties
```

## 📋 Endpoint Özeti

### Public Endpoints (Token Gerekli Değil)
- `GET /api/properties` - Tüm properties göster
- `GET /api/categories` - Tüm kategoriler göster
- `GET /api/cities` - Tüm şehirler göster
- `GET /api/districts` - Tüm districtler göster
- `GET /api/property-types` - Tüm property tipleri göster

### Authentication Endpoints
- `POST /api/auth/login` - Giriş yap ve token al
- `GET /api/auth/validate` - Token geçerliliğini kontrol et

### Admin Tarafından Kontrollenen Endpoints
- `POST /api/categories` - Kategori ekle (ADMIN)
- `PUT /api/cities/{id}` - Şehir güncelle (ADMIN)
- `DELETE /api/districts/{id}` - District sil (ADMIN)
- `GET /api/roles` - Rolleri listele (ADMIN)
- `GET /api/users` - Kullanıcıları listele (ADMIN)

### Agent/Admin Kontrollenen Endpoints
- `POST /api/properties` - Property ekle (AGENT, ADMIN)
- `PUT /api/properties/{id}` - Property güncelle (AGENT, ADMIN)
- `DELETE /api/properties/{id}` - Property sil (AGENT, ADMIN)
- `POST /api/property-images` - Resim yükle (AGENT, ADMIN)

### Authenticated Kullanıcılar İçin
- `POST /api/favorites` - Favorileri ekle (Authenticated)
- `GET /api/messages` - Mesajları al (Authenticated)
- `POST /api/messages` - Mesaj gönder (Authenticated)

## ⚙️ Konfigürasyon Dosyaları

### application.yml
```yaml
app:
  jwt:
    secret: mySecretKeyForJwtTokenGenerationAndValidationPurposesOnlyNotForProductionUse
    expiration: 86400000  # 24 saat
```

### pom.xml
Spring Security ve JWT bağımlılıkları zaten eklenmiştir.

## 🔑 Güvenlik Özellikleri

- ✅ JWT (JSON Web Token) tabanlı kimlik doğrulama
- ✅ BCrypt şifre hashleme
- ✅ Rol tabanlı erişim kontrollü (RBAC)
- ✅ Stateless session management (REST API)
- ✅ CSRF koruması devre dışı (REST API için uygun)
- ✅ Token validation ve expiration

## 🛠️ Troubleshooting

### 1. "401 Unauthorized" Hatası
- Token'ı doğru formatıyla gönderdin mi? `Authorization: Bearer <token>`
- Token geçerlilik süresi geçmiş mi?
- Username/password doğru mu?

### 2. "403 Forbidden" Hatası
- Kullanıcının gerekli rolü var mı?
- `@PreAuthorize` annotation'ı doğru mu tanımlanmış?

### 3. Token doğrulama başarısız
```
org.springframework.security.authentication.BadCredentialsException
```
- Kullanıcı adı ve şifreyi kontrol et
- Veritabanında kullanıcı var mı kontrol et

## 📚 Detaylı Dokümantasyon

Daha detaylı bilgi için: `SPRING_SECURITY_SETUP.md` dosyasını oku.

## 🎯 Sonraki Geliştirmeler

1. **Refresh Token** - Token refresh mekanizması
2. **OAuth2** - Google, GitHub gibi sağlayıcılarla entegrasyon
3. **2FA** - İki faktörlü doğrulama
4. **Audit Logging** - Kim ne zaman işlem yaptı kaydı
5. **Rate Limiting** - Brute force saldırılarına karşı koruma

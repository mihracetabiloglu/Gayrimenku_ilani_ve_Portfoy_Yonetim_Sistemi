# Spring Security Katmanı - Kurulum ve Kullanım Rehberi

## 📋 Genel Bakış

Bu proje, Spring Boot 3.3.4 için tam bir Spring Security katmanı ve JWT (JSON Web Token) tabanlı kimlik doğrulama sistemi içermektedir. Sistem, rol tabanlı erişim kontrolü (RBAC - Role-Based Access Control) sunmaktadır.

## 🔐 Implementasyonlar

### 1. **Spring Security Bağımlılıkları**
```xml
- Spring Security Starter
- JWT (jjwt) - Token oluşturma ve doğrulama
```

### 2. **Güvenlik Bileşenleri**

#### **JwtUtil.java**
JWT tokenlarını oluşturur, doğrular ve username'i extract eder.
- `generateToken()`: Kullanıcı için token üretir
- `validateToken()`: Token geçerliliğini kontrol eder
- `extractUsername()`: Tokenden username'i çıkarır
- Token geçerlilik süresi: 24 saat (yapılandırılabilir)

#### **CustomUserDetailsService.java**
Spring Security'nin UserDetailsService arayüzünü implement eder.
- Kullanıcıyı veritabanından yükler
- Kullanıcının rollerini GrantedAuthority'ye dönüştürür
- BCrypt ile şifre doğrulaması

#### **JwtAuthenticationFilter.java**
Her HTTP isteğini interceptor olarak çalışır.
- Request header'ından Bearer token'ı extract eder
- Token'ı doğrular
- Valid token'ı varsa SecurityContext'e authentication bilgisini yerleştirir

#### **SecurityConfig.java**
Tüm Spring Security yapılandırmasının merkez noktası.
- CSRF koruması devre dışı (REST API için)
- Session management: STATELESS (JWT kullanarak)
- HTTP endpoint güvenliği:
  - Public endpoints: `/api/auth/**`, `/api/public/**`
  - GET istekleri: Genel erişim (Category, City, District, PropertyType, Property)
  - POST/PUT/DELETE istekleri: Rol kontrollü

### 3. **Rol Tabanlı Erişim Kontrollü Endpoints**

| Endpoint | GET | POST | PUT | DELETE | Gerekli Rol |
|----------|-----|------|-----|--------|-----------|
| `/api/roles` | ✓ | ✓ | ✓ | ✓ | ADMIN |
| `/api/users` | ✓ | ✓ | ✓ | ✓ | ADMIN |
| `/api/categories` | ✓ | ✓ | ✓ | ✓ | ADMIN |
| `/api/cities` | ✓ | ✓ | ✓ | ✓ | ADMIN |
| `/api/districts` | ✓ | ✓ | ✓ | ✓ | ADMIN |
| `/api/property-types` | ✓ | ✓ | ✓ | ✓ | ADMIN |
| `/api/properties` | ✓ | ✓ | ✓ | ✓ | AGENT, ADMIN |
| `/api/property-images` | ✓ | ✓ | - | ✓ | AGENT, ADMIN |
| `/api/favorites` | ✓ | ✓ | ✓ | ✓ | Authenticated |
| `/api/messages` | ✓ | ✓ | ✓ | ✓ | Authenticated |
| `/api/logs` | ✓ | ✓ | - | - | ADMIN |

## 🔑 API Endpointleri

### 1. **Login / Kimlik Doğrulama**

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "username": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "user@example.com",
  "roles": ["ROLE_ADMIN", "ROLE_AGENT"]
}
```

**Response (401 Unauthorized):**
```json
"Hatalı kullanıcı adı veya şifre"
```

### 2. **Token Doğrulama**

**Endpoint:** `GET /api/auth/validate`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response (200 OK):**
```json
true
```

## 🚀 Kullanım Örnekleri

### 1. **Postman ile Login**

1. **POST** `http://localhost:8080/api/auth/login`
2. **Body** → **raw** → **JSON**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```
3. Response'den `token` değerini kopyala

### 2. **Protected Endpoint'e İstek Gönderme**

1. **GET** `http://localhost:8080/api/roles`
2. **Headers** → `Authorization: Bearer <token>`
3. Response: Tüm roller listesi

### 3. **cURL Örneği**

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Token ile istek
curl -X GET http://localhost:8080/api/roles \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

## 📝 Controller'a @PreAuthorize Ekleme

Yeni bir controller oluştururken, method'lara aşağıdaki gibi ekleyin:

```java
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/example")
public class ExampleController {

    // Herkes erişebilir
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok("data");
    }

    // Sadece ADMIN erişebilir
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Example example) {
        return ResponseEntity.ok(example);
    }

    // AGENT veya ADMIN erişebilir
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Example example) {
        return ResponseEntity.ok(example);
    }

    // Authenticated kullanıcılar erişebilir
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
```

## 🔧 Konfigürasyon

`application.yml` dosyasında JWT ayarları:

```yaml
app:
  jwt:
    secret: mySecretKeyForJwtTokenGenerationAndValidationPurposesOnlyNotForProductionUse
    expiration: 86400000  # 24 saat (ms cinsinden)
```

**Üretim Ortamı Için Tavsiyeler:**
- `secret` değerini güçlü bir şey ile değiştir (en az 32 karakter)
- `expiration` süresini gereksinimlerinize göre ayarla
- `secret` değerini environment variable'dan oku

**Güvenli Konfigürasyon Örneği:**
```yaml
app:
  jwt:
    secret: ${JWT_SECRET:changeme}
    expiration: ${JWT_EXPIRATION:86400000}
```

## 🛡️ Rol Tanımları

Sistem 3 ana rol kullanmaktadır:

1. **ADMIN**
   - Tüm varlıkları yönetebilir
   - System logs'u görebilir
   - Tüm kullanıcıları ve rolleri yönetebilir

2. **AGENT**
   - Property (gayrimenkul) oluşturabilir ve düzenleyebilir
   - Property resmi yükleyebilir
   - Kendi property'lerini görebilir

3. **USER**
   - Tüm property'leri görebilir
   - Favoriler oluşturabilir
   - Mesaj gönderebilir

## 🔒 Güvenlik En İyi Uygulamaları

1. **Şifre Yönetimi**
   - Şifreler BCrypt ile hash'lenir
   - Düz metin olarak depolanmaz
   - Update ettikten sonra re-hash'lenir

2. **Token Güvenliği**
   - Token'lar HTTPS üzerinden gönderilmeli (üretim ortamında)
   - Token'lar Short-lived olmalı
   - Client-side'da secure cookie'de saklanmalı

3. **CORS Konfigürasyonu**
   - Frontend URL'i whitelist'e ekle
   - Üretim ortamında domain-specific CORS ayarla

## 🐛 Sorun Giderme

### 1. "401 Unauthorized" Hatası
- Token geçerli mi kontrol et
- Token'ın geçerlilik süresi geçmiş mi kontrol et
- Authorization header'ı düzgün formatlanmış mı: `Bearer <token>`

### 2. "403 Forbidden" Hatası
- Kullanıcının gerekli rol'ü var mı
- SecurityConfig'deki kuralları kontrol et

### 3. "UsernameNotFoundException" Hatası
- Kullanıcı veritabanında var mı
- Username doğru yazılı mı (case-sensitive)

## 📚 İlgili Dosyalar

- **Security Katmanı:** `/src/main/java/com/gayrimenkul/system/security/`
  - `JwtUtil.java`
  - `CustomUserDetailsService.java`
  - `JwtAuthenticationFilter.java`
  - `SecurityConfig.java`

- **DTOs:** `/src/main/java/com/gayrimenkul/system/dto/`
  - `AuthenticationRequest.java`
  - `AuthenticationResponse.java`

- **Controllers:** `/src/main/java/com/gayrimenkul/system/Controller/`
  - `AuthenticationController.java`
  - Updated controllers with `@PreAuthorize` annotations

- **Konfigürasyon:** `/src/main/resources/`
  - `application.yml`
  - `pom.xml`

## 🎯 Sonraki Adımlar

1. **Refresh Token** implementasyonu ekle
2. **OAuth2** desteği ekle (Google, GitHub gibi)
3. **Audit Logging** ekle (kim ne zaman erişti)
4. **Rate Limiting** ekle (brute force saldırılarını önle)
5. **Two-Factor Authentication (2FA)** ekle

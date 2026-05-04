Ekip olarak ortak bir veritabanı üzerinden senkronize çalışabilmemiz için gerekli yapılandırmalar aşağıda belirtilmiştir.

___
# ⚠️ Dikkat Edilmesi Gerekenler
- Git Güvenliği: Lütfen application.yml dosyasını GitHub'a pushlamayın. Bu dosya .gitignore listesine eklenmiştir, ancak manuel olarak zorlamayın.

- Ortak Veri Alanı: Şu an hepimiz canlı bir veritabanına bağlıyız. Eklediğiniz veya sildiğiniz veriler tüm ekip tarafından anlık olarak görülecektir. Test verileri eklerken dikkatli olalım.

- Lombok Desteği: Kod yazarken Getter/Setter veya Constructor hataları almamak için IDE'nizde (VS Code veya IntelliJ) Lombok eklentisinin kurulu ve "Annotation Processing" seçeneğinin açık olduğundan emin olun.

---
## Başlangıç ve Kurulum

Projeyi yerelinizde sorunsuz çalıştırmak için lütfen aşağıdaki adımları sırasıyla takip edin:

### 1. Projeyi Klonlayın
Önce terminalinizi açın ve repoyu bilgisayarınıza indirin:
```bash
git clone [https://github.com/mihracetabiloglu/Gayrimenku_ilani_ve_Portfoy_Yonetim_Sistemi.git](https://github.com/mihracetabiloglu/Gayrimenku_ilani_ve_Portfoy_Yonetim_Sistemi.git)
cd Gayrimenku_ilani_ve_Portfoy_Yonetim_Sistemi
```

### 2. Veritabanı Yapılandırması (Önemli!)
Güvenlik nedeniyle application.yml dosyası GitHub'a yüklenmemiştir. Ortak bulut veritabanına (Railway) bağlanabilmeniz için:

src/main/resources/ klasörüne gidin.

WhatsApp grubumuzdan paylaştığım application.yml dosyasünü bu klasörün içine yapıştırın.

Eğer dosya sizde yoksa, lütfen benimle iletişime geçin.

### 3. Maven Bağımlılıklarını Yükleyin
Projenin ihtiyaç duyduğu kütüphaneleri (JPA, MySQL Driver, Lombok vb.) yüklemek için:
```bash
mvn clean install
```

### 4. Uygulamayı Çalıştırın
Her şey hazır olduğunda projeyi şu komutla ayağa kaldırabilirsiniz:
```bash
mvn spring-boot:run
```
Uygulama başarıyla çalıştığında http://localhost:8080 adresinden erişilebilir olacaktır.

## Kullanılan Teknolojiler
- Java 17

- Spring Boot 3.3.4 (Data JPA, Web, Validation, Lombok)

- MySQL (Bulut üzerinde Railway.app aracılığıyla host edilmektedir)

- Maven (Bağımlılık yönetimi)

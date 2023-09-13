# Migros Challenge Project

Bu proje, kurye konumlarını izleyen, mağazaların yakınında olduğunda kaydeden ve Kafka üzerinden konum bilgisi gönderen ve alan bir Spring Boot servisidir.

## Başlangıç

### Ön Koşullar

- **Java 17**
- **Maven**
- **Redis** (varsayılan ayarlarla)
- **Kafka** (varsayılan ayarlarla)

### Projenin Kurulumu

1. Projenin ana dizinine gidin.
2. Maven ile bağımlılıkları indirin ve projeyi derleyin:
  -- mvn clean install
### Uygulamayı çalıştırın:
  -- mvn spring-boot:run
Uygulama http://localhost:8080 üzerinde çalışmaktadır.

### Redis ve Kafka'nın Başlatılması
Redis ve Kafka'nın her ikisi de uygulama başlamadan önce çalışıyor olmalıdır. Eğer bu servisler zaten kurulu değilse, öncelikle bu servisleri kurmalı ve varsayılan ayarlarla başlatmalısınız.

### API Dökümantasyonu
API dökümantasyonu ve testi için http://localhost:8080/swagger-ui.html adresini ziyaret edebilirsiniz.



TBL324 — İleri Java Uygulamaları
Proje Strateji ve Görev Dağılımı Raporu
## Kocaeli Üniversitesi — Bilişim Sistemleri Mühendisliği
## Dr. Öğr. Üyesi Samet Diri — 2 Kişilik Ekip Planı (1+ Ay)
- Genel Strateji ve Puan Hedefi
Bu proje; zorunlu 65 puan iskelet üzerine inşa edilen 35 puanlık ek özelliklerle toplamda 100/100
hedeflemektedir. Tüm ek özellikleri seçtiğiniz ve 1+ ay zamanınız olduğu için tam puan tamamen
erişilebilir bir hedeftir.

Kritik puan birleşimleri şunlardır:
- API (10 pt) + Mikroservis Mimarisi (+10 pt) = 20 puan tek bileşenden
- Custom GUI (10 pt) + Mobil GUI (+5 pt) = 15 puan tek bileşenden
- Kalan zorunlu kriterler: 30 pt | Kalan ek özellikler: 20 pt

- Değerlendirme Kriterleri ve Puan Tablosu

## Kriter Dikkat Edilmesi Gereken Nokta Puan
## ZORUNLU KISIM
API + Mikroservis
Monolitik yerine mikroservis tercih edilmesi
durumunda iki puan birleşiyor (10+10). Servisler
JSON ile haberleşmeli.
20 pt
## Generic Yapılar
Sadece koleksiyon değil, Generic<T> ile özel
sınıflar (response wrapper, paginator vb.) yazılmalı.
10 pt
Mobil GUI
Custom GUI (10) + Mobil GUI (5) birleşiyor. Android
Java ile özel bileşenler ve custom grafikler şart.
15 pt
JDBC + NoSQL
Redis (session) ve MongoDB (döküman) izole
katmanlarda kullanılmalı. Generic repository pattern
önerilir.
10 pt
## SOLID & OOP
Repository, Factory, Strategy gibi design pattern'lar
koda entegre edilmeli.
10 pt
## Hata Yönetimi
GlobalExceptionHandler ile standart 4xx/5xx HTTP
kodları dönmeli.
5 pt
## Performans Testleri
k6 veya JMeter ile yük + kırılma testleri yapılmalı ve
rapor GitHub'a yüklenmeli.
5 pt
## Analiz & Doküman
GitHub README'de Mermaid diyagramları (mimari,
DB şeması, akış). Test raporları dahil.
5 pt
## EK ÖZELLİKLER
## Mikroservis Mimarisi
API puanıyla zaten birleşti; yukarıda 20 pt olarak
sayıldı.
+10 pt
## Docker Compose
docker-compose up ile tüm sistem (DB, servisler,
gateway) ayağa kalkmalı.
+5 pt

## TDD
Tarih damgaları inceleniyor. Testler koddan önce
yazılmış olmalı (Red-Green-Refactor).
+10 pt
## Gateway
Kong veya benzeri. Tüm trafik gateway üzerinden
geçmeli; route tanımları dokümente edilmeli.
+5 pt
Mobil GUI GUI ile birleşti; yukarıda 15 pt olarak sayıldı. +5 pt
## HEDEF TOPLAM
## 100 /
## 100

## 3. Sıfırlama Riskleri — Kesinlikle Kaçınılacaklar
Teknik başarınızdan bağımsız olarak projeyi doğrudan başarısız kılabilecek kurallar aşağıdadır:

## ! Risk Açıklaması
## !
Java dışında herhangi bir dil kullanmak — mobil dahil her bileşen saf Java ile yazılmalı.
Kotlin bile risk taşır.
## !
Düzensiz veya tek kişiye yığılmış commitler — her iki üye orantılı ve düzenli commit atmalı.
Son gün toplu yükleme yasak.
## !
TDD testlerini en sona bırakmak — tarih damgaları inceleniyor; testler implementation'dan
önce yazılmış olmalı.
! Grup kaydı ve proje gönderimini belirtilen tarihe kadar yapmamak.
! Ayrı sunum talep etmek — yalnızca tek, ortak sunum kabul ediliyor.

## 4. Ekip Görev Dağılımı
## Üye 1 — Back-end & Altyapı
- Mikroservis mimarisinin kurulumu: servis ayrımı, JSON haberleşme protokolleri
- Kong Gateway yapılandırması ve route tanımları
- JDBC katmanı — Generic<T> repository pattern ile MySQL/PostgreSQL
- NoSQL katmanı — Redis (session yönetimi) + MongoDB (döküman verileri)
- Hata yönetimi — GlobalExceptionHandler, standart 4xx/5xx HTTP kodları
- Docker Compose: tüm servisleri ve veritabanlarını containerize etmek
- SOLID prensipleri ve Design Patterns uygulaması (Repository, Factory, Strategy vb.)

## Üye 2 — Front-end, Test & Dokümantasyon
- Android (Java) mobil GUI — custom bileşenler ve özel grafik çizimleri
- TDD döngüsünü yönetmek: her servis için implementation öncesi test yazmak
- Generic<T> yardımcı sınıflar: response wrapper, paginator, generic validator
- k6 veya JMeter ile yük testi ve kırılma noktası analizi
- GitHub README: Mermaid diyagramları (sistem mimarisi, DB şeması, API akışı)
- Performans test raporunun hazırlanması ve GitHub'a yüklenmesi
- Commit düzeninin takibi — her iki üye orantılı commit atmalı


## 5. 6 Haftalık Paralel Çalışma Planı
İki üye birbirini beklemeden paralel çalışır. Üye 2, Üye 1'in tamamlamasını beklemiyor; her servis
yazıldığında test hemen arkasından geliyor.

Hafta Aşama Üye 1 (Back-end & Altyapı) Üye 2 (Front-end, Test & Dok)
## Hafta 1 Kurulum
Proje repo kurulumu, Docker
Compose iskelet, mikroservis
klasör yapısı, Maven/Gradle
modülleri
Git flow kuralları belirlenir; TDD
altyapısı (JUnit 5, Mockito)
kurulur; test paketi oluşturulur
Hafta 2 DB & API
JDBC katmanı (Generic
repository), MongoDB bağlantısı,
Redis session yönetimi, temel
entity'ler
İlk servis için testler önce yazılır
(Red aşaması); Generic<T>
yapılar tasarlanır
## Hafta 3 İş Mantığı
Mikroservis iş mantığı, SOLID
uygulaması, design pattern'lar,
hata yönetimi
(GlobalExceptionHandler)
Üye 1'in yazdığı servislere karşılık
testler yeşile alınır (Green);
refactor yapılır
## Hafta 4 Gateway
Kong Gateway kurulumu, route
tanımları, load balancer, API hata
kodlarının testi
Android Java projesi başlatılır;
temel ekranlar, navigation, API
bağlantısı
## Hafta 5
## GUI &
## Test
Performans testleri için API
hazırlığı; Docker Compose final
konfigürasyonu
Custom Android bileşenler, özel
grafikler; k6/JMeter yük + kırılma
testleri
## Hafta 6 Final
docker-compose up son testi, tüm
servislerin entegrasyon testi, code
review
GitHub README Mermaid
diyagramları, test raporları,
dokümantasyon tamamlama

- TDD Akışı — Kim Ne Zaman Yapar?
TDD puanı için en kritik mesaj şudur: testler koddan önce yazılmalı ve bu tarih damgalarıyla
kanıtlanmalıdır. İki üye arasında koordinasyon şöyle işler:

## Adım Üye 2 Yapar Commit Üye 1 Yapar Commit
## 1 – RED
Test dosyasını
oluştur ve başarısız
testi yaz
git commit: 'test:
UserService kayıt
testi (RED)'
## Implementation
yazılmaz — henüz
boş
## —
## 2 – GREEN
Commit tarihini
incele; testin
koddan önce
yazıldığını doğrula
## —
Minimum kodu yaz;
test geçsin
git commit:
## 'feat:
UserService
kayıt impl'
## 3 –
## REFACTOR
Yeni test veya edge
case ekle
git commit: 'test:
edge case eklendi'
Kodu temizle,
SOLID'e uygun hale
getir
git commit:
## 'refactor:
UserService
## SOLID'

Önemli: Üye 1 implementation yazmadan önce Üye 2'nin ilgili testi commit etmiş olması
şarttır. Bu sıra bozulursa TDD puanı riske girer.


## 7. Önerilen Mikroservis Mimarisi
En az 3 ayrı mikroservis önerilir. Örnek bir yapı:

- user-service
◦ Kullanıcı kayıt/giriş işlemleri
◦ JWT veya session tabanlı kimlik doğrulama
◦ Redis ile session yönetimi
- core-service
◦ Uygulamanın ana iş mantığı
◦ JDBC ile ilişkisel DB erişimi
◦ MongoDB ile doküman bazlı veri saklama
- notification-service / report-service
◦ Yan hizmet: e-posta, bildirim veya raporlama
◦ Diğer servislerden JSON mesajla tetiklenir
- api-gateway (Kong)
◦ Tüm istemci trafiği buradan geçer
◦ Rate limiting, logging, authentication proxy

## 8. Kritik İpuçları
## Mermaid Diyagramları
GitHub README'ye en az üç diyagram eklenmelidir: (1) sistem mimarisi C4 veya bileşen diagramı,
(2) veritabanı ER şeması, (3) kritik bir API akış diagramı. Bu üç diyagram 5 puanlık doküman
kriterini neredeyse garantiler.

NoSQL Seçimi
Redis + MongoDB kombinasyonu en güçlü gösterimi sağlar. Redis, hız gerektiren session ve cache
işlemleri için; MongoDB ise doküman bazlı yapılandırılmamış veriler için kullanılmalıdır. İkisini aynı
anda kullanmak mimarî vizyonunuzu kanıtlar.

## Commit Disiplini
GitHub commit geçmişi değerlendirilecektir. Her iki üye haftada en az 3-4 anlamlı commit atmalıdır.
Commit mesajları konvansiyona uygun olmalı: feat:, fix:, test:, refactor:, docs: ön ekleri
kullanılmalıdır.

## Docker Compose Son Kontrol
docker-compose up komutu; gateway, tüm mikroservisler, MySQL/PostgreSQL, MongoDB ve
Redis'i birlikte başlatabilmeli. Sunum öncesi bu komutun temiz çalıştığından emin olun.

TBL324 — Proje Strateji Raporu | Kocaeli Üniversitesi
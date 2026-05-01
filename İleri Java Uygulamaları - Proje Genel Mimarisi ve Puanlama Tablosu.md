



## KOCAELİ ÜNİVERSİTESİ

## TEKNOLOJİ FAKÜLTESİ
## BİLİŞİM SİSTEMLERİ MÜHENDİSLİĞİ


TBL324 - İleri Java Uygulamaları Dersi
## Proje Gereksinimleri Dokümanı
Hazırlayan: Dr. Öğr. Üyesi Samet DİRİ



Proje; dersin öğrenme kazanımları ve içeriğini referans alarak kapsamlı bir Java
uygulaması geliştirilmesini amaçlamaktadır. Öğrenciler, sistem bileşenlerini
kurgulayacak, tüm isterleri karşılayan güvenli ve ölçeklenebilir bir uygulama
tasarlayacaklardır. Proje; Java dili ve kütüphaneleri, nesne yönelimli
programlama, servis orkestrasyonu ve modern test süreçlerini bütünleştirici
biçimde ölçmeyi hedeflemektedir.

TBL324 - İleri Java Uygulamaları Dersi Proje Gereksinimleri
## Dr. Öğr. Üyesi Samet Diri
Proje Genel Mimarisi ve Puanlama Tablosu
Proje, temel kazanımları ölçen zorunlu bir iskelet (65 puan) ve öğrencinin vizyonuna bırakılmış ileri seviye
ek isterlerden (35 puan) oluşmaktadır. Mobil ara yüz dahil tüm bileşenler Java dili ile geliştirilmelidir.
Proje Değerlendirme Kriterleri (Zorunlu 65 Puan + Ek Özellikler 35 Puan = Toplam 100 Puan)
Projenin gerçeklenmesi istenen zorunlu özellikleri ve seçiminize göre ek özellikleri bulunmaktadır. Tüm
kriterler Tablo  1’de  gösterilmiştir. Zorunlu  kısımdaki maddelerin  eksiksiz  tamamlanması,  dersin temel
öğrenme kazanımları bakımından gereklidir. Ek özellikler ise yapılması halinde öğrenmenize ayrıca fayda
sağlayacak  nitelikte  işlevler  barındırmaktadır.  Ek  özelliklerden  bazıları  zorunlu  isterlerin  yerine
geliştirilmelidir. Örneğin mikroservis mimarisi kullanarak projenin geliştirilmesi halinde API’den alınacak
olan 10 puan + mikroservis mimarisininden gelen 10 puan birleştirilerek değerlendirme yapılacaktır. Yine
benzer şekilde Custom GUI yerine mobil GUI geliştirilmesi halinde 10 puan + 5 puan olacak şekilde
değerlendirme  yapılacaktır. Bu  isterlere  ek  olarak  öğrencilerin  önereceği/geliştireceği  ek  özellikler
geliştirilen özelliğin işlevine göre ayrıca puanlanacaktır.
Tablo 1. Proje değerlendirme kriterleri ve açıklamaları

## Kriter Açıklama Puan
## ZORUNLU

API & Back-end
Uygulamanın iş mantığını yürüten, veriye erişim sağlayan arka yüz
servisidir.
## 10
## Generic Yapılar
Tip güvenliğini sağlayan, kod tekrarını önleyen Generic<T>
sınıfların ve koleksiyonların kullanımı.
## 10
Custom GUI
Swing veya JavaFX ile geliştirilmiş, standart bileşenlerin dışında
Custom Graphics içeren arayüz.
## 10
JDBC & NoSQL
Verilerin izole katmanlarda, JDBC ve gerçek bir NoSQL motoru
(Redis, MongoDB vb.) ile saklanması.
## 10
## SOLID & OOP
Kodun nesne yönelimli prensiplere (SOLID, Design Patterns) tam
uyumu.
## 10
## Hata Yönetimi
API hatalarda standart HTTP durum kodlarını (4xx, 5xx)
dönmelidir.
## 5
## Performans Testleri
Projeye ait API'nin kırılma ve yük testlerinin yapılması ve
raporlanması
## 5
## Analiz & Doküman
Projenin tamamının tanıtımı, Jmeter, k6 vb. ile performans testi ve
GitHub üzerinde Mermaid/Markdown ile teknik raporlama.
## 5
## ZORUNLU KISIM TOPLAM 65
## EK ÖZELLİKLER

## Mikroservis Mimarisi
Monolitik API yerine, birbirleriyle JSON üzerinden haberleşen,
tamamen izole dağıtık servis yapısı.
## +10
## Gateway
Tüm trafiği yöneten, istekleri ilgili API/Servis birimlerine
yönlendiren Gateway yapısı. (Kong vb bir platform kullanılabilir)
## +5
Mobil GUI
Masaüstü arayüzüne ek olarak Android (Java) veya JavaFX/Gluon
ile geliştirilmiş
## +5
Test-Driven
## Geliştirme
Projenin Red-Green-Refactor döngüsüyle, test dosyalarının tarih
damgası kontrol edilerek geliştirilmesi.
## +10
## Dockerize Sistem
Tüm mimarinin (Veritabanı, Servisler vb.) docker-compose up
komutuyla çalıştırılabilir olması.
## +5
## EK ÖZELLİKLER TOPLAM 35
## GENEL TOPLAM
## 100



TBL324 - İleri Java Uygulamaları Dersi Proje Gereksinimleri
## Dr. Öğr. Üyesi Samet Diri
## Önemli Notlar & Hatırlatmalar
*NOT-1: Kopya çektiği/intihal yaptığı tespit edilen projeler 0 (sıfır) olarak notlandırılacaktır.
*NOT-2:  GitHub’a  düzenli  ve  ekip  üyesi  sayısına  göre  orantılı  commit  yapmayanlar 0  (sıfır) olarak
notlandırılacaktır.
*NOT-3: Java dili dışında bir dil ile geliştirme yapanları ile OOP vb. prensiplere uygun geliştirme yapmayan
öğrenciler/projeler 0 (sıfır) olarak notlandırılacaktır.
*NOT-4: Belirtilen tarihe kadar; grup oluşturmayan, ekip arkadaşı olmasa dahi listeye ismini eklemeyen ve
proje dosyalarının gönderimini sağlamayan öğrenciler/projeler 0 (sıfır) olarak notlandırılacaktır.
*NOT-5: Her grup yalnızca tek ve ortak bir proje geliştirip sunacaktır. Ayrı ayrı sunum alınmayacak olup ayrı
ayrı sunum talep edilmesi halinde ekibin tamamı 0 (sıfır) olarak notlandırılacaktır.
*NOT-6: Başarılar dilerim.

## Dr. Öğr. Üyesi Samet Diri
## Bilişim Sistemleri Mühendisliği
## Teknoloji Fakültesi
## Kocaeli Üniversitesi
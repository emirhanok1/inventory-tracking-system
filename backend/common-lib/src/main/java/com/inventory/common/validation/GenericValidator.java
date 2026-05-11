package com.inventory.common.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║         Generic<T> Yapı: GenericValidator                           ║
 * ║                                                                      ║
 * ║  TDD GREEN aşaması — GenericValidatorTest testlerini geçecek         ║
 * ║  minimum ve temiz implementasyon.                                    ║
 * ║                                                                      ║
 * ║  COMMIT NOTU (Üye 2):                                                ║
 * ║    feat: GenericValidator implementation (GREEN phase)              ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * <p>Tüm servislerin kullanabileceği, tip-bağımsız doğrulama araç sınıfı.
 *
 * <p>Özellikler:
 * <ul>
 *   <li>Statik yardımcı metodlar — instance gerekmez</li>
 *   <li>Builder tabanlı {@link ValidationResult} ile toplu doğrulama</li>
 *   <li>Özel {@link ValidationException} ile hata yönetimi</li>
 *   <li>SOLID — her metot tek bir kuralı doğrular (SRP)</li>
 * </ul>
 *
 * <p>Kullanım örneği:
 * <pre>{@code
 * // Tekil doğrulama
 * GenericValidator.requireNonBlank(name, "productName");
 *
 * // Toplu doğrulama (fail-all, fail-fast değil)
 * ValidationResult result = GenericValidator.of()
 *     .requireNonBlank(name,  "name")
 *     .requirePositive(price, "price")
 *     .build();
 *
 * if (!result.isValid()) {
 *     return GenericResponse.error("Doğrulama hatası", result.getErrors());
 * }
 * }</pre>
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @version 1.0.0
 * @since 2026-05-12
 */
public final class GenericValidator {

    // =========================================================================
    // SABITLER
    // =========================================================================

    /**
     * RFC-5322 tabanlı basit e-posta regex.
     * Aşırı kısıtlayıcı değil; gerçek dünya e-postalarını yakalar.
     */
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    // =========================================================================
    // CONSTRUCTOR — Utility class olduğu için instantiation yasak
    // =========================================================================

    private GenericValidator() {
        throw new UnsupportedOperationException("GenericValidator utility sınıfıdır — instantiate edilemez.");
    }

    // =========================================================================
    // STATİK DOĞRULAMA METODLARI
    // =========================================================================

    /**
     * Verilen string null, boş veya yalnızca boşluk karakteri içeriyorsa true döner.
     *
     * @param value Kontrol edilecek string (null olabilir)
     * @return null, boş veya yalnızca whitespace ise true
     */
    public static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Verilen string null veya boş değilse doğrulama geçer ve değeri döndürür.
     * Fluent API zincirinde kullanım için değer döndürülür.
     *
     * @param value     Doğrulanacak string
     * @param fieldName Hata mesajında kullanılacak alan adı
     * @return Doğrulanmış value (değiştirilmeden)
     * @throws ValidationException value null veya boş ise
     */
    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(
                "'" + fieldName + "' alanı boş olamaz.");
        }
        return value;
    }

    /**
     * Sayısal değerin [min, max] aralığında olup olmadığını kontrol eder.
     * Sınır değerler dahildir (inclusive).
     *
     * @param value Kontrol edilecek değer
     * @param min   Minimum (dahil)
     * @param max   Maksimum (dahil)
     * @return [min, max] aralığında ise true
     * @throws IllegalArgumentException min > max ise (geçersiz aralık tanımı)
     */
    public static boolean isInRange(int value, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(
                "Geçersiz aralık: min (" + min + ") > max (" + max + ")");
        }
        return value >= min && value <= max;
    }

    /**
     * Değerin pozitif (> 0) olmasını zorunlu kılar.
     * Stok miktarı, fiyat gibi alanlar için kullanılır.
     *
     * @param value     Doğrulanacak sayısal değer
     * @param fieldName Hata mesajında kullanılacak alan adı
     * @throws ValidationException value ≤ 0 ise
     */
    public static void requirePositive(double value, String fieldName) {
        if (value <= 0.0) {
            throw new ValidationException(
                "'" + fieldName + "' alanı pozitif bir değer olmalıdır, verildi: " + value);
        }
    }

    /**
     * E-posta formatını basit regex ile doğrular.
     *
     * @param email Doğrulanacak e-posta adresi
     * @return Geçerli format ise true, aksi halde false
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // =========================================================================
    // BUILDER — Toplu Doğrulama (Fail-All, fail-fast değil)
    // =========================================================================

    /**
     * Toplu doğrulama builder'ını başlatır.
     *
     * @return Yeni bir {@link ValidationBuilder} instance'ı
     */
    public static ValidationBuilder of() {
        return new ValidationBuilder();
    }

    /**
     * Birden fazla alanı tek seferde doğrulayan builder.
     * Fail-fast DEĞİL: tüm hatalar toplanır ve birlikte raporlanır.
     *
     * <p>Bu yaklaşım kullanıcı deneyimini iyileştirir: tek gönderimde
     * tüm hatalar gösterilir, form tekrar tekrar gönderilmek zorunda kalmaz.
     */
    public static final class ValidationBuilder {

        private final List<String> errors = new ArrayList<>();

        private ValidationBuilder() { }

        /**
         * String alanın boş olmadığını kontrol eder.
         * Hata varsa listeye eklenir, exception fırlatılmaz.
         *
         * @param value     Kontrol edilecek değer
         * @param fieldName Alan adı
         * @return this (fluent chaining)
         */
        public ValidationBuilder requireNonBlank(String value, String fieldName) {
            if (value == null || value.isBlank()) {
                errors.add("'" + fieldName + "' alanı boş olamaz.");
            }
            return this;
        }

        /**
         * Sayısal değerin pozitif olduğunu kontrol eder.
         * Hata varsa listeye eklenir, exception fırlatılmaz.
         *
         * @param value     Kontrol edilecek sayı
         * @param fieldName Alan adı
         * @return this (fluent chaining)
         */
        public ValidationBuilder requirePositive(double value, String fieldName) {
            if (value <= 0.0) {
                errors.add("'" + fieldName + "' alanı pozitif olmalıdır, verildi: " + value);
            }
            return this;
        }

        /**
         * Doğrulama işlemini tamamlar ve sonucu döner.
         *
         * @return Toplanan hatalarla birlikte {@link ValidationResult}
         */
        public ValidationResult build() {
            return new ValidationResult(new ArrayList<>(errors));
        }
    }

    // =========================================================================
    // DOĞRULAMA SONUCU — Immutable
    // =========================================================================

    /**
     * Toplu doğrulamanın sonucunu taşıyan immutable değer nesnesi.
     * Doğrudan {@link com.inventory.common.dto.GenericResponse#error} metoduna beslenebilir.
     */
    public static final class ValidationResult {

        private final List<String> errors;

        private ValidationResult(List<String> errors) {
            this.errors = errors != null ? List.copyOf(errors) : List.of();
        }

        /**
         * Tüm alanlar geçerliyse true döner.
         *
         * @return Hata listesi boşsa true
         */
        public boolean isValid() {
            return errors.isEmpty();
        }

        /**
         * Toplanan hata mesajlarını döner.
         * Boş liste döner, asla null döndürmez.
         *
         * @return Hata mesajları (immutable)
         */
        public List<String> getErrors() {
            return errors;
        }
    }

    // =========================================================================
    // ÖZEL İSTİSNA
    // =========================================================================

    /**
     * Tek-alan doğrulaması başarısız olduğunda fırlatılır.
     * {@link RuntimeException} olduğu için checked exception yükü yoktur.
     */
    public static class ValidationException extends RuntimeException {

        /**
         * @param message Doğrulama hatası açıklaması
         */
        public ValidationException(String message) {
            super(message);
        }
    }
}

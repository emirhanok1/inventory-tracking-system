package com.inventory.common.validation;

import com.inventory.common.dto.GenericResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║         TDD — RED PHASE : GenericValidatorTest                      ║
 * ║                                                                      ║
 * ║  Bu dosya TDD döngüsünün ilk adımıdır (RED).                        ║
 * ║  GenericValidator sınıfı henüz YAZILMAMIŞTIR.                       ║
 * ║  Derleme/çalışma hatası BEKLENEN davranıştır.                       ║
 * ║                                                                      ║
 * ║  COMMIT NOTU (Üye 2):                                                ║
 * ║    test: GenericValidator RED phase - tests before implementation   ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * <p>Test edilen sınıf: {@link GenericValidator}
 *
 * <p>GenericValidator, backend'den gelen ya da Android'den gönderilecek
 * verileri doğrulayan, tüm servisler tarafından ortak kullanılan bir
 * utility sınıfıdır. Hem backend hem de Android tarafında aynı sınıf
 * kullanılarak doğrulama kuralları merkezi bir noktada tutulur.
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @version 1.0.0
 * @since 2026-05-12  ← Implementation tarihinden ÖNCE
 */
@DisplayName("GenericValidator — Unit Tests (TDD RED Phase)")
class GenericValidatorTest {

    // =========================================================================
    // NESTED CLASS 1: Boş / Null String Doğrulama
    // =========================================================================

    /**
     * String alanların null ve boşluk kontrollerini doğrular.
     * Tüm form alanları ve API request body alanları için kullanılır.
     */
    @Nested
    @DisplayName("1 — String Validation Tests")
    class StringValidationTests {

        /**
         * [RED-V01] Null veya boş string → isNullOrBlank() true dönmeli.
         *
         * @NullAndEmptySource: null + "" (boş string) otomatik test eder.
         */
        @ParameterizedTest(name = "[RED-V01] ''{0}'' → isNullOrBlank=true")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n", "  \t  "})
        @DisplayName("[RED-V01] Null, boş ve sadece boşluk stringler → isNullOrBlank=true")
        void isNullOrBlank_nullEmptyWhitespace_shouldReturnTrue(String input) {
            // Sınıf henüz yok — RED aşaması
            assertTrue(GenericValidator.isNullOrBlank(input),
                "'" + input + "' isNullOrBlank=true olmalı");
        }

        /**
         * [RED-V02] Geçerli (dolu) string → isNullOrBlank() false dönmeli.
         */
        @ParameterizedTest(name = "[RED-V02] ''{0}'' → isNullOrBlank=false")
        @ValueSource(strings = {"a", "hello", "123", " text ", "TBL324"})
        @DisplayName("[RED-V02] Dolu stringler → isNullOrBlank=false")
        void isNullOrBlank_nonEmptyStrings_shouldReturnFalse(String input) {
            assertFalse(GenericValidator.isNullOrBlank(input),
                "'" + input + "' dolu olduğu için isNullOrBlank=false olmalı");
        }

        /**
         * [RED-V03] requireNonBlank() — geçersiz input'ta ValidationException fırlatmalı.
         * Fluent API doğrulama zinciri için kullanılır.
         */
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("[RED-V03] requireNonBlank(null/boş) → ValidationException fırlatmalı")
        void requireNonBlank_invalidInput_shouldThrowValidationException(String input) {
            assertThrows(GenericValidator.ValidationException.class,
                () -> GenericValidator.requireNonBlank(input, "testField"),
                "Boş alan için ValidationException fırlatılmalı");
        }

        /**
         * [RED-V04] requireNonBlank() — geçerli input'ta exception fırlatmamalı
         * ve girdiyi değiştirmeden döndürmeli.
         */
        @Test
        @DisplayName("[RED-V04] requireNonBlank('valid') → değeri döndürmeli, exception yok")
        void requireNonBlank_validInput_shouldReturnValue() {
            String result = assertDoesNotThrow(
                () -> GenericValidator.requireNonBlank("geçerli-değer", "testField")
            );
            assertEquals("geçerli-değer", result, "Değer değiştirilmeden döndürülmeli");
        }

        /**
         * [RED-V05] ValidationException mesajı, alan adını içermeli.
         * Hata mesajı kullanıcıya hangi alanın hatalı olduğunu söylemeli.
         */
        @Test
        @DisplayName("[RED-V05] ValidationException mesajı alan adını içermeli")
        void validationException_message_shouldContainFieldName() {
            GenericValidator.ValidationException ex = assertThrows(
                GenericValidator.ValidationException.class,
                () -> GenericValidator.requireNonBlank("", "productName")
            );

            assertTrue(ex.getMessage().contains("productName"),
                "Hata mesajı alan adı 'productName' içermeli");
        }
    }

    // =========================================================================
    // NESTED CLASS 2: Sayısal Aralık Doğrulama
    // =========================================================================

    /**
     * Sayısal değerlerin min/max aralığını doğrular.
     * Stok miktarı, fiyat, sayfa numarası gibi sayısal alanlar için.
     */
    @Nested
    @DisplayName("2 — Numeric Range Validation Tests")
    class NumericValidationTests {

        /**
         * [RED-V06] Geçerli aralıktaki değer → isInRange() true.
         */
        @Test
        @DisplayName("[RED-V06] 5 ∈ [1, 10] → isInRange=true")
        void isInRange_valueInsideRange_shouldReturnTrue() {
            assertTrue(GenericValidator.isInRange(5, 1, 10),
                "5, [1,10] aralığında olduğu için true dönmeli");
        }

        /**
         * [RED-V07] Sınır değerleri (boundary) → isInRange() true (inclusive).
         */
        @Test
        @DisplayName("[RED-V07] Sınır değerleri 1 ve 10 ∈ [1, 10] → inclusive true")
        void isInRange_boundaryValues_shouldReturnTrueInclusive() {
            assertTrue(GenericValidator.isInRange(1, 1, 10),  "Min sınır dahil olmalı");
            assertTrue(GenericValidator.isInRange(10, 1, 10), "Max sınır dahil olmalı");
        }

        /**
         * [RED-V08] Aralık dışı değerler → isInRange() false.
         */
        @ParameterizedTest(name = "[RED-V08] {0} ∉ [1, 10] → isInRange=false")
        @ValueSource(ints = {0, -1, 11, 100, Integer.MIN_VALUE, Integer.MAX_VALUE})
        @DisplayName("[RED-V08] Aralık dışı değerler → isInRange=false")
        void isInRange_outOfRange_shouldReturnFalse(int value) {
            assertFalse(GenericValidator.isInRange(value, 1, 10),
                value + " [1,10] dışında olduğu için false dönmeli");
        }

        /**
         * [RED-V09] min > max → IllegalArgumentException (tanımsız aralık).
         */
        @Test
        @DisplayName("[RED-V09] min > max → IllegalArgumentException (geçersiz aralık)")
        void isInRange_minGreaterThanMax_shouldThrowIllegalArgument() {
            assertThrows(IllegalArgumentException.class,
                () -> GenericValidator.isInRange(5, 10, 1),
                "min > max geçersiz aralık tanımı, exception fırlatmalı");
        }

        /**
         * [RED-V10] requirePositive() — 0 ve negatif sayılarda ValidationException.
         * Stok miktarı / fiyat her zaman pozitif olmalı.
         */
        @ParameterizedTest(name = "[RED-V10] requirePositive({0}) → exception")
        @ValueSource(doubles = {0.0, -0.1, -100.0, Double.MIN_VALUE * -1})
        @DisplayName("[RED-V10] requirePositive(≤0) → ValidationException fırlatmalı")
        void requirePositive_nonPositiveValues_shouldThrow(double value) {
            assertThrows(GenericValidator.ValidationException.class,
                () -> GenericValidator.requirePositive(value, "stockAmount"),
                value + " için ValidationException bekleniyor");
        }

        /**
         * [RED-V11] requirePositive() — pozitif değerde exception fırlatmamalı.
         */
        @Test
        @DisplayName("[RED-V11] requirePositive(0.01) → exception fırlatmamalı")
        void requirePositive_positiveValue_shouldNotThrow() {
            assertDoesNotThrow(() -> GenericValidator.requirePositive(0.01, "price"));
            assertDoesNotThrow(() -> GenericValidator.requirePositive(999.99, "price"));
        }
    }

    // =========================================================================
    // NESTED CLASS 3: E-posta Doğrulama
    // =========================================================================

    /**
     * Kullanıcı kaydı ve bildirim servisi için e-posta format doğrulaması.
     */
    @Nested
    @DisplayName("3 — Email Validation Tests")
    class EmailValidationTests {

        /**
         * [RED-V12] Geçerli e-posta formatları → isValidEmail() true.
         */
        @ParameterizedTest(name = "[RED-V12] ''{0}'' → geçerli e-posta")
        @ValueSource(strings = {
            "user@example.com",
            "user.name+tag@domain.co.uk",
            "inventory@kocaeli.edu.tr",
            "test123@test-domain.org"
        })
        @DisplayName("[RED-V12] Geçerli e-posta formatları → isValidEmail=true")
        void isValidEmail_validFormats_shouldReturnTrue(String email) {
            assertTrue(GenericValidator.isValidEmail(email),
                "'" + email + "' geçerli e-posta formatı");
        }

        /**
         * [RED-V13] Geçersiz e-posta formatları → isValidEmail() false.
         */
        @ParameterizedTest(name = "[RED-V13] ''{0}'' → geçersiz e-posta")
        @ValueSource(strings = {
            "plaintext",
            "@nodomain.com",
            "missing-at-sign",
            "double@@at.com",
            "spaces in@email.com",
            ""
        })
        @NullAndEmptySource
        @DisplayName("[RED-V13] Geçersiz e-posta formatları → isValidEmail=false")
        void isValidEmail_invalidFormats_shouldReturnFalse(String email) {
            assertFalse(GenericValidator.isValidEmail(email),
                "'" + email + "' geçersiz e-posta olmalı");
        }
    }

    // =========================================================================
    // NESTED CLASS 4: ValidationResult Toplu Doğrulama
    // =========================================================================

    /**
     * Birden fazla alanı tek seferde doğrulamayı destekleyen
     * ValidationResult builder API'sini test eder.
     *
     * Kullanım örneği:
     * <pre>{@code
     * GenericValidator.of()
     *     .requireNonBlank(name, "name")
     *     .requirePositive(price, "price")
     *     .validate();  // Tüm hatalar bir arada fırlatılır
     * }</pre>
     */
    @Nested
    @DisplayName("4 — ValidationResult Builder (Toplu Doğrulama) Tests")
    class ValidationResultTests {

        /**
         * [RED-V14] Tüm alanlar geçerliyse isValid()=true, getErrors() boş.
         */
        @Test
        @DisplayName("[RED-V14] Geçerli alanlarla build → isValid=true, errors boş")
        void validationResult_allValid_shouldBeValid() {
            GenericValidator.ValidationResult result = GenericValidator.of()
                .requireNonBlank("Laptop", "productName")
                .requirePositive(1499.99, "price")
                .build();

            assertTrue(result.isValid(),         "Tüm alanlar geçerli → isValid=true");
            assertTrue(result.getErrors().isEmpty(), "Hata listesi boş olmalı");
        }

        /**
         * [RED-V15] Birden fazla geçersiz alan → tüm hatalar toplanmalı (fail-fast değil).
         */
        @Test
        @DisplayName("[RED-V15] 2 geçersiz alan → 2 hata mesajı toplanmalı")
        void validationResult_multipleErrors_shouldCollectAll() {
            GenericValidator.ValidationResult result = GenericValidator.of()
                .requireNonBlank("", "productName")     // geçersiz
                .requirePositive(-5.0, "price")          // geçersiz
                .requireNonBlank("W-100", "warehouseId") // geçerli
                .build();

            assertFalse(result.isValid(),
                "Geçersiz alanlar var → isValid=false");
            assertEquals(2, result.getErrors().size(),
                "2 geçersiz alan → 2 hata mesajı toplanmalı");
        }

        /**
         * [RED-V16] ValidationResult.getErrors() → GenericResponse.error()'a doğrudan verilebilmeli.
         * Tüm generic yapıların entegrasyonunu kanıtlar.
         */
        @Test
        @DisplayName("[RED-V16] ValidationResult.getErrors() → GenericResponse.error() entegrasyonu")
        void validationResult_errorsCanFeedGenericResponseError() {
            GenericValidator.ValidationResult result = GenericValidator.of()
                .requireNonBlank("", "name")
                .build();

            // ValidationResult → GenericResponse entegrasyonu
            GenericResponse<Object> response =
                GenericResponse.error("Doğrulama başarısız", result.getErrors());

            assertEquals("ERROR", response.getStatus());
            assertFalse(response.getErrors().isEmpty());
            assertTrue(response.hasErrors());
        }
    }
}

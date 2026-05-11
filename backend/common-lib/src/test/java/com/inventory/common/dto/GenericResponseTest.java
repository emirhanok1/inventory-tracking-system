package com.inventory.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║         TDD — RED PHASE : GenericResponseTest                       ║
 * ║                                                                      ║
 * ║  Bu dosya TDD döngüsünün ilk adımıdır (RED).                        ║
 * ║  GenericResponse<T> sınıfı henüz YAZILMAMIŞTIR.                     ║
 * ║  Testlerin derleme/çalışma hatası vermesi BEKLENEN ve ISTENEN        ║
 * ║  davranıştır — bu, TDD'nin "Red" aşamasının kanıtıdır.              ║
 * ║                                                                      ║
 * ║  COMMIT NOTU (Üye 2):                                                ║
 * ║    test: GenericResponse RED phase - tests before implementation     ║
 * ║                                                                      ║
 * ║  Üye 1, implementation'ı YALNIZCA bu commit'ten SONRA yazacaktır.   ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * <p>Test edilen sınıf: {@link GenericResponse}
 * <p>Kapsam hedefi: %80+ branch & line coverage
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @version 1.0.0
 * @since 2026-05-12  ← Bu tarih implementation tarihinden ÖNCE olmalıdır!
 */
@DisplayName("GenericResponse<T> — Unit Tests (TDD RED Phase)")
class GenericResponseTest {

    // =========================================================================
    // TEST SABITLERI (Test constants — sınıf genelinde tekrar kullanılır)
    // =========================================================================

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_ERROR   = "ERROR";
    private static final String MSG_OK         = "İşlem başarılı";
    private static final String MSG_NOT_FOUND  = "Kayıt bulunamadı";

    // =========================================================================
    // NESTED CLASS 1: Builder / Factory Method Testleri
    // =========================================================================

    /**
     * GenericResponse'un statik factory metodlarının doğru nesne
     * üretip üretmediğini doğrular.
     */
    @Nested
    @DisplayName("1 — Factory / Builder Tests")
    class FactoryTests {

        /**
         * [RED-01] success() factory metodu çağrıldığında:
         *  - status alanı "SUCCESS" olmalı
         *  - data alanı verilen nesne olmalı
         *  - errors listesi boş olmalı
         *  - timestamp null OLMAMALI (otomatik set edilmeli)
         *
         * BEKLENEN DURUM (RED): GenericResponse sınıfı henüz yok →
         * derleme hatası → test zaten başarısız.
         */
        @Test
        @DisplayName("[RED-01] success() → status=SUCCESS, data dolu, errors boş, timestamp set")
        void success_factory_shouldBuildCorrectResponse() {
            // ARRANGE
            String payload = "test-payload";

            // ACT
            // Bu satır derleme hatası verir — sınıf henüz yok (RED aşaması)
            GenericResponse<String> response = GenericResponse.success(MSG_OK, payload);

            // ASSERT
            assertAll("success() factory doğrulama",
                () -> assertEquals(STATUS_SUCCESS, response.getStatus(),
                    "Status 'SUCCESS' olmalıdır"),
                () -> assertEquals(MSG_OK, response.getMessage(),
                    "Message doğru set edilmelidir"),
                () -> assertEquals(payload, response.getData(),
                    "Data field verilen payload olmalıdır"),
                () -> assertNotNull(response.getTimestamp(),
                    "Timestamp otomatik set edilmeli, null olmamalıdır"),
                () -> assertTrue(response.getErrors().isEmpty(),
                    "Başarılı yanıtta errors listesi boş olmalıdır")
            );
        }

        /**
         * [RED-02] error() factory metodu çağrıldığında:
         *  - status alanı "ERROR" olmalı
         *  - data alanı null olmalı
         *  - errors listesi verilen hataları içermeli
         */
        @Test
        @DisplayName("[RED-02] error() → status=ERROR, data=null, errors dolu")
        void error_factory_shouldBuildCorrectResponse() {
            // ARRANGE
            List<String> errors = List.of("Alan boş olamaz", "Geçersiz e-posta");

            // ACT
            GenericResponse<Object> response = GenericResponse.error(MSG_NOT_FOUND, errors);

            // ASSERT
            assertAll("error() factory doğrulama",
                () -> assertEquals(STATUS_ERROR, response.getStatus(),
                    "Status 'ERROR' olmalıdır"),
                () -> assertEquals(MSG_NOT_FOUND, response.getMessage()),
                () -> assertNull(response.getData(),
                    "Hata yanıtında data null olmalıdır"),
                () -> assertFalse(response.getErrors().isEmpty(),
                    "Errors listesi dolu olmalıdır"),
                () -> assertEquals(2, response.getErrors().size(),
                    "Verilen 2 hata mesajı korunmalıdır"),
                () -> assertNotNull(response.getTimestamp(),
                    "Hata yanıtında da timestamp set edilmelidir")
            );
        }

        /**
         * [RED-03] Parametreli test — Farklı data tipleriyle generic yapı doğrulanır.
         * Integer, Boolean ve bir POJO ile çalışabilmeli.
         */
        @Test
        @DisplayName("[RED-03] Generic T — Integer ve Boolean tipleriyle doğru çalışmalı")
        void success_factory_withDifferentGenericTypes() {
            // Integer
            GenericResponse<Integer> intResponse = GenericResponse.success("Sayı döndü", 42);
            assertEquals(42, intResponse.getData());

            // Boolean
            GenericResponse<Boolean> boolResponse = GenericResponse.success("Boolean döndü", true);
            assertTrue(boolResponse.getData());

            // List<String>
            GenericResponse<List<String>> listResponse =
                GenericResponse.success("Liste döndü", List.of("a", "b", "c"));
            assertEquals(3, listResponse.getData().size());
        }
    }

    // =========================================================================
    // NESTED CLASS 2: Getter / Setter (Encapsulation) Testleri
    // =========================================================================

    /**
     * POJO alanlarının get/set döngüsünü doğrular.
     * Hem fluent builder hem de setter tabanlı yaklaşımı test eder.
     */
    @Nested
    @DisplayName("2 — Field Getter/Setter Tests")
    class FieldTests {

        /**
         * [RED-04] Tüm alanlar manuel olarak set edilip get edilebilmeli.
         */
        @Test
        @DisplayName("[RED-04] Tüm alanlar set/get döngüsünü geçmeli")
        void allFields_getterSetterRoundTrip() {
            // ARRANGE & ACT
            GenericResponse<String> response = new GenericResponse<>();
            response.setStatus(STATUS_SUCCESS);
            response.setMessage(MSG_OK);
            response.setData("test-data");
            response.setErrors(List.of("hata-1"));

            // ASSERT
            assertAll("Getter/Setter round-trip",
                () -> assertEquals(STATUS_SUCCESS, response.getStatus()),
                () -> assertEquals(MSG_OK,          response.getMessage()),
                () -> assertEquals("test-data",     response.getData()),
                () -> assertEquals(1,                response.getErrors().size()),
                () -> assertEquals("hata-1",        response.getErrors().get(0))
            );
        }

        /**
         * [RED-05] Varsayılan (no-arg) constructor sonrası errors alanı
         * null DEĞİL, boş bir liste olarak initialize edilmeli.
         * Bu kural NullPointerException'ları önler (Defensive Programming).
         */
        @Test
        @DisplayName("[RED-05] No-arg constructor → errors alanı boş liste olmalı, null değil")
        void noArgConstructor_errorsShouldBeEmptyList_notNull() {
            GenericResponse<String> response = new GenericResponse<>();

            // errors null olursa getErrors().isEmpty() NPE fırlatır
            assertNotNull(response.getErrors(),
                "errors field hiçbir zaman null olmamalı — NPE riski!");
            assertTrue(response.getErrors().isEmpty(),
                "Başlangıçta errors listesi boş olmalıdır");
        }

        /**
         * [RED-06] timestamp alanı varsayılan constructor'da null olmalı;
         * yalnızca factory metodları timestamp'i otomatik atar.
         */
        @Test
        @DisplayName("[RED-06] No-arg constructor → timestamp başlangıçta null olabilir")
        void noArgConstructor_timestampShouldBeNull() {
            GenericResponse<Object> response = new GenericResponse<>();
            // Factory metodu kullanılmadıysa timestamp null — bu beklenen davranış
            assertNull(response.getTimestamp(),
                "Timestamp yalnızca factory metodlarında otomatik set edilmeli");
        }
    }

    // =========================================================================
    // NESTED CLASS 3: İş Kuralı (Business Rule) Testleri
    // =========================================================================

    /**
     * GenericResponse'un taşıdığı iş mantığı kurallarını doğrular.
     */
    @Nested
    @DisplayName("3 — Business Rule Tests")
    class BusinessRuleTests {

        /**
         * [RED-07] isSuccess() yardımcı metodu —
         * status "SUCCESS" ise true, diğer durumlarda false dönmeli.
         */
        @Test
        @DisplayName("[RED-07] isSuccess() → SUCCESS için true, ERROR için false")
        void isSuccess_shouldReturnCorrectBoolean() {
            GenericResponse<String> successResp = GenericResponse.success(MSG_OK, "data");
            GenericResponse<Object> errorResp   = GenericResponse.error(MSG_NOT_FOUND, List.of());

            assertTrue(successResp.isSuccess(),  "SUCCESS yanıtı için isSuccess() true dönmeli");
            assertFalse(errorResp.isSuccess(),   "ERROR yanıtı için isSuccess() false dönmeli");
        }

        /**
         * [RED-08] hasErrors() yardımcı metodu —
         * errors listesi dolu ise true dönmeli.
         */
        @Test
        @DisplayName("[RED-08] hasErrors() → errors dolu ise true, boş ise false")
        void hasErrors_shouldReflectErrorsListState() {
            GenericResponse<Object> withErrors =
                GenericResponse.error("Hata", List.of("E1", "E2"));

            GenericResponse<String> withoutErrors =
                GenericResponse.success(MSG_OK, "veri");

            assertTrue(withErrors.hasErrors(),    "Dolu errors için hasErrors() true dönmeli");
            assertFalse(withoutErrors.hasErrors(), "Boş errors için hasErrors() false dönmeli");
        }

        /**
         * [RED-09] Sınır testi: null data ile success() çağrısı hata fırlatmamalı.
         * API, "veri yok ama işlem başarılı" senaryosunu desteklemeli.
         */
        @Test
        @DisplayName("[RED-09] success() null data → istisna fırlamamalı, status SUCCESS olmalı")
        void success_withNullData_shouldNotThrow() {
            assertDoesNotThrow(() -> {
                GenericResponse<String> response = GenericResponse.success(MSG_OK, null);
                assertEquals(STATUS_SUCCESS, response.getStatus());
                assertNull(response.getData());
            }, "null data ile success() çağrısı exception fırlatmamalıdır");
        }

        /**
         * [RED-10] Parametreli test: Birden fazla HTTP-benzeri durum kodu
         * ile status alanı doğru set edilmeli.
         */
        @ParameterizedTest(name = "[RED-10] setStatus(''{0}'') → getStatus() aynısını döndürmeli")
        @ValueSource(strings = {"SUCCESS", "ERROR", "PENDING", "PARTIAL"})
        @DisplayName("[RED-10] Farklı status değerleri doğru saklanmalı")
        void setStatus_shouldPersistArbitraryStatusStrings(String statusValue) {
            GenericResponse<Object> response = new GenericResponse<>();
            response.setStatus(statusValue);
            assertEquals(statusValue, response.getStatus(),
                "Status değeri değiştirilmeden saklanmalıdır");
        }
    }

    // =========================================================================
    // NESTED CLASS 4: equals() / hashCode() / toString() Testleri
    // =========================================================================

    /**
     * equals/hashCode sözleşmesini ve toString çıktısının null güvenliğini
     * doğrular. Koleksiyon (Set/Map) kullanımı için kritiktir.
     */
    @Nested
    @DisplayName("4 — equals / hashCode / toString Tests")
    class EqualityTests {

        /**
         * [RED-11] Aynı değerlerle oluşturulan iki nesne equals() açısından
         * eşit olmalı ve aynı hashCode üretmeli.
         */
        @Test
        @DisplayName("[RED-11] Aynı veriyle oluşturulan iki nesne equals & hashCode sözleşmesini geçmeli")
        void equals_andHashCode_sameData_shouldBeEqual() {
            GenericResponse<String> r1 = GenericResponse.success(MSG_OK, "payload");
            GenericResponse<String> r2 = GenericResponse.success(MSG_OK, "payload");

            // equals sözleşmesi: reflexive, symmetric, transitive
            assertEquals(r1, r2,              "Aynı veriyle iki nesne eşit olmalı");
            assertEquals(r1.hashCode(), r2.hashCode(),
                "Eşit nesnelerin hashCode değerleri aynı olmalı");
        }

        /**
         * [RED-12] toString() null dönmemeli ve temel alanları içermeli.
         * Logging için kritiktir.
         */
        @Test
        @DisplayName("[RED-12] toString() null dönmemeli ve status/message içermeli")
        void toString_shouldNotBeNullAndContainKeyFields() {
            GenericResponse<String> response = GenericResponse.success(MSG_OK, "veri");
            String str = response.toString();

            assertNotNull(str, "toString() null dönmemelidir");
            assertTrue(str.contains("SUCCESS") || str.contains("status"),
                "toString() çıktısı en azından status bilgisi içermelidir");
        }
    }
}

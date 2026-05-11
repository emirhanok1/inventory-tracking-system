package com.inventory.common.exception;

import com.inventory.common.dto.GenericResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║         TDD — RED PHASE : GlobalExceptionHandlerTest                ║
 * ║                                                                      ║
 * ║  Bu dosya TDD döngüsünün ilk adımıdır (RED).                        ║
 * ║  GlobalExceptionHandler sınıfı henüz YAZILMAMIŞTIR.                 ║
 * ║  Derleme/çalışma hatası BEKLENEN davranıştır.                       ║
 * ║                                                                      ║
 * ║  COMMIT NOTU (Üye 2):                                                ║
 * ║    test: GlobalExceptionHandler RED phase - tests before impl (TDD) ║
 * ║                                                                      ║
 * ║  Strateji Raporu Karşılığı:                                          ║
 * ║    "GlobalExceptionHandler ile standart 4xx/5xx HTTP kodları        ║
 * ║     dönmeli" — Hata Yönetimi (5 pt)                                 ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * <p><b>Test Stratejisi:</b> Spring konteksti başlatılmaz (hızlı).
 * GlobalExceptionHandler bir POJO gibi instantiate edilip handler
 * metodları doğrudan çağrılır. ResponseEntity dönüş değerleri
 * ve içindeki GenericResponse body'si assert edilir.</p>
 *
 * <p><b>Kapsanan HTTP durum kodları:</b></p>
 * <ul>
 *   <li>400 Bad Request — doğrulama hataları ({@code ResourceNotFoundException})</li>
 *   <li>401 Unauthorized — kimlik doğrulama başarısız</li>
 *   <li>404 Not Found — kayıt bulunamadı</li>
 *   <li>409 Conflict — kayıt zaten mevcut</li>
 *   <li>500 Internal Server Error — beklenmeyen hatalar</li>
 * </ul>
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @version 1.0.0
 * @since 2026-05-12  ← Implementation tarihinden ÖNCE olmalıdır!
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler — Unit Tests (TDD RED Phase)")
class GlobalExceptionHandlerTest {

    /**
     * Test edilen nesne. Spring bağlamı olmadan düz POJO gibi kullanılır.
     * Bu yaklaşım: hızlı, bağımsız, gerçek unit test.
     */
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        // Her test öncesi temiz instance — stateful side-effect yok
        handler = new GlobalExceptionHandler();
    }

    // =========================================================================
    // NESTED CLASS 1: 404 Not Found
    // =========================================================================

    /**
     * Kayıt bulunamadığında standart 404 ve anlamlı mesaj dönmeli.
     */
    @Nested
    @DisplayName("1 — 404 Not Found (ResourceNotFoundException)")
    class NotFoundTests {

        /**
         * [RED-E01] ResourceNotFoundException → 404 Not Found.
         */
        @Test
        @DisplayName("[RED-E01] ResourceNotFoundException → HTTP 404, status=ERROR")
        void handleResourceNotFound_shouldReturn404() {
            // ARRANGE — Sınıf henüz yok (RED aşaması)
            ResourceNotFoundException ex =
                new ResourceNotFoundException("Product", "id", 42L);

            // ACT
            ResponseEntity<GenericResponse<?>> response =
                handler.handleResourceNotFoundException(ex);

            // ASSERT
            assertAll("404 Not Found doğrulama",
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                    "HTTP durum kodu 404 olmalı"),
                () -> assertNotNull(response.getBody(),
                    "Response body null olmamalı"),
                () -> assertEquals("ERROR", response.getBody().getStatus(),
                    "GenericResponse.status 'ERROR' olmalı"),
                () -> assertFalse(response.getBody().isSuccess(),
                    "isSuccess() false olmalı"),
                () -> assertNotNull(response.getBody().getMessage(),
                    "Hata mesajı null olmamalı"),
                () -> assertTrue(response.getBody().getMessage().contains("Product"),
                    "Hata mesajı kaynak tipini ('Product') içermeli"),
                () -> assertNotNull(response.getBody().getTimestamp(),
                    "Timestamp set edilmeli")
            );
        }

        /**
         * [RED-E02] ResourceNotFoundException mesajı kaynak adını ve ID'yi içermeli.
         * Örnek: "Product with id '42' not found"
         */
        @Test
        @DisplayName("[RED-E02] Exception mesajı kaynak adı ve ID içermeli")
        void resourceNotFoundException_message_shouldContainResourceAndId() {
            ResourceNotFoundException ex =
                new ResourceNotFoundException("InventoryItem", "barcode", "ITEM-001");

            ResponseEntity<GenericResponse<?>> response =
                handler.handleResourceNotFoundException(ex);

            String message = response.getBody().getMessage();
            assertAll("Mesaj içerik doğrulama",
                () -> assertTrue(message.contains("InventoryItem"),
                    "Mesaj kaynak tipini içermeli"),
                () -> assertTrue(message.contains("ITEM-001"),
                    "Mesaj değeri içermeli")
            );
        }

        /**
         * [RED-E03] Farklı kaynak tipleriyle ResourceNotFoundException doğru çalışmalı.
         */
        @ParameterizedTest(name = "[RED-E03] {0} bulunamadı → 404")
        @ValueSource(strings = {"Product", "User", "InventoryItem", "Warehouse", "Supplier"})
        @DisplayName("[RED-E03] Farklı kaynak tipleri → her biri 404 dönmeli")
        void handleResourceNotFound_differentResourceTypes_allReturn404(String resourceType) {
            ResourceNotFoundException ex =
                new ResourceNotFoundException(resourceType, "id", 1L);

            ResponseEntity<GenericResponse<?>> response =
                handler.handleResourceNotFoundException(ex);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                resourceType + " için 404 dönmeli");
        }
    }

    // =========================================================================
    // NESTED CLASS 2: 400 Bad Request — Doğrulama Hataları
    // =========================================================================

    /**
     * Gelen istek verisi geçersizse 400 ve tüm doğrulama hataları dönmeli.
     */
    @Nested
    @DisplayName("2 — 400 Bad Request (Validation Errors)")
    class BadRequestTests {

        /**
         * [RED-E04] ValidationException → 400 Bad Request.
         */
        @Test
        @DisplayName("[RED-E04] ValidationException → HTTP 400, errors listesi dolu")
        void handleValidationException_shouldReturn400WithErrors() {
            // ARRANGE
            List<String> validationErrors = List.of(
                "productName: boş olamaz",
                "price: sıfırdan büyük olmalı"
            );
            ValidationException ex = new ValidationException("Doğrulama başarısız", validationErrors);

            // ACT
            ResponseEntity<GenericResponse<?>> response =
                handler.handleValidationException(ex);

            // ASSERT
            assertAll("400 Bad Request doğrulama",
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                    "HTTP durum kodu 400 olmalı"),
                () -> assertEquals("ERROR", response.getBody().getStatus()),
                () -> assertFalse(response.getBody().getErrors().isEmpty(),
                    "Errors listesi dolu olmalı"),
                () -> assertEquals(2, response.getBody().getErrors().size(),
                    "2 hata mesajı korunmalı"),
                () -> assertTrue(response.getBody().hasErrors(),
                    "hasErrors() true olmalı")
            );
        }

        /**
         * [RED-E05] Spring'in MethodArgumentNotValidException → 400 (Bean Validation entegrasyonu).
         * @Valid annotation'ının tetiklediği hataları yakalamayı test eder.
         */
        @Test
        @DisplayName("[RED-E05] MethodArgumentNotValidException → HTTP 400")
        void handleMethodArgumentNotValid_shouldReturn400() {
            // Spring'in BindingResult'ını mock ile simüle ederiz
            // Implementation bu metodu @ExceptionHandler ile yakalar
            org.springframework.validation.BindingResult bindingResult =
                org.mockito.Mockito.mock(org.springframework.validation.BindingResult.class);

            org.springframework.validation.FieldError fieldError =
                new org.springframework.validation.FieldError(
                    "productRequest", "productName", "boş olamaz"
                );

            org.mockito.Mockito.when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError));

            org.springframework.web.bind.MethodArgumentNotValidException ex =
                new org.springframework.web.bind.MethodArgumentNotValidException(
                    null, bindingResult
                );

            ResponseEntity<GenericResponse<?>> response =
                handler.handleMethodArgumentNotValid(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertFalse(response.getBody().getErrors().isEmpty(),
                "Field error'lar GenericResponse.errors listesine dönüştürülmeli");
        }
    }

    // =========================================================================
    // NESTED CLASS 3: 401 Unauthorized
    // =========================================================================

    /**
     * Kimlik doğrulama başarısız olduğunda 401 dönmeli.
     */
    @Nested
    @DisplayName("3 — 401 Unauthorized (UnauthorizedException)")
    class UnauthorizedTests {

        /**
         * [RED-E06] UnauthorizedException → 401 Unauthorized.
         */
        @Test
        @DisplayName("[RED-E06] UnauthorizedException → HTTP 401")
        void handleUnauthorized_shouldReturn401() {
            UnauthorizedException ex = new UnauthorizedException("Geçersiz token");

            ResponseEntity<GenericResponse<?>> response =
                handler.handleUnauthorizedException(ex);

            assertAll("401 Unauthorized doğrulama",
                () -> assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()),
                () -> assertEquals("ERROR", response.getBody().getStatus()),
                () -> assertTrue(response.getBody().getMessage().contains("Geçersiz token"))
            );
        }
    }

    // =========================================================================
    // NESTED CLASS 4: 409 Conflict
    // =========================================================================

    /**
     * Kayıt zaten mevcutsa (duplicate) 409 Conflict dönmeli.
     */
    @Nested
    @DisplayName("4 — 409 Conflict (DuplicateResourceException)")
    class ConflictTests {

        /**
         * [RED-E07] DuplicateResourceException → 409 Conflict.
         */
        @Test
        @DisplayName("[RED-E07] DuplicateResourceException → HTTP 409")
        void handleDuplicate_shouldReturn409() {
            DuplicateResourceException ex =
                new DuplicateResourceException("Product", "barcode", "ITEM-001");

            ResponseEntity<GenericResponse<?>> response =
                handler.handleDuplicateResourceException(ex);

            assertAll("409 Conflict doğrulama",
                () -> assertEquals(HttpStatus.CONFLICT, response.getStatusCode(),
                    "HTTP 409 Conflict dönmeli"),
                () -> assertEquals("ERROR", response.getBody().getStatus()),
                () -> assertTrue(response.getBody().getMessage().contains("ITEM-001"),
                    "Mesaj çakışan değeri içermeli")
            );
        }
    }

    // =========================================================================
    // NESTED CLASS 5: 500 Internal Server Error
    // =========================================================================

    /**
     * Beklenmeyen (yakalanmamış) exception → 500 dönmeli.
     * Stack trace asla response'a sızmamalı.
     */
    @Nested
    @DisplayName("5 — 500 Internal Server Error (Unexpected Exception)")
    class InternalServerErrorTests {

        /**
         * [RED-E08] RuntimeException → 500 Internal Server Error.
         */
        @Test
        @DisplayName("[RED-E08] RuntimeException → HTTP 500, stack trace mesajda görünmemeli")
        void handleGenericException_shouldReturn500() {
            Exception ex = new RuntimeException("DB bağlantısı kesildi");

            ResponseEntity<GenericResponse<?>> response =
                handler.handleGenericException(ex);

            assertAll("500 Internal Server Error doğrulama",
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(),
                    "Beklenmeyen hata → 500 dönmeli"),
                () -> assertEquals("ERROR", response.getBody().getStatus()),
                () -> assertNotNull(response.getBody().getMessage(),
                    "Genel bir hata mesajı bulunmalı"),
                () -> assertFalse(
                    response.getBody().getMessage().contains("DB bağlantısı kesildi"),
                    "Teknik hata detayı client'a sızmamalı — güvenlik riski!")
            );
        }

        /**
         * [RED-E09] NullPointerException → 500, uygulama çökmemeli.
         */
        @Test
        @DisplayName("[RED-E09] NullPointerException → 500, uygulama çökmemeli")
        void handleNullPointerException_shouldReturn500Gracefully() {
            Exception ex = new NullPointerException();

            assertDoesNotThrow(() -> {
                ResponseEntity<GenericResponse<?>> response =
                    handler.handleGenericException(ex);

                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            }, "NPE'de bile uygulama çökmemeli, 500 dönmeli");
        }
    }

    // =========================================================================
    // NESTED CLASS 6: Response Yapısı Tutarlılığı
    // =========================================================================

    /**
     * Tüm hata response'larında GenericResponse yapısının tutarlı olduğunu doğrular.
     * Farklı HTTP kodlarında bile timestamp set edilmeli.
     */
    @Nested
    @DisplayName("6 — Response Consistency Tests")
    class ResponseConsistencyTests {

        /**
         * [RED-E10] Her exception tipinde timestamp set edilmiş olmalı.
         * Frontend'in "ne zaman hata oldu?" sorusunu yanıtlar.
         */
        @Test
        @DisplayName("[RED-E10] Her hata response'unda timestamp set edilmeli")
        void allErrorResponses_shouldHaveTimestamp() {
            ResponseEntity<GenericResponse<?>> r404 =
                handler.handleResourceNotFoundException(
                    new ResourceNotFoundException("X", "id", 1L));

            ResponseEntity<GenericResponse<?>> r400 =
                handler.handleValidationException(
                    new ValidationException("Hata", List.of("e1")));

            ResponseEntity<GenericResponse<?>> r500 =
                handler.handleGenericException(new RuntimeException("fail"));

            assertAll("Timestamp tutarlılığı",
                () -> assertNotNull(r404.getBody().getTimestamp(), "404 → timestamp null değil"),
                () -> assertNotNull(r400.getBody().getTimestamp(), "400 → timestamp null değil"),
                () -> assertNotNull(r500.getBody().getTimestamp(), "500 → timestamp null değil")
            );
        }

        /**
         * [RED-E11] Tüm hata response'larında status="ERROR" olmalı.
         * Hiçbir hata "SUCCESS" dönmemeli.
         */
        @Test
        @DisplayName("[RED-E11] Tüm exception handler'lar status='ERROR' döndürmeli")
        void allExceptionHandlers_shouldReturnErrorStatus() {
            String expectedStatus = "ERROR";

            ResponseEntity<GenericResponse<?>> r404 =
                handler.handleResourceNotFoundException(
                    new ResourceNotFoundException("X", "id", 1L));

            ResponseEntity<GenericResponse<?>> r400 =
                handler.handleValidationException(
                    new ValidationException("e", List.of()));

            ResponseEntity<GenericResponse<?>> r401 =
                handler.handleUnauthorizedException(
                    new UnauthorizedException("yetkisiz"));

            ResponseEntity<GenericResponse<?>> r500 =
                handler.handleGenericException(new RuntimeException("fail"));

            assertAll("Tüm handler'lar ERROR statüsü döndürmeli",
                () -> assertEquals(expectedStatus, r404.getBody().getStatus(), "404"),
                () -> assertEquals(expectedStatus, r400.getBody().getStatus(), "400"),
                () -> assertEquals(expectedStatus, r401.getBody().getStatus(), "401"),
                () -> assertEquals(expectedStatus, r500.getBody().getStatus(), "500")
            );
        }

        /**
         * [RED-E12] Hata response'larında data alanı null olmalı.
         * Hata durumunda veri dönmek mantıksal çelişki.
         */
        @Test
        @DisplayName("[RED-E12] Hata response'larında data=null olmalı")
        void allErrorResponses_dataShouldBeNull() {
            ResponseEntity<GenericResponse<?>> r404 =
                handler.handleResourceNotFoundException(
                    new ResourceNotFoundException("X", "id", 1L));

            ResponseEntity<GenericResponse<?>> r500 =
                handler.handleGenericException(new RuntimeException());

            assertAll("data null tutarlılığı",
                () -> assertNull(r404.getBody().getData(), "404 data null olmalı"),
                () -> assertNull(r500.getBody().getData(), "500 data null olmalı")
            );
        }
    }
}

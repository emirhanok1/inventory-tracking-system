package com.inventory.common.exception;

import com.inventory.common.dto.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║         GlobalExceptionHandler — Spring @RestControllerAdvice        ║
 * ║                                                                      ║
 * ║  TDD GREEN aşaması — GlobalExceptionHandlerTest testlerini geçecek   ║
 * ║  minimum ve temiz implementasyon.                                    ║
 * ║                                                                      ║
 * ║  COMMIT NOTU (Üye 2):                                                ║
 * ║    feat: GlobalExceptionHandler implementation (GREEN phase)        ║
 * ║                                                                      ║
 * ║  Strateji Raporu Karşılığı:                                          ║
 * ║    "GlobalExceptionHandler ile standart 4xx/5xx HTTP kodları        ║
 * ║     dönmeli" — Hata Yönetimi (5 pt)                                 ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * <p>Tüm mikroservislerde ortak exception-to-HTTP-response dönüşümünü sağlar.
 * Her handler {@link GenericResponse} kullanarak tutarlı JSON yapısı döner.
 *
 * <p>Güvenlik notu: 500 handler'ı teknik hata detaylarını (stack trace, DB mesajı)
 * client'a sızdırmaz. Yalnızca genel bir mesaj döner.
 *
 * <p>Desteklenen HTTP durum kodları:
 * <ul>
 *   <li>400 Bad Request — {@link ValidationException}, {@link MethodArgumentNotValidException}</li>
 *   <li>401 Unauthorized — {@link UnauthorizedException}</li>
 *   <li>404 Not Found — {@link ResourceNotFoundException}</li>
 *   <li>409 Conflict — {@link DuplicateResourceException}</li>
 *   <li>500 Internal Server Error — {@link Exception} (catch-all)</li>
 * </ul>
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @version 1.0.0
 * @since 2026-05-12
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================================
    // 404 NOT FOUND
    // =========================================================================

    /**
     * Kayıt bulunamadığında HTTP 404 döner.
     *
     * @param ex Kaynak tipini ve aranan değeri taşıyan exception
     * @return 404 ResponseEntity içinde GenericResponse(ERROR)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericResponse<?>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        GenericResponse<?> body = GenericResponse.error(ex.getMessage(), List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // =========================================================================
    // 400 BAD REQUEST — İş Kuralı Doğrulama
    // =========================================================================

    /**
     * Özel {@link ValidationException} → HTTP 400.
     * Detaylı alan hataları {@link GenericResponse#getErrors()} içinde taşınır.
     *
     * @param ex Hata listesini taşıyan exception
     * @return 400 ResponseEntity
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<GenericResponse<?>> handleValidationException(ValidationException ex) {
        GenericResponse<?> body = GenericResponse.error(ex.getMessage(), ex.getErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Spring @Valid / @Validated tetikli {@link MethodArgumentNotValidException} → HTTP 400.
     * FieldError'lar "alan: mesaj" formatında GenericResponse.errors listesine dönüştürülür.
     *
     * @param ex Spring'in doğrulama exception'ı
     * @return 400 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<?>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.toList());

        GenericResponse<?> body = GenericResponse.error("İstek doğrulama hatası", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // =========================================================================
    // 401 UNAUTHORIZED
    // =========================================================================

    /**
     * Kimlik doğrulama başarısız → HTTP 401.
     *
     * @param ex Kimlik doğrulama hatasını açıklayan exception
     * @return 401 ResponseEntity
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<GenericResponse<?>> handleUnauthorizedException(
            UnauthorizedException ex) {

        GenericResponse<?> body = GenericResponse.error(ex.getMessage(), List.of());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // =========================================================================
    // 409 CONFLICT
    // =========================================================================

    /**
     * Kayıt zaten mevcut → HTTP 409.
     *
     * @param ex Çakışan kaynak bilgisini taşıyan exception
     * @return 409 ResponseEntity
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<GenericResponse<?>> handleDuplicateResourceException(
            DuplicateResourceException ex) {

        GenericResponse<?> body = GenericResponse.error(ex.getMessage(), List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // =========================================================================
    // 500 INTERNAL SERVER ERROR — Catch-all (Güvenli)
    // =========================================================================

    /**
     * Yakalanmamış tüm exception'lar için güvenli 500 handler.
     *
     * <p><b>Güvenlik:</b> Teknik detaylar (stack trace, mesaj) client'a sızdırılmaz.
     * Genel bir mesaj döner; detay server loglarına yazılır (Üye 1'in log konfigürasyonu).
     *
     * @param ex Beklenmeyen exception
     * @return 500 ResponseEntity — teknik detay içermez
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<?>> handleGenericException(Exception ex) {
        // Teknik detay loglanabilir ama client'a sızdırılmaz
        // log.error("Beklenmeyen hata: {}", ex.getMessage(), ex);

        GenericResponse<?> body = GenericResponse.error(
            "Sunucu tarafında beklenmeyen bir hata oluştu. Lütfen daha sonra tekrar deneyin.",
            List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

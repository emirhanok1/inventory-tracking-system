package com.inventory.common.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║         Generic<T> Yapı: GenericResponse<T>                         ║
 * ║                                                                      ║
 * ║  TDD GREEN aşaması — GenericResponseTest testlerini geçecek          ║
 * ║  minimum ve temiz implementasyon.                                    ║
 * ║                                                                      ║
 * ║  COMMIT NOTU (Üye 2):                                                ║
 * ║    feat: GenericResponse<T> implementation (GREEN phase)            ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * <p>Tüm mikroservislerin API yanıtlarını standartlaştıran Generic wrapper.
 * Her endpoint bu sınıfı döndürerek tutarlı bir JSON yapısı sağlar:
 *
 * <pre>
 * {
 *   "status":    "SUCCESS" | "ERROR",
 *   "message":   "İşlem mesajı",
 *   "data":      { ... },           // T tipi
 *   "errors":    [ "hata1", ... ],  // Hata listesi
 *   "timestamp": "2026-05-12T..."
 * }
 * </pre>
 *
 * <p>Kullanım örnekleri:
 * <pre>{@code
 * // Başarılı yanıt
 * GenericResponse<Product> resp = GenericResponse.success("Ürün bulundu", product);
 *
 * // Hatalı yanıt
 * GenericResponse<Object> err = GenericResponse.error("Doğrulama hatası", errorList);
 *
 * // Sayfalı yanıt (nested generic)
 * GenericResponse<GenericPaginator<Product>> paged =
 *     GenericResponse.success("Ürünler listelendi", paginator);
 * }</pre>
 *
 * @param <T> Yanıtta taşınan veri tipi
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @version 1.0.0
 * @since 2026-05-12
 */
public class GenericResponse<T> {

    // =========================================================================
    // SABITLER
    // =========================================================================

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR   = "ERROR";

    // =========================================================================
    // ALANLAR
    // =========================================================================

    /** Yanıt durumu: "SUCCESS", "ERROR", "PENDING", "PARTIAL" vb. */
    private String status;

    /** Kullanıcıya gösterilecek mesaj. */
    private String message;

    /** Generic veri alanı — herhangi bir Java tipi olabilir. */
    private T data;

    /**
     * Doğrulama veya iş kuralı hataları.
     * <b>Hiçbir zaman null olmamalı</b> — NPE'yi önlemek için ArrayList başlatılır.
     */
    private List<String> errors = new ArrayList<>();

    /** Yanıtın oluşturulma zamanı (ISO-8601). Factory metodları otomatik set eder. */
    private LocalDateTime timestamp;

    // =========================================================================
    // CONSTRUCTOR'LAR
    // =========================================================================

    /**
     * No-arg constructor — Jackson JSON deserializasyonu ve test için gereklidir.
     * <p><b>Not:</b> {@code errors} alanı boş ArrayList ile başlatılır, null DEĞİL.
     */
    public GenericResponse() {
        this.errors = new ArrayList<>();
        // timestamp yalnızca factory metodlarında set edilir
    }

    // =========================================================================
    // STATIC FACTORY METODLARI (Builder alternatifi — sade ve tip-güvenli)
    // =========================================================================

    /**
     * Başarılı yanıt üretir.
     *
     * @param message Kullanıcıya gösterilecek mesaj
     * @param data    Taşınan veri (null olabilir — "veri yok ama başarılı" senaryosu)
     * @param <T>     Veri tipi
     * @return Status=SUCCESS, errors boş, timestamp set edilmiş yanıt
     */
    public static <T> GenericResponse<T> success(String message, T data) {
        GenericResponse<T> resp = new GenericResponse<>();
        resp.status    = STATUS_SUCCESS;
        resp.message   = message;
        resp.data      = data;
        resp.timestamp = LocalDateTime.now();
        // errors zaten boş ArrayList — ek işlem gerekmez
        return resp;
    }

    /**
     * Hata yanıtı üretir.
     *
     * @param message İnsan-okunabilir hata özeti
     * @param errors  Detaylı hata mesajları listesi (ValidationResult'tan beslenebilir)
     * @param <T>     Veri tipi (genellikle Object veya Void)
     * @return Status=ERROR, data=null, errors dolu yanıt
     */
    public static <T> GenericResponse<T> error(String message, List<String> errors) {
        GenericResponse<T> resp = new GenericResponse<>();
        resp.status    = STATUS_ERROR;
        resp.message   = message;
        resp.data      = null;
        resp.errors    = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
        resp.timestamp = LocalDateTime.now();
        return resp;
    }

    // =========================================================================
    // İŞ KURALI YARDIMCI METODLARI
    // =========================================================================

    /**
     * Status "SUCCESS" ise true döner.
     *
     * @return Status'un SUCCESS olup olmadığı
     */
    public boolean isSuccess() {
        return STATUS_SUCCESS.equals(this.status);
    }

    /**
     * Errors listesi boş değilse true döner.
     *
     * @return En az bir hata mesajı varsa true
     */
    public boolean hasErrors() {
        return this.errors != null && !this.errors.isEmpty();
    }

    // =========================================================================
    // GETTER / SETTER (Jackson serializasyonu için gereklidir)
    // =========================================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // =========================================================================
    // equals / hashCode / toString
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenericResponse)) return false;
        GenericResponse<?> that = (GenericResponse<?>) o;
        return Objects.equals(status,  that.status)
            && Objects.equals(message, that.message)
            && Objects.equals(data,    that.data)
            && Objects.equals(errors,  that.errors);
        // timestamp kasıtlı olarak dışarıda — iki farklı zamanda oluşturulmuş
        // aynı içerikteki yanıtlar eşit sayılmalı
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message, data, errors);
    }

    @Override
    public String toString() {
        return "GenericResponse{"
            + "status='"  + status  + '\''
            + ", message='" + message + '\''
            + ", data="    + data
            + ", errors="  + errors
            + ", timestamp=" + timestamp
            + '}';
    }
}

package com.inventory.common.exception;

import java.util.List;

/**
 * Gelen istek verisi iş kurallarını ihlal ettiğinde fırlatılır.
 * {@link GlobalExceptionHandler} tarafından HTTP 400'e çevirilir.
 *
 * <p>Detaylı hata mesajları {@link #getErrors()} ile erişilebilir ve
 * {@link com.inventory.common.dto.GenericResponse#error} metoduna beslenebilir.
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @since 2026-05-12
 */
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    /**
     * @param message Özet hata mesajı
     * @param errors  Detaylı alan bazlı hata listesi
     */
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors != null ? List.copyOf(errors) : List.of();
    }

    /**
     * @param message Tek hata mesajı (liste gerekmeyen durumlar)
     */
    public ValidationException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    /** @return Detaylı hata mesajları (immutable) */
    public List<String> getErrors() {
        return errors;
    }
}

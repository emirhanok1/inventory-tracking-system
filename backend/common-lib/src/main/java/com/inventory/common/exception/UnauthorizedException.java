package com.inventory.common.exception;

/**
 * Kimlik doğrulama başarısız olduğunda fırlatılır (geçersiz token, yetersiz yetki vb.).
 * {@link GlobalExceptionHandler} tarafından HTTP 401'e çevirilir.
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @since 2026-05-12
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * @param message Kimlik doğrulama hatası açıklaması
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}

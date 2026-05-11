package com.inventory.common.exception;

/**
 * Aynı benzersiz alana sahip bir kayıt zaten mevcutsa fırlatılır.
 * {@link GlobalExceptionHandler} tarafından HTTP 409'a çevirilir.
 *
 * <p>Örnek: aynı barkodla iki ürün oluşturulmaya çalışıldığında.
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @since 2026-05-12
 */
public class DuplicateResourceException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * @param resourceName Kayıt tipi (örn. "Product")
     * @param fieldName    Çakışan alan (örn. "barcode")
     * @param fieldValue   Çakışan değer (örn. "ITEM-001")
     */
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s with %s '%s' already exists", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName    = fieldName;
        this.fieldValue   = fieldValue;
    }

    public String getResourceName() { return resourceName; }
    public String getFieldName()    { return fieldName; }
    public Object getFieldValue()   { return fieldValue; }
}

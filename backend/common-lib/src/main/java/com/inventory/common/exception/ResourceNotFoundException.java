package com.inventory.common.exception;

/**
 * Kayıt veritabanında bulunamadığında fırlatılır.
 * {@link GlobalExceptionHandler} tarafından HTTP 404'e çevirilir.
 *
 * <p>Örnek kullanım:
 * <pre>{@code
 * Product p = repo.findById(id)
 *     .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
 * }</pre>
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @since 2026-05-12
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * @param resourceName Kayıt tipi (örn. "Product", "InventoryItem")
     * @param fieldName    Aranan alan (örn. "id", "barcode")
     * @param fieldValue   Aranan değer (Long veya String)
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s with %s '%s' not found", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName    = fieldName;
        this.fieldValue   = fieldValue;
    }

    public String getResourceName() { return resourceName; }
    public String getFieldName()    { return fieldName; }
    public Object getFieldValue()   { return fieldValue; }
}

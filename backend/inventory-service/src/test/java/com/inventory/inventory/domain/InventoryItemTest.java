package com.inventory.inventory.domain;

import com.inventory.common.dto.GenericResponse;
import com.inventory.common.dto.GenericPaginator;
import com.inventory.common.validation.GenericValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║         TDD — RED PHASE : InventoryItemTest                         ║
 * ║                                                                      ║
 * ║  Bu dosya Hafta 2 TDD döngüsünün ilk adımıdır (RED).                ║
 * ║  InventoryItem sınıfı henüz YAZILMAMIŞTIR.                          ║
 * ║  Derleme/çalışma hatası BEKLENEN ve ISTENEN davranıştır.            ║
 * ║                                                                      ║
 * ║  COMMIT NOTU (Üye 2):                                                ║
 * ║    test: InventoryItem RED phase - Hafta 2 TDD başlangıcı           ║
 * ║                                                                      ║
 * ║  Üye 1, InventoryItem entity'sini YALNIZCA bu commit'ten SONRA      ║
 * ║  yazacaktır. Tarih damgası TDD kanıtıdır.                           ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * <p><b>Test kapsamı:</b> inventory-service'in çekirdek domain nesnesi
 * olan {@code InventoryItem} entity'si.
 *
 * <p><b>Beklenen alan yapısı:</b>
 * <pre>
 * InventoryItem {
 *   id:          Long           (otomatik - DB tarafından)
 *   name:        String         (zorunlu, boş olamaz)
 *   barcode:     String         (zorunlu, benzersiz)
 *   quantity:    int            (≥ 0)
 *   unitPrice:   BigDecimal     (> 0)
 *   warehouseId: String         (hangi depoda)
 *   category:    String         (ürün kategorisi)
 *   createdAt:   LocalDateTime  (otomatik)
 *   updatedAt:   LocalDateTime  (güncelleme zamanı)
 * }
 * </pre>
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @version 1.0.0
 * @since 2026-05-12  ← Bu tarih implementation tarihinden ÖNCE olmalıdır!
 */
@DisplayName("InventoryItem — Unit Tests (TDD RED Phase, Hafta 2)")
class InventoryItemTest {

    // =========================================================================
    // TEST SABİTLERİ
    // =========================================================================

    private static final String VALID_NAME       = "Laptop Dell XPS 15";
    private static final String VALID_BARCODE    = "ITEM-001";
    private static final String VALID_WAREHOUSE  = "WH-ISTANBUL-01";
    private static final String VALID_CATEGORY   = "Elektronik";
    private static final int    VALID_QUANTITY   = 50;
    private static final BigDecimal VALID_PRICE  = BigDecimal.valueOf(24999.99);

    // =========================================================================
    // NESTED CLASS 1: Constructor & Builder Testleri
    // =========================================================================

    /**
     * InventoryItem'ın doğru şekilde oluşturulup oluşturulmadığını doğrular.
     */
    @Nested
    @DisplayName("1 — Constructor & Builder Tests")
    class ConstructorTests {

        /**
         * [RED-I01] Geçerli parametrelerle InventoryItem oluşturulabilmeli.
         * Tüm zorunlu alanlar set edilmeli.
         *
         * BEKLENEN DURUM (RED): InventoryItem sınıfı henüz yok →
         * derleme hatası → test zaten başarısız.
         */
        @Test
        @DisplayName("[RED-I01] Geçerli parametrelerle InventoryItem oluşturulmalı")
        void constructor_validParams_shouldCreateItem() {
            // ACT — Bu satır derleme hatası verir — sınıf henüz yok (RED aşaması)
            InventoryItem item = new InventoryItem(
                VALID_NAME, VALID_BARCODE, VALID_QUANTITY, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            // ASSERT
            assertAll("InventoryItem temel alanlar",
                () -> assertEquals(VALID_NAME,      item.getName(),        "Name doğru set edilmeli"),
                () -> assertEquals(VALID_BARCODE,   item.getBarcode(),     "Barcode doğru set edilmeli"),
                () -> assertEquals(VALID_QUANTITY,  item.getQuantity(),    "Quantity doğru set edilmeli"),
                () -> assertEquals(VALID_PRICE,     item.getUnitPrice(),   "UnitPrice doğru set edilmeli"),
                () -> assertEquals(VALID_WAREHOUSE, item.getWarehouseId(), "WarehouseId doğru set edilmeli"),
                () -> assertEquals(VALID_CATEGORY,  item.getCategory(),    "Category doğru set edilmeli")
            );
        }

        /**
         * [RED-I02] No-arg constructor sonrası tüm alanlar erişilebilir olmalı (null veya default).
         * Jackson JSON deserializasyonu için zorunludur.
         */
        @Test
        @DisplayName("[RED-I02] No-arg constructor → Jackson serializasyonu için gerekli")
        void noArgConstructor_shouldNotThrow() {
            assertDoesNotThrow(() -> {
                InventoryItem item = new InventoryItem();
                // No-arg constructor'dan sonra alanlar null/default — NPE yok
                assertNull(item.getId(),        "id başlangıçta null olmalı");
                assertNull(item.getName(),      "name başlangıçta null olmalı");
                assertNull(item.getBarcode(),   "barcode başlangıçta null olmalı");
                assertEquals(0, item.getQuantity(), "quantity default 0 olmalı");
            });
        }

        /**
         * [RED-I03] createdAt alanı constructor'da otomatik set edilmeli.
         * Veritabanı timestamp'i yerine Java tarafında da set edilebilir olmalı.
         */
        @Test
        @DisplayName("[RED-I03] Parametreli constructor → createdAt otomatik set edilmeli")
        void constructor_withParams_createdAtShouldBeSet() {
            InventoryItem item = new InventoryItem(
                VALID_NAME, VALID_BARCODE, VALID_QUANTITY, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );
            assertNotNull(item.getCreatedAt(),
                "createdAt constructor'da otomatik set edilmeli");
        }
    }

    // =========================================================================
    // NESTED CLASS 2: İş Kuralı Doğrulama Testleri
    // =========================================================================

    /**
     * InventoryItem iş kurallarını doğrular.
     * GenericValidator ile entegrasyonu test eder.
     */
    @Nested
    @DisplayName("2 — Business Rule & Validation Tests")
    class BusinessRuleTests {

        /**
         * [RED-I04] isLowStock() — Düşük stok eşiği kontrolü.
         * Eşik değeri (threshold) ≤ quantity ise false, > quantity ise true dönmeli.
         */
        @Test
        @DisplayName("[RED-I04] isLowStock(threshold) → stok eşik kontrolü")
        void isLowStock_belowThreshold_shouldReturnTrue() {
            InventoryItem item = new InventoryItem(
                VALID_NAME, VALID_BARCODE, 5, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            assertTrue(item.isLowStock(10),  "quantity(5) < threshold(10) → düşük stok");
            assertFalse(item.isLowStock(5),  "quantity(5) = threshold(5)  → eşit, düşük değil");
            assertFalse(item.isLowStock(3),  "quantity(5) > threshold(3)  → yeterli stok");
        }

        /**
         * [RED-I05] addStock(int amount) — Stok artırma.
         * Pozitif amount eklendiğinde quantity artmalı.
         */
        @Test
        @DisplayName("[RED-I05] addStock(amount) → quantity artar")
        void addStock_positiveAmount_shouldIncreaseQuantity() {
            InventoryItem item = new InventoryItem(
                VALID_NAME, VALID_BARCODE, 10, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            item.addStock(5);
            assertEquals(15, item.getQuantity(), "10 + 5 = 15 olmalı");

            item.addStock(0);
            assertEquals(15, item.getQuantity(), "0 ekleme → değişmemeli");
        }

        /**
         * [RED-I06] addStock(negativeAmount) → IllegalArgumentException.
         * Negatif stok eklemek mantıksal hata — removeStock() kullanılmalı.
         */
        @Test
        @DisplayName("[RED-I06] addStock(negatif) → IllegalArgumentException")
        void addStock_negativeAmount_shouldThrowException() {
            InventoryItem item = new InventoryItem(
                VALID_NAME, VALID_BARCODE, VALID_QUANTITY, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            assertThrows(IllegalArgumentException.class,
                () -> item.addStock(-1),
                "Negatif amount için IllegalArgumentException bekleniyor");
        }

        /**
         * [RED-I07] removeStock(amount) — Stok azaltma.
         * Yeterli stok varsa quantity azalmalı.
         */
        @Test
        @DisplayName("[RED-I07] removeStock(amount) → quantity azalır")
        void removeStock_sufficientStock_shouldDecreaseQuantity() {
            InventoryItem item = new InventoryItem(
                VALID_NAME, VALID_BARCODE, 20, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            item.removeStock(5);
            assertEquals(15, item.getQuantity(), "20 - 5 = 15 olmalı");
        }

        /**
         * [RED-I08] removeStock(amount) — Yetersiz stok → InsufficientStockException.
         * Stok eksi gidemez — domain invariant.
         */
        @Test
        @DisplayName("[RED-I08] removeStock(amount > quantity) → InsufficientStockException")
        void removeStock_insufficientQuantity_shouldThrowException() {
            InventoryItem item = new InventoryItem(
                VALID_NAME, VALID_BARCODE, 3, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            assertThrows(Exception.class, // InsufficientStockException (Üye 1 tanımlar)
                () -> item.removeStock(10),
                "Yetersiz stokta exception fırlatılmalı");
        }

        /**
         * [RED-I09] getTotalValue() — Toplam değer hesaplama.
         * totalValue = unitPrice * quantity
         */
        @Test
        @DisplayName("[RED-I09] getTotalValue() → unitPrice × quantity")
        void getTotalValue_shouldCalculateCorrectly() {
            // 100 adet, birim fiyat 25.50 → toplam 2550.00
            InventoryItem item = new InventoryItem(
                VALID_NAME, VALID_BARCODE, 100,
                BigDecimal.valueOf(25.50),
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            BigDecimal expected = BigDecimal.valueOf(2550.00);
            assertEquals(0, expected.compareTo(item.getTotalValue()),
                "100 * 25.50 = 2550.00 olmalı");
        }

        /**
         * [RED-I10] validate() — GenericValidator ile alan doğrulaması.
         * name veya barcode boş ise ValidationResult geçersiz olmalı.
         */
        @Test
        @DisplayName("[RED-I10] validate() → isim/barkod boş → isValid=false")
        void validate_blankNameOrBarcode_shouldReturnInvalid() {
            InventoryItem invalidItem = new InventoryItem();
            invalidItem.setName("");
            invalidItem.setBarcode("");
            invalidItem.setQuantity(-1);
            invalidItem.setUnitPrice(BigDecimal.ZERO);

            GenericValidator.ValidationResult result = invalidItem.validate();
            assertFalse(result.isValid(),
                "Geçersiz alanlarla validate() → isValid=false olmalı");
            assertFalse(result.getErrors().isEmpty(),
                "Hata listesi boş olmamalı");
        }

        /**
         * [RED-I11] validate() — Geçerli item → ValidationResult geçerli.
         */
        @Test
        @DisplayName("[RED-I11] validate() geçerli item → isValid=true")
        void validate_validItem_shouldReturnValid() {
            InventoryItem item = new InventoryItem(
                VALID_NAME, VALID_BARCODE, VALID_QUANTITY, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            GenericValidator.ValidationResult result = item.validate();
            assertTrue(result.isValid(), "Geçerli item → isValid=true");
            assertTrue(result.getErrors().isEmpty(), "Hata yok → errors boş");
        }
    }

    // =========================================================================
    // NESTED CLASS 3: Getter / Setter Testleri
    // =========================================================================

    /**
     * POJO alanlarının get/set döngüsünü doğrular.
     */
    @Nested
    @DisplayName("3 — Getter / Setter Tests")
    class GetterSetterTests {

        /**
         * [RED-I12] Tüm alanlar setter ile set edilip getter ile okunabilmeli.
         */
        @Test
        @DisplayName("[RED-I12] Tüm alanlar set/get round-trip geçmeli")
        void allFields_getterSetterRoundTrip() {
            InventoryItem item = new InventoryItem();

            item.setId(1L);
            item.setName(VALID_NAME);
            item.setBarcode(VALID_BARCODE);
            item.setQuantity(VALID_QUANTITY);
            item.setUnitPrice(VALID_PRICE);
            item.setWarehouseId(VALID_WAREHOUSE);
            item.setCategory(VALID_CATEGORY);

            LocalDateTime now = LocalDateTime.now();
            item.setCreatedAt(now);
            item.setUpdatedAt(now);

            assertAll("Getter/Setter round-trip",
                () -> assertEquals(1L,             item.getId()),
                () -> assertEquals(VALID_NAME,     item.getName()),
                () -> assertEquals(VALID_BARCODE,  item.getBarcode()),
                () -> assertEquals(VALID_QUANTITY, item.getQuantity()),
                () -> assertEquals(VALID_PRICE,    item.getUnitPrice()),
                () -> assertEquals(VALID_WAREHOUSE,item.getWarehouseId()),
                () -> assertEquals(VALID_CATEGORY, item.getCategory()),
                () -> assertEquals(now,            item.getCreatedAt()),
                () -> assertEquals(now,            item.getUpdatedAt())
            );
        }

        /**
         * [RED-I13] Stok miktarı 0 olabilmeli — "tükenmiş ürün" senaryosu.
         */
        @Test
        @DisplayName("[RED-I13] quantity=0 → tükenmiş ürün, hata yok")
        void quantity_zeroIsValid_outOfStockScenario() {
            InventoryItem item = new InventoryItem();
            item.setQuantity(0);
            assertEquals(0, item.getQuantity(), "Sıfır miktar geçerli — tükenmiş ürün");
            assertTrue(item.isLowStock(1), "quantity=0 → threshold=1 ile düşük stok");
        }
    }

    // =========================================================================
    // NESTED CLASS 4: equals / hashCode / toString Testleri
    // =========================================================================

    /**
     * Entity kimlik sözleşmesi — barcode benzersiz tanımlayıcıdır.
     */
    @Nested
    @DisplayName("4 — equals / hashCode / toString Tests")
    class EqualityTests {

        /**
         * [RED-I14] Aynı barcode'a sahip iki item equals açısından eşit olmalı.
         * Barcode, iş domain'inde benzersiz tanımlayıcıdır.
         */
        @Test
        @DisplayName("[RED-I14] Aynı barcode → equals=true, hashCode eşit")
        void equals_sameBarcode_shouldBeEqual() {
            InventoryItem item1 = new InventoryItem(
                "Laptop A", "BARCODE-XYZ", 10, BigDecimal.TEN, "WH-01", "Elektronik"
            );
            InventoryItem item2 = new InventoryItem(
                "Laptop B", "BARCODE-XYZ", 20, BigDecimal.ONE, "WH-02", "Bilgisayar"
            );

            assertEquals(item1, item2,
                "Aynı barcode → business equals sözleşmesi");
            assertEquals(item1.hashCode(), item2.hashCode(),
                "Eşit nesnelerin hashCode'ları aynı olmalı");
        }

        /**
         * [RED-I15] Farklı barcode → equals=false.
         */
        @Test
        @DisplayName("[RED-I15] Farklı barcode → equals=false")
        void equals_differentBarcode_shouldNotBeEqual() {
            InventoryItem item1 = new InventoryItem(
                VALID_NAME, "BARCODE-001", VALID_QUANTITY, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );
            InventoryItem item2 = new InventoryItem(
                VALID_NAME, "BARCODE-002", VALID_QUANTITY, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            assertNotEquals(item1, item2, "Farklı barcode → eşit olmamalı");
        }

        /**
         * [RED-I16] toString() null dönmemeli ve temel alanları içermeli.
         */
        @Test
        @DisplayName("[RED-I16] toString() null olmamalı, name/barcode içermeli")
        void toString_shouldContainKeyFields() {
            InventoryItem item = new InventoryItem(
                VALID_NAME, VALID_BARCODE, VALID_QUANTITY, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            String str = item.toString();
            assertNotNull(str, "toString() null dönmemeli");
            assertTrue(str.contains(VALID_NAME) || str.contains("name"),
                "toString() name alanını içermeli");
        }
    }

    // =========================================================================
    // NESTED CLASS 5: GenericResponse Entegrasyon Testleri
    // =========================================================================

    /**
     * InventoryItem'ın GenericResponse içinde sarmalanmasını doğrular.
     * Gerçek API response formatını simüle eder.
     */
    @Nested
    @DisplayName("5 — GenericResponse<InventoryItem> Entegrasyon Testleri")
    class GenericResponseIntegrationTests {

        /**
         * [RED-I17] InventoryItem, GenericResponse<InventoryItem> içine sarmalanabilmeli.
         */
        @Test
        @DisplayName("[RED-I17] GenericResponse<InventoryItem> sarmalama çalışmalı")
        void inventoryItem_wrappedInGenericResponse_shouldWork() {
            InventoryItem item = new InventoryItem(
                VALID_NAME, VALID_BARCODE, VALID_QUANTITY, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            GenericResponse<InventoryItem> response =
                GenericResponse.success("Stok kalemi bulundu", item);

            assertAll("GenericResponse<InventoryItem> sarmalama",
                () -> assertEquals("SUCCESS", response.getStatus()),
                () -> assertNotNull(response.getData()),
                () -> assertEquals(VALID_BARCODE, response.getData().getBarcode()),
                () -> assertEquals(VALID_QUANTITY, response.getData().getQuantity()),
                () -> assertTrue(response.isSuccess()),
                () -> assertFalse(response.hasErrors())
            );
        }

        /**
         * [RED-I18] Sayfalı InventoryItem listesi, GenericResponse<GenericPaginator<InventoryItem>> ile sarmalanmalı.
         * Gerçek /api/inventory?page=0&size=20 endpoint'inin response formatı.
         */
        @Test
        @DisplayName("[RED-I18] GenericResponse<GenericPaginator<InventoryItem>> sayfalı liste")
        void inventoryItemList_paginatedInGenericResponse_shouldWork() {
            // Sayfa içeriği olarak tek bir item
            InventoryItem item = new InventoryItem(
                VALID_NAME, VALID_BARCODE, VALID_QUANTITY, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            java.util.List<InventoryItem> pageContent = java.util.List.of(item);
            GenericPaginator<InventoryItem> paginator =
                new GenericPaginator<>(pageContent, 0, 20, 1L);

            GenericResponse<GenericPaginator<InventoryItem>> response =
                GenericResponse.success("Stok listesi", paginator);

            assertAll("Sayfalı InventoryItem response",
                () -> assertEquals("SUCCESS", response.getStatus()),
                () -> assertEquals(1L, response.getData().getTotalElements()),
                () -> assertEquals(1, response.getData().getContent().size()),
                () -> assertEquals(VALID_BARCODE,
                    response.getData().getContent().get(0).getBarcode()),
                () -> assertFalse(response.getData().hasNext()),
                () -> assertFalse(response.getData().hasPrevious())
            );
        }
    }
}

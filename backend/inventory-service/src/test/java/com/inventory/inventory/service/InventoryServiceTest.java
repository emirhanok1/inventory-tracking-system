package com.inventory.inventory.service;

import com.inventory.common.dto.GenericPaginator;
import com.inventory.common.dto.GenericResponse;
import com.inventory.common.exception.DuplicateResourceException;
import com.inventory.common.exception.ResourceNotFoundException;
import com.inventory.inventory.domain.InventoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║         TDD — RED PHASE : InventoryServiceTest                      ║
 * ║                                                                      ║
 * ║  Bu dosya Hafta 2 TDD döngüsünün ilk adımıdır (RED).                ║
 * ║  InventoryService ve InventoryRepository henüz YAZILMAMIŞTIR.       ║
 * ║  Derleme/çalışma hatası BEKLENEN ve ISTENEN davranıştır.            ║
 * ║                                                                      ║
 * ║  COMMIT NOTU (Üye 2):                                                ║
 * ║    test: InventoryService RED phase - Hafta 2 servis testleri       ║
 * ║                                                                      ║
 * ║  Üye 1, InventoryService'i YALNIZCA bu commit'ten SONRA yazacak.    ║
 * ║  Tarih damgası TDD kanıtıdır.                                        ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * <p><b>Test stratejisi:</b> Spring context başlatılmaz (hızlı unit test).
 * {@link InventoryRepository} Mockito ile mock'lanır.
 * {@link InventoryService} doğrudan instantiate edilir.
 *
 * <p><b>SOLID uygulaması:</b>
 * <ul>
 *   <li>SRP — InventoryService yalnızca stok iş mantığını yönetir</li>
 *   <li>DIP — Service, concrete repository'ye değil interface'e bağımlı</li>
 *   <li>OCP — Yeni stok kuralları service değiştirilmeden genişletilebilir</li>
 * </ul>
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @version 1.0.0
 * @since 2026-05-12  ← Bu tarih implementation tarihinden ÖNCE olmalıdır!
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService — Unit Tests (TDD RED Phase, Hafta 2)")
class InventoryServiceTest {

    // =========================================================================
    // MOCK'LAR VE TEST KONUSU
    // =========================================================================

    /**
     * Repository arayüzü mock'lanır.
     * Gerçek veritabanı bağlantısı gerekmez → hızlı, izole unit test.
     *
     * BEKLENEN DURUM (RED): InventoryRepository arayüzü henüz yok → derleme hatası.
     */
    @Mock
    private InventoryRepository inventoryRepository;

    /**
     * Test edilen servis — dependency injection Mockito ile sağlanır.
     *
     * BEKLENEN DURUM (RED): InventoryService sınıfı henüz yok → derleme hatası.
     */
    @InjectMocks
    private InventoryService inventoryService;

    // =========================================================================
    // TEST SABİTLERİ
    // =========================================================================

    private static final Long   VALID_ID        = 1L;
    private static final String VALID_BARCODE   = "ITEM-001";
    private static final String VALID_NAME      = "Laptop Dell XPS 15";
    private static final String VALID_WAREHOUSE = "WH-ISTANBUL-01";
    private static final String VALID_CATEGORY  = "Elektronik";
    private static final int    VALID_QUANTITY  = 50;
    private static final BigDecimal VALID_PRICE = BigDecimal.valueOf(24999.99);

    /** Test fixture: geçerli bir InventoryItem nesnesi */
    private InventoryItem sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = new InventoryItem(
            VALID_NAME, VALID_BARCODE, VALID_QUANTITY, VALID_PRICE,
            VALID_WAREHOUSE, VALID_CATEGORY
        );
        sampleItem.setId(VALID_ID);
    }

    // =========================================================================
    // NESTED CLASS 1: findById() — Kayıt Arama
    // =========================================================================

    /**
     * ID ile stok kalemi arama senaryoları.
     */
    @Nested
    @DisplayName("1 — findById() Tests")
    class FindByIdTests {

        /**
         * [RED-S01] Var olan ID ile findById() → GenericResponse(SUCCESS, item).
         */
        @Test
        @DisplayName("[RED-S01] Var olan ID → SUCCESS response, item dolu")
        void findById_existingId_shouldReturnSuccessResponse() {
            // ARRANGE — Repository mock davranışı
            when(inventoryRepository.findById(VALID_ID))
                .thenReturn(Optional.of(sampleItem));

            // ACT
            GenericResponse<InventoryItem> response = inventoryService.findById(VALID_ID);

            // ASSERT
            assertAll("findById başarılı senaryo",
                () -> assertEquals("SUCCESS", response.getStatus(),
                    "Var olan ID → SUCCESS"),
                () -> assertNotNull(response.getData(),
                    "Data null olmamalı"),
                () -> assertEquals(VALID_BARCODE, response.getData().getBarcode(),
                    "Doğru item döndürülmeli"),
                () -> assertTrue(response.isSuccess()),
                () -> assertFalse(response.hasErrors())
            );

            // Repository'nin tam olarak 1 kez çağrıldığını doğrula
            verify(inventoryRepository, times(1)).findById(VALID_ID);
        }

        /**
         * [RED-S02] Olmayan ID ile findById() → ResourceNotFoundException fırlatmalı.
         * GlobalExceptionHandler bunu HTTP 404'e çevirir.
         */
        @Test
        @DisplayName("[RED-S02] Olmayan ID → ResourceNotFoundException (→ HTTP 404)")
        void findById_nonExistingId_shouldThrowResourceNotFoundException() {
            // ARRANGE
            when(inventoryRepository.findById(999L))
                .thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThrows(ResourceNotFoundException.class,
                () -> inventoryService.findById(999L),
                "Olmayan ID → ResourceNotFoundException bekleniyor");

            verify(inventoryRepository, times(1)).findById(999L);
        }

        /**
         * [RED-S03] null ID → IllegalArgumentException.
         * Null ID veritabanı sorgusuna ulaşmamalı.
         */
        @Test
        @DisplayName("[RED-S03] null ID → IllegalArgumentException (erken doğrulama)")
        void findById_nullId_shouldThrowIllegalArgument() {
            assertThrows(IllegalArgumentException.class,
                () -> inventoryService.findById(null),
                "null ID için IllegalArgumentException bekleniyor");

            // Repository hiç çağrılmamalı
            verifyNoInteractions(inventoryRepository);
        }
    }

    // =========================================================================
    // NESTED CLASS 2: findByBarcode() — Barkod ile Arama
    // =========================================================================

    /**
     * Barkod ile stok kalemi arama senaryoları.
     */
    @Nested
    @DisplayName("2 — findByBarcode() Tests")
    class FindByBarcodeTests {

        /**
         * [RED-S04] Var olan barkodla findByBarcode() → SUCCESS response.
         */
        @Test
        @DisplayName("[RED-S04] Var olan barkod → SUCCESS response")
        void findByBarcode_existingBarcode_shouldReturnItem() {
            when(inventoryRepository.findByBarcode(VALID_BARCODE))
                .thenReturn(Optional.of(sampleItem));

            GenericResponse<InventoryItem> response =
                inventoryService.findByBarcode(VALID_BARCODE);

            assertEquals("SUCCESS", response.getStatus());
            assertEquals(VALID_BARCODE, response.getData().getBarcode());
        }

        /**
         * [RED-S05] Olmayan barkod → ResourceNotFoundException.
         */
        @Test
        @DisplayName("[RED-S05] Olmayan barkod → ResourceNotFoundException")
        void findByBarcode_nonExisting_shouldThrowNotFoundException() {
            when(inventoryRepository.findByBarcode("UNKNOWN-BARCODE"))
                .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> inventoryService.findByBarcode("UNKNOWN-BARCODE"));
        }

        /**
         * [RED-S06] Boş barkod → IllegalArgumentException (erken doğrulama).
         */
        @ParameterizedTest(name = "[RED-S06] Boş barkod: ''{0}''")
        @ValueSource(strings = {"", "   "})
        @DisplayName("[RED-S06] Boş/whitespace barkod → IllegalArgumentException")
        void findByBarcode_blankBarcode_shouldThrowIllegalArgument(String barcode) {
            assertThrows(IllegalArgumentException.class,
                () -> inventoryService.findByBarcode(barcode));

            verifyNoInteractions(inventoryRepository);
        }
    }

    // =========================================================================
    // NESTED CLASS 3: createItem() — Stok Kalemi Oluşturma
    // =========================================================================

    /**
     * Yeni stok kalemi oluşturma senaryoları.
     */
    @Nested
    @DisplayName("3 — createItem() Tests")
    class CreateItemTests {

        /**
         * [RED-S07] Geçerli item ile createItem() → kayıt edilmeli, SUCCESS response.
         */
        @Test
        @DisplayName("[RED-S07] Geçerli item → kayıt edilir, SUCCESS response")
        void createItem_validItem_shouldSaveAndReturnSuccess() {
            // ARRANGE
            when(inventoryRepository.existsByBarcode(VALID_BARCODE))
                .thenReturn(false);
            when(inventoryRepository.save(any(InventoryItem.class)))
                .thenReturn(sampleItem);

            // ACT
            GenericResponse<InventoryItem> response = inventoryService.createItem(sampleItem);

            // ASSERT
            assertAll("createItem başarılı senaryo",
                () -> assertEquals("SUCCESS", response.getStatus()),
                () -> assertNotNull(response.getData()),
                () -> assertEquals(VALID_ID, response.getData().getId())
            );

            verify(inventoryRepository, times(1)).save(sampleItem);
        }

        /**
         * [RED-S08] Mevcut barkodla createItem() → DuplicateResourceException.
         * GlobalExceptionHandler bunu HTTP 409'a çevirir.
         */
        @Test
        @DisplayName("[RED-S08] Mevcut barkod → DuplicateResourceException (→ HTTP 409)")
        void createItem_duplicateBarcode_shouldThrowDuplicateException() {
            // ARRANGE — Barkod zaten var
            when(inventoryRepository.existsByBarcode(VALID_BARCODE))
                .thenReturn(true);

            // ACT & ASSERT
            assertThrows(DuplicateResourceException.class,
                () -> inventoryService.createItem(sampleItem),
                "Mevcut barkod → DuplicateResourceException bekleniyor");

            // Kaydetme çağrılmamalı
            verify(inventoryRepository, never()).save(any());
        }

        /**
         * [RED-S09] null item ile createItem() → IllegalArgumentException.
         */
        @Test
        @DisplayName("[RED-S09] null item → IllegalArgumentException")
        void createItem_nullItem_shouldThrowIllegalArgument() {
            assertThrows(IllegalArgumentException.class,
                () -> inventoryService.createItem(null));

            verifyNoInteractions(inventoryRepository);
        }
    }

    // =========================================================================
    // NESTED CLASS 4: updateStock() — Stok Güncelleme
    // =========================================================================

    /**
     * Stok miktarı güncelleme senaryoları.
     */
    @Nested
    @DisplayName("4 — updateStock() Tests")
    class UpdateStockTests {

        /**
         * [RED-S10] Pozitif amount → stok eklenir, SUCCESS response.
         */
        @Test
        @DisplayName("[RED-S10] updateStock(id, +5) → stok artar")
        void updateStock_positiveAmount_shouldAddStock() {
            // ARRANGE
            when(inventoryRepository.findById(VALID_ID))
                .thenReturn(Optional.of(sampleItem));
            when(inventoryRepository.save(any(InventoryItem.class)))
                .thenAnswer(inv -> inv.getArgument(0)); // save edilen nesneyi döndür

            // ACT
            GenericResponse<InventoryItem> response =
                inventoryService.updateStock(VALID_ID, 5);

            // ASSERT
            assertEquals("SUCCESS", response.getStatus());
            assertEquals(VALID_QUANTITY + 5, response.getData().getQuantity(),
                "Stok 5 artmalı: " + VALID_QUANTITY + " + 5 = " + (VALID_QUANTITY + 5));
        }

        /**
         * [RED-S11] Negatif amount → stok azaltılır (removeStock çağrılır).
         */
        @Test
        @DisplayName("[RED-S11] updateStock(id, -5) → stok azalır")
        void updateStock_negativeAmount_shouldRemoveStock() {
            when(inventoryRepository.findById(VALID_ID))
                .thenReturn(Optional.of(sampleItem));
            when(inventoryRepository.save(any(InventoryItem.class)))
                .thenAnswer(inv -> inv.getArgument(0));

            GenericResponse<InventoryItem> response =
                inventoryService.updateStock(VALID_ID, -5);

            assertEquals("SUCCESS", response.getStatus());
            assertEquals(VALID_QUANTITY - 5, response.getData().getQuantity(),
                "Stok 5 azalmalı");
        }

        /**
         * [RED-S12] Olmayan ID ile updateStock() → ResourceNotFoundException.
         */
        @Test
        @DisplayName("[RED-S12] Olmayan ID → ResourceNotFoundException")
        void updateStock_nonExistingId_shouldThrowNotFoundException() {
            when(inventoryRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> inventoryService.updateStock(999L, 10));
        }
    }

    // =========================================================================
    // NESTED CLASS 5: listItems() — Sayfalı Liste
    // =========================================================================

    /**
     * Sayfalı stok listesi senaryoları.
     */
    @Nested
    @DisplayName("5 — listItems() Paginated Tests")
    class ListItemsTests {

        /**
         * [RED-S13] listItems() → GenericResponse<GenericPaginator<InventoryItem>> dönmeli.
         * Gerçek /api/inventory?page=0&size=20 endpoint'i formatı.
         */
        @Test
        @DisplayName("[RED-S13] listItems(page, size) → sayfalı GenericResponse")
        void listItems_shouldReturnPaginatedGenericResponse() {
            // ARRANGE
            List<InventoryItem> items = List.of(sampleItem);
            long totalCount = 1L;

            when(inventoryRepository.findAll(anyInt(), anyInt()))
                .thenReturn(items);
            when(inventoryRepository.count())
                .thenReturn(totalCount);

            // ACT
            GenericResponse<GenericPaginator<InventoryItem>> response =
                inventoryService.listItems(0, 20);

            // ASSERT
            assertAll("Sayfalı liste response",
                () -> assertEquals("SUCCESS", response.getStatus()),
                () -> assertNotNull(response.getData()),
                () -> assertEquals(1L, response.getData().getTotalElements()),
                () -> assertEquals(1, response.getData().getContent().size()),
                () -> assertEquals(VALID_BARCODE,
                    response.getData().getContent().get(0).getBarcode()),
                () -> assertFalse(response.getData().hasNext()),
                () -> assertTrue(response.getData().isFirstPage())
            );
        }

        /**
         * [RED-S14] Boş liste → SUCCESS response, boş paginator.
         */
        @Test
        @DisplayName("[RED-S14] Boş depo → SUCCESS, içerik boş")
        void listItems_emptyRepository_shouldReturnEmptyPage() {
            when(inventoryRepository.findAll(anyInt(), anyInt()))
                .thenReturn(List.of());
            when(inventoryRepository.count())
                .thenReturn(0L);

            GenericResponse<GenericPaginator<InventoryItem>> response =
                inventoryService.listItems(0, 20);

            assertEquals("SUCCESS", response.getStatus());
            assertTrue(response.getData().getContent().isEmpty(),
                "Boş depo → içerik boş olmalı");
            assertEquals(0, response.getData().getTotalPages(),
                "Toplam sayfa = 0");
        }

        /**
         * [RED-S15] Geçersiz sayfa parametreleri → IllegalArgumentException.
         */
        @Test
        @DisplayName("[RED-S15] page < 0 veya size <= 0 → IllegalArgumentException")
        void listItems_invalidPagination_shouldThrowException() {
            assertThrows(IllegalArgumentException.class,
                () -> inventoryService.listItems(-1, 20),
                "Negatif sayfa numarası geçersiz");

            assertThrows(IllegalArgumentException.class,
                () -> inventoryService.listItems(0, 0),
                "Sıfır sayfa boyutu geçersiz");

            verifyNoInteractions(inventoryRepository);
        }
    }

    // =========================================================================
    // NESTED CLASS 6: deleteItem() — Stok Silme
    // =========================================================================

    /**
     * Stok kalemi silme senaryoları.
     */
    @Nested
    @DisplayName("6 — deleteItem() Tests")
    class DeleteItemTests {

        /**
         * [RED-S16] Var olan ID → başarıyla silinir, SUCCESS response.
         */
        @Test
        @DisplayName("[RED-S16] Var olan ID → silinir, SUCCESS response")
        void deleteItem_existingId_shouldDeleteAndReturnSuccess() {
            when(inventoryRepository.findById(VALID_ID))
                .thenReturn(Optional.of(sampleItem));
            doNothing().when(inventoryRepository).deleteById(VALID_ID);

            GenericResponse<Void> response = inventoryService.deleteItem(VALID_ID);

            assertEquals("SUCCESS", response.getStatus());
            verify(inventoryRepository, times(1)).deleteById(VALID_ID);
        }

        /**
         * [RED-S17] Olmayan ID ile deleteItem() → ResourceNotFoundException.
         */
        @Test
        @DisplayName("[RED-S17] Olmayan ID → ResourceNotFoundException")
        void deleteItem_nonExistingId_shouldThrowNotFoundException() {
            when(inventoryRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                () -> inventoryService.deleteItem(999L));

            verify(inventoryRepository, never()).deleteById(anyLong());
        }
    }

    // =========================================================================
    // NESTED CLASS 7: getLowStockItems() — Düşük Stok Uyarısı
    // =========================================================================

    /**
     * Düşük stoklu kalemler için uyarı listesi senaryoları.
     * Bu özellik Android mobil istemcideki göstergede kullanılır.
     */
    @Nested
    @DisplayName("7 — getLowStockItems() Tests")
    class LowStockTests {

        /**
         * [RED-S18] Eşiğin altındaki item'lar → doğru filtrelenmiş liste döner.
         */
        @Test
        @DisplayName("[RED-S18] getLowStockItems(threshold) → eşik altı item'lar")
        void getLowStockItems_shouldReturnItemsBelowThreshold() {
            InventoryItem lowStockItem = new InventoryItem(
                "Kritik Ürün", "ITEM-LOW", 3, VALID_PRICE,
                VALID_WAREHOUSE, VALID_CATEGORY
            );

            when(inventoryRepository.findByQuantityLessThan(10))
                .thenReturn(List.of(lowStockItem));

            GenericResponse<List<InventoryItem>> response =
                inventoryService.getLowStockItems(10);

            assertAll("Düşük stok listesi",
                () -> assertEquals("SUCCESS", response.getStatus()),
                () -> assertEquals(1, response.getData().size()),
                () -> assertEquals("ITEM-LOW", response.getData().get(0).getBarcode()),
                () -> assertTrue(response.getData().get(0).isLowStock(10))
            );
        }

        /**
         * [RED-S19] Tüm stoklar yeterliyse → boş liste, SUCCESS.
         */
        @Test
        @DisplayName("[RED-S19] Tüm stoklar yeterli → boş liste, SUCCESS")
        void getLowStockItems_allSufficient_shouldReturnEmptyList() {
            when(inventoryRepository.findByQuantityLessThan(anyInt()))
                .thenReturn(List.of());

            GenericResponse<List<InventoryItem>> response =
                inventoryService.getLowStockItems(5);

            assertEquals("SUCCESS", response.getStatus());
            assertTrue(response.getData().isEmpty(), "Düşük stoklu item yok");
        }
    }
}

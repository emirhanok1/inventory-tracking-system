package com.inventory.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║         TDD — RED PHASE : GenericPaginatorTest                      ║
 * ║                                                                      ║
 * ║  Bu dosya TDD döngüsünün ilk adımıdır (RED).                        ║
 * ║  GenericPaginator<T> sınıfı henüz YAZILMAMIŞTIR.                    ║
 * ║  Derleme/çalışma hatası BEKLENEN davranıştır.                       ║
 * ║                                                                      ║
 * ║  COMMIT NOTU (Üye 2):                                                ║
 * ║    test: GenericPaginator RED phase - tests before implementation   ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * <p>Test edilen sınıf: {@link GenericPaginator}
 * <p>Sayfalama (pagination) mantığını tüm kenar senaryolarıyla doğrular.
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @version 1.0.0
 * @since 2026-05-12  ← Implementation tarihinden ÖNCE
 */
@DisplayName("GenericPaginator<T> — Unit Tests (TDD RED Phase)")
class GenericPaginatorTest {

    // =========================================================================
    // TEST VERİSİ YARDIMCISI
    // =========================================================================

    /**
     * N elemanlı string listesi üretir. Test verisi DRY prensibiyle merkezde.
     */
    private static List<String> items(int count) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add("item-" + i);
        }
        return list;
    }

    // =========================================================================
    // NESTED CLASS 1: Constructor & Temel Sayfalama Hesabı
    // =========================================================================

    /**
     * GenericPaginator<T>'nin constructor parametrelerini alıp doğru
     * sayfalama metadata'sını hesaplayıp hesaplamadığını doğrular.
     */
    @Nested
    @DisplayName("1 — Constructor & Page Calculation Tests")
    class ConstructorTests {

        /**
         * [RED-P01] 10 eleman, sayfa boyutu 3 → toplam 4 sayfa (ceil(10/3)).
         *
         * Beklenen hesaplama: Math.ceil(totalElements / pageSize)
         */
        @Test
        @DisplayName("[RED-P01] 10 eleman, pageSize=3 → totalPages=4")
        void constructor_10Items_pageSize3_shouldCalculate4Pages() {
            // ARRANGE
            List<String> data = items(3); // Sadece mevcut sayfa içeriği
            int page        = 0;
            int pageSize    = 3;
            long totalItems = 10L;

            // ACT — Sınıf henüz yok (RED aşaması)
            GenericPaginator<String> paginator =
                new GenericPaginator<>(data, page, pageSize, totalItems);

            // ASSERT
            assertAll("Sayfa hesaplama doğrulama",
                () -> assertEquals(4, paginator.getTotalPages(),
                    "ceil(10/3)=4 sayfa olmalı"),
                () -> assertEquals(10L, paginator.getTotalElements(),
                    "Toplam eleman sayısı 10 olmalı"),
                () -> assertEquals(0, paginator.getCurrentPage(),
                    "Mevcut sayfa 0 (sıfır-tabanlı indeks)"),
                () -> assertEquals(3, paginator.getPageSize(),
                    "Sayfa boyutu 3 olmalı"),
                () -> assertEquals(3, paginator.getContent().size(),
                    "Mevcut sayfada 3 eleman olmalı")
            );
        }

        /**
         * [RED-P02] Parametreli test — Farklı toplam eleman / sayfa boyutu kombinasyonları.
         * totalPages = ceil(totalElements / pageSize)
         */
        @ParameterizedTest(name = "[RED-P02] total={0}, pageSize={1} → expectedPages={2}")
        @CsvSource({
            "10, 3, 4",   // ceil(10/3) = 4
            "9,  3, 3",   // ceil(9/3)  = 3 (tam bölünür)
            "1,  5, 1",   // tek eleman, büyük sayfa → 1 sayfa
            "0,  5, 0",   // sıfır eleman → 0 sayfa
            "100,10,10"   // tam bölünür
        })
        @DisplayName("[RED-P02] totalPages = ceil(totalElements / pageSize)")
        void totalPages_calculatedCorrectly(long total, int pageSize, int expectedPages) {
            List<String> data = total > 0 ? items((int) Math.min(total, pageSize)) : List.of();
            GenericPaginator<String> p = new GenericPaginator<>(data, 0, pageSize, total);
            assertEquals(expectedPages, p.getTotalPages(),
                String.format("ceil(%d/%d) = %d olmalı", total, pageSize, expectedPages));
        }

        /**
         * [RED-P03] Boş içerik listesiyle oluşturulduğunda exception fırlatmamalı.
         */
        @Test
        @DisplayName("[RED-P03] Boş content listesi → exception fırlatmamalı")
        void constructor_emptyContent_shouldNotThrow() {
            assertDoesNotThrow(() -> {
                GenericPaginator<String> p =
                    new GenericPaginator<>(Collections.emptyList(), 0, 10, 0L);
                assertTrue(p.getContent().isEmpty());
                assertEquals(0, p.getTotalPages());
            });
        }

        /**
         * [RED-P04] pageSize ≤ 0 → IllegalArgumentException fırlatmalı.
         * Geçersiz sayfa boyutu sonsuz döngüye yol açabilir.
         */
        @Test
        @DisplayName("[RED-P04] pageSize ≤ 0 → IllegalArgumentException")
        void constructor_zerOrNegativePageSize_shouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> new GenericPaginator<>(items(5), 0, 0, 10L),
                "Sıfır pageSize IllegalArgumentException fırlatmalı");

            assertThrows(IllegalArgumentException.class,
                () -> new GenericPaginator<>(items(5), 0, -1, 10L),
                "Negatif pageSize IllegalArgumentException fırlatmalı");
        }

        /**
         * [RED-P05] totalElements < 0 → IllegalArgumentException fırlatmalı.
         */
        @Test
        @DisplayName("[RED-P05] totalElements < 0 → IllegalArgumentException")
        void constructor_negativeTotalElements_shouldThrow() {
            assertThrows(IllegalArgumentException.class,
                () -> new GenericPaginator<>(items(5), 0, 10, -1L),
                "Negatif totalElements IllegalArgumentException fırlatmalı");
        }
    }

    // =========================================================================
    // NESTED CLASS 2: Navigasyon (Sayfa Geçişi) Testleri
    // =========================================================================

    /**
     * Önceki/sonraki sayfa navigasyon sinyallerini doğrular.
     */
    @Nested
    @DisplayName("2 — Navigation State Tests (hasNext / hasPrevious)")
    class NavigationTests {

        /**
         * [RED-P06] İlk sayfada (page=0): hasPrevious=false, hasNext=true.
         */
        @Test
        @DisplayName("[RED-P06] İlk sayfa → hasPrevious=false, hasNext=true")
        void firstPage_shouldHaveNoPreviewAndHasNext() {
            GenericPaginator<String> p =
                new GenericPaginator<>(items(3), 0, 3, 10L); // 4 sayfa var

            assertFalse(p.hasPrevious(), "İlk sayfanın öncesi olmamalı");
            assertTrue(p.hasNext(),      "İlk sayfadan sonra sayfa var");
        }

        /**
         * [RED-P07] Son sayfada: hasNext=false, hasPrevious=true.
         */
        @Test
        @DisplayName("[RED-P07] Son sayfa → hasNext=false, hasPrevious=true")
        void lastPage_shouldHaveNxtAndHasPrevious() {
            // 10 eleman, pageSize=3 → pages: 0,1,2,3 → son sayfa: page=3
            GenericPaginator<String> p =
                new GenericPaginator<>(items(1), 3, 3, 10L);

            assertFalse(p.hasNext(),      "Son sayfanın sonrası olmamalı");
            assertTrue(p.hasPrevious(),   "Son sayfanın öncesi olmalı");
        }

        /**
         * [RED-P08] Orta sayfada: hem hasNext hem hasPrevious = true.
         */
        @Test
        @DisplayName("[RED-P08] Orta sayfa → hasNext=true, hasPrevious=true")
        void middlePage_shouldHaveBothNextAndPrevious() {
            GenericPaginator<String> p =
                new GenericPaginator<>(items(3), 1, 3, 10L); // sayfa 1 / toplam 4

            assertTrue(p.hasNext(),     "Orta sayfadan sonra sayfa var");
            assertTrue(p.hasPrevious(), "Orta sayfanın öncesi var");
        }

        /**
         * [RED-P09] Tek sayfa varsa: hasNext=false, hasPrevious=false.
         */
        @Test
        @DisplayName("[RED-P09] Tek sayfa → hasNext=false, hasPrevious=false")
        void singlePage_noPreviousNoNext() {
            GenericPaginator<String> p =
                new GenericPaginator<>(items(3), 0, 10, 3L); // 3 eleman, pageSize=10 → 1 sayfa

            assertFalse(p.hasNext(),     "Tek sayfada sonraki sayfa olmamalı");
            assertFalse(p.hasPrevious(), "Tek sayfada önceki sayfa olmamalı");
        }
    }

    // =========================================================================
    // NESTED CLASS 3: Veri İçeriği (Content) Testleri
    // =========================================================================

    /**
     * Sayfalayıcının içerik (content) alanını doğru taşıyıp taşımadığını kontrol eder.
     */
    @Nested
    @DisplayName("3 — Content Integrity Tests")
    class ContentTests {

        /**
         * [RED-P10] Verilen content listesi değiştirilmeden saklanmalı.
         */
        @Test
        @DisplayName("[RED-P10] Content listesi değiştirilmeden döndürülmeli")
        void content_shouldBePreservedUnchanged() {
            List<String> original = List.of("A", "B", "C");
            GenericPaginator<String> p = new GenericPaginator<>(original, 0, 3, 10L);

            assertEquals(original, p.getContent(), "Content listesi birebir korunmalı");
        }

        /**
         * [RED-P11] Generic T — Integer listesiyle çalışmalı.
         * GenericPaginator<T>'nin gerçek anlamda generic olduğunu kanıtlar.
         */
        @Test
        @DisplayName("[RED-P11] Generic T — Integer listesiyle doğru çalışmalı")
        void content_withIntegerType_shouldWork() {
            List<Integer> intData = List.of(10, 20, 30, 40, 50);
            GenericPaginator<Integer> p = new GenericPaginator<>(intData, 0, 5, 20L);

            assertEquals(5, p.getContent().size());
            assertEquals(20, p.getContent().get(1));  // İkinci eleman 20 olmalı
            assertEquals(4, p.getTotalPages());        // ceil(20/5)=4
        }

        /**
         * [RED-P12] isFirstPage() ve isLastPage() yardımcı metodları.
         */
        @Test
        @DisplayName("[RED-P12] isFirstPage() ve isLastPage() doğru çalışmalı")
        void isFirstAndLastPage_shouldReturnCorrectValues() {
            GenericPaginator<String> firstPage = new GenericPaginator<>(items(3), 0, 3, 9L);
            GenericPaginator<String> lastPage  = new GenericPaginator<>(items(3), 2, 3, 9L);
            GenericPaginator<String> midPage   = new GenericPaginator<>(items(3), 1, 3, 9L);

            assertTrue(firstPage.isFirstPage(),   "page=0 için isFirstPage() true olmalı");
            assertFalse(firstPage.isLastPage(),   "page=0, totalPages=3 için isLastPage() false");

            assertTrue(lastPage.isLastPage(),     "Son sayfa için isLastPage() true olmalı");
            assertFalse(lastPage.isFirstPage(),   "Son sayfa için isFirstPage() false olmalı");

            assertFalse(midPage.isFirstPage());
            assertFalse(midPage.isLastPage());
        }
    }

    // =========================================================================
    // NESTED CLASS 4: GenericResponse Entegrasyon Testi
    // =========================================================================

    /**
     * GenericPaginator'ın GenericResponse içinde sarmalanmasını doğrular.
     * Bu kullanım backend API'nin gerçek response formatıdır.
     */
    @Nested
    @DisplayName("4 — GenericResponse<GenericPaginator<T>> Entegrasyon")
    class IntegrationWithGenericResponseTests {

        /**
         * [RED-P13] Sayfalı veri, GenericResponse içine sarmalanabilmeli.
         * Backend'in döndüreceği gerçek format: GenericResponse<GenericPaginator<Item>>
         */
        @Test
        @DisplayName("[RED-P13] GenericResponse<GenericPaginator<String>> sarmalama çalışmalı")
        void paginatedResponse_wrappedInGenericResponse_shouldWork() {
            // ARRANGE
            List<String> pageContent = List.of("Ürün-A", "Ürün-B", "Ürün-C");
            GenericPaginator<String> paginator =
                new GenericPaginator<>(pageContent, 0, 3, 15L);

            // ACT — GenericResponse<GenericPaginator<String>>
            GenericResponse<GenericPaginator<String>> response =
                GenericResponse.success("Ürünler listelendi", paginator);

            // ASSERT
            assertAll("Sarmalı paginated response",
                () -> assertEquals("SUCCESS", response.getStatus()),
                () -> assertNotNull(response.getData()),
                () -> assertEquals(5, response.getData().getTotalPages()),   // ceil(15/3)=5
                () -> assertEquals(15L, response.getData().getTotalElements()),
                () -> assertEquals(3, response.getData().getContent().size()),
                () -> assertTrue(response.getData().hasNext()),
                () -> assertFalse(response.getData().hasPrevious())
            );
        }
    }
}

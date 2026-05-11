package com.inventory.common.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║         Generic<T> Yapı: GenericPaginator<T>                        ║
 * ║                                                                      ║
 * ║  TDD GREEN aşaması — GenericPaginatorTest testlerini geçecek         ║
 * ║  minimum ve temiz implementasyon.                                    ║
 * ║                                                                      ║
 * ║  COMMIT NOTU (Üye 2):                                                ║
 * ║    feat: GenericPaginator<T> implementation (GREEN phase)           ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 *
 * <p>Tüm liste endpoint'lerini sayfalayan generic yardımcı sınıf.
 * API yanıtında kullanım örneği:
 *
 * <pre>
 * {
 *   "content":       [ {...}, {...} ],
 *   "currentPage":   0,
 *   "pageSize":      20,
 *   "totalElements": 150,
 *   "totalPages":    8,
 *   "first":         true,
 *   "last":          false
 * }
 * </pre>
 *
 * <p>SOLID — Single Responsibility:
 * Bu sınıf yalnızca sayfalama meta-verisini hesaplar ve taşır.
 * Veritabanı sorgusu veya filtreleme bu sınıfın sorumluluğu DEĞİLDİR.
 *
 * @param <T> Sayfalanan öğelerin tipi
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @version 1.0.0
 * @since 2026-05-12
 */
public class GenericPaginator<T> {

    // =========================================================================
    // ALANLAR
    // =========================================================================

    /** Mevcut sayfanın içeriği (değiştirilemeyen kopya). */
    private final List<T> content;

    /** Sıfır-tabanlı mevcut sayfa indeksi. */
    private final int currentPage;

    /** Sayfa başına düşen maksimum eleman sayısı. */
    private final int pageSize;

    /** Tüm sayfalardaki toplam eleman sayısı (COUNT sorgusu sonucu). */
    private final long totalElements;

    /** Hesaplanmış toplam sayfa sayısı: ceil(totalElements / pageSize). */
    private final int totalPages;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Sayfalayıcı oluşturur ve meta-veriyi hesaplar.
     *
     * @param content       Mevcut sayfanın öğeleri (null veya boş olabilir)
     * @param currentPage   Sıfır-tabanlı sayfa numarası (≥ 0)
     * @param pageSize      Sayfa boyutu (> 0 olmalı)
     * @param totalElements Toplam kayıt sayısı (≥ 0 olmalı)
     * @throws IllegalArgumentException pageSize ≤ 0 veya totalElements < 0 ise
     */
    public GenericPaginator(List<T> content, int currentPage, int pageSize, long totalElements) {
        // ---- Girdi doğrulama ----
        if (pageSize <= 0) {
            throw new IllegalArgumentException(
                "pageSize pozitif olmalıdır, verildi: " + pageSize);
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException(
                "totalElements negatif olamaz, verildi: " + totalElements);
        }

        // ---- Savunmacı kopya (defensive copy) — orijinal listeyi korur ----
        this.content       = content != null
                             ? Collections.unmodifiableList(new ArrayList<>(content))
                             : Collections.emptyList();
        this.currentPage   = currentPage;
        this.pageSize      = pageSize;
        this.totalElements = totalElements;

        // ---- Sayfa sayısı hesaplama ----
        // totalElements=0 için özel durum: 0 sayfa (bölme hatasından kaçın)
        if (totalElements == 0) {
            this.totalPages = 0;
        } else {
            // Math.ceil(totalElements / pageSize) — integer arithmetic'ten kaçın
            this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        }
    }

    // =========================================================================
    // NAVIGASYON YARDIMCI METODLARI
    // =========================================================================

    /**
     * Bir sonraki sayfa mevcutsa true döner.
     *
     * @return currentPage, totalPages - 1'den küçükse true
     */
    public boolean hasNext() {
        return currentPage < totalPages - 1;
    }

    /**
     * Önceki sayfa mevcutsa true döner.
     *
     * @return currentPage > 0 ise true
     */
    public boolean hasPrevious() {
        return currentPage > 0;
    }

    /**
     * Mevcut sayfa ilk sayfa mı?
     *
     * @return currentPage == 0 ise true
     */
    public boolean isFirstPage() {
        return currentPage == 0;
    }

    /**
     * Mevcut sayfa son sayfa mı?
     *
     * @return currentPage == totalPages - 1 ise true (tek sayfa durumu dahil)
     */
    public boolean isLastPage() {
        return totalPages == 0 || currentPage >= totalPages - 1;
    }

    // =========================================================================
    // GETTER'LAR (Jackson serializasyonu ve testler için)
    // =========================================================================

    public List<T> getContent() {
        return content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    // =========================================================================
    // equals / hashCode / toString
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenericPaginator)) return false;
        GenericPaginator<?> that = (GenericPaginator<?>) o;
        return currentPage   == that.currentPage
            && pageSize      == that.pageSize
            && totalElements == that.totalElements
            && totalPages    == that.totalPages
            && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, currentPage, pageSize, totalElements, totalPages);
    }

    @Override
    public String toString() {
        return "GenericPaginator{"
            + "currentPage="   + currentPage
            + ", pageSize="    + pageSize
            + ", totalPages="  + totalPages
            + ", totalElements=" + totalElements
            + ", contentSize=" + content.size()
            + '}';
    }
}

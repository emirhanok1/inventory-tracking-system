package com.inventory.mobileclient;

import android.app.Application;

/**
 * InventoryApp — Android Application Sınıfı
 *
 * <p>Uygulama genelinde tek seferlik başlatma işlemleri burada yapılır.
 * Retrofit istemcisi, loglama konfigürasyonu gibi singleton'lar
 * bu katmanda hayata geçirilir.</p>
 *
 * <h3>Sorumluluklar (SRP — Single Responsibility Principle):</h3>
 * <ul>
 *   <li>HTTP client (Retrofit) singleton konfigürasyonu</li>
 *   <li>Global exception handler kurulumu (uncaught exception loglama)</li>
 *   <li>Uygulama başlangıç durumunun başlatılması</li>
 * </ul>
 *
 * @author Üye 2 — TBL324 Inventory Tracking System
 * @version 1.0.0
 */
public class InventoryApp extends Application {

    /** Uygulama genelinde kullanılacak loglama etiketi */
    public static final String TAG = "InventoryApp";

    /** Singleton uygulama referansı (Context gerektiren yerlerde kullanılır) */
    private static InventoryApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // TODO (Faz 2): RetrofitClient.init(this) — Gateway URL ile başlat
        // TODO (Faz 2): SharedPrefsManager.init(this) — Token yönetimi
    }

    /**
     * Uygulama singleton'ına erişim sağlar.
     * @return Aktif {@link InventoryApp} instance'ı
     */
    public static InventoryApp getInstance() {
        return instance;
    }
}

package com.example.cryptotracker

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

/**
 * CryptoPriceService — Started + Bound сервис.
 *
 * • Started  → живёт независимо от Activity (startService / stopService)
 * • Bound    → Activity получает ссылку на сервис и может вызвать fetchNow()
 *
 * Периодически опрашивает CryptoCompare API и рассылает результат через
 * LocalBroadcastManager.
 */
class CryptoPriceService : Service() {

    companion object {
        private const val TAG = "CryptoPriceService"

        // Монета для отслеживания
        const val CRYPTO_SYMBOL = "BTC"

        // Валюты для конвертации
        private const val TSYMS = "USD,RUB,JPY"

        // URL запроса: https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=USD,RUB,JPY
        private const val BASE_URL =
            "https://min-api.cryptocompare.com/data/price?fsym=$CRYPTO_SYMBOL&tsyms=$TSYMS"

        // Интервал обновления (мс)
        private const val REFRESH_INTERVAL_MS = 15_000L

        // Actions для LocalBroadcast
        const val ACTION_PRICE_UPDATE = "com.example.cryptotracker.PRICE_UPDATE"
        const val ACTION_ERROR = "com.example.cryptotracker.ERROR"

        // Extras
        const val EXTRA_USD = "extra_usd"
        const val EXTRA_RUB = "extra_rub"
        const val EXTRA_JPY = "extra_jpy"
        const val EXTRA_SYMBOL = "extra_symbol"
        const val EXTRA_ERROR = "extra_error"
    }

    // ------------------------------------------------------------------ Binder

    inner class LocalBinder : Binder() {
        fun getService(): CryptoPriceService = this@CryptoPriceService
    }

    private val binder = LocalBinder()

    // ------------------------------------------------------------------ OkHttp

    private val okHttpClient: OkHttpClient by lazy {
        // Принудительно используем системные корневые сертификаты Android.
        // Это решает "Chain validation failed" на устройствах, где OkHttp
        // не находит нужный CA-bundle по умолчанию.
        val trustManager = javax.net.ssl.TrustManagerFactory
            .getInstance(javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm())
            .also { it.init(null as java.security.KeyStore?) }
            .trustManagers
            .filterIsInstance<javax.net.ssl.X509TrustManager>()
            .first()

        val sslContext = javax.net.ssl.SSLContext.getInstance("TLS").also {
            it.init(null, arrayOf(trustManager), null)
        }

        OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .addInterceptor(
                okhttp3.logging.HttpLoggingInterceptor().apply {
                    level = okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
                }
            )
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    // ------------------------------------------------------------------ Периодический опрос

    private val handler = Handler(Looper.getMainLooper())

    private val fetchRunnable = object : Runnable {
        override fun run() {
            fetchPrice()
            handler.postDelayed(this, REFRESH_INTERVAL_MS)
        }
    }

    // ------------------------------------------------------------------ Service lifecycle

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    /**
     * Вызывается при startService(). Запускаем периодический опрос.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        handler.removeCallbacks(fetchRunnable)   // сбросить предыдущий, если был
        handler.post(fetchRunnable)              // запустить немедленно
        return START_STICKY                       // система перезапустит сервис при убийстве
    }

    /**
     * Вызывается при bindService(). Возвращаем Binder для прямого взаимодействия.
     */
    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return true   // разрешаем повторную привязку (onRebind)
    }

    override fun onRebind(intent: Intent?) {
        Log.d(TAG, "onRebind")
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(fetchRunnable)
        okHttpClient.dispatcher.cancelAll()
        Log.d(TAG, "onDestroy — сервис остановлен")
    }

    // ------------------------------------------------------------------ Public API (для Bound-клиентов)

    /**
     * Немедленно выполнить запрос (вызывается из Activity через Binder).
     */
    fun fetchNow() {
        fetchPrice()
    }

    // ------------------------------------------------------------------ Сетевой запрос

    private fun fetchPrice() {
        val request = Request.Builder()
            .url(BASE_URL)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Ошибка сети: ${e.message}")
                broadcastError(e.message ?: "Ошибка сети")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    broadcastError("HTTP ${response.code}: ${response.message}")
                    return
                }

                val body = response.body?.string()
                if (body.isNullOrBlank()) {
                    broadcastError("Пустой ответ от сервера")
                    return
                }

                Log.d(TAG, "Ответ API: $body")
                parseAndBroadcast(body)
            }
        })
    }

    // ------------------------------------------------------------------ JSON-парсинг

    /**
     * Пример ответа: {"USD":1930.75,"JPY":289378.99,"RUB":195852.18}
     */
    private fun parseAndBroadcast(json: String) {
        try {
            val obj = JSONObject(json)
            val usd = obj.getDouble("USD")
            val rub = obj.getDouble("RUB")
            val jpy = obj.getDouble("JPY")
            broadcastPrices(CRYPTO_SYMBOL, usd, rub, jpy)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка парсинга: ${e.message}")
            broadcastError("Ошибка парсинга ответа: ${e.message}")
        }
    }

    // ------------------------------------------------------------------ Broadcast helpers

    private fun broadcastPrices(symbol: String, usd: Double, rub: Double, jpy: Double) {
        val intent = Intent(ACTION_PRICE_UPDATE).apply {
            putExtra(EXTRA_SYMBOL, symbol)
            putExtra(EXTRA_USD, usd)
            putExtra(EXTRA_RUB, rub)
            putExtra(EXTRA_JPY, jpy)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun broadcastError(message: String) {
        val intent = Intent(ACTION_ERROR).apply {
            putExtra(EXTRA_ERROR, message)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}

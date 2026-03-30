package com.example.cryptotracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import kotlin.math.abs

class CryptoPriceService : Service() {

    companion object {
        private const val TAG = "CryptoPriceService"

        const val CRYPTO_SYMBOL = "BTC"
        private const val TSYMS = "USD,RUB,JPY"
        private const val BASE_URL =
            "https://min-api.cryptocompare.com/data/price?fsym=$CRYPTO_SYMBOL&tsyms=$TSYMS"

        private const val REFRESH_INTERVAL_MS = 15_000L

        // Порог изменения цены (в USD) по умолчанию
        const val PRICE_CHANGE_THRESHOLD_USD = 100.0

        // Notification
        private const val CHANNEL_ID   = "crypto_price_alerts"
        private const val CHANNEL_NAME = "Котировки криптовалют"
        private const val NOTIFICATION_ID = 1001

        // LocalBroadcast
        const val ACTION_PRICE_UPDATE = "com.example.cryptotracker.PRICE_UPDATE"
        const val ACTION_ERROR        = "com.example.cryptotracker.ERROR"

        const val EXTRA_USD    = "extra_usd"
        const val EXTRA_RUB    = "extra_rub"
        const val EXTRA_JPY    = "extra_jpy"
        const val EXTRA_SYMBOL = "extra_symbol"
        const val EXTRA_ERROR  = "extra_error"
    }

    // ------------------------------------------------------------------ Binder

    inner class LocalBinder : Binder() {
        fun getService(): CryptoPriceService = this@CryptoPriceService
    }

    private val binder = LocalBinder()

    // ------------------------------------------------------------------ Порог уведомления

    private var thresholdUsd: Double = PRICE_CHANGE_THRESHOLD_USD

    fun applyThreshold(usd: Double) {
        thresholdUsd = usd
        lastNotifiedPriceUsd = -1.0
        Log.d(TAG, "Новый порог: $$usd")
    }

    // ------------------------------------------------------------------ Состояние цены

    /** Последняя цена в USD, при которой отправлялось уведомление */
    private var lastNotifiedPriceUsd: Double = -1.0

    /** Последняя полученная цена (для отображения дельты в следующем уведомлении) */
    private var previousPriceUsd: Double = -1.0

    // ------------------------------------------------------------------ OkHttp

    private val okHttpClient: OkHttpClient by lazy {
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

    // ------------------------------------------------------------------ Lifecycle

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        handler.removeCallbacks(fetchRunnable)
        handler.post(fetchRunnable)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onUnbind(intent: Intent?): Boolean = true

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(fetchRunnable)
        okHttpClient.dispatcher.cancelAll()
        Log.d(TAG, "onDestroy")
    }

    // ------------------------------------------------------------------ Public API

    fun fetchNow() = fetchPrice()

    /** Позволяет Activity обновить порог прямо через Binder */
    fun setThreshold(usd: Double) {
        // порог сейчас константа — в будущем можно сделать var
    }

    // ------------------------------------------------------------------ Сетевой запрос

    private fun fetchPrice() {
        val request = Request.Builder().url(BASE_URL).build()

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

    // ------------------------------------------------------------------ Парсинг

    private fun parseAndBroadcast(json: String) {
        try {
            val obj = JSONObject(json)
            val usd = obj.getDouble("USD")
            val rub = obj.getDouble("RUB")
            val jpy = obj.getDouble("JPY")

            checkAndNotify(usd)

            previousPriceUsd = usd
            broadcastPrices(CRYPTO_SYMBOL, usd, rub, jpy)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка парсинга: ${e.message}")
            broadcastError("Ошибка парсинга: ${e.message}")
        }
    }

    // ------------------------------------------------------------------ Логика уведомлений

    /**
     * Отправляет уведомление, если цена изменилась на PRICE_CHANGE_THRESHOLD_USD
     * относительно последнего уведомления.
     */
    private fun checkAndNotify(currentUsd: Double) {
        // Первое значение — запоминаем как базу, уведомление не нужно
        if (lastNotifiedPriceUsd < 0) {
            lastNotifiedPriceUsd = currentUsd
            return
        }

        val delta = currentUsd - lastNotifiedPriceUsd
        if (abs(delta) >= thresholdUsd) {
            val isUp = delta > 0
            sendPriceNotification(currentUsd, delta, isUp)
            lastNotifiedPriceUsd = currentUsd
        }
    }

    private fun sendPriceNotification(currentUsd: Double, delta: Double, isUp: Boolean) {
        val arrow     = if (isUp) "↑" else "↓"
        val sign      = if (isUp) "+" else ""
        val direction = if (isUp) "вырос" else "упал"

        val title = "$CRYPTO_SYMBOL $arrow ${"$"}${"%.2f".format(currentUsd)}"
        val text  = "Курс $direction на $sign${"$"}${"%.2f".format(delta)}"

        val openAppIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent)
            .build()

        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, notification)

        Log.d(TAG, "Уведомление: $title — $text")
    }

    // ------------------------------------------------------------------ Notification channel

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Оповещения об изменении курса $CRYPTO_SYMBOL"
                enableLights(true)
                enableVibration(true)
            }
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
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

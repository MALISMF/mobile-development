package com.example.cryptotracker

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.cryptotracker.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Флаг привязки к сервису
    private var isBound = false

    // Ссылка на сервис (для управления через Bind)
    private var cryptoService: CryptoPriceService? = null

    // ServiceConnection — обратные вызовы привязки
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as CryptoPriceService.LocalBinder
            cryptoService = localBinder.getService()
            isBound = true
            updateButtonState(true)
            Toast.makeText(this@MainActivity, "Сервис подключён", Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            cryptoService = null
            isBound = false
            updateButtonState(false)
        }
    }

    // BroadcastReceiver — получаем котировки из сервиса
    private val priceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                CryptoPriceService.ACTION_PRICE_UPDATE -> {
                    val usd = intent.getDoubleExtra(CryptoPriceService.EXTRA_USD, 0.0)
                    val rub = intent.getDoubleExtra(CryptoPriceService.EXTRA_RUB, 0.0)
                    val jpy = intent.getDoubleExtra(CryptoPriceService.EXTRA_JPY, 0.0)
                    val symbol = intent.getStringExtra(CryptoPriceService.EXTRA_SYMBOL) ?: "ETH"
                    displayPrices(symbol, usd, rub, jpy)
                }

                CryptoPriceService.ACTION_ERROR -> {
                    val error = intent.getStringExtra(CryptoPriceService.EXTRA_ERROR) ?: "Неизвестная ошибка"
                    showError(error)
                }
            }
        }
    }

    // ------------------------------------------------------------------ lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        registerPriceReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterPriceReceiver()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    // ------------------------------------------------------------------ UI setup

    private fun setupUI() {
        // Запустить / остановить сервис
        binding.btnStartService.setOnClickListener { startCryptoService() }
        binding.btnStopService.setOnClickListener  { stopCryptoService() }

        // Ручное обновление
        binding.btnRefresh.setOnClickListener {
            cryptoService?.fetchNow()
                ?: Toast.makeText(this, "Сервис не запущен", Toast.LENGTH_SHORT).show()
        }

        updateButtonState(false)
    }

    // ------------------------------------------------------------------ service control

    private fun startCryptoService() {
        val intent = Intent(this, CryptoPriceService::class.java)
        startService(intent)          // запускаем как Foreground-capable Started Service
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)   // привязываемся для управления
        binding.tvStatus.text = "Подключаемся к сервису…"
    }

    private fun stopCryptoService() {
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        val intent = Intent(this, CryptoPriceService::class.java)
        stopService(intent)
        cryptoService = null
        updateButtonState(false)
        binding.tvStatus.text = "Сервис остановлен"
        clearPrices()
    }

    // ------------------------------------------------------------------ display helpers

    private fun displayPrices(symbol: String, usd: Double, rub: Double, jpy: Double) {
        binding.tvUsd.text = "$%.2f".format(usd)
        binding.tvRub.text = "%.2f ₽".format(rub)
        binding.tvJpy.text = "¥%.2f".format(jpy)

        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        binding.tvLastUpdate.text = "Последнее обновление: $time"
        binding.tvStatus.text = "Данные получены"
        binding.cardPrices.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.tvError.text = "Ошибка: $message"
        binding.tvError.visibility = View.VISIBLE
        binding.tvStatus.text = "Произошла ошибка"
    }

    private fun clearPrices() {
        binding.cardPrices.visibility = View.GONE
        binding.tvError.visibility = View.GONE
    }

    private fun updateButtonState(serviceRunning: Boolean) {
        binding.btnStartService.isEnabled = !serviceRunning
        binding.btnStopService.isEnabled = serviceRunning
        binding.btnRefresh.isEnabled = serviceRunning
    }

    // ------------------------------------------------------------------ BroadcastReceiver

    private fun registerPriceReceiver() {
        val filter = IntentFilter().apply {
            addAction(CryptoPriceService.ACTION_PRICE_UPDATE)
            addAction(CryptoPriceService.ACTION_ERROR)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(priceReceiver, filter)
    }

    private fun unregisterPriceReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(priceReceiver)
    }
}

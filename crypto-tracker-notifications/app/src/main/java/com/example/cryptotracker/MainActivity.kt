package com.example.cryptotracker

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.cryptotracker.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isBound = false
    private var cryptoService: CryptoPriceService? = null

    // Текущий порог уведомления (в USD)
    private var currentThreshold = CryptoPriceService.PRICE_CHANGE_THRESHOLD_USD

    // ------------------------------------------------------------------ Запрос разрешения на уведомления (Android 13+)

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(this, "Уведомления включены", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Уведомления отключены в настройках", Toast.LENGTH_LONG).show()
            }
        }

    // ------------------------------------------------------------------ ServiceConnection

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            cryptoService = (binder as CryptoPriceService.LocalBinder).getService()
            isBound = true
            updateButtonState(true)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            cryptoService = null
            isBound = false
            updateButtonState(false)
        }
    }

    // ------------------------------------------------------------------ BroadcastReceiver

    private val priceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                CryptoPriceService.ACTION_PRICE_UPDATE -> {
                    val usd = intent.getDoubleExtra(CryptoPriceService.EXTRA_USD, 0.0)
                    val rub = intent.getDoubleExtra(CryptoPriceService.EXTRA_RUB, 0.0)
                    val jpy = intent.getDoubleExtra(CryptoPriceService.EXTRA_JPY, 0.0)
                    displayPrices(usd, rub, jpy)
                }
                CryptoPriceService.ACTION_ERROR -> {
                    val error = intent.getStringExtra(CryptoPriceService.EXTRA_ERROR) ?: "Неизвестная ошибка"
                    showError(error)
                }
            }
        }
    }

    // ------------------------------------------------------------------ Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        registerPriceReceiver()
        askNotificationPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterPriceReceiver()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    // ------------------------------------------------------------------ UI

    private fun setupUI() {
        binding.btnStartService.setOnClickListener { startCryptoService() }
        binding.btnStopService.setOnClickListener  { stopCryptoService() }
        binding.btnRefresh.setOnClickListener {
            cryptoService?.fetchNow()
                ?: Toast.makeText(this, "Сервис не запущен", Toast.LENGTH_SHORT).show()
        }

        binding.btnApplyThreshold.setOnClickListener { applyThreshold() }

        updateButtonState(false)
    }

    // ------------------------------------------------------------------ Порог

    private fun applyThreshold() {
        val input = binding.etThreshold.text.toString().trim()
        val value = input.toDoubleOrNull()
        if (value == null || value <= 0) {
            Toast.makeText(this, "Введите корректное число больше 0", Toast.LENGTH_SHORT).show()
            return
        }
        currentThreshold = value
        // Передаём новый порог в сервис через его публичный метод
        cryptoService?.applyThreshold(value)
        binding.tvThresholdInfo.text = "Уведомление придёт при изменении курса на \$${"%.0f".format(value)}"
        Toast.makeText(this, "Порог обновлён: \$${"%.0f".format(value)}", Toast.LENGTH_SHORT).show()

        // Скрываем клавиатуру
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etThreshold.windowToken, 0)
    }

    // ------------------------------------------------------------------ Сервис

    private fun startCryptoService() {
        val intent = Intent(this, CryptoPriceService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        binding.tvStatus.text = "Подключаемся…"
    }

    private fun stopCryptoService() {
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        stopService(Intent(this, CryptoPriceService::class.java))
        cryptoService = null
        updateButtonState(false)
        binding.tvStatus.text = "Сервис остановлен"
        clearPrices()
    }

    // ------------------------------------------------------------------ Display

    private fun displayPrices(usd: Double, rub: Double, jpy: Double) {
        binding.tvUsd.text = "\$%.2f".format(usd)
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
        binding.btnStopService.isEnabled  = serviceRunning
        binding.btnRefresh.isEnabled      = serviceRunning
    }

    // ------------------------------------------------------------------ Разрешение уведомлений

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // ------------------------------------------------------------------ BroadcastReceiver reg

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

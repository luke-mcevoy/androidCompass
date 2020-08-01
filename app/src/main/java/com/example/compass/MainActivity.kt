package com.example.compass

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.compass.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val direction = intent?.getStringExtra(CompassService.KEY_DIRECTION)
            val angle = intent?.getDoubleExtra(CompassService.KEY_ANGLE, 0.0)
            val angleWithDirection = "$angle $direction"
            binding.directionTextView.text = angleWithDirection

            if (angle != null) {
                binding.compassImageView.rotation = angle.toFloat() * -1
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                IntentFilter(CompassService.KEY_ON_SENSOR_CHANGED_ACTION))

    }

    override fun onResume() {
        super.onResume()
        startForegroundServiceForSensors(false)

    }

    private fun startForegroundServiceForSensors(background: Boolean) {
        val compassIntent = Intent(this, CompassService::class.java)
        compassIntent.putExtra(CompassService.KEY_BACKGROND, background)

        ContextCompat.startForegroundService(this, compassIntent)
    }

    override fun onPause() {
        super.onPause()
        startForegroundServiceForSensors(true)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }
}
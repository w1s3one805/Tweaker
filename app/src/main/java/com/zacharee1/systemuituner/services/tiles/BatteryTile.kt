package com.zacharee1.systemuituner.services.tiles

import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.os.BatteryManager
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.content.ContextCompat
import androidx.core.service.quicksettings.PendingIntentActivityWrapper
import androidx.core.service.quicksettings.TileServiceCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.safeUpdateTile

@TargetApi(Build.VERSION_CODES.N)
class BatteryTile : TileService() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                setLevel(intent)
            }
        }
    }

    override fun onStartListening() {
        super.onStartListening()

        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
            ContextCompat.RECEIVER_EXPORTED,
        )

        qsTile?.state = Tile.STATE_ACTIVE
        qsTile?.safeUpdateTile()
    }

    override fun onStopListening() {
        super.onStopListening()

        try {
            unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onClick() {
        val intentBatteryUsage = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
        intentBatteryUsage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntentActivityWrapper(
            this, 101, intentBatteryUsage,
            PendingIntent.FLAG_UPDATE_CURRENT, false,
        )

        try {
            TileServiceCompat.startActivityAndCollapse(this, pendingIntent)
        } catch (_: Exception) {}

        super.onClick()
    }

    private fun setLevel(intent: Intent) {
        val batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val batteryCharging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING || batteryStatus == BatteryManager.BATTERY_STATUS_FULL
        val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)

        val resId: Int = when {
            batteryLevel >= 95 -> if (batteryCharging) R.drawable.battery_charging_100 else R.drawable.battery_full
            batteryLevel >= 85 -> if (batteryCharging) R.drawable.battery_charging_90 else R.drawable.battery_90
            batteryLevel >= 75 -> if (batteryCharging) R.drawable.battery_charging_80 else R.drawable.battery_80
            batteryLevel >= 65 -> if (batteryCharging) R.drawable.battery_charging_70 else R.drawable.battery_70
            batteryLevel >= 55 -> if (batteryCharging) R.drawable.battery_charging_60 else R.drawable.battery_60
            batteryLevel >= 45 -> if (batteryCharging) R.drawable.battery_charging_50 else R.drawable.battery_50
            batteryLevel >= 35 -> if (batteryCharging) R.drawable.battery_charging_40 else R.drawable.battery_40
            batteryLevel >= 25 -> if (batteryCharging) R.drawable.battery_charging_30 else R.drawable.battery_30
            batteryLevel >= 15 -> if (batteryCharging) R.drawable.battery_charging_20 else R.drawable.battery_20
            batteryLevel >= 5 -> if (batteryCharging) R.drawable.battery_charging_10 else R.drawable.battery_10
            else -> if (batteryCharging) R.drawable.battery_charging_10 else R.drawable.ic_baseline_battery_alert_24
        }

        qsTile?.icon = Icon.createWithResource(this, resId)
        qsTile?.label = "$batteryLevel%"
        qsTile?.safeUpdateTile()
    }
}
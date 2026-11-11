package com.example.donnen

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val ramBefore = freeRamMb(am)

        val homePackages = packageManager
            .queryIntentActivities(
                Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
                0
            )
            .map { it.activityInfo.packageName }
            .toSet()
        val skip = homePackages + packageName + "com.android.systemui"

        packageManager.getInstalledApplications(0)
            .map { it.packageName }
            .filter { it !in skip }
            .forEach { am.killBackgroundProcesses(it) }

        val appContext = applicationContext
        Handler(Looper.getMainLooper()).postDelayed({
            val ramAfter = freeRamMb(am)
            val freed = (ramAfter - ramBefore).coerceAtLeast(0)
            Toast.makeText(
                appContext,
                "Đã dọn nền xong. RAM trống: $ramAfter MB (+$freed MB)",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }, 1500)
    }

    private fun freeRamMb(am: ActivityManager): Long {
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        return info.availMem / (1024 * 1024)
    }
}

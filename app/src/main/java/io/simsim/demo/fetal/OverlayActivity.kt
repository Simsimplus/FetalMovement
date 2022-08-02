package io.simsim.demo.fetal

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class OverlayActivity : AppCompatActivity() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkOverlayPermission()
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val i =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:$packageName".toUri())
            launcher.launch(i)
        } else {
            startService(Intent(this, OverlayService::class.java))
            finish()
        }
    }
}

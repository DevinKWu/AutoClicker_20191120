package com.chiaruy.autoclick

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.chiaruy.autoclick.AutoService

class MainActivity : AppCompatActivity() {
    var TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mStart: Button? = findViewById(R.id.start)

        mStart?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(applicationContext)) {
                    var intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)

                    intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    Toast.makeText(baseContext, getString(R.string.start_app), Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                } else {
                    startService(Intent(this, AutoService::class.java))
                    finish()
                }
            }
        }
    }
}
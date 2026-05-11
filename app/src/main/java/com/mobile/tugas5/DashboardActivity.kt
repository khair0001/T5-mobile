package com.mobile.tugas5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NAME = "extra_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val userName = intent.getStringExtra(EXTRA_NAME).orEmpty()
        val tvName = findViewById<TextView>(R.id.tvName)
        tvName.text = userName

        // Button untuk navigasi
        val btnDataPasien = findViewById<Button>(R.id.btnDataPasien)
        val btnProfile = findViewById<Button>(R.id.btnProfile)

        btnDataPasien.setOnClickListener {
            startActivity(Intent(this, DataPasienActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}

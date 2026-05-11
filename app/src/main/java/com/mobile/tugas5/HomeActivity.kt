package com.mobile.tugas5

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.tugas5.adapter.PasienAdapter
import com.mobile.tugas5.network.RetrofitClient
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NAME = "extra_name"
    }

    private lateinit var rvPasien: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: PasienAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inisialisasi view
        rvPasien = findViewById(R.id.rvPasien)
        progressBar = findViewById(R.id.progressBar)

        // Tampilkan nama user
        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()
        val tvName = findViewById<TextView>(R.id.tvName)
        tvName.text = name

        // Setup RecyclerView
        adapter = PasienAdapter()
        rvPasien.layoutManager = LinearLayoutManager(this)
        rvPasien.adapter = adapter

        // Setup tombol logout
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        // Load data pasien
        loadDataPasien()
    }

    private fun loadDataPasien() {
        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.apiService.getPasien()

                if (response.isSuccessful) {
                    val pasienList = response.body()?.data ?: emptyList()
                    adapter.setData(pasienList)

                    if (pasienList.isEmpty()) {
                        showMessage("Tidak ada data pasien")
                    }
                } else {
                    val errorMessage = response.body()?.message ?: "Gagal memuat data pasien"
                    showMessage(errorMessage)
                }
            } catch (e: Exception) {
                showMessage("Tidak dapat terhubung ke server: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Logout") { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        // Hapus token dari SharedPreferences
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        prefs.edit().remove("token").apply()

        // Kembali ke MainActivity dan tutup semua Activity di atasnya
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
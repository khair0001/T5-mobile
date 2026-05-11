package com.mobile.tugas5

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mobile.tugas5.model.Pasien
import com.mobile.tugas5.network.RetrofitClient
import kotlinx.coroutines.launch

class AddPasienActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etTelepon: EditText
    private lateinit var etTglLahir: EditText
    private lateinit var etJenisKelamin: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnBatal: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pasien)

        // Inisialisasi view
        etNama = findViewById(R.id.etNama)
        etAlamat = findViewById(R.id.etAlamat)
        etTelepon = findViewById(R.id.etTelepon)
        etTglLahir = findViewById(R.id.etTglLahir)
        etJenisKelamin = findViewById(R.id.etJenisKelamin)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnBatal = findViewById(R.id.btnBatal)
        progressBar = findViewById(R.id.progressBar)

        btnSimpan.setOnClickListener {
            addPasien()
        }

        btnBatal.setOnClickListener {
            finish()
        }
    }

    private fun addPasien() {
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val telepon = etTelepon.text.toString().trim()
        val tglLahir = etTglLahir.text.toString().trim()
        val jenisKelamin = etJenisKelamin.text.toString().trim()

        if (nama.isEmpty() || alamat.isEmpty() || telepon.isEmpty()) {
            showMessage("Semua field wajib diisi")
            return
        }

        lifecycleScope.launch {
            showLoading(true)
            try {
                val pasien = Pasien(
                    id = 0, // ID akan dihasilkan server
                    nama = nama,
                    alamat = alamat,
                    no_telepon = telepon,
                    tanggal_lahir = tglLahir.ifEmpty { null },
                    jenis_kelamin = jenisKelamin.ifEmpty { null }
                )

                val response = RetrofitClient.apiService.createPasien(pasien)

                if (response.isSuccessful) {
                    showMessage("Pasien berhasil ditambahkan")
                    finish()
                } else {
                    showMessage("Gagal menambahkan pasien")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSimpan.isEnabled = !isLoading
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

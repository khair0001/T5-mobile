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

class EditPasienActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PASIEN_ID = "extra_pasien_id"
        const val EXTRA_NAMA = "extra_nama"
        const val EXTRA_ALAMAT = "extra_alamat"
        const val EXTRA_TELEPON = "extra_telepon"
        const val EXTRA_TGL_LAHIR = "extra_tgl_lahir"
        const val EXTRA_JENIS_KELAMIN = "extra_jenis_kelamin"
    }

    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etTelepon: EditText
    private lateinit var etTglLahir: EditText
    private lateinit var etJenisKelamin: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnBatal: Button
    private lateinit var progressBar: ProgressBar
    private var pasienId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pasien)

        // Inisialisasi view
        etNama = findViewById(R.id.etNama)
        etAlamat = findViewById(R.id.etAlamat)
        etTelepon = findViewById(R.id.etTelepon)
        etTglLahir = findViewById(R.id.etTglLahir)
        etJenisKelamin = findViewById(R.id.etJenisKelamin)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnBatal = findViewById(R.id.btnBatal)
        progressBar = findViewById(R.id.progressBar)

        // Ambil data dari intent
        pasienId = intent.getIntExtra(EXTRA_PASIEN_ID, 0)
        etNama.setText(intent.getStringExtra(EXTRA_NAMA))
        etAlamat.setText(intent.getStringExtra(EXTRA_ALAMAT))
        etTelepon.setText(intent.getStringExtra(EXTRA_TELEPON))
        etTglLahir.setText(intent.getStringExtra(EXTRA_TGL_LAHIR) ?: "")
        etJenisKelamin.setText(intent.getStringExtra(EXTRA_JENIS_KELAMIN) ?: "")

        btnSimpan.setOnClickListener {
            updatePasien()
        }

        btnBatal.setOnClickListener {
            finish()
        }
    }

    private fun updatePasien() {
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
                    id = pasienId,
                    nama = nama,
                    alamat = alamat,
                    no_telepon = telepon,
                    tanggal_lahir = tglLahir.ifEmpty { null },
                    jenis_kelamin = jenisKelamin.ifEmpty { null }
                )

                val response = RetrofitClient.apiService.updatePasien(pasienId, pasien)

                if (response.isSuccessful) {
                    showMessage("Pasien berhasil diperbarui")
                    finish()
                } else {
                    showMessage("Gagal memperbarui pasien")
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

package com.mobile.tugas5

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobile.tugas5.adapter.PasienAdapter
import com.mobile.tugas5.model.Pasien
import com.mobile.tugas5.network.RetrofitClient
import kotlinx.coroutines.launch

class DataPasienActivity : AppCompatActivity() {

    private lateinit var rvPasien: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: PasienAdapter
    private var pasienList = listOf<Pasien>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pasien)

        // Inisialisasi view
        rvPasien = findViewById(R.id.rvPasien)
        etSearch = findViewById(R.id.etSearch)
        progressBar = findViewById(R.id.progressBar)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        // Setup RecyclerView
        adapter = PasienAdapter(
            onEditClick = { pasien ->
                editPasien(pasien)
            },
            onDeleteClick = { pasien ->
                deletePasien(pasien)
            }
        )
        rvPasien.layoutManager = LinearLayoutManager(this)
        rvPasien.adapter = adapter

        // Setup search
        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterPasien(s.toString())
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // Setup FAB untuk tambah pasien
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddPasienActivity::class.java))
        }

        // Load data pasien
        loadDataPasien()
    }

    override fun onResume() {
        super.onResume()
        // Reload data saat kembali dari add/edit/delete
        loadDataPasien()
    }

    private fun loadDataPasien() {
        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.apiService.getPasien()

                if (response.isSuccessful) {
                    pasienList = response.body()?.data ?: emptyList()
                    adapter.setData(pasienList)

                    if (pasienList.isEmpty()) {
                        showMessage("Tidak ada data pasien")
                    }
                } else {
                    val errorMessage = response.body()?.message ?: "Gagal memuat data pasien"
                    showMessage(errorMessage)
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun filterPasien(query: String) {
        val filtered = if (query.isEmpty()) {
            pasienList
        } else {
            pasienList.filter { pasien ->
                pasien.nama.contains(query, ignoreCase = true)
            }
        }
        adapter.setData(filtered)
    }

    private fun editPasien(pasien: Pasien) {
        val intent = Intent(this, EditPasienActivity::class.java)
        intent.putExtra(EditPasienActivity.EXTRA_PASIEN_ID, pasien.id)
        intent.putExtra(EditPasienActivity.EXTRA_NAMA, pasien.nama)
        intent.putExtra(EditPasienActivity.EXTRA_ALAMAT, pasien.alamat)
        intent.putExtra(EditPasienActivity.EXTRA_TELEPON, pasien.no_telepon)
        intent.putExtra(EditPasienActivity.EXTRA_TGL_LAHIR, pasien.tanggal_lahir)
        intent.putExtra(EditPasienActivity.EXTRA_JENIS_KELAMIN, pasien.jenis_kelamin)
        startActivity(intent)
    }

    private fun deletePasien(pasien: Pasien) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus ${pasien.nama}?")
            .setPositiveButton("Hapus") { dialog, _ ->
                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.apiService.deletePasien(pasien.id)
                        if (response.isSuccessful) {
                            showMessage("Data pasien berhasil dihapus")
                            loadDataPasien()
                        } else {
                            showMessage("Gagal menghapus data pasien")
                        }
                    } catch (e: Exception) {
                        showMessage("Error: ${e.message}")
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

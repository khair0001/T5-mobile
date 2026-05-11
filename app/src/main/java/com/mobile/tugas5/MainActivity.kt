package com.mobile.tugas5

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mobile.tugas5.model.LoginRequest
import com.mobile.tugas5.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var cbRememberMe: CheckBox
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set context untuk RetrofitClient agar dapat mengakses SharedPreferences
        RetrofitClient.setContext(this)

        // Hubungkan variabel dengan view di layout
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        cbRememberMe = findViewById(R.id.cbRememberMe)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        // Cek jika user sudah login dan remember me aktif
        checkRememberMe()

        // Panggil fungsi login saat tombol diklik
        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun checkRememberMe() {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val rememberMe = prefs.getBoolean("remember_me", false)
        val token = prefs.getString("token", "").orEmpty()
        val userName = prefs.getString("user_name", "").orEmpty()

        // Jika remember me aktif dan ada token, langsung ke dashboard
        if (rememberMe && token.isNotEmpty() && userName.isNotEmpty()) {
            goToDashboard(userName)
        }
    }

    private fun login() {
        // Ambil teks dari input dan hapus spasi di awal/akhir
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val rememberMe = cbRememberMe.isChecked

        // Validasi: pastikan kedua field tidak kosong
        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Email dan password wajib diisi")
            return
        }

        // Jalankan request di coroutine agar tidak memblokir UI
        lifecycleScope.launch {
            showLoading(true)

            try {
                // Buat objek request dan kirim ke API
                val request = LoginRequest(email, password)
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful) {
                    val loginData = response.body()?.data
                    val userName = loginData?.user?.name.orEmpty()
                    val userEmail = loginData?.user?.email.orEmpty()
                    val token = loginData?.token.orEmpty()

                    if (userName.isNotEmpty()) {
                        // Simpan ke SharedPreferences
                        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                        prefs.edit().apply {
                            putString("token", token)
                            putString("user_name", userName)
                            putString("user_email", userEmail)
                            putBoolean("remember_me", rememberMe)
                        }.apply()

                        // Navigasi ke Dashboard
                        goToDashboard(userName)
                    } else {
                        showMessage("Data user tidak ditemukan")
                    }
                } else {
                    val errorMessage = response.body()?.message ?: "Email atau password salah"
                    showMessage(errorMessage)
                }
            } catch (e: Exception) {
                showMessage("Tidak dapat terhubung ke server: ${e.message}")
            } finally {
                // Sembunyikan loading baik berhasil maupun gagal
                showLoading(false)
            }
        }
    }

    private fun goToDashboard(userName: String) {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra(DashboardActivity.EXTRA_NAME, userName)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !isLoading
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
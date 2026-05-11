package com.mobile.tugas5.model

data class Pasien(
    val id: Int,
    val nama: String,
    val alamat: String,
    val no_telepon: String,
    val tanggal_lahir: String? = null,
    val jenis_kelamin: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

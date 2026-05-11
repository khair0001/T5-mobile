package com.mobile.tugas5.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.tugas5.R
import com.mobile.tugas5.model.Pasien

class PasienAdapter(
    private val onEditClick: (Pasien) -> Unit = {},
    private val onDeleteClick: (Pasien) -> Unit = {}
) : RecyclerView.Adapter<PasienAdapter.PasienViewHolder>() {

    private val pasienList = mutableListOf<Pasien>()

    fun setData(newPasien: List<Pasien>) {
        pasienList.clear()
        pasienList.addAll(newPasien)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasienViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pasien, parent, false)
        return PasienViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasienViewHolder, position: Int) {
        holder.bind(pasienList[position])
    }

    override fun getItemCount(): Int = pasienList.size

    inner class PasienViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        private val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamat)
        private val tvNoTelepon: TextView = itemView.findViewById(R.id.tvNoTelepon)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(pasien: Pasien) {
            tvNama.text = pasien.nama
            tvAlamat.text = pasien.alamat
            tvNoTelepon.text = "Telepon: ${pasien.no_telepon}"

            btnEdit.setOnClickListener {
                onEditClick(pasien)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(pasien)
            }
        }
    }
}

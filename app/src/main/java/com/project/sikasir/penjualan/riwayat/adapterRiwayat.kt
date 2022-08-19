package com.project.sikasir.penjualan.riwayat

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.R
import com.project.sikasir.produk.produk.kelolaProduk
import java.text.SimpleDateFormat
import java.util.*

class adapterRiwayat(private val listClassRiwayat: ArrayList<classRiwayat>) :
        RecyclerView.Adapter<adapterRiwayat.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_riwayat, parent, false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = listClassRiwayat[position]

        holder.nama.text = currentitem.namaPegawai
        holder.jabatan.text = currentitem.jabatan
        holder.harga.text = currentitem.total
        holder.tanggal.text = currentitem.tanggal!!.toTimeDateString()

        holder.itemView.setOnClickListener {
            val namaProduk = listClassRiwayat[position].namaPegawai
            val hJual = listClassRiwayat[position].jabatan
            val kategori = listClassRiwayat[position].tanggal
            val hModal = listClassRiwayat[position].total

            val intent = Intent(holder.itemView.context, kelolaProduk::class.java)
            intent.putExtra("Nama_Produk", namaProduk)
            intent.putExtra("Harga_Jual", hJual)
            intent.putExtra("Kategori", kategori)
            intent.putExtra("harga_modal", hModal)

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listClassRiwayat.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.riwayat_tv_nama)
        val jabatan: TextView = itemView.findViewById(R.id.riwayat_tv_jabatan)
        val harga: TextView = itemView.findViewById(R.id.riwayat_tv_harga)
        val tanggal: TextView = itemView.findViewById(R.id.tv_tanggal)
    }

    fun Long.toTimeDateString(): String {
        val dateTime = Date(this)
        val format = SimpleDateFormat("dd-MM-yyyy hh:mm a")
        return format.format(dateTime)
    }
}
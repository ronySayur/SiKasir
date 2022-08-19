package com.project.sikasir.laporan.penjualan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.R
import com.project.sikasir.penjualan.pembayaran.classDetailPenjualan
import java.text.NumberFormat
import java.util.*

/**
 * Dibuat oleh RizkyPratama pada 08-May-22.
 */
class adapterLapDetailPenjualan(private val listPenjualan: ArrayList<classDetailPenjualan>) :
        RecyclerView.Adapter<adapterLapDetailPenjualan.MyViewHolder>() {
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_detail, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = listPenjualan[position]

        val nama = currentitem.nama_Produk
        val harga = currentitem.harga
        val jumlah = currentitem.jumlah_Produk
        val total = currentitem.total

        holder.nama.text = nama
        holder.harga.text = harga
        holder.jumlah.text = jumlah
        holder.total.text = total
    }

    override fun getItemCount(): Int {
        return listPenjualan.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.tv_listnama)
        val harga: TextView = itemView.findViewById(R.id.tv_listharga)
        val jumlah: TextView = itemView.findViewById(R.id.tv_jumlah_note)
        val total: TextView = itemView.findViewById(R.id.tv_tot)
    }

}
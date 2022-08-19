package com.project.sikasir.laporan.pembelian

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.R
import com.project.sikasir.pembelian.classInfoPembelian
import java.text.NumberFormat
import java.util.*

/**
 * Dibuat oleh RizkyPratama pada 08-May-22.
 */
class adapterLapDetailPembelian(private val listPembelian: ArrayList<classInfoPembelian>) :
        RecyclerView.Adapter<adapterLapDetailPembelian.MyViewHolder>() {
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_detail_pembelian, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = listPembelian[position]

        val Nama_Produk = currentitem.Nama
        val Tanggal = currentitem.Tanggal
        val jumlah = currentitem.Jumlah
        val harga = currentitem.Harga
        val beli = currentitem.Total

        holder.nama.text = Nama_Produk
        holder.tgl.text = Tanggal
        holder.jumlah.text = jumlah + "  x  "
        holder.harga.text = harga
        holder.total.text = beli
    }

    override fun getItemCount(): Int {
        return listPembelian.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.textView95)
        val tgl: TextView = itemView.findViewById(R.id.textView94)
        val jumlah: TextView = itemView.findViewById(R.id.textView96)
        val harga: TextView = itemView.findViewById(R.id.textView97)
        val total: TextView = itemView.findViewById(R.id.textView98)
    }

}
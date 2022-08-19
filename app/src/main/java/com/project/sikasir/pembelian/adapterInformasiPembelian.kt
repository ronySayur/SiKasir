package com.project.sikasir.pembelian

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.R

/**
 * Dibuat oleh RizkyPratama pada 08-May-22.
 */
class adapterInformasiPembelian(private val listPembelian: ArrayList<classInfoPembelian>) :
        RecyclerView.Adapter<adapterInformasiPembelian.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_pembelian, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = listPembelian[position]

        val Nama_Produk = currentitem.Nama
        val Tanggal = currentitem.Tanggal
        val jumlah = currentitem.Jumlah
        val harga = currentitem.Harga
        val beli = currentitem.Total

        holder.Nama.text = Nama_Produk
        holder.Harga.text = harga
        holder.Tanggal.text = Tanggal
        holder.total.text = beli
        holder.jumlah.text = jumlah
    }

    override fun getItemCount(): Int {
        return listPembelian.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Nama: TextView = itemView.findViewById(R.id.kp_tv_tgll)
        val Harga: TextView = itemView.findViewById(R.id.tv_hargamodal_pembelian)
        val Tanggal: TextView = itemView.findViewById(R.id.tv_tanggal_pembelian)
        val jumlah: TextView = itemView.findViewById(R.id.tv_jumlah_beli)
        val total: TextView = itemView.findViewById(R.id.list_total_penjualan)
    }

}
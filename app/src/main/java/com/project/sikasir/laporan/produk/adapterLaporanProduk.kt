package com.project.sikasir.laporan.produk

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.R
import com.project.sikasir.pembelian.kelolaPembelian
import com.project.sikasir.produk.produk.classProduk

/**
 * Dibuat oleh RizkyPratama pada 08-May-22.
 */
class adapterLaporanProduk(private val listProduk: ArrayList<classLapProduk>, private val listP: ArrayList<classProduk>) :
        RecyclerView.Adapter<adapterLaporanProduk.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_riwayat_produk, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = listProduk[position]

        val Nama_Produk = currentitem.namaProduk
        val Harga_Jual = currentitem.harga
        val Stok = currentitem.sisaProduk
        val Terjual = currentitem.terjual

        holder.Nama.text = Nama_Produk
        holder.Harga.text = Harga_Jual
        holder.terjual.text = Terjual.toString()
        if (Stok!!.toInt() < 10) {
            holder.stok.setTextColor(Color.parseColor("#FF1744"))
            holder.stok.text = Stok.toString()
        } else {
            holder.stok.setTextColor(Color.parseColor("#00E676"))
            holder.stok.text = Stok.toString()
        }

        holder.itemView.setOnClickListener {
            val namaProduk = listP[position].nama_Produk
            val hModal = listP[position].harga_Modal
            val stok = listP[position].stok
            val kode = listP[position].id_Produk

            val intent = Intent(holder.itemView.context, kelolaPembelian::class.java)
            intent.putExtra("Nama_Produk", namaProduk)
            intent.putExtra("harga_modal", hModal)
            intent.putExtra("stok", stok)
            intent.putExtra("id_Produk", kode)

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listProduk.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Nama: TextView = itemView.findViewById(R.id.riwayat_tv_nama)
        val Harga: TextView = itemView.findViewById(R.id.riwayat_tv_hargai)
        val terjual: TextView = itemView.findViewById(R.id.riwayat_tv_terjual)
        val stok: TextView = itemView.findViewById(R.id.riwayat_tv_sisa)
    }

}
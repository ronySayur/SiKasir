package com.project.sikasir.penjualan.keranjang

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.R

/**
 * Dibuat oleh RizkyPratama pada 21-Jun-22.
 */
class adapterKeranjang(private val listKeranjang: ArrayList<classKeranjang>) : RecyclerView.Adapter<adapterKeranjang.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_keranjang, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = listKeranjang[position]

        val Nama_Produk = currentitem.nama_Produk
        val Jumlah_Produk = currentitem.jumlah_Produk
        val Harga = currentitem.harga
        val Total = currentitem.total
        val Diskon = currentitem.diskon
        val Nama_Diskon = currentitem.nama_Diskon

        if (Diskon.isNullOrEmpty() || Diskon.replace(",00", "").filter { it.isDigit() } == "0") {
            holder.minDiskon.visibility = View.GONE
            holder.Diskon.visibility = View.GONE
            holder.namaDiskon.visibility = View.GONE
        } else {
            holder.Diskon.text = Diskon
            if (Nama_Diskon.isNullOrEmpty()) {
                holder.namaDiskon.text = "(Diskon)"
            } else {
                "(Diskon $Nama_Diskon)".also { holder.namaDiskon.text = it }
            }
        }

        holder.Nama.text = Nama_Produk
        holder.Jumlah_Produk.text = Jumlah_Produk
        holder.Harga.text = Harga

        if (Total.isNullOrEmpty()) {
            holder.Total.text = Harga
        } else {
            holder.Total.text = Total
        }

        holder.itemView.setOnClickListener {
            val Nama_Produk = listKeranjang[position].nama_Produk
            val Jumlah_Produk = listKeranjang[position].jumlah_Produk
            val Harga = listKeranjang[position].harga
            val subtotal = listKeranjang[position].total
            val Diskon = listKeranjang[position].diskon
            val Nama_Diskon = listKeranjang[position].nama_Diskon
            val id_Produk = listKeranjang[position].id_Produk

            val intent = Intent(holder.itemView.context, kelolaKeranjang::class.java)
            intent.putExtra("Nama_Produk", Nama_Produk)
            intent.putExtra("Jumlah_Produk", Jumlah_Produk)
            intent.putExtra("Harga", Harga)
            intent.putExtra("stot", subtotal)
            intent.putExtra("Diskon", Diskon)
            intent.putExtra("Nama_Diskon", Nama_Diskon)
            intent.putExtra("id_Produk", id_Produk)

            holder.itemView.context.startActivity(intent)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Nama: TextView = itemView.findViewById(R.id.tv_l_nama)
        val Harga: TextView = itemView.findViewById(R.id.tv_harga)
        val Jumlah_Produk: TextView = itemView.findViewById(R.id.tv_jumlah)
        val minDiskon: TextView = itemView.findViewById(R.id.tv_min_diskon)
        val Diskon: TextView = itemView.findViewById(R.id.tv_diskon)
        val namaDiskon: TextView = itemView.findViewById(R.id.tv_namaDiskon)
        val Total: TextView = itemView.findViewById(R.id.tv_total)
    }

    override fun getItemCount(): Int {
        return listKeranjang.size
    }
}
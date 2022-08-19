package com.project.sikasir.penjualan.penjualan

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.R
import com.project.sikasir.produk.produk.classProduk

/**
 * Dibuat oleh RizkyPratama pada 21-Jun-22.
 */

class adapterPenjualan(private val listTransaksi: ArrayList<classProduk>, private val transaksiListener: TransaksiListener, private val produkListener: ProdukListener) :
        RecyclerView.Adapter<adapterPenjualan.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_produk, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = listTransaksi[position]

        holder.Nama.text = currentitem.nama_Produk
        holder.Harga_Jual.text = currentitem.harga_Jual

        val Stok = currentitem.stok
        if (Stok!!.toInt() < 10) {
            holder.stok.setTextColor(Color.parseColor("#FF1744"))
            holder.stok.text = Stok.toString()
        } else {
            holder.stok.setTextColor(Color.parseColor("#00E676"))
            holder.stok.text = Stok.toString()
        }

        holder.itemView.setOnClickListener {
            transaksiListener.AddToCart(currentitem)
            produkListener.AddToCart(currentitem)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Nama: TextView = itemView.findViewById(R.id.tv_listnama)
        val Harga_Jual: TextView = itemView.findViewById(R.id.tv_listharga)
        val stok: TextView = itemView.findViewById(R.id.tv_stok)
    }

    override fun getItemCount(): Int {
        return listTransaksi.size
    }

    interface TransaksiListener {
        fun AddToCart(produk: classProduk)
    }

    interface ProdukListener {
        fun AddToCart(produk: classProduk)
    }
}
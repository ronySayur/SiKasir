package com.project.sikasir.pembelian

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.R
import com.project.sikasir.produk.produk.classProduk

/**
 * Dibuat oleh RizkyPratama pada 08-May-22.
 */
class adapterPembelian(private val listProduk: ArrayList<classProduk>) :
        RecyclerView.Adapter<adapterPembelian.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_produk, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = listProduk[position]

        val Nama_Produk = currentitem.nama_Produk
        val Harga_Jual = currentitem.harga_Modal
        val Stok = currentitem.stok

        holder.Nama.text = Nama_Produk
        holder.Harga_Jual.text = Harga_Jual
        holder.stok.text = Stok

        holder.itemView.setOnClickListener {
            val namaProduk = listProduk[position].nama_Produk
            val hModal = listProduk[position].harga_Modal
            val stok = listProduk[position].stok
            val id_Produk = listProduk[position].id_Produk

            val intent = Intent(holder.itemView.context, kelolaPembelian::class.java)
            intent.putExtra("Nama_Produk", namaProduk)
            intent.putExtra("harga_modal", hModal)
            intent.putExtra("stok", stok)
            intent.putExtra("id_Produk", id_Produk)

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listProduk.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Nama: TextView = itemView.findViewById(R.id.tv_listnama)
        val Harga_Jual: TextView = itemView.findViewById(R.id.tv_listharga)
        val stok: TextView = itemView.findViewById(R.id.tv_stok)
    }

}
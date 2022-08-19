package com.project.sikasir.produk.produk

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.R

/**
 * Dibuat oleh RizkyPratama pada 08-May-22.
 */
class adapterProduk(private val listProduk: ArrayList<classProduk>) :
        RecyclerView.Adapter<adapterProduk.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_produk, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = listProduk[position]

        val Nama_Produk = currentitem.nama_Produk
        val Harga_Jual = currentitem.harga_Jual
        val Stok = currentitem.stok

        holder.Nama.text = Nama_Produk
        holder.Harga_Jual.text = Harga_Jual
        holder.stok.text = Stok

        holder.itemView.setOnClickListener {
            val namaProduk = currentitem.nama_Produk
            val hJual = currentitem.harga_Jual
            val kategori = currentitem.kategori
            val hModal = currentitem.harga_Modal
            val barcode = currentitem.barcode
            val merek = currentitem.merek
            val foto = currentitem.foto
            val stok = currentitem.stok
            val id_Produk = currentitem.id_Produk
            val id_Toko = currentitem.id_Toko

            val intent = Intent(holder.itemView.context, kelolaProduk::class.java)
            intent.putExtra("Nama_Produk", namaProduk)
            intent.putExtra("Harga_Jual", hJual)
            intent.putExtra("Kategori", kategori)
            intent.putExtra("harga_modal", hModal)
            intent.putExtra("Barcode", barcode)
            intent.putExtra("Merek", merek)
            intent.putExtra("Foto", foto)
            intent.putExtra("Stok", stok)
            intent.putExtra("id_Produk", id_Produk)
            intent.putExtra("id_Toko", id_Toko)
            intent.putExtra("Edit", "true")

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
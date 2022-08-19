package com.project.sikasir.penjualan.penjualan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.databinding.ListProdukBinding
import com.project.sikasir.produk.produk.classProduk

class adapterSearchTransaksi(val listTransaksi: ArrayList<classProduk>) : ListAdapter<classProduk, adapterSearchTransaksi.MyHolder>(COMPARATOR) {

    private object COMPARATOR : DiffUtil.ItemCallback<classProduk>() {
        override fun areItemsTheSame(oldItem: classProduk, newItem: classProduk): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: classProduk, newItem: classProduk): Boolean {
            return oldItem.nama_Produk == newItem.nama_Produk
        }
    }

    // An innerclass that maps data with the available views
    inner class MyHolder(private val binding: ListProdukBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(produk: classProduk?) {
            binding.tvListnama.text = produk?.nama_Produk.toString()
            binding.tvListharga.text = produk?.harga_Jual.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ListProdukBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val currentitem = listTransaksi[position]

        holder.itemView.setOnClickListener {
            println(currentitem.nama_Produk)
        }
    }
}
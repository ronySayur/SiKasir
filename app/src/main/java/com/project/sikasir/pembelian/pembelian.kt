package com.project.sikasir.pembelian

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import com.project.sikasir.produk.produk.classProduk
import kotlinx.android.synthetic.main.fragment_pembelian.view.*
import kotlinx.android.synthetic.main.fragment_produk.view.fabTambahProduk
import kotlinx.android.synthetic.main.fragment_produk.view.rv_pegawai_kp

class pembelian : Fragment() {
    val listProduk = ArrayList<classProduk>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_pembelian, container, false)

        view.fabTambahProduk.visibility = View.GONE
        view.rv_pegawai_kp.layoutManager = GridLayoutManager(activity, 1)
        view.rv_pegawai_kp.setHasFixedSize(true)

        val dbref = FirebaseDatabase.getInstance().getReference("Produk")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listProduk.clear()
                if (snapshot.exists()) {
                    view.cl_pemb.visibility = View.GONE
                    for (snap in snapshot.children) {
                        val beli = snap.getValue(classProduk::class.java)
                        listProduk.add(beli!!)
                    }
                    view.rv_pegawai_kp.adapter = adapterPembelian(listProduk)
                } else {
                    view.cl_pemb.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return view
    }
}

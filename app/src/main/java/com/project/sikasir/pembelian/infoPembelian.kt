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

class infoPembelian : Fragment() {
    val listPembelian = ArrayList<classInfoPembelian>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_pembelian, container, false)
        view.textView24.visibility = View.GONE
        view.textView54.visibility = View.GONE

        view.fabTambahProduk.visibility = View.GONE

        view.rv_pegawai_kp.layoutManager = GridLayoutManager(activity, 1)
        view.rv_pegawai_kp.setHasFixedSize(true)

        val refPembelian = FirebaseDatabase.getInstance().getReference("Pembelian")
        val refDPembelian = FirebaseDatabase.getInstance().getReference("DetailPembelian")
        val refProduk = FirebaseDatabase.getInstance().getReference("Produk")

        refPembelian.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshotPembelian: DataSnapshot) {
                if (snapshotPembelian.exists()) {
                    refDPembelian.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshotDetail: DataSnapshot) {
                            if (snapshotDetail.exists()) {
                                refProduk.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshotProduk: DataSnapshot) {
                                        if (snapshotProduk.exists()) {
                                            listPembelian.clear()
                                            view.cl_pemb.visibility = View.GONE
                                            view.rv_pegawai_kp.visibility = View.VISIBLE

                                            for (snapPembelian in snapshotPembelian.children) {
                                                val info = classInfoPembelian()
                                                val pembelian = snapPembelian.getValue(classPembelian::class.java)
                                                info.Tanggal = pembelian?.tanggal
                                                info.Total = pembelian?.totalPembelian
                                                for (snapDetail in snapshotDetail.children) {
                                                    val Detail = snapDetail.getValue(classDetailPembelian::class.java)
                                                    if (pembelian?.id_pembelian == Detail?.id_detailPembelian) {
                                                        info.Jumlah = Detail?.jumlah_Produk
                                                        for (snapProduk in snapshotProduk.children) {
                                                            val Produk = snapProduk.getValue(classProduk::class.java)
                                                            if (Detail?.id_Produk == Produk?.id_Produk) {
                                                                info.Nama = Produk?.nama_Produk
                                                                info.Harga = Produk?.harga_Modal
                                                            }
                                                        }
                                                    }
                                                }
                                                listPembelian.add(info)
                                            }
                                            view.rv_pegawai_kp.adapter = adapterInformasiPembelian(listPembelian)
                                        } else {
                                            view.cl_pemb.visibility = View.VISIBLE
                                            view.rv_pegawai_kp.visibility = View.GONE
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                } else {
                    view.cl_pemb.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return view
    }
}

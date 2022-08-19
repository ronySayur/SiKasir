package com.project.sikasir.laporan.pembelian

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import com.project.sikasir.pembelian.classDetailPembelian
import com.project.sikasir.pembelian.classInfoPembelian
import com.project.sikasir.pembelian.classPembelian
import com.project.sikasir.produk.produk.classProduk
import kotlinx.android.synthetic.main.invoice_supplier.*
import java.text.NumberFormat
import java.util.*

class invoiceSup : AppCompatActivity() {
    private lateinit var ssPembelian: DataSnapshot
    private lateinit var ssProduk: DataSnapshot
    private lateinit var ssDetail: DataSnapshot
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    val listPembelian = ArrayList<classInfoPembelian>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.invoice_supplier)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val nama_Vendor: String = this.intent.getStringExtra("nama_Vendor").toString()
        val jumlahTransaksi: String = this.intent.getStringExtra("jumlahTransaksi").toString()
        val total_Pembelian: String = this.intent.getStringExtra("total_Pembelian").toString()
        val no_Vendor: String = this.intent.getStringExtra("no_Vendor").toString()
        val email_Vendor: String = this.intent.getStringExtra("email_Vendor").toString()
        val nama_PIC: String = this.intent.getStringExtra("nama_PIC").toString()
        val No_PIC: String = this.intent.getStringExtra("No_PIC").toString()
        val keterangan: String = this.intent.getStringExtra("keterangan").toString()

        tv_nm_vendor.text = nama_Vendor
        textView103.text = nama_PIC
        textView104.text = "Telp: $no_Vendor"
        textView105.text = "Email: $email_Vendor"
        textView106.text = "Telp: $No_PIC"

        if (keterangan.isBlank()) {
            textView116.text = "Tidak ada keteranggan"
        } else {
            textView116.text = keterangan
        }

        textView109.text = total_Pembelian

        getData()

    }

    private fun getData() {
        rv_lapBeli.layoutManager = GridLayoutManager(this, 1)
        rv_lapBeli.setHasFixedSize(true)

        val refTransaksi = FirebaseDatabase.getInstance().getReference("Pembelian")
        val refProduk = FirebaseDatabase.getInstance().getReference("Produk")
        val refDetail = FirebaseDatabase.getInstance().getReference("DetailPembelian")

        refTransaksi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshotPembelian: DataSnapshot) {
                if (snapshotPembelian.exists()) {
                    refProduk.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshotProduk: DataSnapshot) {
                            if (snapshotPembelian.exists()) {
                                refDetail.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshotDetail: DataSnapshot) {
                                        if (snapshotPembelian.exists()) {
                                            ssPembelian = snapshotPembelian
                                            ssProduk = snapshotProduk
                                            ssDetail = snapshotDetail
                                            initAdapter()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initAdapter() {
        if (this::ssPembelian.isInitialized && this::ssProduk.isInitialized && this::ssDetail.isInitialized) {
            listPembelian.clear()

            for (snapPembelian in ssPembelian.children) {
                val info = classInfoPembelian()
                val pembelian = snapPembelian.getValue(classPembelian::class.java)
                info.Tanggal = pembelian?.tanggal
                info.Total = pembelian?.totalPembelian
                for (snapDetail in ssDetail.children) {
                    val Detail = snapDetail.getValue(classDetailPembelian::class.java)
                    if (pembelian?.id_pembelian == Detail?.id_detailPembelian) {
                        info.Jumlah = Detail?.jumlah_Produk
                        for (snapProduk in ssProduk.children) {
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
        }
        rv_lapBeli.adapter = adapterLapDetailPembelian(listPembelian)
    }
}

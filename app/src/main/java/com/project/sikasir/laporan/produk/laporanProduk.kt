package com.project.sikasir.laporan.produk

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import com.project.sikasir.penjualan.pembayaran.classDetailPenjualan
import com.project.sikasir.penjualan.pembayaran.classPenjualan
import com.project.sikasir.produk.produk.classProduk
import kotlinx.android.synthetic.main.laporan_produk.*

class laporanProduk : AppCompatActivity() {
    val classLapProduk = ArrayList<classLapProduk>()
    val clasProduk = ArrayList<classProduk>()
    private lateinit var ssTransaksi: DataSnapshot
    private lateinit var ssDetail: DataSnapshot
    private lateinit var ssProduk: DataSnapshot

    private var awal = -1L
    private var akhir = -1L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.laporan_produk)
        getData()
        tvA3toA6.setOnClickListener { finish() }
        editTextDate4.setOnClickListener { tgl() }
    }

    private fun tgl() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
        datePicker.show(supportFragmentManager, "DatePicker")

        editTextDate4.isEnabled = false
        Toast.makeText(this, "tunggu sebentar", Toast.LENGTH_SHORT).show()

        datePicker.addOnPositiveButtonClickListener {
            awal = datePicker.selection!!.first
            akhir = datePicker.selection!!.second

            editTextDate4.setText(datePicker.headerText)
            editTextDate4.isEnabled = true
            initAdapter()
        }

        datePicker.addOnNegativeButtonClickListener { editTextDate4.isEnabled = true }

        datePicker.addOnCancelListener { editTextDate4.isEnabled = true }
    }

    private fun getData() {
        rv_riwayat.layoutManager = GridLayoutManager(this, 1)
        rv_riwayat.setHasFixedSize(true)

        val refTransaksi = FirebaseDatabase.getInstance().getReference("Penjualan").orderByValue()
        val refDetail = FirebaseDatabase.getInstance().getReference("DetailTransaksi")
        val refProduk = FirebaseDatabase.getInstance().getReference("Produk")

        refDetail.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshotDetail: DataSnapshot) {
                if (snapshotDetail.exists()) {
                    refTransaksi.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshotTransaksi: DataSnapshot) {
                            if (snapshotTransaksi.exists()) {
                                refProduk.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshotProduk: DataSnapshot) {
                                        if (snapshotProduk.exists()) {
                                            ssDetail = snapshotDetail
                                            ssTransaksi = snapshotTransaksi
                                            ssProduk = snapshotProduk
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
        if (this::ssTransaksi.isInitialized && this::ssDetail.isInitialized && this::ssProduk.isInitialized) {
            var jumlahProduk = 0
            classLapProduk.clear()

            //jika awal dan akhir kosong
            if (awal == -1L && akhir == -1L) {
                for (snapDetail in ssDetail.children) {
                    val cd = snapDetail.getValue(classDetailPenjualan::class.java)
                    jumlahProduk += cd?.jumlah_Produk?.toInt()!!
                }
            } else {
                for (snapDetail in ssDetail.children) {
                    for (snapTransaksi in ssTransaksi.children) {
                        val t = snapTransaksi.getValue(classPenjualan::class.java)
                        val cd = snapDetail.getValue(classDetailPenjualan::class.java)
                        //jika tanggal lebih dari sama dengan awal dan tanggal kurang dari sama dengan akhir
                        if (t?.tanggal!! >= awal && t.tanggal!! <= akhir) {
                            jumlahProduk += cd?.jumlah_Produk?.toInt()!!
                        }
                    }
                }
            }

/*            val totalString = Rp.format(totalTransaksi)
            val total = totalString.substring(0, 2) + " " + totalString.substring(2, totalString.length)*/

            totaltransaksi_nama.text = "$jumlahProduk Produk"


            //RV
            for (snapProduk in ssProduk.children) {
                val p = snapProduk.getValue(classProduk::class.java)
                clasProduk.add(p!!)
                val lp = classLapProduk(p.nama_Produk, p.harga_Jual, p.stok)

                //jika awal dan akhir kosong
                if (awal == -1L && akhir == -1L) {
                    for (snapTransaksi in ssDetail.children) {
                        val cd = snapTransaksi.getValue(classDetailPenjualan::class.java)

                        //jika classTransaksi.emailpegawai = classPegawai.emailPegawai
                        if (cd?.nama_Produk.equals(p.nama_Produk)) {
                            //terjual
                            lp.terjual = cd?.jumlah_Produk?.replace(",00", "")?.filter { it.isDigit() }?.let {
                                lp.terjual?.plus(it.toInt())
                            }
                        }
                    }
                } else {
                    for (snapDetail in ssDetail.children) {
                        for (snapTransaksi in ssTransaksi.children) {
                            val t = snapTransaksi.getValue(classPenjualan::class.java)
                            val cd = snapDetail.getValue(classDetailPenjualan::class.java)

                            //transaksi.emailPegawai sama dengan pegawai.emailPegawai
                            if (cd?.nama_Produk.equals(p.nama_Produk) && t?.tanggal!! >= awal && t.tanggal!! <= akhir) {
                                //terjual
                                lp.terjual = cd?.jumlah_Produk?.replace(",00", "")?.filter { it.isDigit() }?.let {
                                    lp.terjual?.plus(it.toInt())
                                }
                            }
                        }
                    }
                }
                classLapProduk.add(lp)
            }
            rv_riwayat.adapter = adapterLaporanProduk(classLapProduk, clasProduk)
        }
    }
}
package com.project.sikasir.laporan.pembelian

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
import com.project.sikasir.pembelian.classPembelian
import com.project.sikasir.produk.produk.classProduk
import com.project.sikasir.supplier.classSupplier
import kotlinx.android.synthetic.main.laporan_pembelian.*
import java.text.NumberFormat
import java.util.*

class laporanPembelian : AppCompatActivity() {
    val classBeli = ArrayList<classLapPembelian>()
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    private lateinit var ssPembelian: DataSnapshot
    private lateinit var ssSupplier: DataSnapshot
    private lateinit var ssProduk: DataSnapshot

    private var awal = -1L
    private var akhir = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.laporan_pembelian)
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

        val refPembelian = FirebaseDatabase.getInstance().getReference("Pembelian").orderByValue()
        val refSupplier = FirebaseDatabase.getInstance().getReference("Supplier")
        val refProduk = FirebaseDatabase.getInstance().getReference("Produk")

        refSupplier.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshotSupplier: DataSnapshot) {
                refPembelian.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshotPembelian: DataSnapshot) {
                        if (snapshotPembelian.exists()) {
                            refProduk.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshotProduk: DataSnapshot) {
                                    if (snapshotPembelian.exists()) {
                                        ssPembelian = snapshotPembelian
                                        ssSupplier = snapshotSupplier
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

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initAdapter() {
        if (this::ssSupplier.isInitialized && this::ssPembelian.isInitialized && this::ssProduk.isInitialized) {
            var jumlahTransaksi = 0
            var totalTransaksi = 0
            classBeli.clear()

            if (awal == -1L && akhir == -1L) {
                for (snapBeli in ssPembelian.children) {
                    val pembelian = snapBeli.getValue(classPembelian::class.java)
                    jumlahTransaksi += 1
                    totalTransaksi += pembelian?.totalPembelian?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                }
            } else {
                for (snapBeli in ssPembelian.children) {
                    val pembelian = snapBeli.getValue(classPembelian::class.java)

                    if (pembelian?.id_pembelian!! >= awal && pembelian.id_pembelian!! <= akhir) {
                        jumlahTransaksi += 1
                        totalTransaksi += pembelian.totalPembelian?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                    }
                }
            }

            val totalString = Rp.format(totalTransaksi)
            val total = totalString.substring(0, 2) + " " + totalString.substring(2, totalString.length)

            tv_tot_pembelian.text = total
            tv_jmltransaksi.text = jumlahTransaksi.toString()

            for (snapSupplier in ssSupplier.children) {
                val supplier = snapSupplier.getValue(classSupplier::class.java)
                val lPembelian = classLapPembelian()
                lPembelian.nama_Vendor = supplier?.nama_Vendor
                lPembelian.email_Vendor = supplier?.email_Vendor
                lPembelian.no_Vendor = supplier?.no_Vendor
                lPembelian.nama_PIC = supplier?.nama_PIC
                lPembelian.noPIC = supplier?.noPIC

                for (snapProduk in ssProduk.children) {
                    val produk = snapProduk.getValue(classProduk::class.java)
                    if (produk?.nama_Vendor == supplier?.nama_Vendor) {
                        for (snapPembelian in ssPembelian.children) {
                            val Pembelian = snapPembelian.getValue(classPembelian::class.java)

                            lPembelian.totalPembelian = Pembelian?.totalPembelian?.replace(",00", "")?.filter { it.isDigit() }?.let { lPembelian.totalPembelian?.plus(it.toInt()) }
                            lPembelian.keterangan = Pembelian?.keterangan
                            lPembelian.jumlahTransaksi = lPembelian.jumlahTransaksi?.plus(1)

                        }
                    }
                }
                if (lPembelian.jumlahTransaksi?.toString()?.toInt()!! > 0) {
                    classBeli.add(lPembelian)
                }
            }
            rv_riwayat.adapter = adapterLaporanPembelian(classBeli)
        }
    }
}
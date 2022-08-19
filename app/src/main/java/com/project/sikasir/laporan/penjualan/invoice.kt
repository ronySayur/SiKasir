package com.project.sikasir.laporan.penjualan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import com.project.sikasir.penjualan.pembayaran.classDetailPenjualan
import kotlinx.android.synthetic.main.fragment_nota.*
import java.text.NumberFormat
import java.util.*

class invoice : AppCompatActivity() {
    private lateinit var ssDetail: DataSnapshot
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    val listDetail = ArrayList<classDetailPenjualan>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_nota)

        val namaPegawai: String = this.intent.getStringExtra("namaPegawai").toString()
        val tanggal: String = this.intent.getStringExtra("tanggal").toString()
        val nt: String = this.intent.getStringExtra("nt").toString()
        val diskon: String = this.intent.getStringExtra("diskon").toString()
        val jabatan: String = this.intent.getStringExtra("jabatan").toString()
        val total: String = this.intent.getStringExtra("total").toString()

        note_jabatan.text = jabatan
        note_namap.text = namaPegawai
        note_tanggal.text = tanggal
        note_no_nota.text = nt
        note_diskon.text = diskon
        note_total.text = total
        note_sub.text = Rp.format(total.replace(",00", "").filter { it.isDigit() }.toInt() - diskon.replace(",00", "").filter { it.isDigit() }.toInt()).toString()

        getData()
    }

    private fun getData() {
        rv_note.layoutManager = GridLayoutManager(this, 1)
        rv_note.setHasFixedSize(true)

        val refTransaksi = FirebaseDatabase.getInstance().getReference("DetailTransaksi")

        refTransaksi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshotDetail: DataSnapshot) {
                if (snapshotDetail.exists()) {
                    ssDetail = snapshotDetail
                    initAdapter()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initAdapter() {
        val dt: String = this.intent.getStringExtra("detailTransaksi").toString()
        if (this::ssDetail.isInitialized) {
            listDetail.clear()
            for (snapPenjualan in ssDetail.children) {
                val cd = snapPenjualan.getValue(classDetailPenjualan::class.java)
                if (cd?.detailTransaksi!! == dt) {
                    val cd = snapPenjualan.getValue(classDetailPenjualan::class.java)
                    listDetail.add(cd!!)
                }
            }
            rv_note.adapter = adapterLapDetailPenjualan(listDetail)
        }
    }
}
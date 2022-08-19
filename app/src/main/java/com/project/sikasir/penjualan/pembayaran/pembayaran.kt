package com.project.sikasir.penjualan.pembayaran

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.*
import com.project.sikasir.R
import com.project.sikasir.penjualan.keranjang.adapterKeranjang
import com.project.sikasir.penjualan.keranjang.classKeranjang
import com.project.sikasir.penjualan.penjualan.penjualan
import com.project.sikasir.penjualan.penjualan.transaksiBerhasil
import kotlinx.android.synthetic.main.transaksi_pembayaran.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class pembayaran : AppCompatActivity() {
    private var USERNAME_KEY = "username_key"
    private var username_key = ""
    private var username_key_new = ""
    var diterima = ""
    var kembalian = ""
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    val waktu = SimpleDateFormat("dd-MM-yyyy").format(Date())

    var emailPegawai = ""
    var jabatanPegawai = ""
    var namaPegawai = ""

    val keranjangList = ArrayList<classKeranjang>()

    private lateinit var refTransaksi: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaksi_pembayaran)
        getIdPegawai()
        uangDiterima()
        getKeranjang()

        tvA8toA7.setOnClickListener { finish() }
    }

    private fun uangDiterima() {
        val format: NumberFormat = DecimalFormat("Rp#,###")
        edUangPass.setBackgroundColor(ContextCompat.getColor(baseContext, android.R.color.holo_blue_bright))

        val tagihan: String = intent.getStringExtra("tagihan").toString().replace(",00", "").replace(".", "").replace("Rp ", "")

        edUangPass.setOnClickListener {
            diterima = tagihan
            passData()
        }

        edUangDiterima.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    edUangPass.setBackgroundColor(ContextCompat.getColor(baseContext, android.R.color.holo_blue_bright))
                    edUangPass.text = "Uang Pas"
                    edUangPass.isEnabled = true
                    edUangPass.isClickable = true
                    edUangPass.setOnClickListener {
                        diterima = tagihan
                        passData()
                    }
                } else {
                    if (s.toString().toInt() < tagihan.toInt()) {
                        val kurang = tagihan.toInt() - s.toString().toInt()
                        edUangPass.setBackgroundColor(ContextCompat.getColor(baseContext, android.R.color.darker_gray))
                        edUangPass.text = "Uang Kurang " + format.format(kurang)
                        edUangPass.isEnabled = false
                        edUangPass.isClickable = false
                    } else {
                        val k = s.toString().toInt() - tagihan.toInt()
                        edUangPass.setBackgroundColor(ContextCompat.getColor(baseContext, android.R.color.holo_green_light))
                        edUangPass.text = "Kembalian " + format.format(k)
                        edUangPass.isEnabled = true
                        edUangPass.isClickable = true
                        kembalian = k.toString()

                        edUangPass.setOnClickListener {
                            diterima = edUangDiterima.text.toString()
                            passData()
                        }
                    }
                }
            }
        })
    }

    private fun passData() {
        val mediaPlayer = MediaPlayer.create(baseContext, R.raw.mario)
        mediaPlayer.start()

        val total: String = intent.getStringExtra("tagihan").toString()

        val intent = Intent(this, transaksiBerhasil::class.java)
        intent.putExtra("Diterima", diterima)
        intent.putExtra("Total_Tagihan", total)
        intent.putExtra("Tanggalan", waktu)
        intent.putExtra("Pegawai", emailPegawai)
        intent.putExtra("NPegawai", namaPegawai)
        intent.putExtra("Jabatan", jabatanPegawai)
        intent.putExtra("Kembalian", kembalian)
        startActivity(intent)
        finish()
    }

    private fun getIdPegawai() {
        val sharedPreference: SharedPreferences = getSharedPreferences(USERNAME_KEY, Context.MODE_PRIVATE)
        username_key_new = sharedPreference.getString(username_key, "").toString()
        refTransaksi = FirebaseDatabase.getInstance().reference.child("Pegawai").child(username_key_new)
        refTransaksi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                emailPegawai = dataSnapshot.child("Email_Pegawai").value.toString()
                namaPegawai = dataSnapshot.child("Nama_Pegawai").value.toString()
                jabatanPegawai = dataSnapshot.child("Nama_Jabatan").value.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getKeranjang() {
        rv_pembayaran.layoutManager = GridLayoutManager(this, 1)
        rv_pembayaran.setHasFixedSize(true)
        val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val refKeranjang = FirebaseDatabase.getInstance().getReference("Keranjang $id")

        refKeranjang.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    rv_pembayaran.visibility = View.VISIBLE
                    cl_keranjang_kosong.visibility = View.GONE
                    keranjangList.clear()
                    var i = 0
                    var totalKeranjang = 0
                    var diskon = 0
                    for (Keranjang in snapshot.children) {
                        //DataKeranjang
                        val keranjang = Keranjang.getValue(classKeranjang::class.java)
                        keranjangList.add(keranjang!!)
                        //Total Harga di Keranjang
                        if (Keranjang.child("total").exists()) {
                            totalKeranjang += Integer.parseInt(Keranjang.child("total").getValue(String::class.java)!!.replace(",00", "").replace(".", "").replace("Rp ", ""))
                        }
                        //Total Diskon di Keranjang
                        if (Keranjang.child("diskon").exists()) {
                            diskon += Integer.parseInt(Keranjang.child("diskon").getValue(String::class.java)!!.replace(",00", "").replace(".", "").replace("Rp ", ""))
                        }
                        //total produk di keranjang
                        i += 1
                    }
                    val totalString = Rp.format(totalKeranjang)
                    val total = totalString.substring(0, 2) + " " + totalString.substring(2, totalString.length)
                    val diskonString = Rp.format(diskon)
                    val disk = diskonString.substring(0, 2) + " " + diskonString.substring(2, diskonString.length)
                    val subs = Rp.format(totalKeranjang + diskon)
                    val subst = subs.substring(0, 2) + " " + subs.substring(2, subs.length)

                    tv_sub_sheet.text = subst
                    tv_diskon_sheet.text = disk
                    tv_subtotal_keranjang.text = total

                    rv_pembayaran.adapter = adapterKeranjang(keranjangList)

                } else {
                    startActivity(Intent(this@pembayaran, penjualan::class.java))
                    finish()
                    cl_keranjang_kosong.visibility = View.VISIBLE
                    rv_pembayaran.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

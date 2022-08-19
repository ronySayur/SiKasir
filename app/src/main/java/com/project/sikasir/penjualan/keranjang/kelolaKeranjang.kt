package com.project.sikasir.penjualan.keranjang

import android.app.AlertDialog
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import com.project.sikasir.produk.produk.classProduk
import kotlinx.android.synthetic.main.kelolakeranjang.*
import java.text.NumberFormat
import java.util.*

class kelolaKeranjang : AppCompatActivity() {
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    val refProduk = FirebaseDatabase.getInstance().getReference("Produk")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kelolakeranjang)
        groupDiskon.visibility = View.GONE

        val Nama_Produk: String = intent.getStringExtra("Nama_Produk").toString()
        val jumlahProduk: String = intent.getStringExtra("Jumlah_Produk").toString()
        val harga: String = intent.getStringExtra("Harga").toString()
        val subTotal: String = intent.getStringExtra("stot").toString()
        val nama_Diskon: String = intent.getStringExtra("Nama_Diskon").toString()
        val diskon: String = intent.getStringExtra("Diskon").toString()

        tv_harga_keranjang.text = harga
        tv_jumlah_produk.text = jumlahProduk
        tv_namaitem.text = Nama_Produk

        if (subTotal == "null") {
            tv_sub_total.text = harga
        } else {
            tv_sub_total.text = subTotal
        }

        if (diskon.isEmpty() || diskon == "0" || diskon == "null") {
            switchPembayaran.isChecked = false
            tv_sub_total_judul.text = "Total"
            groupDiskon.visibility = View.GONE
            tv_diskon_keranjang.visibility = View.GONE
            tv_diskon_judul.visibility = View.GONE
            tv_total_judul.visibility = View.GONE
            tv_total_keranjang.visibility = View.GONE
            tv_total_keranjang.text = "0"
        } else {
            switchPembayaran.isChecked = true
            tv_sub_total_judul.text = "Sub-total"
            groupDiskon.visibility = View.VISIBLE
            tv_diskon_keranjang.visibility = View.VISIBLE
            tv_diskon_judul.visibility = View.VISIBLE
            tv_total_judul.visibility = View.VISIBLE
            tv_total_keranjang.visibility = View.VISIBLE

            edDiskonRp.setText(diskon.replace(",00", "").filter { it.isDigit() })
            tv_diskon_keranjang.text = diskon

            totalKeranjang(
                Integer.parseInt(tv_sub_total.text.toString().replace(",00", "").filter { it.isDigit() })
                        - Integer.parseInt(tv_diskon_keranjang.text.toString().replace(",00", "").filter { it.isDigit() })
            )

            if (nama_Diskon.isNotEmpty()) {
                edNamaDiskon.setText(nama_Diskon)
            }
        }

        switchPembayaran.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tv_sub_total_judul.text = "Sub-total"
                groupDiskon.visibility = View.VISIBLE
                tv_diskon_keranjang.visibility = View.VISIBLE
                tv_diskon_judul.visibility = View.VISIBLE
                tv_total_judul.visibility = View.VISIBLE
                tv_total_keranjang.visibility = View.VISIBLE
            } else {
                tv_sub_total_judul.text = "Total"
                groupDiskon.visibility = View.GONE
                tv_diskon_keranjang.visibility = View.GONE
                tv_diskon_judul.visibility = View.GONE
                tv_total_judul.visibility = View.GONE
                tv_total_keranjang.visibility = View.GONE
            }
        }

        plus.setOnClickListener {
            penambahan()
        }

        minus.setOnClickListener {
            if (tv_jumlah_produk.text.toString().toInt() <= 0) {
                hapusKeranjang()
            } else {
                pengurangan()
            }
        }

        cekStok()

        toggle.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            Toast.makeText(this, "Diskon berdasarkan " + radio.text, Toast.LENGTH_SHORT).show()

            when (radio) {
                tog_persen -> {
                    edDiskonRp.setText("")
                    edDiskonRp.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            val jumlah = tv_jumlah_produk.text.toString().toInt()
                            val harga = tv_harga_keranjang.text.toString().filter { it.isDigit() }.toInt()
                            val diskon = edDiskonRp.text.toString().filter { it.isDigit() }
                            val stotal = tv_sub_total.text.toString().replace(",00", "").filter { it.isDigit() }.toInt()

                            if (diskon.isNotEmpty()) {
                                //(jumlah * harga) - (diskon * sub-total)/100
                                val diskonPersen = (jumlah * harga) - (Integer.parseInt(diskon) * Integer.parseInt(stotal.toString())) / 100

                                totalKeranjang(diskonPersen)
                                totalDiskon(Integer.parseInt(stotal.toString()) - diskonPersen)
                            } else {
                                edDiskonRp.setText("0")
                            }
                        }

                        override fun afterTextChanged(p0: Editable?) {}
                    })
                }
                tog_rp -> {
                    edDiskonRp.setText("")
                    edDiskonRp.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            val jumlah = tv_jumlah_produk.text.toString().toInt()
                            val harga = tv_harga_keranjang.text.toString().filter { it.isDigit() }.toInt()
                            val diskon = edDiskonRp.text.toString().filter { it.isDigit() }
                            val stotal = tv_sub_total.text.toString().replace(",00", "").filter { it.isDigit() }.toInt()

                            if (diskon.isNotEmpty()) {
                                val diskonRupiah = (jumlah * harga) - Integer.parseInt(diskon) //(jumlah * harga) - diskon

                                totalKeranjang(diskonRupiah)
                                totalDiskon(Integer.parseInt(stotal.toString()) - diskonRupiah)
                            } else {
                                edDiskonRp.setText("0")
                            }
                        }

                        override fun afterTextChanged(p0: Editable?) {}
                    })
                }
            }
        }

        cl_hapus.setOnClickListener {
            hapusKeranjang()
            refProduk.child(tv_namaitem.text.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val prdk = snapshot.getValue(classProduk::class.java)
                        if (prdk != null) {
                            //kembalikan Stok
                            prdk.stok = (prdk.stok?.toInt()?.plus(Integer.parseInt(jumlahProduk))).toString()

                            refProduk.child(prdk.id_Produk!!).setValue(prdk)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        imageView23.setOnClickListener { finish() }

        btn_simpan.setOnClickListener {
            if (tv_jumlah_produk.text.toString().toInt() == 0) {
                hapusKeranjang()
            } else {
                Toast.makeText(this, "Keranjang berhasil diubah", Toast.LENGTH_SHORT).show()
                updateData(
                    Nama_Produk, harga,
                    tv_jumlah_produk.text.toString(),
                    edNamaDiskon.text.toString(),
                    tv_diskon_keranjang.text.toString(),
                    tv_total_keranjang.text.toString()
                )
                finish()
            }
        }

    }

    private fun cekStok() {
        val id_Produk: String = intent.getStringExtra("id_Produk").toString()
        refProduk.child(id_Produk).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val stok = snapshot.child("stok")
                    if (stok.toString().isNotEmpty()) {
                        if (stok.value.toString().toInt() < 1) {
                            plus.isEnabled = false
                            Toast.makeText(this@kelolaKeranjang, "Stok Produk Habis!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun penambahan() {
        val id_Produk: String = intent.getStringExtra("id_Produk").toString()
        val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val refKeranjang = FirebaseDatabase.getInstance().getReference("Keranjang $id")
        refKeranjang.child(tv_namaitem.text.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val krnjg = snapshot.getValue(classKeranjang::class.java)
                    if (krnjg != null) {
                        //+1 jumlah_Produk
                        krnjg.jumlah_Produk = (krnjg.jumlah_Produk?.toInt()?.plus(1)).toString()
                        tv_jumlah_produk.text = krnjg.jumlah_Produk

                        //jumlah_Produk * harga
                        val totalString = Rp.format((krnjg.harga!!.filter { it.isDigit() }.toDouble() * krnjg.jumlah_Produk!!.toDouble()))
                        krnjg.total = totalString.substring(0, 2) + " " + totalString.substring(2, totalString.length)
                        tv_sub_total.text = krnjg.total

                        refKeranjang.child(krnjg.nama_Produk!!).setValue(krnjg)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        refProduk.child(id_Produk).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val prdk = snapshot.getValue(classProduk::class.java)
                    val stok = snapshot.child("stok")

                    if (stok.toString().isNotEmpty()) {
                        if (stok.value.toString().toInt() < 2) {
                            plus.isEnabled = false
                            Toast.makeText(this@kelolaKeranjang, "Stok Produk Habis!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (prdk != null) {
                        //kembalikan Stok
                        prdk.stok = (prdk.stok?.toInt()?.minus(1)).toString()

                        refProduk.child(prdk.id_Produk!!).setValue(prdk)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun pengurangan() {
        val id_Produk: String = intent.getStringExtra("id_Produk").toString()
        val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val refKeranjang = FirebaseDatabase.getInstance().getReference("Keranjang $id")
        refKeranjang.child(tv_namaitem.text.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val krnjg = snapshot.getValue(classKeranjang::class.java)
                    if (krnjg != null) {
                        //-1 jumlah_Produk
                        krnjg.jumlah_Produk = (krnjg.jumlah_Produk?.toInt()?.minus(1)).toString()
                        tv_jumlah_produk.text = krnjg.jumlah_Produk

                        //jumlah_Produk * harga
                        val totalString = Rp.format((krnjg.harga!!.filter { it.isDigit() }.toDouble() * krnjg.jumlah_Produk!!.toDouble()))
                        krnjg.total = totalString.substring(0, 2) + " " + totalString.substring(2, totalString.length)
                        tv_sub_total.text = krnjg.total

                        refKeranjang.child(krnjg.nama_Produk!!).setValue(krnjg)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        refProduk.child(id_Produk).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val prdk = snapshot.getValue(classProduk::class.java)
                    if (prdk != null) {
                        prdk.stok = (prdk.stok?.toInt()?.plus(1)).toString()

                        refProduk.child(prdk.id_Produk!!).setValue(prdk)
                    }
                    plus.isEnabled = true
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun hapusKeranjang() {
        val Nama_Produk: String = intent.getStringExtra("Nama_Produk").toString()
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Yakin Untuk Menghapus Data?").setCancelable(false).setPositiveButton("Ya") { dialog, id ->
            val refKeranjang = FirebaseDatabase.getInstance().reference.child("Keranjang").child(Nama_Produk)
            refKeranjang.removeValue()
            Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
            finish()
        }.setNegativeButton("Tidak") { dialog, id -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    private fun updateData(Nama_Produk: String, Harga: String, jumlah_Produk: String, nama_Diskon: String, diskon: String, total: String) {
        val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val refKeranjang = FirebaseDatabase.getInstance().getReference("Keranjang $id")

        val keranjang = mapOf<String, String>(
            "nama_Produk" to Nama_Produk,
            "harga" to Harga,
            "jumlah_Produk" to jumlah_Produk,
            "nama_Diskon" to nama_Diskon,
            "diskon" to diskon,
            "total" to total
        )

        refKeranjang.child(Nama_Produk).updateChildren(keranjang)
    }

    private fun totalKeranjang(hasil: Int) {
        val totalString = Rp.format(hasil)
        val total = totalString.substring(0, 2) + " " + totalString.substring(2, totalString.length)
        tv_total_keranjang.text = total
    }

    private fun totalDiskon(hasil: Int) {
        val totalString = Rp.format(hasil)
        val total = totalString.substring(0, 2) + " " + totalString.substring(2, totalString.length)
        tv_diskon_keranjang.text = total
    }
}
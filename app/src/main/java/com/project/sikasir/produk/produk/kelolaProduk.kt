package com.project.sikasir.produk.produk

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.sikasir.R
import com.project.sikasir.produk.scanBarcodeTambahProduk
import com.project.sikasir.supplier.classSupplier
import com.project.sikasir.toko.classToko
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.produk_kelola.*
import java.text.NumberFormat
import java.util.*


class kelolaProduk : AppCompatActivity() {
    private lateinit var reference: DatabaseReference
    private lateinit var storage: StorageReference
    private var PHOTO_MAX: Int = 1
    private lateinit var photo_location: Uri
    val id_Produk = Calendar.getInstance().time.time.toString()
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    private var USERNAME_KEY = "username_key"
    private var username_key = ""
    private var username_key_new = ""
    var namaPegawai = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.produk_kelola)
        getUsernameLocal()
        getSup()
        getT()

        val QR: String = intent.getStringExtra("DataQR").toString()
        val edit: String = intent.getStringExtra("Edit").toString()

        onClick()
        setSwitch()

        if (edit == "true") {
            setEditProduk()
            btnSimpanProduk.setOnClickListener {
                edStok.visibility = View.GONE
                if (edNamaProduk.text.isEmpty()) {
                    edNamaProduk.error = "Nama Produk tidak boleh kosong"
                } else {
                    if (edHargaJual.text!!.isEmpty()) {
                        edHargaJual.error = "Harga Jual tidak boleh kosong"
                    } else {
                        if (edHargaModal.text!!.isEmpty()) {
                            edHargaModal.error = "Harga Modal tidak boleh kosong"
                        } else {
                            if (edMerek.text.isEmpty()) {
                                edMerek.error = "Merek tidak boleh kosong"
                            } else {
                                if (edKategori.text.isEmpty()) {
                                    edKategori.error = "Kategori Harus diisi"
                                } else {
                                    editProduk()
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        } else {
            cv_hapus.visibility = View.GONE
            btnSimpanProduk.setOnClickListener {
                if (edNamaProduk.text.isEmpty()) {
                    edNamaProduk.error = "Nama Produk tidak boleh kosong"
                } else {
                    if (edHargaJual.text!!.isEmpty()) {
                        edHargaJual.error = "Harga Jual tidak boleh kosong"
                    } else {
                        if (edHargaModal.text!!.isEmpty()) {
                            edHargaJual.error = "Harga Modal tidak boleh kosong"
                        } else {
                            if (edMerek.text.isEmpty()) {
                                edMerek.error = "Nomor HP tidak boleh kosong"
                            } else {
                                if (edKategori.text.isEmpty()) {
                                    edKategori.error = "Kategori Harus diisi"
                                } else {
                                    if (edStok.text.isEmpty()) {
                                        edStok.error = "Stok Harus diisi"
                                    } else {
                                        tambahProduk()
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateData(Nama_Produk: String, stok: String, Harga_Jual: String, Merek: String, Kategori: String, Harga_Modal: String, Barcode: String, nama_Vendor: String, namaPegawai: String) {
        val kode: String = intent.getStringExtra("id_Produk").toString()
        val refProduk = FirebaseDatabase.getInstance().getReference("Produk")

        val produk = mapOf<String, String>(
            "nama_Produk" to Nama_Produk,
            "harga_Jual" to Harga_Jual,
            "merek" to Merek,
            "kategori" to Kategori,
            "harga_Modal" to Harga_Modal,
            "barcode" to Barcode,
            "nama_Vendor" to nama_Vendor,
            "stok" to stok,
            "namaPegawai" to namaPegawai
        )

        refProduk.child(kode).updateChildren(produk)
    }

    private fun deleteProduk() {
        val id_Produk: String = intent.getStringExtra("id_Produk").toString()
        FirebaseDatabase.getInstance().reference.child("Produk").child(id_Produk).removeValue()
        Toast.makeText(this@kelolaProduk, "Data berhasil terhapus", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun editProduk() {
        val id_Produk: String = intent.getStringExtra("id_Produk").toString()

        val namaProduk: String = edNamaProduk.text.toString()
        val hargaJual: String = edHargaJual.text.toString()
        val merek: String = edMerek.text.toString()
        val kategori: String = edKategori.text.toString()
        val hargaModal: String = edHargaModal.text.toString()
        val barcode: String = edBarcode.text.toString()
        val stok: String = edStok.text.toString()
        val nama_Vendor: String = spinKategori.selectedItem.toString()

        val refProduk = FirebaseDatabase.getInstance().reference.child("Produk").child(id_Produk)
        refProduk.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                updateData(
                    namaProduk,
                    stok,
                    hargaJual,
                    merek,
                    kategori,
                    hargaModal,
                    barcode,
                    nama_Vendor,
                    namaPegawai
                )

                if (select_photo.alpha == 0f) {
                    uploadPhoto()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        Toast.makeText(this, "$namaProduk Berhasil Dirubah", Toast.LENGTH_SHORT).show()
    }

    private fun tambahProduk() {
        val namaProduk: String = edNamaProduk.text.toString()
        val hargaJual: String = edHargaJual.text.toString()
        val merek: String = edMerek.text.toString()
        val kategori: String = edKategori.text.toString()
        val hargaModal: String = edHargaModal.text.toString()
        val barcode: String = edBarcode.text.toString()
        val stok: String = edStok.text.toString()
        val nama_Vendor: String = spinKategori.selectedItem.toString()

        val refProduk = FirebaseDatabase.getInstance().reference.child("Produk").child(id_Produk)
        refProduk.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapProduk: DataSnapshot) {
                if (!snapProduk.exists()) {

                    if (select_photo.alpha == 0f) {
                        uploadPhoto()
                    }
                    snapProduk.ref.child("id_Produk").setValue(id_Produk)
                    snapProduk.ref.child("nama_Produk").setValue(namaProduk)
                    snapProduk.ref.child("harga_Jual").setValue(hargaJual)
                    snapProduk.ref.child("merek").setValue(merek)
                    snapProduk.ref.child("kategori").setValue(kategori)
                    snapProduk.ref.child("stok").setValue(stok)
                    snapProduk.ref.child("nama_Vendor").setValue(nama_Vendor)
                    snapProduk.ref.child("namaPegawai").setValue(namaPegawai)
/*                    snapProduk.ref.child("nama_Toko").setValue(spinT.selectedItem.toString())*/
                    snapProduk.ref.child("harga_Modal").setValue(hargaModal)
                    snapProduk.ref.child("barcode").setValue(barcode)
                } else {
                    val alert = AlertDialog.Builder(this@kelolaProduk)
                    alert.setTitle("Peringatan")
                    alert.setMessage("Data Sudah Ada!")
                    alert.setPositiveButton("OK", null)
                    alert.show()
                }
            }

            override fun onCancelled(dataSnapshot: DatabaseError) {}
        })
        Toast.makeText(this, "$namaProduk Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
    }

    private fun setEditProduk() {
        cv_hapus.visibility = View.VISIBLE
        switch1.isChecked = true
        tv_opsional2.visibility = View.VISIBLE
        edBarcode.visibility = View.VISIBLE
        ivToAddQR.visibility = View.VISIBLE

        val nama: String = intent.getStringExtra("Nama_Produk").toString()
        val hJual: String = intent.getStringExtra("Harga_Jual").toString()
        val kategori: String = intent.getStringExtra("Kategori").toString()
        val harga_modal: String = intent.getStringExtra("harga_modal").toString()
        val barcode: String = intent.getStringExtra("Barcode").toString()
        val merek: String = intent.getStringExtra("Merek").toString()
        val foto: String = intent.getStringExtra("Foto").toString()
        val stok: String = intent.getStringExtra("Stok").toString()

        if (barcode != "null") {
            edBarcode.setText(barcode)
        }

        Picasso.get().load(foto).centerCrop().fit().into(iv_produk)

        edNamaProduk.setText(nama)
        edHargaJual.setText(hJual)
        edHargaModal.setText(harga_modal)
        edKategori.setText(kategori)
        edMerek.setText(merek)
        edStok.setText(stok)
    }

    private fun setSwitch() {
        if (switch1.isChecked) {
            textView17.setOnClickListener {
                switch1.isChecked = false
                tv_opsional2.visibility = View.GONE
                edBarcode.visibility = View.GONE
                ivToAddQR.visibility = View.GONE
            }
        } else {
            textView17.setOnClickListener {
                switch1.isChecked = true
                tv_opsional2.visibility = View.VISIBLE
                edBarcode.visibility = View.VISIBLE
                ivToAddQR.visibility = View.VISIBLE
            }
        }
    }

    private fun onClick() {
        switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The switch enabled
                edBarcode.visibility = View.VISIBLE
                tv_opsional2.visibility = View.VISIBLE
                ivToAddQR.visibility = View.VISIBLE
            } else {
                // The switch disabled
                tv_opsional2.visibility = View.GONE
                edBarcode.visibility = View.GONE
                ivToAddQR.visibility = View.GONE
            }
        }

        cv_hapus.setOnClickListener {
            val builder = AlertDialog.Builder(this@kelolaProduk)
            builder.setMessage("Yakin Untuk Menghapus Data?")
                .setCancelable(false)
                .setPositiveButton("Ya") { _, _ ->
                    deleteProduk()
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        ivToAddQR.setOnClickListener { startActivity(Intent(this, scanBarcodeTambahProduk::class.java)) }

        tvA7toA6.setOnClickListener { finish() }

/*        edKategori.setOnClickListener { onClickKategori().show(supportFragmentManager, kategori) }*/

        select_photo.setOnClickListener { findPhoto() }
        cv_btnupload.setOnClickListener { findPhoto() }
    }

    private fun findPhoto() {
        val pictureIntent = Intent()
        pictureIntent.type = "image/*"
        pictureIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(pictureIntent, PHOTO_MAX)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PHOTO_MAX && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            photo_location = data.data!!
            Picasso.get().load(photo_location).centerCrop().fit().into(iv_produk)
            select_photo.alpha = 0f
        }
    }

    fun getFileExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = contentResolver
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadPhoto() {
        btnSimpanProduk.isEnabled = false
        "Loading ...".also { btnSimpanProduk.text = it }

        storage = FirebaseStorage.getInstance().reference.child("Photoproduk").child(edNamaProduk.text.toString())

        val mStorageReference: StorageReference = storage.child(System.currentTimeMillis().toString() + "." + getFileExtension(photo_location))

        mStorageReference.putFile(photo_location)
            .addOnFailureListener { Toast.makeText(this, "Failure Upload", Toast.LENGTH_SHORT).show() }
            .addOnSuccessListener {
                mStorageReference.downloadUrl.addOnSuccessListener { uri ->
                    reference.ref.child("Foto").setValue(uri.toString())
                }

            }.addOnCompleteListener {}
    }


    private fun getT() {
        val refToko = FirebaseDatabase.getInstance().getReference("Toko")
        refToko.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<String>()
                if (snapshot.exists()) {
                    for (s in snapshot.children) {
                        s.getValue(classToko::class.java)?.let {
                            it.nama_Toko?.let { it1 ->
                                list.add(it1)
                            }
                        }
                    }
                    spinT.adapter = ArrayAdapter(this@kelolaProduk, android.R.layout.simple_spinner_item, list)
                } else {
                    spinT.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getSup() {
        val refSup = FirebaseDatabase.getInstance().getReference("Supplier")
        refSup.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<String>()
                if (snapshot.exists()) {
                    for (s in snapshot.children) {
                        s.getValue(classSupplier::class.java)?.let {
                            it.nama_Vendor?.let { it1 ->
                                list.add(it1)
                            }
                        }
                    }
                    spinKategori.adapter = ArrayAdapter(this@kelolaProduk, android.R.layout.simple_spinner_item, list)
                } else {
                    rv_spin.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getUsernameLocal() {
        val sharedPreference: SharedPreferences = getSharedPreferences(USERNAME_KEY, Context.MODE_PRIVATE)
        username_key_new = sharedPreference.getString(username_key, "").toString()
        val reference = FirebaseDatabase.getInstance().reference.child("Pegawai").child(username_key_new)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                namaPegawai = dataSnapshot.child("Nama_Pegawai").value.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
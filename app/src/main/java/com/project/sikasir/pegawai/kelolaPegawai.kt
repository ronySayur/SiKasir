package com.project.sikasir.pegawai

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.sikasir.R
import com.project.sikasir.toko.classToko
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.pegawai_kelola.*


class kelolaPegawai : AppCompatActivity() {
    //Firebase RealtimeDatabase
    private var akses = ""
    private lateinit var refPegawai: DatabaseReference
    private lateinit var storage: StorageReference

    private var PHOTO_MAX: Int = 1
    private lateinit var photo_location: Uri

    private var USERNAME_KEY = "username_key"
    private var username_key = ""
    private var username_key_new = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pegawai_kelola)
        getToko()


        //Set
        val tedit: String = intent.getStringExtra("Edit").toString()
        getUsernameLocal()
        edHp.setText("+62")

        onClick()
        spinnerJabatan()

        //Jika Edit true
        if (tedit == "true") {
            editPegawai()
        } else {
            tambahPegawai()
        }
    }

    private fun editPegawai() {
        val tNama_Pegawai: String = intent.getStringExtra("Nama_Pegawai").toString()
        val tNama_Jabatan: String = intent.getStringExtra("Nama_Jabatan").toString()
        val tEmail: String = intent.getStringExtra("Email").toString()
        val tHP: String = intent.getStringExtra("HP").toString()
        val tFoto: String = intent.getStringExtra("Foto").toString()
        val tPin: String = intent.getStringExtra("Pin").toString()

        "Edit Pegawai".also { tv_judul.text = it }
        edNamaPegawai.setText(tNama_Pegawai)
        edNamaJabatan.setText(tNama_Jabatan)
        edemailpegawai.setText(tEmail)
        edHp.setText(tHP)
        edpin.setText(tPin)


        Picasso.get().load(tFoto).centerCrop().fit().into(iv_pegawai)
        selectphoto_button_register.visibility = View.GONE


        btn_ubahpass.visibility = View.VISIBLE
        btn_ubahemail.visibility = View.VISIBLE
        edpin.visibility = View.GONE

        btnsimpan_pegawai.setOnClickListener(View.OnClickListener {
            if (edNamaPegawai.text.isEmpty()) {
                edNamaPegawai.error = "Nama pegawai tidak boleh kosong"
            } else {
                if (edNamaJabatan.text.isEmpty()) {
                    edNamaJabatan.error = "Nama jabatan tidak boleh kosong"
                } else {
                    if (edHp.text.isEmpty()) {
                        edHp.error = "Nomor HP tidak boleh kosong"
                    } else {
                        if (edemailpegawai.text.isEmpty()) {
                            edemailpegawai.error = "Email tidak boleh kosong"
                        } else {
                            if (edpin.text.toString().isEmpty()) {
                                edpin.error = "Pin Harus diisi"
                            } else {
                                if (edpin.text.toString().trim().length < 6) {
                                    edpin.error = "Pin Harus 6 digit angka"
                                } else {
                                    if (edemailpegawai.text.toString().isValidEmail()) {
                                        refPegawai = FirebaseDatabase.getInstance().reference.child("Pegawai")

                                        val user = mapOf<String, String>(
                                            "Nama_Pegawai" to edNamaPegawai.text.toString(),
                                            "HP" to edHp.text.toString(),
                                            "Hak_Akses" to akses,
                                            "nama_Toko" to spinToko.selectedItem.toString(),
                                            "Nama_Jabatan" to edNamaJabatan.text.toString(),
                                            "Email_Pegawai" to edemailpegawai.text.toString(),
                                            "Pin" to edpin.text.toString()
                                        )

                                        refPegawai.child(edemailpegawai.text.toString().replace(".", ",")).updateChildren(user)

                                        if (selectphoto_button_register.alpha == 0f) {
                                            uploadPhoto()
                                        }

                                        val gotoHomeIntent = Intent(this@kelolaPegawai, pegawai::class.java)
                                        startActivity(gotoHomeIntent)
                                        finish()
                                        Toast.makeText(this@kelolaPegawai, edNamaPegawai.text.toString() + " Berhasil dirubah", Toast.LENGTH_SHORT).show()
                                    } else {
                                        edemailpegawai.error = "Format email tidak benar"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun tambahPegawai() {
        "Tambah Pegawai".also { tv_judul.text = it }
        cv_hapus.visibility = View.GONE

        btnsimpan_pegawai.setOnClickListener(View.OnClickListener {
            if (edNamaPegawai.text.isEmpty()) {
                edNamaPegawai.error = "Nama pegawai tidak boleh kosong"
            } else {
                if (edNamaJabatan.text.isEmpty()) {
                    edNamaJabatan.error = "Nama jabatan tidak boleh kosong"
                } else {
                    if (edHp.text.isEmpty()) {
                        edHp.error = "Nomor HP tidak boleh kosong"
                    } else {
                        if (edemailpegawai.text.toString().isEmpty()) {
                            edemailpegawai.error = "Email tidak boleh kosong"
                        } else {
                            if (edpin.text.toString().isEmpty()) {
                                edpin.error = "Pin Harus diisi"
                            } else {
                                if (edpin.text.toString().trim().length < 6) {
                                    edpin.error = "Pin Harus 6 digit angka"
                                } else {
                                    if (edemailpegawai.text.toString().isValidEmail()) {
                                        refPegawai = FirebaseDatabase.getInstance().reference.child("Pegawai").child(edemailpegawai.text.toString().replace(".", ","))
                                        refPegawai.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    val alert = AlertDialog.Builder(this@kelolaPegawai)
                                                    alert.setTitle("Peringatan")
                                                    alert.setMessage("Data dengan email " + edemailpegawai.text.toString() + " Sudah Ada!")
                                                    alert.setPositiveButton("Ok", null)
                                                    alert.show()
                                                } else {
                                                    if (selectphoto_button_register.alpha == 0f) {
                                                        uploadPhoto()
                                                    }

                                                    dataSnapshot.ref.child("Email_Pegawai").setValue(edemailpegawai.text.toString())
                                                    dataSnapshot.ref.child("Nama_Pegawai").setValue(edNamaPegawai.text.toString())
                                                    dataSnapshot.ref.child("Pin").setValue(edpin.text.toString())
                                                    dataSnapshot.ref.child("Hak_Akses").setValue(akses)
                                                    dataSnapshot.ref.child("nama_Toko").setValue(spinToko.selectedItem.toString())
                                                    dataSnapshot.ref.child("HP").setValue(edHp.text.toString())
                                                    dataSnapshot.ref.child("Nama_Jabatan").setValue(edNamaJabatan.text.toString())

                                                    val gotoHomeIntent = Intent(this@kelolaPegawai, pegawai::class.java)
                                                    startActivity(gotoHomeIntent)
                                                    finish()
                                                    Toast.makeText(this@kelolaPegawai, edNamaPegawai.text.toString() + " Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                                                }
                                            }

                                            override fun onCancelled(dataSnapshot: DatabaseError) {}
                                        })
                                    } else {
                                        edemailpegawai.error = "Format email tidak benar"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }


    private fun onClick() {
        tvA7toA6.setOnClickListener {
            startActivity(Intent(this@kelolaPegawai, pegawai::class.java))
            finish()
        }
        btn_ubahemail.setOnClickListener {
            edemailpegawai.visibility = View.VISIBLE
            btn_ubahemail.visibility = View.GONE
        }
        btn_ubahpass.setOnClickListener {
            edpin.visibility = View.VISIBLE
            btn_ubahpass.visibility = View.GONE
        }
        cv_selectImage.setOnClickListener { findPhoto() }
        selectphoto_button_register.setOnClickListener { findPhoto() }
        cv_hapus.setOnClickListener { hapusPegawai() }
    }

    private fun hapusPegawai() {
        val builder = AlertDialog.Builder(this@kelolaPegawai)
        builder.setMessage("Yakin Untuk Menghapus Data?")
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
                refPegawai = FirebaseDatabase.getInstance().reference.child("Pegawai").child(edemailpegawai.text.toString().replace(".", ","))
                refPegawai.removeValue()
                startActivity(Intent(this@kelolaPegawai, pegawai::class.java))
                finish()
            }
            .setNegativeButton("Tidak") { dialog, id -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    private fun spinnerJabatan() {
        val jab = resources.getStringArray(R.array.Jabatan)
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jab)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    akses = jab[position].toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun uploadPhoto() {
        btnsimpan_pegawai.isEnabled = false
        "Loading ...".also { btnsimpan_pegawai.text = it }

        storage = FirebaseStorage.getInstance().reference.child("Photousers").child(edemailpegawai.text.toString())

        val mStorageReference: StorageReference = storage.child(System.currentTimeMillis().toString() + "." + getFileExtension(photo_location))

        mStorageReference.putFile(photo_location)
            .addOnFailureListener { Toast.makeText(this, "Failure Upload", Toast.LENGTH_SHORT).show() }
            .addOnSuccessListener {
                mStorageReference.downloadUrl.addOnSuccessListener { uri ->
                    refPegawai.ref.child("Foto").setValue(uri.toString())
                }

            }.addOnCompleteListener {}
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
            Picasso.get().load(photo_location).centerCrop().fit().into(iv_pegawai)
            selectphoto_button_register.alpha = 0f

        }
    }

    fun getFileExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = contentResolver
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun getUsernameLocal() {
        val sharedPreference: SharedPreferences = getSharedPreferences(USERNAME_KEY, Context.MODE_PRIVATE)
        username_key_new = sharedPreference.getString(username_key, "").toString()
    }

    private fun getToko() {
        val refToko = FirebaseDatabase.getInstance().getReference("Toko")
        refToko.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapToko: DataSnapshot) {
                val list = ArrayList<String>()
                if (snapToko.exists()) {
                    for (s in snapToko.children) {
                        s.getValue(classToko::class.java)?.let {
                            it.nama_Toko?.let { it1 ->
                                list.add(it1)
                            }
                        }
                    }
                    spinToko.adapter = ArrayAdapter(this@kelolaPegawai, android.R.layout.simple_spinner_item, list)
                } else {
                    rv_spin.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}
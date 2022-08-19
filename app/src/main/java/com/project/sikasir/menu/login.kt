package com.project.sikasir.menu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.project.sikasir.R
import kotlinx.android.synthetic.main.sheet_bottomlogin.*

class login : AppCompatActivity() {
    private lateinit var reference: DatabaseReference
    private var USERNAME_KEY = "username_key"
    private var username_key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pradashboard_login)

        //menuju dashboard
        btn_login.setOnClickListener(View.OnClickListener {
            // ubah state menjadi loading
            btn_login.isEnabled = false
            btn_login.text = "Loading ..."

            val Email: String = etEmailLog.text.toString().replace(".", ",")
            val Pin: String = etPassLog.text.toString()

            //Username kosong
            if (Email.isEmpty()) {
                etEmailLog.error = "Username tidak boleh kosong"
                btn_login.isEnabled = true
                btn_login.text = "Sign In"
            } else {
                //password kosong
                if (Pin.isEmpty()) {
                    etPassLog.error = "Pin tidak boleh kosong"
                    btn_login.isEnabled = true
                    btn_login.text = "Sign In"
                } else {
                    if (Pin.length < 6) {
                        etPassLog.error = "Pin harus 6 digit angka"
                        btn_login.isEnabled = true
                        btn_login.text = "Sign In"
                    } else {
                        reference = FirebaseDatabase.getInstance().reference.child("Pegawai").child(Email)

                        reference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists() && Email != "") {
                                    //Ambil data dari firebase (password)
                                    val passwordFromFirebase: String = dataSnapshot.child("Pin").value.toString()
                                    val Nama: String = dataSnapshot.child("Nama_Pegawai").value.toString()

                                    //validasi password firebase
                                    if (Pin == passwordFromFirebase) {

                                        //menyimpan kepada lokal storage/smartphone
                                        val sharedPreferences: SharedPreferences = getSharedPreferences(USERNAME_KEY, Context.MODE_PRIVATE)
                                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                                        editor.putString(username_key, Email).apply()

                                        //berpindah activity
                                        val gotoHomeIntent = Intent(this@login, dashboard::class.java)
                                        startActivity(gotoHomeIntent)
                                        finish()
                                        Toast.makeText(this@login, "Selamat datang " + Nama, Toast.LENGTH_SHORT).show()

                                        //Password salah
                                    } else {
                                        btn_login.isEnabled = true
                                        btn_login.text = "Sign In"
                                        Toast.makeText(this@login, "Username atau Password Salah", Toast.LENGTH_SHORT).show()
                                    }
                                    //Username/Pass salah
                                } else {
                                    btn_login.isEnabled = true
                                    btn_login.text = "Sign In"
                                    Toast.makeText(this@login, "Username atau Password Salah", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
                    }
                }
            }
        })
    }
}
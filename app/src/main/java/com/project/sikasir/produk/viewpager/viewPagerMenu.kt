package com.project.sikasir.produk.viewpager

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import kotlinx.android.synthetic.main.produk_dashboard.*

class viewPagerMenu : AppCompatActivity() {
    private var USERNAME_KEY = "username_key"
    private var username_key = ""
    private var username_key_new = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.produk_dashboard)

        getUsernameLocal()

        tvA3toA2.setOnClickListener { _ ->
            finish()
        }
    }

    private fun getUsernameLocal() {
        val sharedPreference: SharedPreferences = getSharedPreferences(USERNAME_KEY, Context.MODE_PRIVATE)
        username_key_new = sharedPreference.getString(username_key, "").toString()

        val reference = FirebaseDatabase.getInstance().reference.child("Pegawai").child(username_key_new)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hak = dataSnapshot.child("Hak_Akses").value.toString()

                if (hak == "Pegawai") {
                    tabLayoutproduk.visibility = View.GONE
                    viewPagerProduk.adapter = viewPagerPegawai(supportFragmentManager, lifecycle)
                    TabLayoutMediator(tabLayoutproduk, viewPagerProduk) { tab, position ->
                        when (position) {
                            0 -> {
                                tab.text = "Kelola Produk"
                            }
                        }
                    }.attach()
                } else {
                    viewPagerProduk.adapter = viewPagerPemilik(supportFragmentManager, lifecycle)
                    TabLayoutMediator(tabLayoutproduk, viewPagerProduk) { tab, position ->
                        when (position) {
                            0 -> {
                                tab.text = "Kelola Produk"
                            }
                            1 -> {
                                tab.text = "Pembelian Produk"
                            }
                            2 -> {
                                tab.text = "Informasi Pembelian"
                            }
                        }
                    }.attach()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}


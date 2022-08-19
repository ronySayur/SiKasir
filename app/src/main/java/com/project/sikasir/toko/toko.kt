package com.project.sikasir.toko

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import kotlinx.android.synthetic.main.supplier_menu.g_kosong
import kotlinx.android.synthetic.main.supplier_menu.rv_sup
import kotlinx.android.synthetic.main.toko.*

class toko : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.toko)
        getToko()
        fab_toko.setOnClickListener { startActivity(Intent(this, kelolaToko::class.java)) }
    }

    private fun getToko() {
        rv_sup.layoutManager = GridLayoutManager(this, 1)
        rv_sup.setHasFixedSize(true)
        val TList = arrayListOf<classToko>()
        val refToko = FirebaseDatabase.getInstance().getReference("Toko")

        refToko.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    rv_sup.visibility = View.VISIBLE
                    g_kosong.visibility = View.GONE
                    TList.clear()
                    for (TSnap in snapshot.children) {
                        val s = TSnap.getValue(classToko::class.java)
                        TList.add(s!!)
                    }
                    rv_sup.adapter = adapterToko(TList)
                } else {
                    rv_sup.visibility = View.GONE
                    g_kosong.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}
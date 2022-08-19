package com.project.sikasir.penjualan.penjualan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.provider.Settings.Secure
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*
import com.project.sikasir.R
import com.project.sikasir.laporan.laporan
import com.project.sikasir.menu.aboutMe
import com.project.sikasir.menu.dashboard
import com.project.sikasir.navPack.ClickListener
import com.project.sikasir.navPack.NavigationItemModel
import com.project.sikasir.navPack.NavigationRVAdapter
import com.project.sikasir.navPack.RecyclerTouchListener
import com.project.sikasir.pegawai.pegawai
import com.project.sikasir.penjualan.keranjang.adapterKeranjang
import com.project.sikasir.penjualan.keranjang.classKeranjang
import com.project.sikasir.penjualan.pembayaran.pembayaran
import com.project.sikasir.penjualan.pengaturan
import com.project.sikasir.penjualan.riwayat.riwayatTransaksi
import com.project.sikasir.produk.produk.classProduk
import com.project.sikasir.produk.viewpager.viewPagerMenu
import kotlinx.android.synthetic.main.sheet_bottomtransaksi.*
import kotlinx.android.synthetic.main.transaksi_menu.*
import java.text.NumberFormat
import java.util.*


class penjualan : AppCompatActivity() {
    private lateinit var refPegawai: DatabaseReference

    private var USERNAME_KEY = "username_key"
    private var username_key = ""
    private var username_key_new = ""
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    var toko = ""
    val keranjangList = ArrayList<classKeranjang>()
    val produkList = ArrayList<classProduk>()
    private lateinit var adapter: NavigationRVAdapter

    private val produkAdapter: adapterSearchTransaksi by lazy {
        adapterSearchTransaksi(produkList)
    }

    lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val produkListener = object : adapterPenjualan.ProdukListener {
        override fun AddToCart(produk: classProduk) {
            val refProduk = FirebaseDatabase.getInstance().getReference("Produk")
            produk.id_Produk?.let {
                refProduk.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val prdk = snapshot.getValue(classProduk::class.java)
                            if (prdk != null) {
                                //stok - onClick
                                prdk.stok = (prdk.stok?.toInt()?.minus(1)).toString()

                                refProduk.child(prdk.id_Produk!!).setValue(prdk)
                                getProduk()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }
    private val trasaksiListener = object : adapterPenjualan.TransaksiListener {
        override fun AddToCart(produk: classProduk) {
            val id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val refKeranjang = FirebaseDatabase.getInstance().getReference("Keranjang $id")
            produk.nama_Produk?.let {
                refKeranjang.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val keranjang = snapshot.getValue(classKeranjang::class.java)

                            if (keranjang != null) {
                                //qty
                                keranjang.jumlah_Produk = (keranjang.jumlah_Produk?.toInt()?.plus(1)).toString()

                                //(harga_jual * qty)-diskon
                                val totalString = Rp.format(
                                    (keranjang.harga!!.filter { it.isDigit() }.toDouble() * keranjang.jumlah_Produk!!.toDouble())
                                            - keranjang.diskon!!.replace(",00", "").filter { it.isDigit() }.toDouble()
                                )
                                keranjang.total = totalString.substring(0, 2) + " " + totalString.substring(2, totalString.length)

                                //harga_modal * qty
                                if (keranjang.harga_Modal.toString() == "Rp") {
                                    val totalModal = Rp.format((keranjang.harga_Modal!!.filter { it.isDigit() }.toDouble() * keranjang.jumlah_Produk!!.toDouble()))
                                    keranjang.total_Modal = totalModal.substring(0, 2) + " " + totalModal.substring(2, totalModal.length)
                                }

                                refKeranjang.child(produk.nama_Produk!!).setValue(keranjang)
                            }
                        } else {
                            refKeranjang.child(produk.nama_Produk!!).setValue(
                                classKeranjang(
                                    produk.nama_Produk,
                                    produk.harga_Jual,
                                    produk.harga_Modal,
                                    jumlah_Produk = "1",
                                    nama_Diskon = "",
                                    diskon = "0",
                                    total_Modal = produk.harga_Modal,
                                    total = produk.harga_Jual,
                                    id_Produk = produk.id_Produk
                                )
                            )
                        }
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaksi_menu)

        getUsernameLocal()
        getKeranjang()
        getQR()
        getKategori()

        bottomSheetBehavior()
        navigation_rv()
        onClick()

/*
        spinKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val text = spinKategori.selectedItem.toString()
                rv_transaksi.layoutManager = GridLayoutManager(this@transaksi, 2)
                rv_transaksi.setHasFixedSize(true)

                val refProduk = FirebaseDatabase.getInstance().getReference("Produk")

                refProduk.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            rv_transaksi.visibility = View.VISIBLE
                            cl_produk_kosong.visibility = View.GONE
                            produkList.clear()
                            for (snapshot in snapshot.children) {
                                val kat = snapshot.getValue(classProduk::class.java)
                                produkList.add(kat!!)
                            }
                            rv_transaksi.adapter = adapterTransaksi(produkList, trasaksiListener)
                        } else {
                            cl_produk_kosong.visibility = View.VISIBLE
                            rv_transaksi.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
*/

        if (edCariProdukTransaksi.text.isEmpty()) {
            getProduk()
        }

    }

    private fun onClick() {
        ivToQR.setOnClickListener { startActivity(Intent(this, scanBarcodeTambahTransaksi::class.java)) }

        edCariProdukTransaksi.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    produkList.clear()
                    searchByName(edCariProdukTransaksi.text.toString())
                } else {
                    getProduk()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
    }

    private fun getQR() {
        val QR: String = intent.getStringExtra("DataQR").toString()

        if (QR == "null") {
            edCariProdukTransaksi.setText("")
        } else {
            edCariProdukTransaksi.setText(QR)
        }
    }

    private fun searchByName(search: String) {
        val refProduk = FirebaseDatabase.getInstance().getReference("Produk")
        refProduk.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapProduk in snapshot.children) {
                        val prdk = snapProduk.getValue(classProduk::class.java)
                        if (prdk!!.nama_Produk == search) {
                            produkList.add(prdk)
                        }
                    }
                    produkAdapter.submitList(produkList)
                    rv_transaksi.adapter = adapterPenjualan(produkList, trasaksiListener, produkListener)
                } else {
                    Toast.makeText(applicationContext, "Data does not exist", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getKategori() {
        val refKategori = FirebaseDatabase.getInstance().getReference("Produk")

        refKategori.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<String>()
                if (snapshot.exists()) {
                    for (kategori in snapshot.children) {
                        kategori.getValue(classProduk::class.java)?.let {
                            it.kategori?.let { it1 ->
                                list.add(it1)
                            }
                        }
                    }
                    spinKategori.adapter = ArrayAdapter(this@penjualan, android.R.layout.simple_spinner_item, list)
                } else {
                    rv_spin.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getKeranjang() {
        rv_keranjang.layoutManager = GridLayoutManager(this, 1)
        rv_keranjang.setHasFixedSize(true)
        val id = Secure.getString(contentResolver, Secure.ANDROID_ID)
        val refKeranjang = FirebaseDatabase.getInstance().getReference("Keranjang $id")

        refKeranjang.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    tv_jmlBarang.visibility = View.VISIBLE
                    title_produk.visibility = View.VISIBLE
                    rv_keranjang.visibility = View.VISIBLE
                    cl_keranjang_kosong.visibility = View.GONE
                    keranjangList.clear()
                    var i = 0
                    var totalKeranjang = 0
                    var diskon = 0
                    for (Keranjang in snapshot.children) {
                        //DataKeranjang
                        val keranjang = Keranjang.getValue(classKeranjang::class.java)

                        //Total Harga di Keranjang
                        if (Keranjang.child("total").exists()) {
                            totalKeranjang += Integer.parseInt(Keranjang.child("total").getValue(String::class.java)!!.replace(",00", "").filter { it.isDigit() })
                        }
                        //Total Diskon di Keranjang
                        if (Keranjang.child("diskon").exists()) {
                            diskon += Integer.parseInt(Keranjang.child("diskon").getValue(String::class.java)!!.replace(",00", "").filter { it.isDigit() })
                        }
                        //Total produk di keranjang
                        i += 1

                        keranjangList.add(keranjang!!)
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

                    btnTagih.text = total

                    tv_jmlBarang.text = i.toString()
                    rv_keranjang.adapter = adapterKeranjang(keranjangList)

                    btnTagih.setOnClickListener {
                        val intent = Intent(this@penjualan, pembayaran::class.java)
                        intent.putExtra("tagihan", btnTagih.text.toString())
                        startActivity(intent)
                        finish()
                    }
                } else {
                    cl_keranjang_kosong.visibility = View.VISIBLE
                    rv_keranjang.visibility = View.GONE
                    tv_jmlBarang.visibility = View.GONE
                    title_produk.visibility = View.GONE
                    btnTagih.setOnClickListener {
                        Toast.makeText(this@penjualan, "Isi keranjang terlebih dahulu", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getProduk() {
        rv_transaksi.layoutManager = GridLayoutManager(this, 1)
        rv_transaksi.setHasFixedSize(true)

        val refProduk = FirebaseDatabase.getInstance().getReference("Produk")

        refProduk.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    rv_transaksi.visibility = View.VISIBLE
                    cl_produk_kosong.visibility = View.GONE
                    produkList.clear()
                    for (snapProd in snapshot.children) {
                        val kat = snapProd.getValue(classProduk::class.java)
                        val stok = snapProd.child("stok")

                        if (stok.toString().isNotEmpty()) {
                            if (stok.value.toString().toInt() >= 1) {
                                produkList.add(kat!!)
                            }
                        } else {
                            cl_produk_kosong.visibility = View.VISIBLE
                            rv_spin.visibility = View.GONE
                        }
                    }
                    rv_transaksi.adapter = adapterPenjualan(produkList, trasaksiListener, produkListener)
                } else {
                    cl_produk_kosong.visibility = View.VISIBLE
                    rv_transaksi.visibility = View.GONE
                    rv_spin.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun bottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetTransaksi)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (tv_jmlBarang.text == "0") {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> btnTagih.visibility = View.VISIBLE
                        BottomSheetBehavior.STATE_COLLAPSED -> btnTagih.visibility = View.GONE
                    }
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> ivkosong.visibility = View.VISIBLE
                        BottomSheetBehavior.STATE_COLLAPSED -> ivkosong.visibility = View.GONE
                    }
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> tvkosong.visibility = View.VISIBLE
                        BottomSheetBehavior.STATE_COLLAPSED -> tvkosong.visibility = View.GONE
                    }
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> rv_keranjang.visibility = View.GONE
                    }
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> cl_total.visibility = View.GONE
                    }
                } else {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> btnTagih.visibility = View.VISIBLE
                        BottomSheetBehavior.STATE_COLLAPSED -> btnTagih.visibility = View.GONE
                    }
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> ivkosong.visibility = View.GONE
                    }
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> tvkosong.visibility = View.GONE
                    }
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> rv_keranjang.visibility = View.VISIBLE
                        BottomSheetBehavior.STATE_COLLAPSED -> rv_keranjang.visibility = View.GONE
                    }
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> cl_total.visibility = View.VISIBLE
                        BottomSheetBehavior.STATE_COLLAPSED -> cl_total.visibility = View.GONE
                    }
                }
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> btnTagih.text = tv_jmlBarang.text.toString() + " Barang"
                }
            }
        })
    }

    private fun navigation_rv() {
        navigation_layout.visibility = View.VISIBLE
        drawerLayout = findViewById(R.id.drawer_layout)
        setSupportActionBar(activity_main_toolbar)
        navigation_rv.layoutManager = LinearLayoutManager(this)
        navigation_rv.setHasFixedSize(true)
        navigation_header_img.setImageResource(R.drawable.logoaida)
        tv_titleitems.text = "Transaksi"

        updateAdapter(0)

        val toggle: ActionBarDrawerToggle =
            object : ActionBarDrawerToggle(this, drawerLayout, activity_main_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                override fun onDrawerClosed(drawerView: View) {
                    super.onDrawerClosed(drawerView)
                    try {
                        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    } catch (e: Exception) {
                        e.stackTrace
                    }
                }

                override fun onDrawerOpened(drawerView: View) {
                    super.onDrawerOpened(drawerView)
                    try {
                        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                    } catch (e: Exception) {
                        e.stackTrace
                    }
                }
            }
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun getUsernameLocal() {
        val sharedPreference: SharedPreferences = getSharedPreferences(USERNAME_KEY, Context.MODE_PRIVATE)
        username_key_new = sharedPreference.getString(username_key, "").toString()
    }

    private fun updateAdapter(highlightItemPos: Int) {
        refPegawai = FirebaseDatabase.getInstance().reference.child("Pegawai").child(username_key_new)
        refPegawai.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tv_namaakun.text = dataSnapshot.child("Nama_Pegawai").value.toString()
                tv_nmjabatan.text = dataSnapshot.child("Nama_Jabatan").value.toString()
                toko = dataSnapshot.child("nama_Toko").value.toString().filter { it.isLetterOrDigit() }

                val hak = dataSnapshot.child("Hak_Akses").value.toString()
                if (hak == "Pegawai") {
                    val items = arrayListOf(
                        NavigationItemModel(R.drawable.ic_baseline_home_24, "Beranda"),
                        NavigationItemModel(R.drawable.ic_baseline_camera_alt_24, "Kelola Produk"),
                        NavigationItemModel(R.drawable.ic_baseline_receipt_24, "Transaksi"),
                        NavigationItemModel(R.drawable.ic_baseline_receipt_long_24, "Riwayat Transaksi"),
                        NavigationItemModel(R.drawable.ic_baseline_settings_24, "Pengaturan"),
                    )
                    adapter = NavigationRVAdapter(items, highlightItemPos)
                    navigation_rv.adapter = adapter
                    navigation_rv.addOnItemTouchListener(RecyclerTouchListener(this@penjualan, object : ClickListener {
                        override fun onClick(view: View, position: Int) {
                            when (position) {
                                0 -> {
                                    startActivity(Intent(this@penjualan, dashboard::class.java))
                                    finish()
                                }
                                1 -> {
                                    startActivity(Intent(this@penjualan, viewPagerMenu::class.java))
                                }
                                2 -> {
                                    startActivity(Intent(this@penjualan, penjualan::class.java))
                                    finish()
                                }
                                3 -> {
                                    startActivity(Intent(this@penjualan, riwayatTransaksi::class.java))
                                    finish()
                                }
                                4 -> {
                                    startActivity(Intent(this@penjualan, pengaturan::class.java))
                                    finish()
                                }
                            }
                            if (position != 6 && position != 4) {
                                updateAdapter(position)
                            }
                            Handler().postDelayed({
                            }, 200)
                        }
                    }))
                } else {
                    val items = arrayListOf(
                        NavigationItemModel(R.drawable.ic_baseline_home_24, "Beranda"),
                        NavigationItemModel(R.drawable.ic_baseline_camera_alt_24, "Kelola Produk"),
                        NavigationItemModel(R.drawable.ic_baseline_receipt_24, "Transaksi"),
                        NavigationItemModel(R.drawable.ic_baseline_receipt_long_24, "Riwayat Transaksi"),

                        NavigationItemModel(R.drawable.ic_baseline_people_24, "Pegawai"),
                        NavigationItemModel(R.drawable.ic_baseline_corporate_fare_24, "Laporan"),

                        NavigationItemModel(R.drawable.ic_baseline_settings_24, "Pengaturan"),
                        NavigationItemModel(R.drawable.ic_baseline_account_circle_24, "Tentang Saya")
                    )
                    adapter = NavigationRVAdapter(items, highlightItemPos)
                    navigation_rv.adapter = adapter
                    navigation_rv.addOnItemTouchListener(RecyclerTouchListener(this@penjualan, object : ClickListener {
                        override fun onClick(view: View, position: Int) {
                            when (position) {
                                0 -> {
                                    startActivity(Intent(this@penjualan, dashboard::class.java))
                                    finish()
                                }
                                1 -> {
                                    startActivity(Intent(this@penjualan, viewPagerMenu::class.java))
                                }
                                2 -> {
                                    startActivity(Intent(this@penjualan, penjualan::class.java))
                                    finish()
                                }
                                3 -> {
                                    startActivity(Intent(this@penjualan, riwayatTransaksi::class.java))
                                    finish()
                                }
                                4 -> {
                                    startActivity(Intent(this@penjualan, pegawai::class.java))
                                }
                                5 -> {
                                    startActivity(Intent(this@penjualan, laporan::class.java))
                                    finish()
                                }
                                6 -> {
                                    startActivity(Intent(this@penjualan, pengaturan::class.java))
                                    finish()
                                }
                                7 -> {
                                    startActivity(Intent(this@penjualan, aboutMe::class.java))
                                }
                            }
                            if (position != 6 && position != 4) {
                                updateAdapter(position)
                            }
                            Handler().postDelayed({
                            }, 200)
                        }
                    }))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }
}
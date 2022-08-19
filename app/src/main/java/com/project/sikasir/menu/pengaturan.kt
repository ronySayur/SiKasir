package com.project.sikasir.penjualan

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.converter.ArabicConverter
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.project.sikasir.R
import com.project.sikasir.laporan.laporan
import com.project.sikasir.menu.aboutMe
import com.project.sikasir.menu.dashboard
import com.project.sikasir.menu.login
import com.project.sikasir.navPack.ClickListener
import com.project.sikasir.navPack.NavigationItemModel
import com.project.sikasir.navPack.NavigationRVAdapter
import com.project.sikasir.navPack.RecyclerTouchListener
import com.project.sikasir.pegawai.pegawai
import com.project.sikasir.penjualan.penjualan.penjualan
import com.project.sikasir.penjualan.riwayat.riwayatTransaksi
import com.project.sikasir.produk.viewpager.viewPagerMenu
import kotlinx.android.synthetic.main.pengaturan.*

class pengaturan : AppCompatActivity() {

    private var printing: Printing? = null
    lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NavigationRVAdapter

    //FIREBASE
    private var USERNAME_KEY = "username_key"
    private var username_key = ""
    private var username_key_new = ""
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pengaturan)

        Printooth.init(this)

        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        initViews()
        initListeners()

        //FIREBASE
        getUsernameLocal()

        navigation_layout.visibility = View.VISIBLE

        reference = FirebaseDatabase.getInstance().reference.child("Pegawai").child(username_key_new)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tv_namaakun.text = dataSnapshot.child("Nama_Pegawai").value.toString()
                tv_nmjabatan.text = dataSnapshot.child("Nama_Jabatan").value.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        tvLogoutPengaturan.setOnClickListener { view ->
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Apakah anda yakin untuk keluar?")
                .setCancelable(false)
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent(this@pengaturan, login::class.java)
                    startActivity(intent)
                    finish()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
            val alert = dialogBuilder.create()
            alert.setTitle("Keluar")
            alert.show()
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        setSupportActionBar(activity_main_toolbar)
        navigation_rv.layoutManager = LinearLayoutManager(this)
        navigation_rv.setHasFixedSize(true)
        navigation_header_img.setImageResource(R.drawable.logoaida)
        tv_titleitems.text = "Pengaturan"

        navigation_rv.addOnItemTouchListener(RecyclerTouchListener(this, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                when (position) {
                    0 -> {
                        startActivity(Intent(this@pengaturan, dashboard::class.java))
                        finish()
                    }
                    1 -> {
                        startActivity(Intent(this@pengaturan, viewPagerMenu::class.java))
                    }
                    2 -> {
                        startActivity(Intent(this@pengaturan, penjualan::class.java))
                        finish()
                    }
                    3 -> {
                        startActivity(Intent(this@pengaturan, riwayatTransaksi::class.java))
                        finish()
                    }
                    4 -> {
                        startActivity(Intent(this@pengaturan, pegawai::class.java))
                    }
                    5 -> {
                        startActivity(Intent(this@pengaturan, laporan::class.java))
                        finish()
                    }
                    6 -> {
                        startActivity(Intent(this@pengaturan, pengaturan::class.java))
                        finish()
                    }
                    7 -> {
                        startActivity(Intent(this@pengaturan, aboutMe::class.java))
                    }
                }
                // Don't highlight the 'Profile' and 'Like us on Facebook' item row
                if (position != 6 && position != 4) {
                    updateAdapter(position)
                }
                Handler().postDelayed({
                }, 200)
            }
        }))

        updateAdapter(0)

        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            activity_main_toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                // Triggered once the drawer closes
                super.onDrawerClosed(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                // Triggered once the drawer opens
                super.onDrawerOpened(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
    }

    private fun updateAdapter(highlightItemPos: Int) {
        val refPegawai = FirebaseDatabase.getInstance().reference.child("Pegawai").child(username_key_new)
        refPegawai.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tv_namaakun.text = dataSnapshot.child("Nama_Pegawai").value.toString()
                tv_nmjabatan.text = dataSnapshot.child("Nama_Jabatan").value.toString()

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
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getUsernameLocal() {
        val sharedPreference: SharedPreferences =
            getSharedPreferences(USERNAME_KEY, Context.MODE_PRIVATE)
        username_key_new = sharedPreference.getString(username_key, "").toString()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Checking for fragment count on back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Go to the previous fragment
                supportFragmentManager.popBackStack()
            } else {
                // Exit the app
                super.onBackPressed()
            }
        }
    }

    private fun initViews() {
        btnPiarUnpair.text = if (Printooth.hasPairedPrinter()) "Un-pair ${Printooth.getPairedPrinter()?.name}" else "Pair with printer"
    }

    private fun initListeners() {
        btnPiarUnpair.setOnClickListener {
            if (Printooth.hasPairedPrinter()) Printooth.removeCurrentPrinter()
            else startActivityForResult(
                Intent(this, ScanningActivity::class.java),
                ScanningActivity.SCANNING_FOR_PRINTER
            )
            initViews()
        }

        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(this@pengaturan, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@pengaturan, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@pengaturan, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@pengaturan, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@pengaturan, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@pengaturan, "Disconnected Printer", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun printSomePrintable() {
        val printables = getSomePrintables()
        printing?.print(printables)
    }

    private fun printSomeImages() {
        val printables = ArrayList<Printable>().apply {
            add(ImagePrintable.Builder(R.drawable.logoaida, resources).build())
        }
        printing?.print(printables)
    }

    private fun getSomePrintables() = ArrayList<Printable>().apply {
        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build()) // feed lines example in raw mode

        add(
            TextPrintable.Builder()
                .setText(" Hello World : été è à '€' içi Bò Xào Coi Xanh")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Hello World : été è à €")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Hello World")
                .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Hello World")
                .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("اختبار العربية")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setCharacterCode(DefaultPrinter.CHARCODE_ARABIC_FARISI)
                .setNewLinesAfter(1)
                .setCustomConverter(ArabicConverter()) // change only the converter for this one
                .build()
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK)
            printSomePrintable()
        initViews()
    }
}

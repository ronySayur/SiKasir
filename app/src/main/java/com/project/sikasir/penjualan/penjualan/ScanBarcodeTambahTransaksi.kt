package com.project.sikasir.penjualan.penjualan

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.project.sikasir.R
import kotlinx.android.synthetic.main.scan_barcode.*

class scanBarcodeTambahTransaksi : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* Setting the content view to the layout file scan_barcode_transaksi.xml */
        setContentView(R.layout.scan_barcode)

        /* Checking if the user has granted the app permission to access the camera. */
        setupPermissions()

        /* A function to scan the barcode. */
        codeScanner()
    }

    /**
     * The above function is a function to scan the barcode.
     */
    private fun codeScanner() {
        codeScanner = CodeScanner(this, scn)

        /* The above code is a function to set the camera settings. */
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            /* This is a function to display the barcode data on the screen. */
            decodeCallback = DecodeCallback {
                runOnUiThread {
                    /* The above code is a function to send the barcode data to the next activity. */
                    val intent = Intent(this@scanBarcodeTambahTransaksi, penjualan::class.java)
                    intent.putExtra("DataQR", it.text)
                    startActivity(intent)
                    finish()
                }
            }

            /* A function to display the error message if the barcode scanner fails to scan the
            barcode. */
            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "codeScanner: ${it.message}")
                }
            }

            /* A function to start the camera preview. */
            scn.setOnClickListener {
                codeScanner.startPreview()
            }
        }
    }

    /**
     * The function starts the camera preview
     */
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    /**
     * It releases the resources used by the scanner.
     */
    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    /**
     * If the app doesn't have permission to use the camera, request it
     */
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    /**
     * > If the user has already granted the permission, then do nothing. Otherwise, request the
     * permission
     */
    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQ
        )
    }

    /**
     * If the user grants the permission, do nothing. If the user denies the permission, show a toast
     * message
     *
     * @param requestCode This is the request code that you passed to requestPermissions()
     * @param permissions The permissions that you are requesting.
     * @param grantResults An array of the results of each permission
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQ -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "You need the camera permission to use this app",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /* A constant value that is used to request the camera permission. */
    companion object {
        private const val CAMERA_REQ = 101
    }
}
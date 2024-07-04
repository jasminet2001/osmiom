package com.example.myapplication1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.telephony.CellInfo
import android.telephony.CellInfoGsm
import android.telephony.CellSignalStrengthGsm
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private lateinit var myLocationTextView: TextView
    private lateinit var locButton: ImageButton
    private lateinit var nodeIdTextView: TextView // TextView برای شناسه گره
    private lateinit var signalStrengthTextView: TextView // TextView برای توان سیگنال
    private lateinit var measurementCountTextView: TextView // TextView برای تعداد دفعات اندازه‌گیری
    private lateinit var telephonyManager: TelephonyManager

    private val handler = Handler(Looper.getMainLooper())
    private val measurementInterval: Long = 5000 // Interval for measurements in milliseconds
    private val measurementCounts = mutableMapOf<Int, Int>() // HashMap برای نگهداری تعداد دفعات اندازه‌گیری برای هر گره

    // متغیرهای سراسری برای ذخیره مقادیر
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var currentNodeId: Int = 0
    private var currentSignalStrength: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        locButton = findViewById(R.id.locbut)
        myLocationTextView = findViewById(R.id.loc)
        nodeIdTextView = findViewById(R.id.answer) // TextView برای شناسه گره
        signalStrengthTextView = findViewById(R.id.pow) // TextView برای توان سیگنال
        measurementCountTextView = findViewById(R.id.measurement_count) // TextView برای تعداد دفعات اندازه‌گیری




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapofuser) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locButton.setOnClickListener {
            checkLocationPermission()
        }


        // شروع اندازه‌گیری‌های بلادرنگ
        startRealTimeMeasurements()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getLocation()
            getSignalStrength()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
                getSignalStrength()
            }
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                map.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                myLocationTextView.text = "Latitude: ${it.latitude}, Longitude: ${it.longitude}"
                // ذخیره لوکیشن
                currentLatitude = it.latitude
                currentLongitude = it.longitude
            }
        }
    }

    private fun getSignalStrength() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val cellInfoList: List<CellInfo> = telephonyManager.allCellInfo

        if (cellInfoList.isNotEmpty()) {
            val cellInfo = cellInfoList[0]

            if (cellInfo is CellInfoGsm) {
                val cellSignalStrength: CellSignalStrengthGsm = cellInfo.cellSignalStrength
                val signalStrength = cellSignalStrength.dbm
                val cellId = cellInfo.cellIdentity.cid

                nodeIdTextView.text = "Cell ID: $cellId"
                signalStrengthTextView.text = "Signal Strength: $signalStrength dBm"
                // ذخیره شناسه گره و توان سیگنال
                currentNodeId = cellId
                currentSignalStrength = signalStrength

                // به‌روزرسانی تعداد دفعات اندازه‌گیری برای این گره
                val count = measurementCounts.getOrDefault(cellId, 0) + 1
                measurementCounts[cellId] = count
                measurementCountTextView.text = "Measurement Count for Node $cellId: $count"
            }
        }
    }

    private fun startRealTimeMeasurements() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                getLocation()
                getSignalStrength()
                handler.postDelayed(this, measurementInterval)
            }
        }, measurementInterval)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}

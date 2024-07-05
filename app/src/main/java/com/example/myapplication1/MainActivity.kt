package com.example.myapplication1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import android.telephony.CellInfoCdma
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.CellSignalStrengthGsm
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var myLocationTextView: TextView
    private lateinit var locButton: ImageButton
    private lateinit var nodeIdTextView: TextView
    private lateinit var signalStrengthTextView: TextView
    private lateinit var measurementCountTextView: TextView
    private lateinit var telephonyManager: TelephonyManager

    private val handler = Handler(Looper.getMainLooper())
    private val measurementInterval: Long = 5000
    private val measurementCounts = mutableMapOf<Int, Int>()

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
        nodeIdTextView = findViewById(R.id.answer)
        signalStrengthTextView = findViewById(R.id.pow)
        measurementCountTextView = findViewById(R.id.measurement_count)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        locButton.setOnClickListener {
            checkLocationPermission()
        }

        setupLocationUpdates()

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

    private fun setupLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            interval = 5000 // 5 seconds
            fastestInterval = 2000 // 2 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation.let {
                    currentLatitude = it.latitude
                    currentLongitude = it.longitude
                    updateLocationTextView(it)
                }
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
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun updateLocationTextView(location: Location) {
        myLocationTextView.text = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
    }

    private fun getSignalStrength() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val cellInfoList: List<CellInfo> = telephonyManager.allCellInfo

        for (cellInfo in cellInfoList) {
            when (cellInfo) {
                is CellInfoGsm -> {
                    val cellSignalStrength = cellInfo.cellSignalStrength
                    val signalStrength = cellSignalStrength.dbm
                    val cellId = cellInfo.cellIdentity.cid
                    updateSignalInfo(cellId, signalStrength, "GSM")
                }
                is CellInfoCdma -> {
                    val cellSignalStrength = cellInfo.cellSignalStrength
                    val signalStrength = cellSignalStrength.dbm
                    val cellId = cellInfo.cellIdentity.basestationId
                    updateSignalInfo(cellId, signalStrength, "CDMA")
                }
                is CellInfoLte -> {
                    val cellSignalStrength = cellInfo.cellSignalStrength
                    val signalStrength = cellSignalStrength.dbm
                    val cellId = cellInfo.cellIdentity.ci
                    updateSignalInfo(cellId, signalStrength, "LTE")
                }
                is CellInfoWcdma -> {
                    val cellSignalStrength = cellInfo.cellSignalStrength
                    val signalStrength = cellSignalStrength.dbm
                    val cellId = cellInfo.cellIdentity.cid
                    updateSignalInfo(cellId, signalStrength, "WCDMA")
                }

                else -> {
                    Log.e("SignalStrength", "Unknown cell info type")
                    nodeIdTextView.text = "Unknown cell info type"
                    signalStrengthTextView.text = "Signal Strength: N/A"
                }
            }
        }
    }

    private fun updateSignalInfo(cellId: Int, signalStrength: Int, networkType: String) {
        nodeIdTextView.text = "Cell ID: $cellId (Type: $networkType)"
        signalStrengthTextView.text = "Signal Strength: $signalStrength dBm"
        currentNodeId = cellId
        currentSignalStrength = signalStrength

        val count = measurementCounts.getOrDefault(cellId, 0) + 1
        measurementCounts[cellId] = count
        measurementCountTextView.text = "Measurement Count for Node $cellId: $count"
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
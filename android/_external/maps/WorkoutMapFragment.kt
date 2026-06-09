package com.fittrack.ui.workout

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fittrack.R
import com.fittrack.data.model.GpsPointRequest
import com.fittrack.databinding.FragmentWorkoutMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WorkoutMapFragment : Fragment(R.layout.fragment_workout_map), OnMapReadyCallback {

    private val vm: WorkoutViewModel by viewModels()
    private var _b: FragmentWorkoutMapBinding? = null
    private val b get() = _b!!

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val trackPoints = mutableListOf<LatLng>()
    private val gpsBuffer = mutableListOf<GpsPointRequest>()
    private var currentWorkoutId: Long? = null
    private var isTracking = false
    private var totalDistance = 0.0

    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms.all { it.value }) startTracking()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.forEach { loc ->
                val latLng = LatLng(loc.latitude, loc.longitude)
                trackPoints.add(latLng)

                // Dystans w km — aktualizacja live
                if (trackPoints.size >= 2) {
                    val prev = trackPoints[trackPoints.size - 2]
                    val results = FloatArray(1)
                    android.location.Location.distanceBetween(
                        prev.latitude, prev.longitude,
                        latLng.latitude, latLng.longitude, results)
                    totalDistance += results[0] / 1000.0
                    b.tvDistance.text = "%.2f km".format(totalDistance)
                }

                // Rysuj ścieżkę na mapie
                map.addPolyline(PolylineOptions().addAll(trackPoints).color(
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary)))
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng))

                // Buforuj punkt GPS do wysłania
                gpsBuffer.add(GpsPointRequest(
                    recordedAt = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    latitude   = loc.latitude,
                    longitude  = loc.longitude,
                    altitudeM  = if (loc.hasAltitude()) loc.altitude else null,
                    speedMs    = if (loc.hasSpeed()) loc.speed.toDouble() else null
                ))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentWorkoutMapBinding.bind(view)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        b.btnStartStop.setOnClickListener {
            if (!isTracking) {
                permLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            } else stopTracking()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    @SuppressLint("MissingPermission")
    private fun startTracking() {
        isTracking = true
        b.btnStartStop.text = "Zatrzymaj"
        trackPoints.clear()
        gpsBuffer.clear()
        totalDistance = 0.0

        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L).build()
        fusedLocationClient.requestLocationUpdates(req, locationCallback, null)
    }

    private fun stopTracking() {
        isTracking = false
        b.btnStartStop.text = "Rozpocznij bieg"
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // Wyślij zebrane punkty GPS na serwer
        currentWorkoutId?.let { wid ->
            if (gpsBuffer.isNotEmpty()) vm.addGpsPoints(wid, gpsBuffer.toList())
        }
    }

    override fun onDestroyView() {
        if (isTracking) fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroyView()
        _b = null
    }
}

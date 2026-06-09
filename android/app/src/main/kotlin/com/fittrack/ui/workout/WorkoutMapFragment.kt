package com.fittrack.ui.workout

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fittrack.R
import com.fittrack.databinding.FragmentWorkoutMapBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * STUB ekranu mapy — w wersji do emulatora bez Google Maps SDK.
 *
 * Pełna implementacja (Google Maps + FusedLocationProvider + polyline trasy) jest
 * w `/android/_external/maps/WorkoutMapFragment.kt`. Aktywacja wymaga klucza API.
 */
@AndroidEntryPoint
class WorkoutMapFragment : Fragment(R.layout.fragment_workout_map) {

    private var _b: FragmentWorkoutMapBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentWorkoutMapBinding.bind(view)

        b.btnClose.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

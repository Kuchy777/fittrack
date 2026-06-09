package com.fittrack.ui.workout

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fittrack.R
import com.fittrack.data.model.WorkoutRequest
import com.fittrack.databinding.FragmentWorkoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WorkoutFragment : Fragment(R.layout.fragment_workout) {

    private val vm: WorkoutViewModel by viewModels()
    private var _b: FragmentWorkoutBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentWorkoutBinding.bind(view)

        vm.loadToday()

        // Przycisk ręcznego dodania treningu
        b.btnAddWorkout.setOnClickListener { showAddWorkoutDialog() }

        // Przycisk startu śledzenia GPS (bieg/spacer)
        b.btnStartTracking.setOnClickListener {
            findNavController().navigate(R.id.main_to_workout_map)
        }

        lifecycleScope.launch {
            vm.workouts.collect { state ->
                if (state is com.fittrack.util.Resource.Success) {
                    val totalBurned = state.data.sumOf { it.kcalBurned }
                    b.tvKcalBurned.text = "Spalone dziś: $totalBurned kcal"
                }
            }
        }
    }

    private fun showAddWorkoutDialog() {
        val types = arrayOf("Siłownia", "Rower", "Pływanie", "Joga", "Inne")
        var selectedType = "Siłownia"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Dodaj trening")
            .setSingleChoiceItems(types, 0) { _, which -> selectedType = types[which] }
            .setPositiveButton("Zapisz") { _, _ ->
                // W produkcji: BottomSheetDialog z polami duration + kcal
                vm.logWorkout(WorkoutRequest(
                    activityDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    activityType = selectedType.uppercase(),
                    durationMin  = 45,
                    kcalBurned   = 300,
                    distanceKm   = null,
                    notes        = null
                ))
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

package com.fittrack.ui.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fittrack.R
import com.fittrack.data.model.ProfileUpdateRequest
import com.fittrack.databinding.FragmentProfileBinding
import com.fittrack.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val vm: ProfileViewModel by viewModels()
    private var _b: FragmentProfileBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentProfileBinding.bind(view)

        b.btnSave.setOnClickListener {
            vm.updateProfile(ProfileUpdateRequest(
                displayName   = b.etName.text.toString().ifBlank { null },
                gender        = when (b.rgGender.checkedRadioButtonId) {
                    R.id.rbMale   -> "MALE"
                    R.id.rbFemale -> "FEMALE"
                    else          -> null
                },
                birthDate     = b.etBirthDate.text.toString().ifBlank { null },
                weightKg      = b.etWeight.text.toString().toDoubleOrNull(),
                heightCm      = b.etHeight.text.toString().toDoubleOrNull(),
                activityLevel = b.spinnerActivity.selectedItem.toString(),
                goal          = when (b.rgGoal.checkedRadioButtonId) {
                    R.id.rbLose     -> "LOSE"
                    R.id.rbMaintain -> "MAINTAIN"
                    R.id.rbGain     -> "GAIN"
                    else            -> null
                }
            ))
        }

        lifecycleScope.launch {
            vm.profile.collect { state ->
                b.progressBar.isVisible = state is Resource.Loading
                if (state is Resource.Success) {
                    val p = state.data
                    b.etName.setText(p.displayName)
                    b.etWeight.setText(p.weightKg?.toString())
                    b.etHeight.setText(p.heightCm?.toString())
                    p.dailyKcalGoal?.let {
                        b.tvKcalGoal.text = "Cel kaloryczny: $it kcal/dzień"
                    }
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

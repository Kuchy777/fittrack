package com.fittrack.ui.diary

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fittrack.R
import com.fittrack.databinding.FragmentBarcodeScannerBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * STUB ekranu skanera — używany w emulatorze bez kamery i ML Kit.
 *
 * Pełna implementacja CameraX + ML Kit jest w `/android/_external/BarcodeScannerFragment.kt`.
 * Tutaj pozwalamy ręcznie wpisać EAN i wywołać API.
 */
@AndroidEntryPoint
class BarcodeScannerFragment : Fragment(R.layout.fragment_barcode_scanner) {

    private val vm: DiaryViewModel by viewModels()
    private var _b: FragmentBarcodeScannerBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentBarcodeScannerBinding.bind(view)

        b.btnFind.setOnClickListener {
            val code = b.etBarcode.text.toString().trim()
            if (code.isNotBlank()) {
                vm.getFoodByBarcode(code)
                findNavController().popBackStack()
            }
        }
        b.btnCancel.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

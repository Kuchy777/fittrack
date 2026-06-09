package com.fittrack.ui.diary

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.R
import com.fittrack.databinding.FragmentDiaryBinding
import com.fittrack.util.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class DiaryFragment : Fragment(R.layout.fragment_diary) {

    private val vm: DiaryViewModel by viewModels()
    private var _b: FragmentDiaryBinding? = null
    private val b get() = _b!!
    private lateinit var adapter: DiaryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentDiaryBinding.bind(view)

        setupRecyclerView()
        setupSwipeToDelete()
        setupFab()
        setupSwipeRefresh()

        vm.loadDay(LocalDate.now())

        lifecycleScope.launch {
            vm.entries.collect { state ->
                b.progressBar.isVisible = state is Resource.Loading
                b.swipeRefresh.isRefreshing = false
                when (state) {
                    is Resource.Success -> adapter.submitList(state.data)
                    is Resource.Error   -> Snackbar.make(view, state.message, Snackbar.LENGTH_LONG).show()
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            vm.summary.collect { state ->
                if (state is Resource.Success) {
                    val s = state.data
                    b.tvKcalConsumed.text  = "${s.kcalConsumed.toInt()} kcal"
                    b.tvKcalRemaining.text = "${s.kcalRemaining.toInt()} pozostało"
                    b.progressKcal.max      = s.kcalGoal
                    b.progressKcal.progress = s.kcalConsumed.toInt()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = DiaryAdapter()
        b.rvDiary.adapter = adapter
        b.rvDiary.layoutManager = LinearLayoutManager(requireContext())
    }

    // Swipe-to-delete (User Story: "przesunięcie w lewo")
    private fun setupSwipeToDelete() {
        val swipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val entry = adapter.currentList[viewHolder.adapterPosition]
                vm.deleteEntry(entry.id)
                Snackbar.make(b.root, "Wpis usunięty", Snackbar.LENGTH_SHORT)
                    .setAction("Cofnij") { /* TODO: soft-delete undo */ }
                    .show()
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(b.rvDiary)
    }

    // FAB → skaner kodu kreskowego lub wyszukiwarka
    private fun setupFab() {
        b.fab.setOnClickListener {
            findNavController().navigate(R.id.main_to_scanner)
        }
    }

    // Pull-to-refresh
    private fun setupSwipeRefresh() {
        b.swipeRefresh.setOnRefreshListener {
            vm.loadDay(vm.currentDate)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

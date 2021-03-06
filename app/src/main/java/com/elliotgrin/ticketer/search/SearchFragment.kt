package com.elliotgrin.ticketer.search

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.elliotgrin.ticketer.R
import com.elliotgrin.ticketer.main.MainViewModel
import com.elliotgrin.ticketer.map.MapFragment
import com.elliotgrin.ticketer.model.CityUiModel
import com.elliotgrin.ticketer.util.ext.hideKeyboard
import com.elliotgrin.ticketer.util.ext.setTextAndDismissDropDown
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val AUTOCOMPLETE_THRESHOLD = 2

class SearchFragment(private val viewModel: SearchViewModel) : Fragment(R.layout.fragment_search) {

    private val sharedViewModel: MainViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSharedViewModel()
    }

    override fun onStart() {
        super.onStart()
        initViews()
    }

    private fun initViews() {
        val departureSuggestionsAdapter = SuggestionsAdapter(viewModel, this::setDeparture)
        val arrivalSuggestionsAdapter = SuggestionsAdapter(viewModel, this::setArrival)

        editTextDeparture.threshold = AUTOCOMPLETE_THRESHOLD
        editTextArrival.threshold = AUTOCOMPLETE_THRESHOLD
        editTextDeparture.setAdapter(departureSuggestionsAdapter)
        editTextArrival.setAdapter(arrivalSuggestionsAdapter)

        setTextChangeListeners()

        buttonSearch.setOnClickListener { openMapFragment() }
    }

    private fun observeSharedViewModel() = sharedViewModel.citiesLiveData.observe(viewLifecycleOwner) { areNotNull ->
        buttonSearch.isEnabled = areNotNull
    }

    private fun setTextChangeListeners() {

        editTextDeparture.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) sharedViewModel.departureCity = null
            viewModel.setLanguage(text)
        }

        editTextArrival.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) sharedViewModel.arrivalCity = null
            viewModel.setLanguage(text)
        }

    }

    private fun setDeparture(departure: CityUiModel) {
        editTextDeparture.setTextAndDismissDropDown(departure.shortName.toString())
        sharedViewModel.departureCity = departure
        editTextArrival.requestFocus()

    }

    private fun setArrival(arrival: CityUiModel) {
        editTextArrival.setTextAndDismissDropDown(arrival.shortName.toString())
        editTextArrival.hideKeyboard()
        sharedViewModel.arrivalCity = arrival
    }

    private fun openMapFragment() = parentFragmentManager.commit {
        add<MapFragment>(R.id.mainContainer)
        addToBackStack(MapFragment::class.java.simpleName)
    }

}

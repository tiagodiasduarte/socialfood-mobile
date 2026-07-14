package pt.socialfood.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.home.GetHomeSectionsUseCase

class HomeViewModel(
    private val getHomeSections: GetHomeSectionsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = HomeUiState.Loading
            fetchSections()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchSections()
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchSections() {
        when (val result = getHomeSections()) {
            is Result.Success -> {
                val active = result.data
                    .filter { it.isActive }
                    .sortedBy { it.position }
                _state.value = HomeUiState.Loaded(active)
            }
            is Result.Error -> _state.value = HomeUiState.Error
        }
    }
}

package pt.socialfood.presentation.author.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.User
import pt.socialfood.domain.usecase.author.GetAuthorsPagingUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorsViewModel(getAuthorsPaging: GetAuthorsPagingUseCase, observeUser: ObserveUserUseCase) : ViewModel() {

    val user: StateFlow<User?> = observeUser()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val authors: Flow<PagingData<Author>> = user
        .filterNotNull()
        .map { it.id }
        .distinctUntilChanged()
        .flatMapLatest { getAuthorsPaging() }
        .cachedIn(viewModelScope)
}

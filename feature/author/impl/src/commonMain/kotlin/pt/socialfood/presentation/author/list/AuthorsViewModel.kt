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
import pt.socialfood.core.domain.repository.AuthorsRepository
import pt.socialfood.core.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.core.model.Author
import pt.socialfood.core.model.User

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorsViewModel(authorsRepository: AuthorsRepository, observeUser: ObserveUserUseCase) : ViewModel() {

    val user: StateFlow<User?> = observeUser()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val authors: Flow<PagingData<Author>> = user
        .filterNotNull()
        .map { it.id }
        .distinctUntilChanged()
        .flatMapLatest { authorsRepository.getAuthorsPagingFlow() }
        .cachedIn(viewModelScope)
}

package pt.socialfood.presentation.author.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.User
import pt.socialfood.domain.usecase.author.GetAuthorsPagingUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCase

class AuthorsViewModel(getAuthorsPaging: GetAuthorsPagingUseCase, observeUser: ObserveUserUseCase) : ViewModel() {

    val authors: Flow<PagingData<Author>> = getAuthorsPaging().cachedIn(viewModelScope)

    val user: StateFlow<User?> = observeUser()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}

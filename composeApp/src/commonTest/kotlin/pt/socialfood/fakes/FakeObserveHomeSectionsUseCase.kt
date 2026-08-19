package pt.socialfood.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.usecase.home.ObserveHomeSectionsUseCase

class FakeObserveHomeSectionsUseCase(initial: List<HomeSection> = emptyList()) : ObserveHomeSectionsUseCase {
    private val sections = MutableStateFlow(initial)

    fun emit(sections: List<HomeSection>) {
        this.sections.value = sections
    }

    override operator fun invoke(): Flow<List<HomeSection>> = sections
}

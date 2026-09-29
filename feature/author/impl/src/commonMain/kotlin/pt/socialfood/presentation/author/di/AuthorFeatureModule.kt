package pt.socialfood.presentation.author.di

import org.koin.dsl.module
import pt.socialfood.presentation.author.detail.AuthorDetailViewModel
import pt.socialfood.presentation.author.list.AuthorsViewModel

val authorFeatureModule =
    module {
        factory { (authorId: String) -> AuthorDetailViewModel(get(), authorId) }
        factory { AuthorsViewModel(get(), get()) }
    }

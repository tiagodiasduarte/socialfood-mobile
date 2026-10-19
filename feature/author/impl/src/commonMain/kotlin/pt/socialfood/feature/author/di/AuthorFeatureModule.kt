package pt.socialfood.feature.author.di

import org.koin.dsl.module
import pt.socialfood.feature.author.detail.AuthorDetailViewModel
import pt.socialfood.feature.author.list.AuthorsViewModel

val authorFeatureModule =
    module {
        factory { (authorId: String) -> AuthorDetailViewModel(get(), authorId) }
        factory { AuthorsViewModel(get(), get()) }
    }

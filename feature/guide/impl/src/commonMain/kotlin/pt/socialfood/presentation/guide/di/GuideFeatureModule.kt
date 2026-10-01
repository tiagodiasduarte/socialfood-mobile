package pt.socialfood.presentation.guide.di

import org.koin.dsl.module
import pt.socialfood.presentation.guide.all.AllGuidesViewModel
import pt.socialfood.presentation.guide.create.CreateGuideViewModel
import pt.socialfood.presentation.guide.detail.GuideDetailViewModel
import pt.socialfood.presentation.guide.edit.EditGuideViewModel
import pt.socialfood.presentation.guide.map.GuideMapViewModel
import pt.socialfood.presentation.guide.my.MyGuidesViewModel
import pt.socialfood.presentation.guide.shared.SharedGuidesViewModel

val guideFeatureModule =
    module {
        factory { AllGuidesViewModel(get(), get(), get()) }
        factory { CreateGuideViewModel(get(), get(), get()) }
        factory { (guideId: String) -> EditGuideViewModel(get(), get(), get(), get(), get(), guideId) }
        factory { (guideId: String) -> GuideDetailViewModel(get(), get(), get(), guideId) }
        factory { (guideId: String) -> GuideMapViewModel(get(), guideId) }
        factory { MyGuidesViewModel(get(), get(), get()) }
        factory { SharedGuidesViewModel(get(), get(), get()) }
    }

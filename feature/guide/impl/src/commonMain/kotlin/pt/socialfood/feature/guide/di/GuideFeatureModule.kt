package pt.socialfood.feature.guide.di

import org.koin.dsl.module
import pt.socialfood.feature.guide.all.AllGuidesViewModel
import pt.socialfood.feature.guide.create.CreateGuideViewModel
import pt.socialfood.feature.guide.detail.GuideDetailViewModel
import pt.socialfood.feature.guide.edit.EditGuideViewModel
import pt.socialfood.feature.guide.map.GuideMapViewModel
import pt.socialfood.feature.guide.my.MyGuidesViewModel
import pt.socialfood.feature.guide.shared.SharedGuidesViewModel

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

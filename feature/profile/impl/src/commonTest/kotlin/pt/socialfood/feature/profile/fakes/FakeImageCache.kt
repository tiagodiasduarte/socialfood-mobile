package pt.socialfood.feature.profile.fakes

import pt.socialfood.core.ui.image.ImageCache

class FakeImageCache : ImageCache {
    var clearedUrls: MutableList<String> = mutableListOf()
        private set

    override fun clear(url: String) {
        clearedUrls.add(url)
    }
}

package pt.socialfood.fakes

import pt.socialfood.presentation.ui.image.ImageCache

class FakeImageCache : ImageCache {
    var clearedUrls: MutableList<String> = mutableListOf()
        private set

    override fun clear(url: String) {
        clearedUrls.add(url)
    }
}

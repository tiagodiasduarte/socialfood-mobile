package pt.socialfood.presentation.ui.image

/** Evicts a cached image so the next load fetches it again (e.g. after a profile picture upload). */
interface ImageCache {
    fun clear(url: String)
}

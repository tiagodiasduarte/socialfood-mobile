package pt.socialfood.di

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient
import pt.socialfood.presentation.ui.image.ImageCache

class AppImageLoaderFactory(private val httpClient: HttpClient) :
    SingletonImageLoader.Factory,
    ImageCache {

    private var imageLoader: ImageLoader? = null

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader ?: ImageLoader.Builder(context)
        .components {
            add(KtorNetworkFetcherFactory(httpClient = { httpClient }))
        }
        .build()
        .also { imageLoader = it }

    override fun clear(url: String) {
        val loader = imageLoader ?: return
        loader.memoryCache?.remove(MemoryCache.Key(url))
        loader.diskCache?.remove(url)
    }
}

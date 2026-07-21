package pt.socialfood.di

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient

interface ImageCache {
    fun clear(url: String)
}

// Builds the shared Coil ImageLoader, wired with the dedicated, unauthenticated
// ImageHttpClient so image requests never pick up KtorHttpClient's Authorization header,
// base-URL/path prefix, or 401 -> session-clear behavior.
class AppImageLoaderFactory(private val httpClient: HttpClient) : SingletonImageLoader.Factory, ImageCache {

    private var imageLoader: ImageLoader? = null

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return imageLoader ?: ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(httpClient = { httpClient }))
            }
            .build()
            .also { imageLoader = it }
    }

    override fun clear(url: String) {
        val loader = imageLoader ?: return
        loader.memoryCache?.remove(MemoryCache.Key(url))
        loader.diskCache?.remove(url)
    }
}

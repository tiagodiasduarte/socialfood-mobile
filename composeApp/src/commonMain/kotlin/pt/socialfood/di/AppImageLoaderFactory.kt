package pt.socialfood.di

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient

// Builds the shared Coil ImageLoader, wired with the dedicated, unauthenticated
// ImageHttpClient so image requests never pick up KtorHttpClient's Authorization header,
// base-URL/path prefix, or 401 -> session-clear behavior.
class AppImageLoaderFactory(private val httpClient: HttpClient) : SingletonImageLoader.Factory {
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(httpClient = { httpClient }))
            }
            .build()
    }
}

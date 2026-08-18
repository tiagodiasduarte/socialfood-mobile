package pt.socialfood.data.network

import co.touchlab.kermit.Logger
import io.ktor.client.plugins.logging.Logger as KtorLogger

class KermitKtorLogger(tag: String) : KtorLogger {
    private val logger = Logger.withTag(tag)

    override fun log(message: String) {
        logger.v { message }
    }
}

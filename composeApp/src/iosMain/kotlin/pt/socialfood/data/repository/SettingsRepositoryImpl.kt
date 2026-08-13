package pt.socialfood.data.repository

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionarySetValue
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDefaults
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import pt.socialfood.domain.repository.SettingsRepository

private const val KEY_TOKEN = "jwt_token"
private const val KEY_PENDING_VERIFICATION_EMAIL = "pending_verification_email"
private const val KEY_LAST_FAVOURITES_SYNCED_AT = "favourites_synced_at"
private const val KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT = "last_favourites_sync_attempt_at"
private const val KEY_LAST_FAVOURITE_RESTAURANTS_SYNCED_AT = "favourite_restaurants_synced_at"
private const val KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT = "last_favourite_restaurants_sync_attempt_at"

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private object KeychainTokenStore {
    private const val SERVICE = "pt.socialfood.session"
    private const val ACCOUNT = "jwt_token"

    @Suppress("MagicNumber")
    fun save(value: String) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val data = requireNotNull((value as NSString).dataUsingEncoding(NSUTF8StringEncoding))
        // Delete-then-add keeps this simple and avoids branching on SecItemUpdate's
        // errSecItemNotFound vs success; token writes are infrequent (login/refresh/logout).
        delete()

        val query = CFDictionaryCreateMutable(null, 5, null, null)
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrService, CFBridgingRetain(SERVICE))
        CFDictionarySetValue(query, kSecAttrAccount, CFBridgingRetain(ACCOUNT))
        CFDictionarySetValue(query, kSecValueData, CFBridgingRetain(data))
        CFDictionarySetValue(query, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)

        SecItemAdd(query, null)
        CFBridgingRelease(query)
    }

    @Suppress("MagicNumber")
    fun get(): String? = memScoped {
        val query = CFDictionaryCreateMutable(null, 4, null, null)
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrService, CFBridgingRetain(SERVICE))
        CFDictionarySetValue(query, kSecAttrAccount, CFBridgingRetain(ACCOUNT))
        CFDictionarySetValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionarySetValue(query, kSecMatchLimit, kSecMatchLimitOne)

        val resultRef = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(query, resultRef.ptr)
        CFBridgingRelease(query)

        if (status != errSecSuccess) return@memScoped null

        val data = CFBridgingRelease(resultRef.value) as? NSData ?: return@memScoped null
        @Suppress("USELESS_CAST")
        NSString.create(data = data, encoding = NSUTF8StringEncoding) as String?
    }

    @Suppress("MagicNumber")
    fun delete() {
        val query = CFDictionaryCreateMutable(null, 3, null, null)
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrService, CFBridgingRetain(SERVICE))
        CFDictionarySetValue(query, kSecAttrAccount, CFBridgingRetain(ACCOUNT))
        SecItemDelete(query)
        CFBridgingRelease(query)
    }
}

@Suppress("TooManyFunctions")
class SettingsRepositoryImpl : SettingsRepository {
    private val defaults = NSUserDefaults.standardUserDefaults

    @Suppress("ReturnCount")
    override suspend fun getToken(): String? {
        KeychainTokenStore.get()?.let { return it }

        // Legacy plaintext token from before Keychain migration: use it as-is, migrate it into
        // the Keychain, and stop leaving a plaintext copy in NSUserDefaults.
        val legacyToken = defaults.stringForKey(KEY_TOKEN) ?: return null
        KeychainTokenStore.save(legacyToken)
        defaults.removeObjectForKey(KEY_TOKEN)
        return legacyToken
    }

    override suspend fun saveToken(token: String) {
        KeychainTokenStore.save(token)
    }

    override suspend fun clearToken() {
        KeychainTokenStore.delete()
        defaults.removeObjectForKey(KEY_TOKEN)
    }

    override suspend fun getPendingVerificationEmail(): String? = defaults.stringForKey(KEY_PENDING_VERIFICATION_EMAIL)

    override suspend fun savePendingVerificationEmail(email: String) {
        defaults.setObject(email, KEY_PENDING_VERIFICATION_EMAIL)
    }

    override suspend fun clearPendingVerificationEmail() {
        defaults.removeObjectForKey(KEY_PENDING_VERIFICATION_EMAIL)
    }

    override suspend fun getLastFavouritesSyncedAt(): String? = defaults.stringForKey(KEY_LAST_FAVOURITES_SYNCED_AT)

    override suspend fun saveLastFavouritesSyncedAt(syncedAt: String) {
        defaults.setObject(syncedAt, KEY_LAST_FAVOURITES_SYNCED_AT)
    }

    override suspend fun getLastFavouritesSyncAttemptAt(): Long? =
        if (defaults.objectForKey(KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT) != null) {
            defaults.integerForKey(KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT)
        } else {
            null
        }

    override suspend fun saveLastFavouritesSyncAttemptAt(timestamp: Long) {
        defaults.setInteger(timestamp, KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT)
    }

    override suspend fun getLastFavouriteRestaurantsSyncedAt(): String? =
        defaults.stringForKey(KEY_LAST_FAVOURITE_RESTAURANTS_SYNCED_AT)

    override suspend fun saveLastFavouriteRestaurantsSyncedAt(syncedAt: String) {
        defaults.setObject(syncedAt, KEY_LAST_FAVOURITE_RESTAURANTS_SYNCED_AT)
    }

    override suspend fun getLastFavouriteRestaurantsSyncAttemptAt(): Long? =
        if (defaults.objectForKey(KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT) != null) {
            defaults.integerForKey(KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT)
        } else {
            null
        }

    override suspend fun saveLastFavouriteRestaurantsSyncAttemptAt(timestamp: Long) {
        defaults.setInteger(timestamp, KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT)
    }
}

package pt.socialfood.data.repository

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import pt.socialfood.domain.repository.SettingsRepository
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private val Context.dataStore by preferencesDataStore(name = "socialfood_settings")

private val KEY_TOKEN = stringPreferencesKey("jwt_token")
private val KEY_PENDING_VERIFICATION_EMAIL = stringPreferencesKey("pending_verification_email")
private val KEY_LAST_FAVOURITES_SYNCED_AT = stringPreferencesKey("favourites_synced_at")
private val KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT = longPreferencesKey("last_favourites_sync_attempt_at")
private val KEY_LAST_FAVOURITE_RESTAURANTS_SYNCED_AT = stringPreferencesKey("favourite_restaurants_synced_at")
private val KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT =
    longPreferencesKey("last_favourite_restaurants_sync_attempt_at")

private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
private const val TOKEN_KEY_ALIAS = "socialfood_jwt_token_key"
private const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"
private const val GCM_IV_LENGTH_BYTES = 12
private const val GCM_TAG_LENGTH_BITS = 128
private const val AES_KEY_SIZE_BITS = 256

private fun getOrCreateTokenKey(): SecretKey {
    val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply { load(null) }
    (keyStore.getKey(TOKEN_KEY_ALIAS, null) as? SecretKey)?.let { return it }

    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE_PROVIDER)
    val spec = KeyGenParameterSpec.Builder(
        TOKEN_KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(AES_KEY_SIZE_BITS)
        .setRandomizedEncryptionRequired(true)
        .build()
    keyGenerator.init(spec)
    return keyGenerator.generateKey()
}

private fun encryptToken(plainText: String): String {
    val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
    cipher.init(Cipher.ENCRYPT_MODE, getOrCreateTokenKey())
    val iv = cipher.iv
    val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(iv + cipherBytes, Base64.NO_WRAP)
}

/**
 * Returns null if [storedValue] isn't a valid AES/GCM payload from [encryptToken] — i.e. it's a
 * legacy plaintext token written before token encryption was added. GCM's auth-tag check reliably
 * throws javax.crypto.AEADBadTagException (a GeneralSecurityException) on such data.
 */
private fun decryptTokenOrNull(storedValue: String): String? = try {
    val combined = Base64.decode(storedValue, Base64.NO_WRAP)
    require(combined.size > GCM_IV_LENGTH_BYTES) { "payload too short to contain an IV" }
    val iv = combined.copyOfRange(0, GCM_IV_LENGTH_BYTES)
    val cipherBytes = combined.copyOfRange(GCM_IV_LENGTH_BYTES, combined.size)

    val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
    cipher.init(Cipher.DECRYPT_MODE, getOrCreateTokenKey(), GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
    String(cipher.doFinal(cipherBytes), Charsets.UTF_8)
} catch (expected: IllegalArgumentException) {
    null
} catch (expected: GeneralSecurityException) {
    null
}

@Suppress("TooManyFunctions")
class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    @Suppress("ReturnCount")
    override suspend fun getToken(): String? {
        val stored = context.dataStore.data.first()[KEY_TOKEN] ?: return null
        decryptTokenOrNull(stored)?.let { return it }

        // Legacy plaintext token from before encryption was added: use it as-is and upgrade it
        // in place so future reads hit the encrypted path.
        saveToken(stored)
        return stored
    }

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { it[KEY_TOKEN] = encryptToken(token) }
    }

    override suspend fun clearToken() {
        context.dataStore.edit { it.remove(KEY_TOKEN) }
    }

    override suspend fun getPendingVerificationEmail(): String? =
        context.dataStore.data.first()[KEY_PENDING_VERIFICATION_EMAIL]

    override suspend fun savePendingVerificationEmail(email: String) {
        context.dataStore.edit { it[KEY_PENDING_VERIFICATION_EMAIL] = email }
    }

    override suspend fun clearPendingVerificationEmail() {
        context.dataStore.edit { it.remove(KEY_PENDING_VERIFICATION_EMAIL) }
    }

    override suspend fun getLastFavouritesSyncedAt(): String? =
        context.dataStore.data.first()[KEY_LAST_FAVOURITES_SYNCED_AT]

    override suspend fun saveLastFavouritesSyncedAt(syncedAt: String) {
        context.dataStore.edit { it[KEY_LAST_FAVOURITES_SYNCED_AT] = syncedAt }
    }

    override suspend fun getLastFavouritesSyncAttemptAt(): Long? =
        context.dataStore.data.first()[KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT]

    override suspend fun saveLastFavouritesSyncAttemptAt(timestamp: Long) {
        context.dataStore.edit { it[KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT] = timestamp }
    }

    override suspend fun getLastFavouriteRestaurantsSyncedAt(): String? =
        context.dataStore.data.first()[KEY_LAST_FAVOURITE_RESTAURANTS_SYNCED_AT]

    override suspend fun saveLastFavouriteRestaurantsSyncedAt(syncedAt: String) {
        context.dataStore.edit { it[KEY_LAST_FAVOURITE_RESTAURANTS_SYNCED_AT] = syncedAt }
    }

    override suspend fun getLastFavouriteRestaurantsSyncAttemptAt(): Long? =
        context.dataStore.data.first()[KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT]

    override suspend fun saveLastFavouriteRestaurantsSyncAttemptAt(timestamp: Long) {
        context.dataStore.edit { it[KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT] = timestamp }
    }
}

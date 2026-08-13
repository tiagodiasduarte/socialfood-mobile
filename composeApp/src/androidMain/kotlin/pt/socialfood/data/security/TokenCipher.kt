package pt.socialfood.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
private const val TOKEN_KEY_ALIAS = "socialfood_jwt_token_key"
private const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"
private const val GCM_IV_LENGTH_BYTES = 12
private const val GCM_TAG_LENGTH_BITS = 128
private const val AES_KEY_SIZE_BITS = 256

/**
 * Encrypts/decrypts strings with an AES-256-GCM key backed by the Android Keystore, used to
 * store the JWT session token at rest instead of as plaintext.
 */
internal class TokenCipher {
    private fun getOrCreateKey(): SecretKey {
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

    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv
        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(iv + cipherBytes, Base64.NO_WRAP)
    }

    /**
     * Returns null if [storedValue] isn't a valid AES/GCM payload from [encrypt] — i.e. it's a
     * legacy plaintext token written before token encryption was added. GCM's auth-tag check
     * reliably throws javax.crypto.AEADBadTagException (a GeneralSecurityException) on such data.
     */
    fun decryptOrNull(storedValue: String): String? = try {
        val combined = Base64.decode(storedValue, Base64.NO_WRAP)
        require(combined.size > GCM_IV_LENGTH_BYTES) { "payload too short to contain an IV" }
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH_BYTES)
        val cipherBytes = combined.copyOfRange(GCM_IV_LENGTH_BYTES, combined.size)

        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        String(cipher.doFinal(cipherBytes), Charsets.UTF_8)
    } catch (expected: IllegalArgumentException) {
        null
    } catch (expected: GeneralSecurityException) {
        null
    }
}

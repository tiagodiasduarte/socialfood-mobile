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

private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
private const val PURPOSES = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT

internal class TokenCipher {

    private val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply { load(null) }

    private fun getKey(): SecretKey {
        val secretKey = (keyStore.getKey(TOKEN_KEY_ALIAS, null) as? SecretKey)?.let { return it }
        return secretKey ?: createKey()
    }

    private fun createKey(): SecretKey = KeyGenerator
        .getInstance(ALGORITHM, ANDROID_KEYSTORE_PROVIDER)
        .apply {
            init(
                KeyGenParameterSpec.Builder(
                    TOKEN_KEY_ALIAS,
                    PURPOSES,
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setKeySize(AES_KEY_SIZE_BITS)
                    .setRandomizedEncryptionRequired(true)
                    .build(),
            )
        }.generateKey()

    fun encrypt(plainText: String): String {
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val iv = cipher.iv
        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(iv + cipherBytes, Base64.NO_WRAP)
    }

    fun decryptOrNull(storedValue: String): String? = try {
        val combined = Base64.decode(storedValue, Base64.NO_WRAP)
        require(combined.size > GCM_IV_LENGTH_BYTES) { "payload too short to contain an IV" }
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH_BYTES)
        val cipherBytes = combined.copyOfRange(GCM_IV_LENGTH_BYTES, combined.size)

        cipher.init(Cipher.DECRYPT_MODE, getKey(), GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        String(cipher.doFinal(cipherBytes), Charsets.UTF_8)
    } catch (_: IllegalArgumentException) {
        null
    } catch (_: GeneralSecurityException) {
        null
    }
}

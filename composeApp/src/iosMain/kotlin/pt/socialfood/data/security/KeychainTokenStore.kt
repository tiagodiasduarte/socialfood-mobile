package pt.socialfood.data.security

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

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal object KeychainTokenStore {
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

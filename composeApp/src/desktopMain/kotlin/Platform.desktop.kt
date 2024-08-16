import java.security.InvalidAlgorithmParameterException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class JVMPlatform() : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val type: PlatformType = PlatformType.Desktop

    companion object {
        var key: SecretKey
        var iv: IvParameterSpec

        init {
            val password = "get_me_from_user_input"
            val salt = "1234567812345678"
            key = getKeyFromPassword(password, salt)
            iv = IvParameterSpec(salt.toByteArray())
        }
    }
}

actual fun getPlatform(): Platform = JVMPlatform()

@OptIn(ExperimentalEncodingApi::class)
actual fun encode(text: String): String {
//    try {
//        val ivParameterSpec: IvParameterSpec = JVMPlatform.iv
//        val key: SecretKey = JVMPlatform.key
//        return encryptPasswordBased(text, key, ivParameterSpec)
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
    return Base64.encode(text.encodeToByteArray())
}

@OptIn(ExperimentalEncodingApi::class)
actual fun decode(text: String): String {
//    try {
//        val ivParameterSpec: IvParameterSpec = JVMPlatform.iv
//        val key: SecretKey = JVMPlatform.key
//        return decryptPasswordBased(text, key, ivParameterSpec)
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
    return Base64.decode(text).decodeToString()
}

@OptIn(ExperimentalEncodingApi::class)
@Throws(
    NoSuchPaddingException::class,
    NoSuchAlgorithmException::class,
    InvalidAlgorithmParameterException::class,
    BadPaddingException::class,
    IllegalBlockSizeException::class
)
fun encryptPasswordBased(plainText: String, key: SecretKey?, iv: IvParameterSpec?): String {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, key, iv)
    return Base64.encode(cipher.doFinal(plainText.toByteArray()))
}

@OptIn(ExperimentalEncodingApi::class)
@Throws(
    NoSuchPaddingException::class,
    NoSuchAlgorithmException::class,
    InvalidAlgorithmParameterException::class,
    BadPaddingException::class,
    IllegalBlockSizeException::class
)
fun decryptPasswordBased(cipherText: String, key: SecretKey, iv: IvParameterSpec): String {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.DECRYPT_MODE, key, iv)
    return String(
        cipher.doFinal(
            Base64.decode(cipherText)
        )
    )
}

@Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
fun getKeyFromPassword(password: String, salt: String): SecretKey {
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), 65536, 256)
    val secret: SecretKey = SecretKeySpec(
        factory.generateSecret(spec)
            .encoded, "AES"
    )
    return secret
}

fun generateIv(): IvParameterSpec {
    val iv = ByteArray(16)
    SecureRandom().nextBytes(iv)
    return IvParameterSpec(iv)
}
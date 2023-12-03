package com.example.database.security

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptString {

    // AES 암호화를 위해 사용되는 키는 16, 24, 36 Byte여야 한다.
    private val secretKey = "12345678901234567890123456789012"

    private val encoder = Base64.getEncoder()
    private val decoder = Base64.getDecoder()

    fun encryptString(plainString: String): String {
        val encryptedString =
            ciperPkcs5(Cipher.ENCRYPT_MODE, secretKey).doFinal(plainString.toByteArray(Charsets.UTF_8))

        return String(encoder.encode(encryptedString))
    }

    fun decryptString(cipherString: String): String {
        val byteString = decoder.decode(cipherString.toByteArray(Charsets.UTF_8))

        return String(ciperPkcs5(Cipher.DECRYPT_MODE, secretKey).doFinal(byteString))
    }

    fun ciperPkcs5(opMode: Int, secretKey: String): Cipher {
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val sk = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
        val iv = IvParameterSpec(secretKey.substring(0, 16).toByteArray(Charsets.UTF_8))
        c.init(opMode, sk, iv)
        return c
    }

}
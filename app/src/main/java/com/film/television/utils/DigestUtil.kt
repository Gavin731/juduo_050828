package com.film.television.utils

import java.security.MessageDigest

object DigestUtil {
    private val hexDigits =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    fun getMD5(s: String): String {
        val digest = MessageDigest.getInstance("MD5")
        digest.update(s.toByteArray())
        return digest.digest().joinToString { byteToHex(it) }
    }

    fun getSHA1(s: String): String {
        val digest = MessageDigest.getInstance("SHA1")
        digest.update(s.toByteArray())
        return digest.digest().joinToString { byteToHex(it) }
    }

    private fun byteToHex(byte: Byte): String {
        val high = ((byte.toInt()) shr 4) and 0x0F
        val low = byte.toInt() and 0x0F
        return "${hexDigits[high]}${hexDigits[low]}"
    }

}
package com.example.easyqueue.Public

import kotlin.experimental.and
import kotlin.experimental.or

class Convert {
    fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                    + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    private val hexArray = "0123456789ABCDEF".toCharArray()
    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v: Int = (bytes[j] and 0xFF.toByte()).toInt()
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    fun toByteArray(hexString: String): ByteArray {
        val hexStringLength = hexString.length
        var byteArray: ByteArray? = null
        var count = 0
        var c: Char
        var i: Int

        // Count number of hex characters
        i = 0
        while (i < hexStringLength) {
            c = hexString[i]
            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f') {
                count++
            }
            i++
        }
        byteArray = ByteArray((count + 1) / 2)
        var first = true
        var len = 0
        var value: Int
        i = 0
        while (i < hexStringLength) {
            c = hexString[i]
            value = if (c >= '0' && c <= '9') {
                c - '0'
            } else if (c >= 'A' && c <= 'F') {
                c - 'A' + 10
            } else if (c >= 'a' && c <= 'f') {
                c - 'a' + 10
            } else {
                -1
            }
            if (value >= 0) {
                if (first) {
                    byteArray[len] = (value shl 4).toByte()
                } else {
                    byteArray[len] = byteArray[len] or value.toByte()
                    len++
                }
                first = !first
            }
            i++
        }
        return byteArray
    }
}
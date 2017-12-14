package com.lp.flashremote.beans

import java.io.Serializable
import java.util.*

/**
 * Created by lzl on 17-12-5.
 * data pack
 */
data class PackByteArray( val flag : Byte ,val len : ByteArray,
                         val body:ByteArray?) :Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PackByteArray

        if (flag != other.flag) return false
        if (!Arrays.equals(body, other.body)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = flag.hashCode()
        result = 31 * result + (body?.let { Arrays.hashCode(it) } ?: 0)
        return result
    }
}
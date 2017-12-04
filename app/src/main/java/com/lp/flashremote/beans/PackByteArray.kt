package com.lp.flashremote.beans

import java.util.*

/**
 * Created by lzl on 17-12-5.
 */
data class PackByteArray(public val flag : Byte , public val body:ByteArray?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PackByteArray

        if (flag != other.flag) return false
        if (!Arrays.equals(body, other.body)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = flag.toInt()
        result = 31 * result + (body?.let { Arrays.hashCode(it) } ?: 0)
        return result
    }
}
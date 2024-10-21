package com.example.devhopeitmtest

import android.util.Log


object LogUtils {
    fun e(tag: String?, msg: String?) {
        var msg = msg
        if (tag == null || tag.length == 0 || msg == null || msg.length == 0) return
        val segmentSize = 3 * 1024
        val length = msg.length.toLong()
        if (length <= segmentSize) { // length is less than or equal to limit print directly
            Log.e(tag, msg)
        } else {
            while (msg!!.length > segmentSize) { // Loop segment print log
                val logContent = msg.substring(0, segmentSize)
                msg = msg.replace(logContent, "")
                Log.e(tag, logContent)
            }
            Log.e(tag, msg) // Print remaining logs
        }
    }
}
package com.whatsappdirect.direct_chat.Utils

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog

/**
 * Created by iblinfotech on 11/09/18.
 */
object CallLogHelper {
    fun getAllCallLogs(cr: ContentResolver): Cursor? {
        val strOrder = CallLog.Calls.DATE + " DESC LIMIT 25"
        val callUri = Uri.parse("content://call_log/calls")
        return cr.query(callUri, null, null, null, strOrder)
    }
}
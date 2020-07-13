package com.whatsappdirect.direct_chat.Utils

/**
 * Created by iblinfotech on 16/11/18.
 */
class SMSModel {
    var id: String? = null
    var address: String? = null
    var msg: String? = null
    var readState //"0" for have not read sms and "1" for have read sms
            : String? = null
    var time: String? = null
    var folderName: String? = null

    override fun toString(): String {
        return "SMSModel{" +
                "_id='" + id + '\'' +
                ", _address='" + address + '\'' +
                ", _msg='" + msg + '\'' +
                ", _readState='" + readState + '\'' +
                ", _time='" + time + '\'' +
                ", _folderName='" + folderName + '\'' +
                '}'
    }
}
package com.wacinfo.easyqueue.Adapter

class ShowQueueitem(name: String, id: String,remain:String) {

    private var Name: String? = name
    private var ID: String? = id
    private var Remain: String? = remain

    fun getName(): String? {
        return Name
    }
    fun getId(): String? {
        return ID
    }

    fun getRemain(): String? {
        return Remain
    }


}
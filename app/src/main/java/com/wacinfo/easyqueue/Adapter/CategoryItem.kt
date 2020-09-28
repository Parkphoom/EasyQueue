package com.wacinfo.easyqueue.Adapter

class CategoryItem(name: String, id: String) {

    private var Name: String? = name
    private var ID: String? = id

    fun getName(): String? {
        return Name
    }
    fun getId(): String? {
        return ID
    }


}
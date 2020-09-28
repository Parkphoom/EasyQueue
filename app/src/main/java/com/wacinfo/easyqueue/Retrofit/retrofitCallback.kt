package com.wacinfo.easyqueue.Retrofit

import org.json.JSONObject


interface retrofitCallback {
    fun onSucess()
    fun onSucess(value: JSONObject)
    fun onSucess(value: ByteArray)
    fun onFailure()
}
package com.example.easyqueue.Retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONArray

class SpeechData {
    @SerializedName("text")
    @Expose
    var text: String? = null

    @SerializedName("lang")
    @Expose
    var lang: String? = null

    @SerializedName("speed")
    @Expose
    var speed: String? = null

}
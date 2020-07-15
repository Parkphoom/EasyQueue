package com.example.queuedemo_kotlin.Retrofit

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class RetrofitData {


    @SerializedName("status")
    var status: String? = null
    @SerializedName("message")
    var message: obj? = null


    class obj {
        @SerializedName("status")
        var status: String? = null
        @SerializedName("queue")
        var queue: String? = null
        @SerializedName("queueWait")
        var queueWait: String? = null

        @SerializedName("values")
        var values: Array<arrayValues>? = null

        fun getqueue(): String? {
            return queue
        }

    }
    class arrayValues{

            @SerializedName("_id")
            var _id: String? = null
            @SerializedName("category")
            var category: String? = null
            @SerializedName("cue")
            var cue: String? = null
            @SerializedName("serviceChannel")
            var serviceChannel: String? = null
    }


}


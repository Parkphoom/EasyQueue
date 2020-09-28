package com.wacinfo.queuedemo_kotlin.Retrofit

import com.google.gson.annotations.SerializedName


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


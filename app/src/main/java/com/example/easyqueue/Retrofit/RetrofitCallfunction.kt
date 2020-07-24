package com.example.easyqueue.Retrofit

import android.app.Activity
import android.util.Log
import com.example.easyqueue.R
import com.example.queuedemo_kotlin.Retrofit.RetrofitData
import com.example.queuedemo_kotlin.Retrofit.SendQueueApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.socket.client.IO
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitCallfunction {
    fun requestQueue(
        activity: Activity, type: String, typecode: String, telnum: String,
        retrofitcallback: retrofitCallback
    ) {
//        val URL = "http://18.139.84.245:4001/addQueue/"
        val URL =
            activity.getString(R.string.URL) + activity.getString(R.string.PORT) + activity.getString(
                R.string.APIaddqueue
            )
        val ID = activity.getString(R.string.ID)
        var Queue: String = ""
        var QueueRemain: String = ""

//                Log.d("urllll", URL)
//                val Upload = resources.getString(R.string.Upload)
//        dialogupload = ProgressDialog(context)
//        dialogupload!!.setMessage("please wait ...")
//        dialogupload!!.setCancelable(false)
//        dialogupload!!.show()


        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.header("Content-Type", "application/json")
                requestBuilder.header("Accept", "application/json")
                return chain.proceed(requestBuilder.build())
            }
        })

        val httpClient = httpClientBuilder.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        var bodyType = type.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
        var bodyTypeCode =
            typecode.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
        var bodyTel =
            telnum.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())

        val api: SendQueueApi = retrofit.create<SendQueueApi>(SendQueueApi::class.java)

        for (i in 0 until 10) {

        }
        val call: Call<RetrofitData> = api.typePost(bodyType, bodyTypeCode, bodyTel, ID)

        //finally performing the call
        //finally performing the call
        call.enqueue(object : Callback<RetrofitData> {
            override fun onResponse(
                call: Call<RetrofitData>,
                response: Response<RetrofitData>
            ) {
                Log.d("ressss", response.body().toString())
                if (response.isSuccessful) {
//                    ToastMessage().message(this@TypeActivity, R.string.fileuploadSuccess.toString())

                    val js = Gson().toJson(response.body())
                    var respData = response.body()

                    var json: JSONObject? = null
                    try {
                        json = JSONObject(js)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    val value: JSONObject = json?.get("message") as JSONObject


                    val socket =
                        IO.socket(activity.getString(R.string.URL) + activity.getString(R.string.PORT))
//                    val socket = IO.socket("http://18.139.84.245:4001")
                    // Sending an object
                    // Sending an object
                    val obj = JSONObject()
                    obj.put("addQueue", "Updated")
                    socket.emit("addQueue", obj)
                    socket.connect()

                    // Receiving an object
                    // Receiving an object
                    socket.on("addQueue") { args ->
                        socket.disconnect()
                    }

                    retrofitcallback.onSucess(value)

                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("ressss_", jObjError.toString())
                        Log.d("ressss_1", jObjError.getString("message"))


                    } catch (e: Exception) {
                        Log.d("ressss_2", e.message)
                    }
                }
            }

            override fun onFailure(call: Call<RetrofitData>, t: Throwable) {
                Log.d("ressss", t.toString())

            }
        })
    }

    fun getAllQueue(activity: Activity, retrofitcallback: retrofitCallback) {
        val URL =
            activity.getString(R.string.URL) + activity.getString(R.string.PORT) + activity.getString(
                R.string.APIgetqueue
            )
        val ID = activity.getString(R.string.ID)
        var Queue: JSONObject


        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.header("Content-Type", "application/json")
                requestBuilder.header("Accept", "application/json")
                return chain.proceed(requestBuilder.build())
            }
        })

        val httpClient = httpClientBuilder.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()


//        var bodyqueueid = id.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())

        val api: SendQueueApi = retrofit.create<SendQueueApi>(SendQueueApi::class.java)
        val call: Call<RetrofitData> = api.getAllQueue(ID)

        //finally performing the call
        //finally performing the call
        call.enqueue(object : Callback<RetrofitData> {
            override fun onResponse(
                call: Call<RetrofitData>,
                response: Response<RetrofitData>
            ) {
                Log.d("getAllQueue_respones", response.toString())
                if (response.isSuccessful) {

                    val js = Gson().toJson(response.body())
                    var respData = response.body()
                    Log.d("getAllQueue_ressss", js)


                    try {
                        val json: JSONObject = JSONObject(js)
                        try {
                            val message: JSONObject = json.get("message") as JSONObject
                            retrofitcallback.onSucess(message)

                        } catch (e: JSONException) {
                            Log.d("getAllQueue_ressss", e.toString())
                            retrofitcallback.onFailure()
                        }
                    } catch (e: JSONException) {
                        Log.d("getAllQueue_ressss", e.toString())
                        retrofitcallback.onFailure()
                    }


                }
            }

            override fun onFailure(call: Call<RetrofitData>, t: Throwable) {
                Log.d("getAllQueue_ressss", "failllll")
                Log.d("getAllQueue_ressss", t.toString())
//                ToastMessage().message(this@TypeActivity, t.message)
            }
        })
    }

    fun postTTS(
        activity: Activity,
        dataSpeech: SpeechData,
        param: retrofitCallback
    ) {

        val URL: String = activity.resources.getString(R.string.URL) + activity.resources
            .getString(R.string.PORT)
        Log.d("urllll", URL)

        val TTS: String = activity.resources.getString(R.string.TexttoSpeech)

        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) //                .client(httpClient)
            .build()
        val api: SendQueueApi = retrofit.create<SendQueueApi>(SendQueueApi::class.java)


        val call: Call<ResponseBody> = api.postTexttoSpeech(dataSpeech, TTS)
        Log.d("urllll", java.lang.String.valueOf(dataSpeech))

        //finally performing the call
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                Log.d("ressss", "$call $response ${response.body()}")
                try {
//                    val directory = File(
//                        Environment.getExternalStorageDirectory()
//                            .toString() + File.separator + "Soundtest"
//                    )
//                    directory.mkdirs()
//                    val file = File(directory, "soundtest")
//                    val fileOutputStream = FileOutputStream(file)
//                    IOUtils.write(response.body()!!.bytes(), fileOutputStream)
//                    Log.e("logTag", directory.absolutePath)

                    param.onSucess(response.body()!!.bytes())
                } catch (e: IOException) {
                    Log.e("logTag", "Error while writing file!")
                    Log.e("logTag", e.toString())
                }
            }

            override fun onFailure(
                call: Call<ResponseBody>,
                t: Throwable
            ) {
                Log.d("ressss", "failllll $t")

            }
        })
    }
}
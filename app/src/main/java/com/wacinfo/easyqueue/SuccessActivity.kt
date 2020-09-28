package com.wacinfo.easyqueue

import android.content.Intent
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.wacinfo.easyqueue.Public.Data
import com.wacinfo.easyqueue.Public.MyExceptionHandler
import com.wacinfo.easyqueue.Public.PublicFunction
import com.wacinfo.easyqueue.Retrofit.RetrofitCallfunction
import com.wacinfo.easyqueue.Retrofit.retrofitCallback
import com.sunmi.peripheral.printer.*
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

class SuccessActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var retrofitCallfuntion: RetrofitCallfunction
    private var sunmiPrinterService: SunmiPrinterService? = null

    private var closeBtn : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)


        initView()
        bindPrintService()

        var index = 0
        for (i in PublicFunction().arCate.indices) {
            if (PublicFunction().arCate[i] == Data.category) {
                index = i
                Data.categorycode = PublicFunction().arCateId[index]
                break
            }
        }

        Log.d(
            PublicFunction().TAG,
            "onCreate: ${Data.userName} ${Data.userCid} ${Data.userTelno} ${Data.category} ${Data.categorycode}"
        )
        callAddqueue(Data.category, Data.categorycode, Data.userTelno,Data.userName)



        Thread.setDefaultUncaughtExceptionHandler(
            MyExceptionHandler(
                this,
                MainActivity::class.java
            )
        )

//        Timer("Finished", false).schedule(120000) {
//           if(!isFinishing){
//               startActivity(Intent(this@SuccessActivity, MainActivity::class.java))
//           }
//
//        }
    }

    private fun initView() {
        sunmiPrinterService = PublicFunction.sunmiPrinterService
        retrofitCallfuntion = RetrofitCallfunction()

        closeBtn = findViewById(R.id.closebtn)
        closeBtn?.setOnClickListener(this)
    }

    override fun onBackPressed() {

    }

    private fun bindPrintService() {
        try {
            InnerPrinterManager.getInstance().bindService(this, object : InnerPrinterCallback() {
                override fun onConnected(service: SunmiPrinterService) {
                    sunmiPrinterService = service
                }

                override fun onDisconnected() {
                    sunmiPrinterService = null
                }
            })
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    private fun sunmiPrintSlip() {
        try {
            sunmiPrinterService!!.enterPrinterBuffer(true)
            sunmiPrinterService!!.setAlignment(1, null)
            sunmiPrinterService!!.printTextWithFont(
                "คิวที่ ${Data.Queue}" + "\n" +
                        "คิวที่กำลังรอ ${Data.QueueRemain} คิว"+"\n",
                "",
                25f,
                innerResultCallbcak
            )
            sunmiPrinterService!!.lineWrap(1, null)
            sunmiPrinterService!!.setAlignment(0, null)
            sunmiPrinterService!!.printTextWithFont(
                "ชื่อ-นามสกุล ${Data.userName}" + "\n" +
                        "เบอร์โทร : ${Data.userTelno}" + "\n",
                "",
                25f,
                innerResultCallbcak
            )
            sunmiPrinterService!!.exitPrinterBufferWithCallback(true, innerResultCallbcak)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("lifecycle", "Finished")
            Timer("Finished", false).schedule(2000) {
                startActivity(Intent(this@SuccessActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private var issucces: Boolean = true
    private val innerResultCallbcak: InnerResultCallbcak = object : InnerResultCallbcak() {
        override fun onRunResult(isSuccess: Boolean) {
            Log.e("lxy", "isSuccess:$isSuccess")
            if (issucces) {
                try {
//                    sunmiPrinterService.printTextWithFont(
//                            printContent + "\n", "", 30, innerResultCallbcak);
                    sunmiPrinterService!!.lineWrap(3, this)
                    issucces = false
                    Log.d("lifecycle", "Finished")
                    Timer("Finished", false).schedule(2000) {

                        startActivity(Intent(this@SuccessActivity, MainActivity::class.java))
                        finish()
                    }
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }

        override fun onReturnString(result: String) {
            Log.e("lxy", "result:$result")
        }

        override fun onRaiseException(code: Int, msg: String) {
            Log.e("lxy", "code:$code,msg:$msg")
        }

        override fun onPrintResult(code: Int, msg: String) {
            Log.e("lxy", "code:$code,msg:$msg")
        }
    }

    var i = 1
    private fun callAddqueue(
        category: String,
        cateId: String,
        userTelno: String,
        name:String
    ) {
        retrofitCallfuntion.requestQueue(
            this,
            category,
            cateId,
            userTelno,
            name,
            object : retrofitCallback {
                override fun onSucess() {

                }

                override fun onSucess(value: JSONObject) {
                    val Queue = value.get("queue").toString()
                    val QueueRemain = value.get("queueWait").toString()
                    Log.d("onSucess", value.toString())
                    Log.d("onSucess", Queue)
                    Log.d("onSucess", QueueRemain)
                    Data.Queue = Queue
                    Data.QueueRemain = QueueRemain
                    sunmiPrintSlip()
                }

                override fun onSucess(value: ByteArray) {
                }

                override fun onFailure() {
                }

            })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.closebtn ->{
                startActivity(Intent(this,MainActivity::class.java))
            }
        }
    }



}
package com.example.easyqueue

import android.content.Intent
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.easyqueue.Public.Data
import com.example.easyqueue.Public.PublicFunction
import com.sunmi.peripheral.printer.*
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

class SuccessActivity : AppCompatActivity() {

    private var sunmiPrinterService: SunmiPrinterService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        Log.d(
            PublicFunction().TAG,
            "onCreate: ${Data.userName} ${Data.userCid} ${Data.userTelno} ${Data.category}"
        )

        initView()

        bindPrintService()


        sunmiPrintSlip()
    }

    private fun initView() {
        sunmiPrinterService = PublicFunction.sunmiPrinterService
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
            sunmiPrinterService!!.setAlignment(0, null)
            sunmiPrinterService!!.printTextWithFont(
                Data.userName + "\n" +
                        "เบอร์โทร : ${Data.userTelno}" + "\n",
                "",
                25f,
                innerResultCallbcak
            )
            sunmiPrinterService!!.exitPrinterBufferWithCallback(true, innerResultCallbcak)

        } catch (e: Exception) {
            e.printStackTrace()
            Timer("Finished", false).schedule(2000) {
                startActivity(Intent(this@SuccessActivity, MainActivity::class.java))
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
                    sunmiPrinterService!!.lineWrap(2, this)
                    issucces = false
                    Timer("Finished", false).schedule(2000) {
                        startActivity(Intent(this@SuccessActivity, MainActivity::class.java))
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


}
package com.example.easyqueue

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sunmi.pay.hardware.aidl.AidlConstants
import com.sunmi.pay.hardware.aidlv2.bean.ApduRecvV2
import com.sunmi.pay.hardware.aidlv2.bean.ApduSendV2
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
import java.io.UnsupportedEncodingException

class ScanIdCardActivity : AppCompatActivity(), View.OnClickListener {

    val mnAc: MainActivity = MainActivity()
    val pub: Public = Public()
    val conv: Convert = Convert()

    var clearnametxtbtn: Button? = null
    var clearidtxtbtn: Button? = null
    var nextbtn: Button? = null

    var nameedt: EditText? = null
    var idedt: EditText? = null

    var rescan: TextView? = null
    private var isReadyToRead: Boolean = true
    private var dialog: Dialog? = null
    private var CARD_ABSENT_dialog: AlertDialog? = null

    private var cardType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_id_card)

        initView()
        checkCard()

    }

    private fun initView() {
        clearnametxtbtn = findViewById(R.id.clear_name_btn)
        clearnametxtbtn?.setOnClickListener(this)
        clearidtxtbtn = findViewById(R.id.clear_id_btn)
        clearidtxtbtn?.setOnClickListener(this)
        nextbtn = findViewById(R.id.next_btn)
        nextbtn?.setOnClickListener(this)

        rescan = findViewById(R.id.rescan)
        rescan?.setOnClickListener(this)
        nameedt = findViewById(R.id.name_edt)
        nameedt?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (start == 0 && count == 0) {
                    clearnametxtbtn?.visibility = View.INVISIBLE
                } else {
                    clearnametxtbtn?.visibility = View.VISIBLE
                }
            }

        })

        idedt = findViewById(R.id.id_edt)
        idedt?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (start == 0 && count == 0) {
                    clearidtxtbtn?.visibility = View.INVISIBLE
                } else {
                    clearidtxtbtn?.visibility = View.VISIBLE
                }
            }

        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.clear_name_btn -> {
                nameedt?.text?.clear()
            }
            R.id.clear_id_btn -> {
                idedt?.text?.clear()
            }
            R.id.next_btn -> {
                startActivity(Intent(this, InputTelnoActivity::class.java))

                Data.userName = nameedt?.text.toString()
                Data.userCid = idedt?.text.toString()
            }
            R.id.rescan -> {
                if (MainActivity.isDisConnectService) {
                    MainActivity().connectPayService()
                    Toast.makeText(this, R.string.connect_loading, Toast.LENGTH_SHORT)
                        .show()
                }
                checkCard()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == pub.REQUEST_WRITE_STORAGE_REQUEST_READCARD) {
            try {
                //支持M1卡
                val allType =
                    AidlConstants.CardType.NFC.value or AidlConstants.CardType.IC.value
                Log.d(
                    pub.TAG,
                    allType.toString()
                )
                Public.mReadCardOptV2!!.checkCard(allType, mReadCardCallback, 300)
                //            Public.mReadCardOptV2.time
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkCard() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    pub.REQUEST_WRITE_STORAGE_REQUEST_READCARD
                )
            }
        } else {
            try {
                val allType =
                    (AidlConstants.CardType.NFC.value or AidlConstants.CardType.IC.value
                            or AidlConstants.CardType.MAGNETIC.value)
                Log.d(
                    pub.TAG,
                    allType.toString()
                )
                Public.mReadCardOptV2!!.checkCard(allType, mReadCardCallback, 5000)
                //            Public.mReadCardOptV2.time
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(
                    pub.TAG,
                    e.toString()
                )
            }
        }
    }

    private val mReadCardCallback: CheckCardCallbackV2 = object : CheckCardCallbackV2.Stub() {
        override fun findMagCard(bundle: Bundle) {
            Log.e(
                pub.TAG,
                "findMagCard,bundle:$bundle"
            )
            cardType = AidlConstants.CardType.MAGNETIC.value
            runOnUiThread(Runnable {
//                name.setText("")
//                id.setText("")
                val track1 = bundle.getString("TRACK1")
                val track2 = bundle.getString("TRACK2")
                val track3 = bundle.getString("TRACK3")
                val isEmpty =
                    TextUtils.isEmpty(track1) && TextUtils.isEmpty(track2) && TextUtils.isEmpty(
                        track3
                    )
                if (isEmpty) {
                    failed()
                } else {
                    if (track1 != null && track2 != null && track3 != null) {
                        success(track1, track2, track3)
                    }
                }
                handleResult(false)
            })
        }

        @Throws(RemoteException::class)
        override fun findICCard(atr: String) {
            if (isReadyToRead) {
                isReadyToRead = false
                Log.e(
                    pub.TAG,
                    "findICCard, atr:$atr"
                )
                cardType = AidlConstants.CardType.IC.value
                Log.e(
                    pub.TAG,
                    "cardtype :$cardType"
                )
                if (Public.mReadCardOptV2!!.getCardExistStatus(cardType) === AidlConstants.CardExistStatus.CARD_ABSENT) {
                    Log.e(
                        pub.TAG,
                        "cardExit :" + "CARD_ABSENT"
                    )
                    CARD_ABSENT_dialog?.cancel()
                } else if (Public.mReadCardOptV2!!.getCardExistStatus(cardType) === AidlConstants.CardExistStatus.CARD_PRESENT) {
                    Log.e(
                        pub.TAG,
                        "cardExit :" + "CARD_PRESENT"
                    )
                }
                runOnUiThread(Runnable {
                    ICReadTask()
                        .execute()
                })
            } else {
                handleResult(false)
            }
        }

        @Throws(RemoteException::class)
        override fun findRFCard(uuid: String) {
            Log.e(
                pub.TAG,
                "findRFCard, uuid:$uuid"
            )
            cardType = AidlConstants.CardType.NFC.value
//            message("uuid:$uuid")
            handleResult(false)
        }

        @Throws(RemoteException::class)
        override fun onError(code: Int, msg: String) {
            Log.e(
                pub.TAG,
                "check card error,code:" + code + "message:" + msg
            )
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ICReadTask : AsyncTask<Void?, Int?, Void?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            nameedt?.setText("")
            idedt?.setText("")
            dialog = Dialog(this@ScanIdCardActivity, android.R.style.Theme_Translucent_NoTitleBar)
            dialog!!.setTitle(null)
            dialog!!.setContentView(R.layout.dialog_loading)
            dialog!!.setCancelable(false)
            dialog!!.show()
        }

        override fun onPostExecute(result: Void?) {
            dialog!!.cancel()

            CARD_ABSENT_dialog = createBuilder().create()
            CARD_ABSENT_dialog!!.show()
        }

        override fun doInBackground(vararg params: Void?): Void? {
            sendApduByApduData()
            handleResult(true)

            return null
        }
    }

    private fun createBuilder(): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this@ScanIdCardActivity)

        // Set the alert dialog title
        builder.setTitle("แจ้งเตือน!!!")

        // Display a message on alert dialog
        builder.setMessage("ดึงบัตรประชาชนออก")

        // Display a negative button on alert dialog
        builder.setNegativeButton("ปิด") { dialog, which ->
//            Toast.makeText(applicationContext, "You are not agree.", Toast.LENGTH_SHORT).show()
        }

        return builder
    }

    private fun sendApduByApduData() {
        val send = ApduSendV2()
        for (i in 0..2) {
            send.command = conv.hexStringToByteArray(pub.CommandAPDU[i])
            send.lc = pub.LcAPDU[i].toShort(16)
            send.dataIn = conv.hexStringToByteArray(pub.DataInAPDU.get(i))
            send.le = pub.LeAPDU[i].toShort(16)
            try {
                val recv = ApduRecvV2()
                var code = 0
                try {
                    code = Public.mReadCardOptV2!!.apduCommand(cardType, send, recv)
                    if (code < 0) {
                        Log.e(
                            pub.TAG,
                            "apduCommand failed,code:\$code"
                        )
                    } else {
                        Log.e(
                            pub.TAG,
                            "apduCommand success,recv:\$recv $i"
                        )
                        //                        showApduRecv(recv.outlen.toInt(), recv.outData, recv.swa, recv.swb)
                        var recStr = ""
                        try {
                            recStr = String(recv.outData, charset("TIS620"))
                        } catch (e: UnsupportedEncodingException) {
                            e.printStackTrace()
                        }
                        recStr = recStr.replace(" ".toRegex(), "")
                        recStr = recStr.replace("\n".toRegex(), "")
                        recStr = recStr.replace("\u0090".toRegex(), "")
                        recStr = recStr.replace("##".toRegex(), " ")
                        recStr = recStr.replace("#".toRegex(), " ")
                        recStr = recStr.replace("\\s".toRegex(), " ")
                        recStr = recStr.replace("\\0", "").replace("\u0000", "")
                        val finalI: Int = i
                        val finalRecStr = recStr

                        runOnUiThread {
                            when (finalI) {
                                1 -> {
                                    run {
                                        idedt?.setText(finalRecStr)
//                                        userData.setIdcard(finalRecStr)
//                                        Log.d("readcard1", userData.getIdcard())
                                        Log.d("readcard1", finalRecStr)
                                    }
                                }
                                2 -> {
                                    run {
                                        nameedt?.setText(finalRecStr)
//                                        userData.setNameth(finalRecStr)
//                                        Log.d("readcard2", userData.getNameth())
                                        Log.d("readcard2", finalRecStr)
                                    }

                                }
                            }
                        }
                    }
                } catch (e: RemoteException) {
                    e.printStackTrace()
                    Log.e(
                        pub.TAG,
                        e.toString()
                    )
                }
            } catch (e: java.lang.Exception) {
                Log.e(
                    pub.TAG,
                    e.toString()
                )
            }
        }
    }


    private val mHandler = Handler()

    private fun handleResult(nfcfinish: Boolean) {
        if (isFinishing) {
            return
        }
        mHandler.post {
            if (cardType == AidlConstants.CardType.MAGNETIC.getValue()) {
                isReadyToRead = true
                checkCard()
            } else if (cardType == AidlConstants.CardType.NFC.getValue()) {
                isReadyToRead = true
                checkCard()
            } else if (cardType == AidlConstants.CardType.IC.getValue()) {
                try {
                    if (nfcfinish && Public.mReadCardOptV2!!.getCardExistStatus(cardType) === AidlConstants.CardExistStatus.CARD_ABSENT) {

                        isReadyToRead = true
                        checkCard()
                    } else if (nfcfinish &&
                        Public.mReadCardOptV2!!.getCardExistStatus(cardType) === AidlConstants.CardExistStatus.CARD_PRESENT
                    ) {
                        isReadyToRead = false
                        handleResult(false)
                    }
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
                // 继续检卡
                if (!isFinishing) {
                    try {
                        if (Public.mReadCardOptV2!!.getCardExistStatus(cardType) === AidlConstants.CardExistStatus.CARD_ABSENT) {
                            Log.e(
                                pub.TAG,
                                "cardExit :" + "CARD_ABSENT"
                            )
                            CARD_ABSENT_dialog?.cancel()
                            isReadyToRead = true
                            checkCard()
                        } else if (Public.mReadCardOptV2!!.getCardExistStatus(cardType) === AidlConstants.CardExistStatus.CARD_PRESENT) {
                            Log.e(
                                pub.TAG,
                                "cardExit :" + "CARD_PRESENT"
                            )
                            isReadyToRead = false
                            handleResult(false)
                        }
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun cancelCheckCard() {
        try {
            Public.mReadCardOptV2!!.cardOff(AidlConstants.CardType.NFC.value)
            Public.mReadCardOptV2!!.cancelCheckCard()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private var mTotalTime = 0
    private var mSuccessTime = 0
    private var mFailTime = 0

    private fun failed() {
        mTotalTime += 1
        mFailTime += 1
    }

    private fun success(
        track1: String,
        track2: String,
        track3: String
    ) {
        try {
            mTotalTime += 1
            mSuccessTime += 1
            var a = arrayOfNulls<String>(2)
            var temp = " $track1"
            temp = temp.replace("^", "@")
            temp = temp.replace("$", "@")
            a = temp.split("@".toRegex()).toTypedArray()
            Log.d("MagneticRead", a[0].toString() + " " + a[1])
//            name.setText(a[3].toString() + " " + a[2] + " " + a[1])
            Log.d("MagneticRead", temp)
            temp = " " + track2.substring(6, 19)
//            id.setText(temp)
            Log.d("MagneticRead", temp)
            temp = " $track3"
            Log.d("MagneticRead", temp)
        } catch (e: java.lang.Exception) {
//            name.setText("")
//            id.setText("")
            Log.d("MagneticRead", e.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelCheckCard()
    }
}
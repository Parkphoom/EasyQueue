package com.example.easyqueue

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.easyqueue.Retrofit.RetrofitCallfunction
import com.example.easyqueue.Retrofit.retrofitCallback
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterException
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.SunmiPrinterService
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONObject
import sunmi.paylib.SunmiPayKernel
import sunmi.paylib.SunmiPayKernel.ConnectCallback
import java.util.*
import kotlin.collections.List
import kotlin.collections.arrayListOf
import kotlin.collections.filterKeys
import kotlin.collections.indices
import kotlin.collections.isNullOrEmpty
import kotlin.collections.mutableMapOf
import kotlin.collections.set

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var retrofitCallfuntion: RetrofitCallfunction
    private var mSMPayKernel: SunmiPayKernel? = null
    private val TAG = "SunmiSTART"

    var newItemMap = mutableMapOf<String, Int>()
    var itemMap = mutableMapOf<String, Int>()

    companion object {
        var isDisConnectService = true
    }

    lateinit var mSocket: Socket

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        connectSocket()

    }

    private fun connectSocket() {
        try {
            //This address is the way you can connect to localhost with AVD(Android Virtual Device)
            mSocket = IO.socket(getString(R.string.URL) + getString(R.string.PORT))
            Log.d("IOsuccess", "mSocket")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("IOfail", "Failed to connect")
        }

        mSocket.connect()
        //Register all the listener and callbacks here.
        mSocket.on(Socket.EVENT_CONNECT) {
            Log.d("IoSockket", "connect")
            callGetAllqueue(false)
        }
        mSocket.on("addQueue") {
            Log.d(
                "IoSockket",
                "addQueue"
            )
            callGetAllqueue(true)
        } // To know if the new user entered the room.
        mSocket.on("sentEndQueue") {
            Log.d(
                "IoSockket",
                "sentEndQueue"
            )
            callGetAllqueue(true)
        } // To know if the new user entered the room.

        mSocket.on(Socket.EVENT_DISCONNECT){
            Log.d("IoSockket", "disconnect")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initView() {
        retrofitCallfuntion = RetrofitCallfunction()

    }



    override fun onStart() {
        super.onStart()
        connectPayService()
        bindPrintService()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createList(
        id: Int,
        cate: String,
        que: String
    ): View? {
        // Creating a new RelativeLayout
        val relativeLayout = RelativeLayout(this)

        relativeLayout.setBackgroundResource(R.drawable.whitebg)

        // Adding the TextView to the RelativeLayout as a child
//        val edtview: EditText = createEditview(id, txt, prefKey)
        val btnview: Button? = createDeletebtn(id)
        val txtcate: TextView? =
            createTextviewCate(id, RelativeLayout.ALIGN_PARENT_START, cate, 20f, 0f)
        val txtque: TextView? =
            createTextviewCate(id, RelativeLayout.ALIGN_PARENT_END, que, 0f, 40f)

        relativeLayout.addView(txtcate)
        relativeLayout.addView(txtque)
        relativeLayout.addView(btnview)

        relativeLayout.setOnClickListener {
            if (isDisConnectService) {
                connectPayService()
                Toast.makeText(this, R.string.connect_loading, Toast.LENGTH_SHORT)
                    .show()
            }
            Data.category = cate
            startActivity(Intent(this, ScanIdCardActivity::class.java))
        }

        return relativeLayout
    }

    private fun createDeletebtn(
        id: Int
    ): Button? {
        val paramsBTN = RelativeLayout.LayoutParams(
            getDP(18f),
            getDP(18f)
        )
        paramsBTN.setMargins(0, 0, getDP(10f), 0)
        paramsBTN.addRule(RelativeLayout.ALIGN_PARENT_END)
        paramsBTN.addRule(RelativeLayout.CENTER_VERTICAL)
        val deletebtn = Button(this)
        deletebtn.layoutParams = paramsBTN
        deletebtn.id = id
        deletebtn.gravity = Gravity.CENTER_VERTICAL
        deletebtn.setBackgroundResource(R.drawable.icons8_add_96px_2)
        return deletebtn
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createTextviewCate(
        id: Int,
        verb: Int,
        txt: String,
        marginstart: Float,
        marginend: Float
    ): TextView? {
        val paramsBTN = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        paramsBTN.addRule(RelativeLayout.CENTER_VERTICAL)

        paramsBTN.marginStart = getDP(marginstart)
        paramsBTN.marginEnd = getDP(marginend)

        paramsBTN.addRule(verb)
        val textView = TextView(this)
        textView.layoutParams = paramsBTN
        textView.id = id
        textView.text = txt
        textView.textSize = 18f
        textView.setTextColor(getColor(R.color.black))
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.setPadding(0, 10, 0, 10)


        return textView
    }


    private fun getDP(value: Float): Int {
        val r: Resources = this.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            r.displayMetrics
        ).toInt()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    fun connectPayService() {
        try {
            mSMPayKernel = SunmiPayKernel.getInstance()
            mSMPayKernel?.initPaySDK(this, mConnectCallback)
        } catch (e: Exception) {
            Log.d("SystemUtil", e.toString())
        }
    }

    private fun bindPrintService() {
        try {
            InnerPrinterManager.getInstance()
                .bindService(this, object : InnerPrinterCallback() {
                    override fun onConnected(service: SunmiPrinterService) {
                        Public.sunmiPrinterService = service
                    }

                    override fun onDisconnected() {
                        Public.sunmiPrinterService = null
                    }
                })
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mSMPayKernel != null) {
            mSMPayKernel?.destroyPaySDK()
        }
    }


    private val mConnectCallback: ConnectCallback = object : ConnectCallback {
        override fun onConnectPaySDK() {
            Log.e(TAG, "onConnectPaySDK")
            try {
                Public.mEMVOptV2 = mSMPayKernel!!.mEMVOptV2
                Public.mBasicOptV2 = mSMPayKernel!!.mBasicOptV2
                Public.mPinPadOptV2 = mSMPayKernel!!.mPinPadOptV2
                Public.mReadCardOptV2 = mSMPayKernel!!.mReadCardOptV2
                Public.mSecurityOptV2 = mSMPayKernel!!.mSecurityOptV2
                Public.mTaxOptV2 = mSMPayKernel!!.mTaxOptV2
                isDisConnectService = false
                Log.d("SystemUtil", "connect success")
            } catch (e: Exception) {
                Log.d("SystemUtil", "connect fail")
                e.printStackTrace()
            }
        }

        override fun onDisconnectPaySDK() {
            Log.e(TAG, "onDisconnectPaySDK")
            Log.d("SystemUtil", "connect faillll")
            isDisConnectService = true
            //            showToast(R.string.connect_fail);
        }
    }

    private fun callGetAllqueue(isUpdate: Boolean) {
        retrofitCallfuntion.getAllQueue(
            this,
            object : retrofitCallback {
                override fun onSucess() {

                }

                override fun onSucess(value: JSONObject) {

                    val values = value["values"] as JSONArray
                    Log.d("getAllQueue_ressss", values.toString())

                    val category = arrayListOf<String>()
                    for (i in 0 until values.length()) {
                        val cate = values[i] as JSONObject
                        val strcate = cate.getString("category")
                        category.add(strcate)
                    }
                    Log.d(TAG, "category: ${category}")
                     setCatelist(category, isUpdate)
                }

                override fun onFailure() {

                }
            })
    }

    private fun setCatelist(
        distinctcategory1: List<String>,
        update: Boolean
    ) {
        // insert into main view
        val insertPoint: ViewGroup = findViewById(R.id.container)
        val param: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.setMargins(0, getDP(10f), getDP(10f), 0)

        if (update) {
            for (i in Public().arCate.indices) {
                if (itemMap.filterKeys { it.contains(Public().arCate[i]) }.isNullOrEmpty()) {
                    itemMap[Public().arCate[i]] = 0
                    val cate = Public().arCate[i]
                    val list = itemMap[Public().arCate[i]]!!

                }
            }
            for (i in distinctcategory1.indices) {
                if (itemMap.filterKeys { it.contains(distinctcategory1[i]) }.isNullOrEmpty()) {
                    itemMap[distinctcategory1[i]] = 1

                    val cate = distinctcategory1[i]
                    val list = itemMap[distinctcategory1[i]]!!

                } else {
                    itemMap[distinctcategory1[i]] = itemMap[distinctcategory1[i]]!!.plus(1)
                }
            }
            val indexes: List<String> = ArrayList<String>(itemMap.keys).asReversed()

            val new: List<Int> = ArrayList<Int>(itemMap.values).asReversed()
            val old: List<Int> = ArrayList<Int>(newItemMap.values).asReversed()
            try {
                for (i in new.indices) {
                    if (new[i] != old[i]) {
                        val li = insertPoint.getChildAt(indexes.indexOf(indexes[i])) as RelativeLayout
                        val txt = li.getChildAt(0) as TextView
                        val txt2 = li.getChildAt(1) as TextView
                        Log.d(TAG, "setCatelist: ${indexes[i]}")
                        Log.d(TAG, "setCatelist: ${indexes.indexOf(indexes[i])}")
                        Log.d(TAG, "setCatelist: ${txt.text}")
                        txt2.text = new[i].toString()
                        newItemMap = itemMap.toMutableMap()
                        itemMap.clear()
                        break
                    }
                }
            } catch (e: IndexOutOfBoundsException){
                Log.d(TAG, "setCatelist: $e")
            }

            Log.d(TAG, "setCatelist: $indexes")
            Log.d(TAG, "setCatelist: $new")
            Log.d(TAG, "setCatelist: $old")
        } else {
            for (i in Public().arCate.indices) {
                if (newItemMap.filterKeys { it.contains(Public().arCate[i]) }.isNullOrEmpty()) {
                    newItemMap[Public().arCate[i]] = 0

                    val cate = Public().arCate[i]
                    val list = newItemMap[Public().arCate[i]]!!

                    // Do things with the list
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    val v = createList(list, cate, list.toString()) as RelativeLayout
//                    Log.d(TAG, "setCatelistView: ${(v.getChildAt(0) as TextView).text}")
                        insertPoint.addView(
                            createList(list, cate, list.toString()),
                            0,
                            param
                        )
                    }
                }
            }
            for (i in distinctcategory1.indices) {
                if (newItemMap.filterKeys { it.contains(distinctcategory1[i]) }.isNullOrEmpty()) {
                    newItemMap[distinctcategory1[i]] = 1

                    val cate = distinctcategory1[i]
                    val list = newItemMap[distinctcategory1[i]]!!

                    // Do things with the list
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    val v = createList(list, cate, list.toString()) as RelativeLayout
//                    Log.d(TAG, "setCatelistView: ${(v.getChildAt(0) as TextView).text}")
                        insertPoint.addView(
                            createList(list, cate, list.toString()),
                            0,
                            param
                        )
                    }
                } else {
//                Log.d(TAG, "setCatelist: ${distinctcategory1[i]} ${itemMap[distinctcategory1[i]]}")

                    newItemMap[distinctcategory1[i]] = newItemMap[distinctcategory1[i]]!!.plus(1)

                    val indexes: List<String> = ArrayList<String>(newItemMap.keys).asReversed()
                    val li =
                        insertPoint.getChildAt(indexes.indexOf(distinctcategory1[i])) as RelativeLayout?
                    val txt = li?.getChildAt(0) as TextView?
                    if (txt?.text == distinctcategory1[i]) {
                        val txt2 = li?.getChildAt(1) as TextView?
                        txt2?.text = newItemMap[distinctcategory1[i]].toString()

                    }

                }
            }
            Log.d(TAG, "setCatelist: ${newItemMap}")

        }

//
//        if (update) {
//            for (entry in a.entries) {
//                val list: Int = entry.value
//                val cate: String = entry.key
//                // Do things with the list
//                val li = insertPoint.getChildAt(0) as RelativeLayout
//                val txt = li.getChildAt(0) as TextView
//                if (txt.text == "กุมารเวชกรรม") {
//                    val txt2 = li.getChildAt(1) as TextView
//                    txt2.text = "1213213131"
//                }
//                Log.d(TAG, "setCatelist: ${txt.text}")
//
//            }
//        } else {
//            for (entry in a.entries) {
//                val list: Int = entry.value
//                val cate: String = entry.key
//                // Do things with the list
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    val v = createList(list, cate, list.toString()) as RelativeLayout
//                    Log.d(TAG, "setCatelistView: ${(v.getChildAt(0) as TextView).text}")
//                    insertPoint.addView(
//                        v,
//                        0,
//                        param
//                    )
//                }
//            }
//        }


    }


}
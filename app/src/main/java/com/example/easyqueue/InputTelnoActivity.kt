package com.example.easyqueue

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.easyqueue.Retrofit.retrofitCallback
import com.example.easyqueue.Retrofit.RetrofitCallfunction
import org.json.JSONArray
import org.json.JSONObject

class InputTelnoActivity : AppCompatActivity(), View.OnClickListener {
    private var nextbtn: Button? = null
    private var txtname: TextView? = null
    private var clear_telono_btn: TextView? = null
    private var txtcitizenID: TextView? = null
    private var telono_edt: EditText? = null

    private lateinit var retrofitCallfuntion: RetrofitCallfunction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_telno)

        initView()

        txtname!!.text = Data.userName
        txtcitizenID!!.text = Data.userCid
    }

    private fun initView() {
        retrofitCallfuntion = RetrofitCallfunction()

        nextbtn = findViewById(R.id.next_btn)
        nextbtn?.setOnClickListener(this)
        clear_telono_btn = findViewById(R.id.clear_telono_btn)
        clear_telono_btn?.setOnClickListener(this)

        txtname = findViewById(R.id.txtname)
        txtcitizenID = findViewById(R.id.txtcitizenID)

        telono_edt = findViewById(R.id.telono_edt)
        telono_edt?.addTextChangedListener(object : TextWatcher {
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
                    clear_telono_btn?.visibility = View.INVISIBLE
                } else {
                    clear_telono_btn?.visibility = View.VISIBLE
                }
            }

        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.next_btn -> {
                Data.userTelno = telono_edt?.text.toString()

                var index = 0
                for (i in Public().arCate.indices) {
                    if (Public().arCate[i] == Data.category) {
                        index = i
                        break
                    }
                }
                callAddqueue(Data.category, Public().arCateId[index], Data.userTelno)
            }
            R.id.clear_telono_btn -> {
                telono_edt?.text?.clear()
            }
        }
    }

    private fun callAddqueue(
        category: String,
        cateId: String,
        userTelno: String
    ) {
        retrofitCallfuntion.requestQueue(
            this,
            category,
            cateId,
            userTelno,
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

                    startActivity(Intent(this@InputTelnoActivity, SuccessActivity::class.java))
                }

                override fun onFailure() {
                }

            })
    }

}
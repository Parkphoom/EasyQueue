package com.wacinfo.easyqueue.Settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wacinfo.easyqueue.DB.DBCategoryManager
import com.wacinfo.easyqueue.R


class EditCategoryActivity : AppCompatActivity(), View.OnClickListener {
    private var dbCategoryManager: DBCategoryManager? = null
    val TAG = "EditCategorylog"
    var header_tv: TextView? = null
    var name_edt: EditText? = null
    var code_edt: EditText? = null
    var name: String? = null
    var code: String? = null
    var savebtn: Button? = null
    var delbtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        initView()
    }

    private fun initView() {
        dbCategoryManager = DBCategoryManager(this)

        header_tv = findViewById(R.id.header_tv)
        name_edt = findViewById(R.id.name_edt)
        code_edt = findViewById(R.id.code_edt)
        savebtn = findViewById(R.id.save_btn)
        savebtn?.setOnClickListener(this)
        delbtn = findViewById(R.id.del_btn)
        delbtn?.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        name = intent.getStringExtra("name")
        code = intent.getStringExtra("code")
        name_edt!!.setText(name)
        header_tv!!.text = name
        code_edt!!.setText(code)
        name_edt!!.setSelection(name_edt!!.text.length)
        initEdt()
        showKeyboard()
    }

    private fun showKeyboard() {
        val inputMethodManager =
            this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

    }

    private fun initEdt() {
        savebtn!!.isEnabled = false
        name_edt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG, "beforeTextChanged: $s")

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, "onTextChanged: $s")
                savebtn?.isEnabled = s.toString().trim() != name
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "afterTextChanged: $s")
            }

        })
        code_edt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG, "beforeTextChanged: $s")

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, "onTextChanged: $s")
                savebtn?.isEnabled = s.toString().trim() != code
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "afterTextChanged: $s")
            }

        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save_btn -> {
                editCategory(name.toString(), code.toString(), name_edt?.text.toString(), code_edt?.text.toString())
            }
            R.id.del_btn -> {
                delCategory(name_edt?.text.toString().trim(), code_edt?.text.toString().trim())
            }
        }
    }

    private fun editCategory(oldname: String, oldcode: String, newname: String, newcode: String) {
        dbCategoryManager?.open()
        if(dbCategoryManager?.updateCategory(oldname, oldcode, newname, newcode)!!){
            Toast.makeText(this,"Update success",Toast.LENGTH_SHORT).show()
            onBackPressed()
        }else{
            Toast.makeText(this,"Update failed",Toast.LENGTH_SHORT).show()
        }
        dbCategoryManager!!.close()
    }
    private fun delCategory(name: String, code: String) {
        dbCategoryManager?.open()
        if(dbCategoryManager?.deleteCategory(name, code)!!){
            Toast.makeText(this,"Delete success",Toast.LENGTH_SHORT).show()
            onBackPressed()
        }else{
            Toast.makeText(this,"Delete failed",Toast.LENGTH_SHORT).show()
        }
        dbCategoryManager!!.close()
    }


}
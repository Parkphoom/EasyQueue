package com.wacinfo.easyqueue.Settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.wacinfo.easyqueue.DB.DBCategoryManager
import com.wacinfo.easyqueue.R

class AddCategoryActivity : AppCompatActivity(), View.OnClickListener {
    private var dbCategoryManager: DBCategoryManager? = null
    var name_edt: EditText? = null
    var code_edt: EditText? = null
    var savebtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        initView()

    }
    private fun initView() {
        dbCategoryManager = DBCategoryManager(this)
        name_edt = findViewById(R.id.name_edt)
        code_edt = findViewById(R.id.code_edt)
        savebtn = findViewById(R.id.save_btn)
        savebtn?.setOnClickListener(this)
    }

    private fun insertCategory( newname: String, newcode: String) {
        dbCategoryManager?.open()
        if( dbCategoryManager?.insertCATEGORY(newname, newcode)!! > 0){
            Toast.makeText(this,"Insert success", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }else{
            Toast.makeText(this,"Insert failed", Toast.LENGTH_SHORT).show()
        }

        dbCategoryManager!!.close()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.save_btn->{
                if(code_edt!!.text.toString().isNotEmpty() && name_edt!!.text.toString().isNotEmpty()){
                    insertCategory(code_edt!!.text.toString(),name_edt!!.text.toString())
                }
            }
        }
    }

}
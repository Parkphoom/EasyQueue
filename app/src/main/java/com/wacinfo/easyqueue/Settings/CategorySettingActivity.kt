package com.wacinfo.easyqueue.Settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.fileChooser
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wacinfo.easyqueue.Adapter.CategoryItem
import com.wacinfo.easyqueue.Adapter.CategoryListAdapter
import com.wacinfo.easyqueue.DB.DBCategoryManager
import com.wacinfo.easyqueue.DB.DatabaseHelper
import com.wacinfo.easyqueue.Public.PublicFunction
import com.wacinfo.easyqueue.R
import org.json.JSONException
import org.json.JSONObject
import java.io.*

class CategorySettingActivity : AppCompatActivity(), View.OnClickListener {

    private var TAG = "CategorySettingLog"
    private var dbCategoryManager:DBCategoryManager? =null
    private var categoryItem: List<CategoryItem>? = null
    var recyclerView: RecyclerView? = null
    var adapter: CategoryListAdapter? = null
    var menubtn: Button? = null
    lateinit var dataDB: MutableList<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_setting)

        initView()

    }

    private fun initView() {
        dbCategoryManager = DBCategoryManager(this)
        menubtn = findViewById(R.id.addmenu_btn)
        menubtn?.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        readDataDB()

        setUpRecyclerView(categoryItem as ArrayList<CategoryItem>)
    }

    private fun readDataDB(){
        categoryItem = ArrayList()
        dbCategoryManager?.open()
        dataDB = dbCategoryManager?.getAll()!!
        for (i in 0 until dataDB.size) {
            val values: JSONObject? = dataDB[i] as JSONObject?
            try {
                val catname = values!!.getString(DatabaseHelper.Category_name)
                val catID = values.getString(DatabaseHelper.Category_code)
                Log.d("dataDB", "$catname $catID")
                (categoryItem as ArrayList<CategoryItem>).add(CategoryItem(catname,catID))

            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("dataDB", e.toString())
            }
        }
        dbCategoryManager?.close()
    }

    private fun setUpRecyclerView(listitem: List<CategoryItem>) {
        this.runOnUiThread {
            recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView

            //        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.layoutManager = layoutManager

            adapter = CategoryListAdapter(this, listitem)
            recyclerView!!.adapter = adapter

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addmenu_btn -> {
                addMenuDialog(menubtn!!)
            }
        }
    }

    private fun addMenuDialog(view: View) {
        val popupMenu = popupMenu {

            dropdownGravity = Gravity.END
            section {
                item {
                    labelRes = R.string.importtextfile
                    icon = R.drawable.icons8_txt_30px //optional
                    callback = { //optional

                        Dexter.withContext(this@CategorySettingActivity)
                            .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ).withListener(object : MultiplePermissionsListener {
                                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                                    val filesDir: File = Environment.getExternalStorageDirectory()
                                    MaterialDialog(this@CategorySettingActivity).show {
                                        fileChooser(this@CategorySettingActivity, filesDir) { _, file ->
                                            Log.d("TAG", "Selected file: ${file.absolutePath}")
                                            readFromFile(
                                                this@CategorySettingActivity,
                                                file
                                            )


                                        }
                                    }
                                }

                                override fun onPermissionRationaleShouldBeShown(
                                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                                    p1: PermissionToken?
                                ) {
                                    Log.d("callPermission", p0.toString())
                                }


                            }).check()


                    }
                }
                item {
                    labelRes = R.string.add
                    iconDrawable = ContextCompat.getDrawable(
                        this@CategorySettingActivity,
                        R.drawable.icons8_plus_math_32px
                    ) //optional
                    callback = { //optional
                       startActivity(Intent(applicationContext,AddCategoryActivity::class.java))
                    }
                }
            }
        }

        popupMenu.show(this@CategorySettingActivity, view)
    }

    private fun readFromFile(context: Context, filename: File): String? {
        dbCategoryManager?.open()
        var ret = ""
        try {
            val inputStream: InputStream = FileInputStream(filename)
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also({ receiveString = it }) != null) {
                    stringBuilder.append("\n").append(receiveString)
                    Log.d("TAG", "Selected file: ${receiveString}")
                    val data=receiveString?.split(",")
                    dbCategoryManager?.insertCATEGORY(data!![0], data[1])
                }
                dbCategoryManager?.close()

                inputStream.close()
                ret = stringBuilder.toString()

                readDataDB()
                adapter?.notifyDataSetChanged()
                setUpRecyclerView(categoryItem as ArrayList<CategoryItem>)
            }
        } catch (e: FileNotFoundException) {
            Log.e("readFromFile", "File not found: " + e.toString())
        } catch (e: IOException) {
            Log.e("readFromFile", "Can not read file: " + e.toString())
        }
        return ret
    }

}
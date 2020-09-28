package com.wacinfo.easyqueue.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.wacinfo.easyqueue.*
import com.wacinfo.easyqueue.Public.Data


class ShowQueueAdapter(
    context: Context,
    category: List<ShowQueueitem>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mNameList: List<ShowQueueitem>? = category
    private var mContext: Context? = context
    var layoutInflater = LayoutInflater.from(mContext)
    private lateinit var prefsettings: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.showque_item, parent, false)
        prefsettings = mContext!!.getSharedPreferences(mContext?.getString(R.string.PrefsSetting), Context.MODE_PRIVATE)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNameList?.size!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            ItemRows(holder, position)
        }
    }

    private class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var textName: TextView
        var textID: TextView
        var container: RelativeLayout

        init {
            textName = itemView.findViewById(R.id.catename)
            textID = itemView.findViewById(R.id.cateid)
            container = itemView.findViewById(R.id.showque_layout)
        }
    }

    private fun ItemRows(holder: ItemViewHolder, position: Int) {

        val currentItem: ShowQueueitem = this.mNameList!![position]

        holder.textName.text = currentItem.getName()
        holder.textID.text = currentItem.getId()
        holder.container.setOnClickListener {

            val goscan = prefsettings.getBoolean(
                mContext?.getString(R.string.scanLayoutSetting),
                true
            )
            val gophone = prefsettings.getBoolean(
                mContext?.getString(R.string.phoneLayoutSetting),
                true
            )
            if (!goscan && !gophone) {
                Data.category = currentItem.getName().toString()
                Data.categorycode = currentItem.getId().toString()
                mContext?.startActivity(Intent(mContext, SuccessActivity::class.java))
                (mContext as Activity).finish()
            } else if (goscan) {
                if (MainActivity.isDisConnectService) {
//                    connectPayService()
                    Toast.makeText(mContext, R.string.connect_loading, Toast.LENGTH_SHORT)
                        .show()
                }
                Data.category = currentItem.getName().toString()
                Data.categorycode = currentItem.getId().toString()
                mContext?.startActivity(Intent(mContext, ScanIdCardActivity::class.java))
                (mContext as Activity).finish()
            } else if (gophone) {
                Data.category = currentItem.getName().toString()
                Data.categorycode = currentItem.getId().toString()
                mContext?.startActivity(Intent(mContext, InputTelnoActivity::class.java))
                (mContext as Activity).finish()
            }

        }


    }


}
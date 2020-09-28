package com.wacinfo.easyqueue.Adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wacinfo.easyqueue.R
import com.wacinfo.easyqueue.Settings.EditCategoryActivity

class CategoryListAdapter(
    context: Context,
    category: List<CategoryItem>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mNameList: List<CategoryItem>? = category
    private var mContext: Context? = context
    var layoutInflater = LayoutInflater.from(mContext)
    private lateinit var prefsettings: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)

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
            container = itemView.findViewById(R.id.category_container)
        }
    }

    private fun ItemRows(holder: ItemViewHolder, position: Int) {

        val currentItem: CategoryItem = this.mNameList!![position]

        holder.textName.text = currentItem.getName()
        holder.textID.text = currentItem.getId()
        holder.container.setOnClickListener {

            mContext?.startActivity(Intent(mContext,EditCategoryActivity::class.java)
                .putExtra("name",currentItem.getName())
                .putExtra("code",currentItem.getId()))


        }


    }


}
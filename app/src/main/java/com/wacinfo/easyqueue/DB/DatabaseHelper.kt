package com.wacinfo.easyqueue.DB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by anupamchugh on 19/10/15.
 */
class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_TABLE_CATEGORY)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY)
        onCreate(db)
    }

    companion object {
        // Table RESTAURANT Name
        // Database Information
        const val DB_NAME = "QueuePOS.DB"

        // database version
        const val DB_VERSION = 1

        const val TABLE_CATEGORY = "CATEGORY"

        // Table MENU columns
        const val _ID = "_Id"
        const val Category_name = "category_name"
        const val Category_code = "category_code"


        private val CREATE_TABLE_CATEGORY = (("create table "
                + TABLE_CATEGORY) + " ("
                + _ID + " integer primary key autoincrement, "
                + Category_name + " text not null, "
                + Category_code  + " text not null"
                + ");")
    }
}
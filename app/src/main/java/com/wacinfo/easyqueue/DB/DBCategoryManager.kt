package com.wacinfo.easyqueue.DB

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class DBCategoryManager(private val context: Context) {
    private var dbHelper: DatabaseHelper? = null
    private var database: SQLiteDatabase? = null

    @Throws(SQLException::class)
    fun open(): DBCategoryManager {
        dbHelper = DatabaseHelper(context)
        database = dbHelper!!.writableDatabase
//        getDBsize();
        return this
    }

    val dBsize: Unit
        get() {
            val f = context.getDatabasePath(DatabaseHelper.DB_NAME)
            val dbSize = f.length()
            Log.d(TAG, "getDBsize: $dbSize")
        }

    fun close() {
        dbHelper!!.close()
    }

    fun getDBsize(): Long {
        val f = context.getDatabasePath(DatabaseHelper.DB_NAME)
        val dbSize = f.length()
        Log.d(TAG, "getDBsize: $dbSize")
        return dbSize

    }

    fun deleteAllData(): Boolean {
        try {
            database?.execSQL("delete from " + DatabaseHelper.TABLE_CATEGORY);
            return true
        } catch (e: Exception) {
            Log.d(TAG, "deleteAllData: $e")
            return false
        }


    }

    val maxData: Int
        get() {
            val countQuery =
                "SELECT count(" + DatabaseHelper._ID + ") FROM " + DatabaseHelper.TABLE_CATEGORY

//        String selectQuery = "SELECT _ID FROM " + TABLE_NAME + " LIMIT " + limit + " OFFSET " + newoffset;
            database = dbHelper!!.readableDatabase
            //        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(k)});
            val countcursor = database!!.rawQuery(countQuery, null)
            Log.d("cursorr", countcursor.toString())
            Log.d("cursorr", countcursor?.count.toString())
            database!!.beginTransaction()
            var maxdata = 0
            try {
                for (i in 0 until countcursor.count) {
                    countcursor.moveToNext()
                    for (j in 0 until countcursor.columnCount) {
//                    Log.d("getcount", countcursor.getColumnName(j));
                        maxdata = countcursor.getInt(j)
                        Log.d("count", "max =$maxdata")
                    }
                }
                database!!.setTransactionSuccessful() // marks a commit
            } finally {
                database!!.endTransaction()
            }
            countcursor.close()
            return maxdata
        }

    fun getAll(): MutableList<Any?>? {
//        val selectQuery =
//            "SELECT " + DatabaseHelper.MENU_ID + "," + DatabaseHelper.RESTAURANT_ID + "," + DatabaseHelper.MENU_NAME +
//                    "," + DatabaseHelper.MENU_PRICE + "," + DatabaseHelper.MENU_DATE + " " +
//                    "FROM " + DatabaseHelper.TABLE_MENU
        val selectQuery =
            "SELECT *" +
                    "FROM " + DatabaseHelper.TABLE_CATEGORY
        database = dbHelper!!.readableDatabase
        val cursor = database!!.rawQuery(selectQuery, null)
        //        Cursor cursor = database.query(DATABASE_TABLE, rank, null, null, null, null, yourColumn+" DESC");
        val data: MutableList<Any?> = mutableListOf<Any?>()
        Log.d("cursorr", "getDataMENU")
        Log.d("cursorr", cursor.toString())
        database!!.beginTransaction()
        try {
            for (j in 0 until cursor.count) {
                // execute SQL

                // get the data into array, or class variable
                cursor.moveToNext()
                val obj = JSONObject()
                for (i in 0 until cursor.columnCount) {
                    Log.d("DBBBBB", cursor.getColumnName(i))
                    Log.d("DBBBBB", cursor.getString(i))
                    try {
                        obj.put(cursor.getColumnName(i), cursor.getString(i))
                    } catch (e: JSONException) {
                        Log.d("DBBBBB", e.toString())
                        e.printStackTrace()
                    }
                }
                data.add(obj)

            }
            database!!.setTransactionSuccessful() // marks a commit
        } finally {
            database!!.endTransaction()
        }
        cursor.close()
        return data
    }


    fun insertCATEGORY(
        name: String, code: String
    ): Long? {
        val cv = ContentValues()
        cv.put(DatabaseHelper.Category_name, name)
        cv.put(DatabaseHelper.Category_code, code)

        return database!!.insert(DatabaseHelper.TABLE_CATEGORY, null, cv)
    }

    //
//    fun fetchMENU(): Cursor? {
//        val columns = arrayOf(
//            DatabaseHelper.MENU_ID,
//            DatabaseHelper.RESTAURANT_ID,
//            DatabaseHelper.MENU_NAME,
//            DatabaseHelper.MENU_PRICE,
//            DatabaseHelper.MENU_DATE
//        )
//        val cursor =
//            database!!.query(DatabaseHelper.TABLE_MENU, columns, null, null, null, null, null)
//        cursor?.moveToFirst()
//        return cursor
//    }
//
//
    fun updateCategory(
        oldname: String?,
        oldcode: String?,
        newname: String?,
        newcode: String?
    ): Boolean {
        val contentValues = ContentValues()
        val whereClause: String = DatabaseHelper.Category_name + "= '" + oldname + "'" + " AND " +
                DatabaseHelper.Category_code  + "= '" + oldcode + "'"

        //final String whereClause = SQLiteCBLC.COL_ORDNAME + " =? AND " + SQLiteCBLC.COL_QUANTITY + " =?";
        //final String[] whereArgs = {
        //orderName, String.valueOf(oldQuantity)
        //};

        contentValues.put(DatabaseHelper.Category_name, newname)
        contentValues.put(DatabaseHelper.Category_code, newcode)
        return database?.update(
            DatabaseHelper.TABLE_CATEGORY,
            contentValues,
            whereClause,
            null
        )!! > 0
    }

    fun deleteCategory(name: String,code: String): Boolean {
        return try {
            database?.execSQL("DELETE FROM " + DatabaseHelper.TABLE_CATEGORY +
                    " WHERE " + DatabaseHelper.Category_name + "= '" + name + "'" +
                    " AND " +
                    DatabaseHelper.Category_code + "= '" + code + "'")
            true
        } catch (e: Exception) {
            Log.e(TAG, "deleteMENU: $e")
            false
        }

    }

    companion object {
        private const val TAG = "SQL_error"
    }
}
package com.wacinfo.easyqueue.Public

import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter

class MyExceptionHandler(private val myContext: Context, private val myActivityClass: Class<*>) :
    Thread.UncaughtExceptionHandler {
    override fun uncaughtException(
        thread: Thread,
        exception: Throwable
    ) {
        val stackTrace = StringWriter()
        exception.printStackTrace(PrintWriter(stackTrace))
        System.err.println(stackTrace) // You can use LogCat too
        val intent = Intent(myContext, myActivityClass)
        val s = stackTrace.toString()
        //you can use this String to know what caused the exception and in which Activity
        intent.putExtra(
            "uncaughtException",
            "Exception is: $stackTrace"
        )
        intent.putExtra("stacktrace", s)
        Log.e(TAG, "uncaughtException: $s")
        myContext.startActivity(intent)
        //for restarting the Activity
        System.exit(0)
    }

    companion object {
        private const val TAG = "MAINERROR"
    }

}
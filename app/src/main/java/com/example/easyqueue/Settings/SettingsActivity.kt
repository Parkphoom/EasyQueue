package com.example.easyqueue.Settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.easyqueue.MainActivity
import com.example.easyqueue.Public.MyExceptionHandler
import com.example.easyqueue.R
import com.github.angads25.toggle.widget.LabeledSwitch

class SettingsActivity : AppCompatActivity() {
    private lateinit var prefsettings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var switchername: LabeledSwitch? = null
    private var switcherphone: LabeledSwitch? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler(this, MainActivity::class.java))
        getSetting()
        initView()
    }

    private fun initView() {
        switchername = findViewById(R.id.switchername)
        switchername?.isOn =
            prefsettings.getBoolean(getString(R.string.scanLayoutSetting), true)
        switchername?.setOnToggledListener { toggleableView, isOn ->

            Log.d("labeledSwitch", "isSoundDeviceEnable: $isOn")
            if (isOn) {
                editor.putBoolean(getString(R.string.scanLayoutSetting), true)
                editor.apply()

            } else {
                editor.putBoolean(getString(R.string.scanLayoutSetting), false)
                editor.apply()
            }

        }

        switcherphone = findViewById(R.id.switcherphone)
        switcherphone?.isOn =
            prefsettings.getBoolean(getString(R.string.phoneLayoutSetting), true)
        switcherphone?.setOnToggledListener { toggleableView, isOn ->

            Log.d("labeledSwitch", "isSoundDeviceEnable: $isOn")
            if (isOn) {
                editor.putBoolean(getString(R.string.phoneLayoutSetting), true)
                editor.apply()

            } else {
                editor.putBoolean(getString(R.string.phoneLayoutSetting), false)
                editor.apply()
            }

        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun getSetting() {
        prefsettings = getSharedPreferences(getString(R.string.PrefsSetting), Context.MODE_PRIVATE)
        editor = getSharedPreferences(
            getString(R.string.PrefsSetting),
            Context.MODE_PRIVATE
        ).edit()
    }
}
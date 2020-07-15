package com.example.easyqueue

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
import com.sunmi.pay.hardware.aidlv2.tax.TaxOptV2
import com.sunmi.peripheral.printer.SunmiPrinterService

class Public {
    var TAG = "Publicfunction"

    companion object {
        var mBasicOptV2 // 获取基础操作模块
                : BasicOptV2? = null
        var mReadCardOptV2 // 获取读卡模块
                : ReadCardOptV2? = null
        var mPinPadOptV2 // 获取PinPad操作模块
                : PinPadOptV2? = null
        var mSecurityOptV2 // 获取安全操作模块
                : SecurityOptV2? = null
        var mEMVOptV2 // 获取EMV操作模块
                : EMVOptV2? = null
        var mTaxOptV2 // 获取税控操作模块
                : TaxOptV2? = null
        var sunmiPrinterService: SunmiPrinterService? = null
    }

    val arCate = arrayOf(
        "เวชศาสตร์ฟื้นฟู",
        "ห้องอุบัติเหตุและฉุกเฉิน",
        "ห้องผ่าตัด",
        "ห้องคลอด",
        "แผนกผู้ป่วยนอก",
        "อายุรกรรม",
        "กุมารเวชกรรม",
        "ศัลยกรรม",
        "แผนกรักษาผู้ป่วยใน",
        "แผนกกายภาพบำบัดและฟื้นฟู"

    )
    val arCateId = arrayOf(
        "Ae",
        "Be",
        "Ce",
        "De",
        "Ee",
        "Fe",
        "Ge",
        "He",
        "Ie"
    )
    var CommandAPDU = arrayOf(
        "00A40400",
        "80B00004",
        "80B00011",
        "80B00075",
        "80B000D9",
        "80B000E1",
        "80B000F6",
        "80B00167",
        "80B0016F",
        "80B01579"
    )
    var LcAPDU = arrayOf(
        "08",
        "02",
        "02",
        "02",
        "02",
        "02",
        "02",
        "02",
        "02",
        "02"
    )
    var DataInAPDU = arrayOf(
        "A000000054480001",
        "000D",
        "0064",
        "0064",
        "0008",
        "0001",
        "0064",
        "0008",
        "0008",
        "0064"
    )
    var LeAPDU = arrayOf(
        "00",
        "0D",
        "64",
        "64",
        "08",
        "01",
        "64",
        "08",
        "08",
        "64"
    )

    val REQUEST_WRITE_STORAGE_REQUEST_CODE = 101
    val REQUEST_WRITE_STORAGE_REQUEST_CODE_PERSON = 102
    val REQUEST_WRITE_STORAGE_REQUEST_READCARD = 103
}
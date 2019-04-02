package vdream.vd.com.vdream.view.fragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.RecordRegisterData
import vdream.vd.com.vdream.data.UserDiaryData
import vdream.vd.com.vdream.data.UserRecordData
import java.util.*
import java.util.regex.Pattern

abstract class BaseRecordFragment: Fragment {
    var layoutRes = 0
    var registerData = RecordRegisterData()
    var rootView: View? = null
    var recordData: UserRecordData? = null

    constructor(){}

    @SuppressLint("ValidFragment")
    constructor(layoutRes: Int){
        this.layoutRes = layoutRes
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(layoutRes, container, false)
        init()
        return rootView
    }

    abstract fun init()
    abstract fun checkRegisterData(): String
    abstract fun getRecordData(): RecordRegisterData
    abstract fun setOldData(oldData: UserRecordData)
    abstract fun setDiaryData(diary: UserDiaryData)
    abstract fun setDataToUi()
}
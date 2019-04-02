package vdream.vd.com.vdream.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.Window
import vdream.vd.com.vdream.R

class FloatingButtonDialog: Dialog, View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.clRecordBtnGroup -> {}
            R.id.clCreateClassBtnGroup -> {}
        }
    }

    var clRecordBtn: ConstraintLayout? = null
    var clCreateClassBtn: ConstraintLayout? = null

    constructor(context: Context): super(context){
        init()
    }

    private fun init() {
        window.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_floating_button)
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        clRecordBtn = findViewById(R.id.clRecordBtnGroup)
        clCreateClassBtn = findViewById(R.id.clCreateClassBtnGroup)
    }
}
package vdream.vd.com.vdream.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import vdream.vd.com.vdream.R

class CommonProgressDialog: Dialog {
    constructor(context: Context): super(context){
        init()
    }

    private fun init(){
        window.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_common_progress)
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
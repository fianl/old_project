package vdream.vd.com.vdream.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.Window

class NoticeOpenTypeDialog: Dialog {
    constructor(context: Context): super(context){
        init()
    }

    private fun init(){
        window.requestFeature(Window.FEATURE_NO_TITLE)

    }
}
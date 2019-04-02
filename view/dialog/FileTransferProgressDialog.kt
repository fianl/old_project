package vdream.vd.com.vdream.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import vdream.vd.com.vdream.R

class FileTransferProgressDialog: Dialog {
    var tvCurrentRate: TextView? = null
    var pbTransferRate: ProgressBar? = null

    constructor(context: Context): super(context){
        init()
    }

    private fun init(){
        window.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_file_transfer_progress)
        window.setLayout((context.resources.displayMetrics.widthPixels * 0.8f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        tvCurrentRate = findViewById(R.id.tvCurrentRate)
        pbTransferRate = findViewById(R.id.pbFileTransferRate)
    }

    internal fun setCurrentRate(rate: Int){
        tvCurrentRate?.text = rate.toString()
        pbTransferRate?.progress = rate
    }
}
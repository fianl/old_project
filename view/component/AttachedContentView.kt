package vdream.vd.com.vdream.view.component

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.network.S3Downloader

class AttachedContentView: FrameLayout {
    var contentPath = ""
    var contentTitle = ""
    var contentSize = 0

    var ivDownload: ImageView? = null
    var tvContentName: TextView? = null

    constructor(context: Context): super(context) {
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_attched_content, this, false)
        ivDownload = rootView.findViewById(R.id.ivAttchedContentDownload)
        tvContentName = rootView.findViewById(R.id.tvAttachedContentName)

        addView(rootView)

        ivDownload?.setOnClickListener({view ->
            var downloader = S3Downloader(context, contentPath, contentTitle)
            downloader.download()
        })
    }

    internal fun setData(path: String, title:String){
        this.contentPath = path
        this.contentTitle = title

        tvContentName?.setText(contentTitle)
    }
}
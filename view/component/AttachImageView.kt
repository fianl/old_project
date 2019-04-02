package vdream.vd.com.vdream.view.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import vdream.vd.com.vdream.R

class AttachImageView: FrameLayout {
    var ivImage: ImageView? = null

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr) {
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_attache_image, this, false)
        ivImage = rootView.findViewById(R.id.ivAttache)

        addView(rootView)
    }
}
package vdream.vd.com.vdream.view.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.CropCircleTransformation
import vdream.vd.com.vdream.R

class RegistedClassSummaryView: FrameLayout {
    var ivProfile: ImageView? = null
    var tvClassTitle: TextView? = null

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_registed_class_summary, this, false)
        ivProfile = rootView.findViewById(R.id.ivRegisterClassProfile)
        tvClassTitle = rootView.findViewById(R.id.tvRegisterClassName)

        addView(rootView)
    }

    internal fun setThumbnailImage(url: String){
        Glide.with(context).load(url).into(ivProfile!!)
    }

    internal fun setClassTitle(title: String){
        tvClassTitle?.setText(title)
    }
}
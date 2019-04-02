package vdream.vd.com.vdream.view.component

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils

class ClassSummaryLargeImageView: FrameLayout {
    var ivThumbnail: ImageView? = null
    var tvCategory: TextView? = null
    var tvTitle: TextView? = null
    var tvTag: TextView? = null

    constructor(context: Context): super(context){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_class_summary_large_image, this, false)

        ivThumbnail = rootView.findViewById(R.id.ivClassThumbnail)
        tvCategory = rootView.findViewById(R.id.tvClassCategory)
        tvTitle = rootView.findViewById(R.id.tvClassTitle)
        tvTag = rootView.findViewById(R.id.tvClassTag)

        addView(rootView)
    }

    internal fun setThumbnailImage(url: String){
        if(url.equals(context.getString(R.string.default_text))){
            Glide.with(context).load(R.drawable.default_bg)
                    .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation((resources.displayMetrics.density * 4).toInt(), 0)))
                    .into(ivThumbnail!!)
        }else {
            var bitmap = ImageCacheUtils.getBitmap(url)

            if(bitmap == null) {
                Glide.with(context)
                        .asBitmap()
                        .load(CommonUtils.getBigImageLinkPath(context, url))
                        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation((resources.displayMetrics.density * 4).toInt(), 0)))
                        .into(object : ViewTarget<ImageView, Bitmap>(ivThumbnail!!){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                ImageCacheUtils.putBitmap(url, resource)
                                ivThumbnail?.setImageBitmap(resource)
                            }
                        })
            }else{
                ivThumbnail?.setImageBitmap(bitmap)
            }
        }
    }

    internal fun setTitle(category: String, title: String) {
        tvCategory?.text = "[$category]"
        tvTitle?.text = title
    }

    internal fun setTag(tag: String){
        tvTag?.text = tag
    }
}
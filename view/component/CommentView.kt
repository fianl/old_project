package vdream.vd.com.vdream.view.component

import android.content.Context
import android.graphics.Bitmap
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.CropCircleTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.CommentData
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils

class CommentView: FrameLayout {
    var flEmpty: FrameLayout? = null
    var ivWriter: ImageView? = null
    var tvWriterAndContent: TextView? = null
    var tvDate: TextView? = null
    var tvLikeCount: TextView? = null
    var tvReply: TextView? = null
    var ivLike: ImageView? = null

    constructor(context: Context): super(context) {
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_comment, this, false)

        flEmpty = rootView.findViewById(R.id.flEmptySpace)
        ivWriter = rootView.findViewById(R.id.ivCommentWriterImage)
        tvWriterAndContent = rootView.findViewById(R.id.tvCommentWriterAndComment)
        tvDate = rootView.findViewById(R.id.tvCommentDate)
        tvLikeCount = rootView.findViewById(R.id.tvCommentLikeCount)
        tvReply = rootView.findViewById(R.id.tvCommentReply)
        ivLike = rootView.findViewById(R.id.ivCommentLike)

        addView(rootView)
    }

    internal fun setData(data: CommentData) {
        if(data.depth == 1){
            flEmpty?.visibility = View.VISIBLE
        }

        if(data.profile_img.equals(context.getString(R.string.default_text))){
            ivWriter?.setImageResource(R.drawable.default_profile)
        }else{
            var bitmap = ImageCacheUtils.getBitmap(data.profile_img)

            if(bitmap == null) {
                Glide.with(context)
                        .asBitmap()
                        .load(CommonUtils.getThumbnailLinkPath(context, data.profile_img))
                        .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                        .into(object : ViewTarget<ImageView, Bitmap>(ivWriter!!){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                ImageCacheUtils.putBitmap(data.profile_img, resource)
                                ivWriter?.setImageBitmap(resource)
                            }
                        })
            }else{
                ivWriter?.setImageBitmap(bitmap)
            }
        }

        tvWriterAndContent?.text = setSpannableString(data.nickname, data.content)
        tvDate?.text = CommonUtils.calculateTimeFromCreated(data.created_at)
    }

    private fun setSpannableString(name: String, content: String): SpannableStringBuilder{
        var spannable = SpannableStringBuilder("""$name $content""")
        spannable.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannable
    }
}
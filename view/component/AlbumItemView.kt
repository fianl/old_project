package vdream.vd.com.vdream.view.component

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.ImageData
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils

class AlbumItemView: FrameLayout, View.OnClickListener {
    var isEditOn = false
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivAlbumFuntion -> {
                isEditOn = !isEditOn

                if(isEditOn) {
                    ivFunction?.setImageResource(R.drawable.image_edit_on)
                    ivDownload?.visibility = View.VISIBLE
                }else{
                    ivFunction?.setImageResource(R.drawable.image_edit_off)
                    ivDownload?.visibility = View.GONE
                }
            }
        }
    }

    var layoutRes = 0

    var ivAlbum: ImageView? = null
    var ivFunction: ImageView? = null
    var ivDownload: ImageView? = null
    var ivDel: ImageView? = null

    constructor(context: Context, layoutRes: Int): super(context){
        this.layoutRes = layoutRes
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(layoutRes, this, false)
        ivAlbum = rootView.findViewById(R.id.ivAlbumImage)
        ivFunction = rootView.findViewById(R.id.ivAlbumFuntion)
        ivDownload = rootView.findViewById(R.id.ivAlbumDownload)
        ivDel = rootView.findViewById(R.id.ivAlbumDel)

        ivFunction?.setOnClickListener(this)

        addView(rootView)
    }

    internal fun setData(data: ImageData){
        var bitmap = ImageCacheUtils.getBitmap(data.primary)

        if(bitmap == null) {
            Glide.with(context)
                    .asBitmap()
                    .load(CommonUtils.getThumbnailLinkPath(context, data.primary))
                    .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation((resources.displayMetrics.density * 8).toInt(), 0)))
                    .into(object : ViewTarget<ImageView, Bitmap>(ivAlbum!!){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            ImageCacheUtils.putBitmap(data.primary, resource)
                            ivAlbum?.setImageBitmap(resource)
                        }
                    })
        }else{
            ivAlbum?.setImageBitmap(bitmap)
        }
    }
}
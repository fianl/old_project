package vdream.vd.com.vdream.view.component

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils

class ImagePagerAdapter: PagerAdapter {
    var context: Context? = null
    var imageResList = ArrayList<String>()

     constructor(context: Context, imageResList: ArrayList<String>){
        this.context = context
        this.imageResList = imageResList
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var iv = ImageView(context!!)
        iv.scaleType = ImageView.ScaleType.CENTER_CROP

        var bitmap = ImageCacheUtils.getBitmap(imageResList.get(position))

        if(bitmap == null) {
            Glide.with(context!!)
                    .asBitmap()
                    .load(CommonUtils.getImageWidePath(context!!, imageResList.get(position)))
                    .into(object : ViewTarget<ImageView, Bitmap>(iv){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            ImageCacheUtils.putBitmap(imageResList[position], resource)
                            iv.setImageBitmap(resource)
                        }
                    })
        }else{
            iv.setImageBitmap(bitmap)
        }
        container.addView(iv)
        return iv
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return obj == view
    }

    override fun getCount(): Int {
        return imageResList.size
    }
}
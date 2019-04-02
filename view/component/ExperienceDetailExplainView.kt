package vdream.vd.com.vdream.view.component

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.FileData
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils

class ExperienceDetailExplainView: FrameLayout {
    var vpImages: ViewPager? = null
    var llIndicator: LinearLayout? = null
    var tvContent: TextView? = null
    var images = ArrayList<FileData>()

    constructor(context: Context): super(context) {
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_experience_detail_explain, this, false)
        vpImages = rootView.findViewById(R.id.vpExpDetailExplainImages)
        llIndicator = rootView.findViewById(R.id.llExpDetailExplainImageIndicator)
        tvContent = rootView.findViewById(R.id.tvExpDetailExplainContent)

        addView(rootView)
    }

    internal fun setData(content: String, images: ArrayList<FileData>) {
        this.images = images
        indicatorInit()
        vpImages?.offscreenPageLimit = images.size
        vpImages?.adapter = ExpImageAdapter()
        vpImages?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                indicatorUiChange(position)
            }
        })

        tvContent?.text = content

    }

    inner class ExpImageAdapter: PagerAdapter() {
        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return obj == view
        }

        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var iv = ImageView(context)
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            var bitmap = ImageCacheUtils.getBitmap(images[position].file_name)

            if(bitmap == null) {
                Glide.with(context!!)
                        .asBitmap()
                        .load(CommonUtils.getImageWidePath(context!!, images[position].uploaded_path))
                        .into(object : ViewTarget<ImageView, Bitmap>(iv){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                ImageCacheUtils.putBitmap(images[position].file_name, resource)
                                iv.setImageBitmap(resource)
                            }
                        })
            }else {
                iv.setImageBitmap(bitmap)
            }

            container.addView(iv)
            return iv
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

    private fun indicatorInit() {
        for(idx in 0..images.lastIndex){
            var indicatorItem = ImageView(context)
            var params = LinearLayout.LayoutParams((resources.displayMetrics.density*6).toInt(), (resources.displayMetrics.density*6).toInt())

            if(idx != 0)
                params.leftMargin = (resources.displayMetrics.density*4).toInt()

            indicatorItem.layoutParams = params

            llIndicator?.addView(indicatorItem)

            indicatorItem.setBackgroundResource(R.drawable.rectangle_200dp_rounded_lightgray_all)

            if(idx == 0)
                indicatorItem.setBackgroundResource(R.drawable.rectangle_200dp_rounded_gray_all)
        }
    }

    private fun indicatorUiChange(order: Int) {
        for (idx in 0 until llIndicator!!.childCount) {
            if (idx == order)
                llIndicator!!.getChildAt(idx).setBackgroundResource(R.drawable.rectangle_200dp_rounded_gray_all)
            else
                llIndicator!!.getChildAt(idx).setBackgroundResource(R.drawable.rectangle_200dp_rounded_lightgray_all)
        }
    }
}
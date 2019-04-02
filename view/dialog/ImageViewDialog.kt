package vdream.vd.com.vdream.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.utils.CommonUtils

class ImageViewDialog: Dialog, View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flLeftArrow -> {
                vpImageContainer?.currentItem = (vpImageContainer!!.currentItem - 1)
            }
            R.id.flRightArrow -> {
                vpImageContainer?.currentItem = (vpImageContainer!!.currentItem + 1)
            }
        }
    }

    var vpImageContainer: ViewPager? = null
    var flLefArrow: FrameLayout? = null
    var flRightArrow: FrameLayout? = null
    var imageList = ArrayList<String>()

    constructor(context: Context, imageList: ArrayList<String>): super(context) {
        this.imageList = imageList
        init()
    }

    private fun init() {
        window.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_image_view)
        window.setLayout((context.resources.displayMetrics.widthPixels*0.9f).toInt(), (context.resources.displayMetrics.heightPixels*0.9f).toInt())
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        vpImageContainer = findViewById(R.id.vpAlbumImageContainer)
        flLefArrow = findViewById(R.id.flLeftArrow)
        flRightArrow = findViewById(R.id.flRightArrow)

        if(imageList.size > 1)
            flRightArrow?.visibility = View.VISIBLE

        vpImageContainer?.adapter = ImageViewAdapter()
        vpImageContainer?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if(position == imageList.lastIndex)
                    flRightArrow?.visibility = View.GONE
                else
                    flRightArrow?.visibility = View.VISIBLE

                if(position == 0)
                    flLefArrow?.visibility = View.GONE
                else
                    flLefArrow?.visibility = View.VISIBLE
            }
        })

        flLefArrow?.setOnClickListener(this)
        flRightArrow?.setOnClickListener(this)
    }

    inner class ImageViewAdapter: PagerAdapter(){
        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return (view == obj)
        }

        override fun getCount(): Int {
            return imageList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var iv = ImageView(context)
            Glide.with(context).load(CommonUtils.getBigImageLinkPath(context, imageList.get(position)))
                    .into(iv)
            container.addView(iv)
            return iv
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }
}
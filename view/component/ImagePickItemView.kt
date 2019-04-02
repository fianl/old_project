package vdream.vd.com.vdream.view.component

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import vdream.vd.com.vdream.R

class ImagePickItemView: FrameLayout {
    var ivMain: ImageView? = null
    var tvCover: TextView? = null
    var selectedOrder = 0

    constructor(context: Context): super(context){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_iamge_pick_item, this, false)
        ivMain = rootView.findViewById(R.id.ivImagePickItemMain)
        tvCover = rootView.findViewById(R.id.tvImagePickCover)

        addView(rootView)

        ivMain?.scaleType = ImageView.ScaleType.FIT_XY
    }

    internal fun setImage(bitmap: Bitmap){
        ivMain?.setImageBitmap(bitmap)
    }

    internal fun setItemSelected(order: Int){
        selectedOrder = order
        tvCover?.text = selectedOrder.toString()
        tvCover?.visibility = View.VISIBLE
    }

    internal fun setItemDisSelected(){
        selectedOrder = 0
        tvCover?.text = ""
        tvCover?.visibility = View.INVISIBLE
    }
}
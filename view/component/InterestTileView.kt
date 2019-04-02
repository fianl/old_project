package vdream.vd.com.vdream.view.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.utils.CommonUtils

class InterestTileView: FrameLayout {
    var ivImage: ImageView? = null
    var ivCover: ImageView? = null
    var itemSelected = false

    constructor(context: Context): super(context){
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_class_list_category_item, this, false)
        ivImage = rootView.findViewById(R.id.ivCategoryImage)
        ivCover = rootView.findViewById(R.id.ivCategoryCover)

        addView(rootView)
    }

    internal fun setImage(url: String) {
        Glide.with(context).load(CommonUtils.getThumbnailLinkPath(context, url)).into(ivImage!!)
    }

    internal fun itemSelecte() {
        itemSelected = true
        ivCover?.visibility = View.GONE
    }

    internal fun itemUnSelect() {
        itemSelected = false
        ivCover?.visibility = View.VISIBLE
    }
}
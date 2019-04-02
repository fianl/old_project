package vdream.vd.com.vdream.view.component

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.CategoryData

class CategoryGroupItem: FrameLayout {
    var tvContent: TextView? = null
    var ivDivider: ImageView? = null
    var categoryData: CategoryData? = null
    constructor(context: Context): super(context) {
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_expandable_textlist_item, this, false)
        tvContent = rootView.findViewById(R.id.tvExpandableListItemContent)
        ivDivider = rootView.findViewById(R.id.ivExpandableListItmeDivider)

        addView(rootView)
    }

    internal fun setData(data: CategoryData) {
        this.categoryData = data
        tvContent?.text = categoryData!!.title
    }

    internal fun itemSelected() {
        tvContent?.setBackgroundColor(ContextCompat.getColor(context, R.color.mainColor))
        tvContent?.setTextColor(Color.WHITE)
    }

    internal fun itemUnselected() {
        tvContent?.setBackgroundColor(Color.TRANSPARENT)
        tvContent?.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
    }

    internal fun setDividerVisible(visivility: Int){
        ivDivider?.visibility = visivility
    }
}
package vdream.vd.com.vdream.view.component

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.CategoryData
import vdream.vd.com.vdream.interfaces.SecondCategorySelectedCallback

class CategoryGroupView: FrameLayout {
    var tvTitle: TextView? = null
    var ivUnderbarSelected: ImageView? = null
    var ivUnderbarUnselected: ImageView? = null
    var llSubCategories: LinearLayout? = null
    var isExpand = false
    var firstCategory: CategoryData? = null
    var subList = ArrayList<CategoryGroupItem>()
    var selectCallback: SecondCategorySelectedCallback? = null

    constructor(context: Context): super(context) {
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_expandable_text_list, this, false)
        tvTitle = rootView.findViewById(R.id.tvExpandableTitle)
        ivUnderbarSelected = rootView.findViewById(R.id.ivExpandableTitleUnderBarSelected)
        ivUnderbarUnselected = rootView.findViewById(R.id.ivExpandableTitleUnderBarUnselected)
        llSubCategories = rootView.findViewById(R.id.llExpandableSubList)

        addView(rootView)
    }

    internal fun groupSelected() {
        isExpand = true
        setBackgroundResource(R.drawable.rectangle_transparent_maincolor_stroke)
        ivUnderbarSelected?.visibility = View.VISIBLE
        ivUnderbarUnselected?.visibility = View.GONE
        tvTitle?.setTextColor(ContextCompat.getColor(context, R.color.mainColor))
        llSubCategories?.visibility = View.VISIBLE
    }

    internal fun groupUnselected() {
        isExpand = false
        setBackgroundResource(R.drawable.rectangle_transparent_lightgray_stroke)
        ivUnderbarSelected?.visibility = View.GONE
        ivUnderbarUnselected?.visibility = View.VISIBLE
        tvTitle?.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
        llSubCategories?.visibility = View.GONE
    }

    internal fun setCategoryData(firstCategory: CategoryData, secondCategories: ArrayList<CategoryData>, callback: SecondCategorySelectedCallback){
        this.firstCategory = firstCategory
        this.selectCallback = callback

        tvTitle?.text = firstCategory.title

        for(fIdx in 0..secondCategories.lastIndex) {
            var item = CategoryGroupItem(context)
            item.setData(secondCategories[fIdx])
            llSubCategories!!.addView(item)
            subList.add(item)

            item.setOnClickListener({
                for(cateogry in subList){
                    if(item == cateogry) {
                        cateogry.itemSelected()
                        selectCallback?.onSelected(firstCategory.title, cateogry.categoryData!!)
                    } else
                        cateogry.itemUnselected()
                }
            })

            if(fIdx == secondCategories.lastIndex)
                item.setDividerVisible(View.INVISIBLE)
        }
    }
}
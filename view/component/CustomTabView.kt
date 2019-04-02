package vdream.vd.com.vdream.view.component

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import vdream.vd.com.vdream.R

class CustomTabView: FrameLayout {
    var tvTabTitle: TextView? = null
    var ivTabUnderbar: ImageView? = null

    var isTabSelected = false
    var tabTitleColorNormal = 0
    var tabTitleColorSelected = 0
    var tabUnderbarColorNormal = 0
    var tabUnderbarColorSelected = 0

    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_custom_tabview, this, false)
        tvTabTitle = rootView.findViewById(R.id.tvCustomTabViewTitle)
        ivTabUnderbar = rootView.findViewById(R.id.ivCustomTabViewUnderbar)

        addView(rootView)
    }

    internal fun setTitle(title: String){
        tvTabTitle?.text = title
    }

    internal fun setImageInPlaceOfText(res: Int){
        tvTabTitle?.text = ""
        tvTabTitle?.layoutParams = ConstraintLayout.LayoutParams(33*resources.displayMetrics.density.toInt(), 10*resources.displayMetrics.density.toInt())
        tvTabTitle?.setBackgroundResource(res)
    }

    internal fun setTabSelected(isSelected: Boolean){
        isTabSelected = isSelected

        if(isTabSelected){
            tvTabTitle?.setTextColor(ContextCompat.getColor(context, tabTitleColorSelected))
            ivTabUnderbar?.setBackgroundColor(ContextCompat.getColor(context, tabUnderbarColorSelected))
        }else{
            tvTabTitle?.setTextColor(ContextCompat.getColor(context, tabTitleColorNormal))
            ivTabUnderbar?.setBackgroundColor(ContextCompat.getColor(context, tabUnderbarColorNormal))
        }
    }

    internal fun setTabColor(textNormal: Int, textSelected: Int, underbarNormal: Int, underbarSelected: Int){
        this.tabTitleColorNormal = textNormal
        this.tabTitleColorSelected = textSelected
        this.tabUnderbarColorNormal = underbarNormal
        this.tabUnderbarColorSelected = underbarSelected
    }

    internal fun getTitle(): String{
        return tvTabTitle?.text.toString()
    }
}
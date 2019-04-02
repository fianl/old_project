package vdream.vd.com.vdream.view.component

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import vdream.vd.com.vdream.R

class ItemTitledSelector: FrameLayout {
    var mainView: View? = null
    var ivCheck: ImageView? = null
    var tvTitle: TextView? = null

    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr) {
        init()
    }

    private fun init() {
        mainView = LayoutInflater.from(context).inflate(R.layout.view_titled_selector_item, this, false)
        ivCheck = mainView!!.findViewById(R.id.ivSelectorCheck)
        tvTitle = mainView!!.findViewById(R.id.tvSelectorItemTitle)

        addView(mainView)
    }

    internal fun setTitle(title: String) {
        tvTitle?.text = title
    }

    internal fun setItemSelected() {
        mainView?.setBackgroundResource(R.drawable.rectangle_transparent_maincolor_stroke)
        ivCheck?.setImageResource(R.drawable.checkbox_on)
        tvTitle?.setTextColor(ContextCompat.getColor(context, R.color.mainColor))

    }

    internal fun setItemUnselect() {
        mainView?.setBackgroundResource(R.drawable.rectangle_transparent_lightgray_stroke)
        ivCheck?.setImageResource(R.drawable.checkbox)
        tvTitle?.setTextColor(ContextCompat.getColor(context, R.color.lightGray))
    }

    internal fun setTitleGravityCenter() {
        tvTitle?.gravity = Gravity.CENTER
    }
}
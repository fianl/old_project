package vdream.vd.com.vdream.view.component

import android.content.Context
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.CategoryData

class InterestItemView: FrameLayout {
    var clcontainer: ConstraintLayout? = null
    var ivCheck: ImageView? = null
    var tvTitle: TextView? = null
    var isItemChecked = false
    var interest: CategoryData? = null
    var title = ""

    constructor(context: Context): super(context) {
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_interest_item, this, false) as ConstraintLayout
        clcontainer = rootView.findViewById(R.id.clInterestItem)
        ivCheck = rootView.findViewById(R.id.ivInterestItemCheck)
        tvTitle = rootView.findViewById(R.id.tvInterestItemTitle)

        addView(rootView)

        clcontainer?.setOnClickListener(View.OnClickListener { view ->
            isItemChecked = !isItemChecked
            setUiWithChecked()
        })
    }

    internal fun setData(data: CategoryData){
        interest = data
        this.title = interest!!.title
        tvTitle?.setText(title)
    }

    internal fun setItemSelected(isSelected: Boolean){
        isItemChecked = isSelected
        setUiWithChecked()
    }

    private fun setUiWithChecked(){
        if(isItemChecked)
            itemSelected()
        else{
            itemDisSelected()
        }
    }

    private fun itemSelected(){
        clcontainer?.setBackgroundResource(R.drawable.rectangle_200dp_rounded_maincolor_all)
        ivCheck?.setImageResource(R.drawable.checkmark)
        tvTitle?.setTextColor(ContextCompat.getColor(context, R.color.white))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var ani = ViewAnimationUtils.createCircularReveal(ivCheck!!, ivCheck!!.width/2, ivCheck!!.height/2, 0f, resources.displayMetrics.density*8)
            ani.duration = 300
            ani.start()
        }
    }

    private fun itemDisSelected(){
        clcontainer?.setBackgroundResource(R.drawable.rectangle_200dp_rounded_transparent_lightgray_stroke)
        ivCheck?.setImageResource(R.drawable.checkmark_off)
        tvTitle?.setTextColor(ContextCompat.getColor(context, R.color.lightGray))
    }
}
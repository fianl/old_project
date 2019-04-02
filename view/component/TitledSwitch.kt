package vdream.vd.com.vdream.view.component

import android.content.Context
import android.graphics.RectF
import android.graphics.drawable.StateListDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.Switch
import android.widget.TextView
import vdream.vd.com.vdream.R

class TitledSwitch: FrameLayout {
    var tvTitle: TextView? = null
    var switch: Switch? = null
    var isSwitchOn = false

    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_titled_switch, this, false)
        tvTitle = rootView.findViewById(R.id.tvSwitchTitle)
        switch = rootView.findViewById(R.id.swContent)
        switch?.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                isSwitchOn = isChecked
            }
        })

        addView(rootView)
    }

    internal fun setTitle(title: String){
        tvTitle?.setText(title)
    }

    internal fun setTitleColor(color: Int){
        tvTitle?.setTextColor(color)
    }

    internal fun setSwitchTextAndBackground(onText: String, offText: String){
        var switchOnDrawable = DrawableWithText()
        switchOnDrawable.mText = onText
        switchOnDrawable.mRect = RectF(0f, context.resources.getDimension(R.dimen.text_drawable_rect_default_top_margin), context.resources.getDimension(R.dimen.text_drawable_rect_default_width),
                context.resources.getDimension(R.dimen.text_drawable_rect_default_height))
        switchOnDrawable.mTextMarginLeft = context.resources.getDimension(R.dimen.text_drawable_text_default_left_margin)
        switchOnDrawable.mTextMarginTop = context.resources.getDimension(R.dimen.text_drawable_text_default_top_margin)
        switchOnDrawable.setTextPaint(ContextCompat.getColor(context, R.color.white), context.resources.getDimension(R.dimen.text_drawable_text_default_text_size))
        switchOnDrawable.setRectPaint(ContextCompat.getColor(context, R.color.mainColor), ContextCompat.getColor(context, R.color.mainLightColor))

        var switchOffDrawable = DrawableWithText()
        switchOffDrawable.mText = offText
        switchOffDrawable.mRect = RectF(0f, context.resources.getDimension(R.dimen.text_drawable_rect_default_top_margin), context.resources.getDimension(R.dimen.text_drawable_rect_default_width),
                context.resources.getDimension(R.dimen.text_drawable_rect_default_height))
        switchOffDrawable.mTextMarginLeft = context.resources.getDimension(R.dimen.text_drawable_text_default_left_margin)
        switchOffDrawable.mTextMarginTop = context.resources.getDimension(R.dimen.text_drawable_text_default_top_margin)
        switchOffDrawable.setTextPaint(ContextCompat.getColor(context, R.color.white), context.resources.getDimension(R.dimen.text_drawable_text_default_text_size))
        switchOffDrawable.setRectPaint(ContextCompat.getColor(context, R.color.gray))
        switchOffDrawable.mIsTextPosRight = true

        var stateDrawable = StateListDrawable()
        stateDrawable.addState(intArrayOf(android.R.attr.state_checked), switchOnDrawable)
        stateDrawable.addState(intArrayOf(), switchOffDrawable)

        switch?.trackDrawable = stateDrawable
    }

    internal fun setSwitchSize(size: Int) {
        switch?.switchMinWidth = (size * resources.displayMetrics.density).toInt()
    }

    internal fun setSwitchChecked(checked: Boolean){
        switch?.isChecked = checked
    }

    internal fun setCheckChangedListener(listener: CompoundButton.OnCheckedChangeListener) {
        switch?.setOnCheckedChangeListener(listener)
    }

    internal fun setTextSize(size: Float){
        tvTitle?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
    }
}
package vdream.vd.com.vdream.view.component

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class CustomScrollView: ScrollView {
    constructor(context: Context): super(context){}
    constructor(context: Context, attr: AttributeSet): super(context, attr){}

    var mScrollable = true

    internal fun setScrollable(enable: Boolean) {
        mScrollable = enable
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return when(ev?.action){
            MotionEvent.ACTION_DOWN -> mScrollable && super.onTouchEvent(ev)
            else -> super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return mScrollable && super.onInterceptTouchEvent(ev)
    }
}
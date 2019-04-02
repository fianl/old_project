package vdream.vd.com.vdream.view.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.vision.text.Line
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.utils.CommonUtils

class TitledDateTime: FrameLayout {
    var tvTitle: TextView? = null
    var llDate: LinearLayout? = null
    var tvDate: TextView? = null
    var llTime: LinearLayout? = null
    var tvTime: TextView? = null

    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_titled_flat_datepicker, this, false)
        tvTitle = rootView.findViewById(R.id.tvFlatTitle)
        tvDate = rootView.findViewById(R.id.tvFlatDate)
        tvTime = rootView.findViewById(R.id.tvFlatTime)

        addView(rootView)

        tvDate?.setOnClickListener({
            CommonUtils.createDatePicker(context, tvDate!!)
        })

        tvTime?.setOnClickListener({
            CommonUtils.createTimePicker(context, tvTime!!)
        })
    }

    internal fun setTitle(title: String){
        tvTitle?.text = title
    }

    internal fun getDate(): String{
        return tvDate!!.text.toString()
    }

    internal fun getTime(): String{
        return tvTime!!.text.toString()
    }

    internal fun hideTime() {
        llTime?.visibility = View.GONE
    }

    internal fun hideDate() {
        llDate?.visibility = View.GONE
    }

    internal fun setDate(date: String){
        tvDate?.text = date
    }

    internal fun setTime(time: String){
        tvTime?.text = time
    }
}
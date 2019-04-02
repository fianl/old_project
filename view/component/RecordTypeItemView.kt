package vdream.vd.com.vdream.view.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import vdream.vd.com.vdream.R

class RecordTypeItemView: FrameLayout {
    var ivType: ImageView? = null
    var tvType: TextView? = null
    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_record_type_item, this, false)
        ivType = rootView.findViewById(R.id.ivItemType)
        tvType = rootView.findViewById(R.id.tvItemType)

        addView(rootView)
    }

    internal fun setTypeData(type: String, res: Int){
        tvType?.text = type
        ivType?.setImageResource(res)
    }
}
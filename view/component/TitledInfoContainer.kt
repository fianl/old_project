package vdream.vd.com.vdream.view.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import vdream.vd.com.vdream.R

class TitledInfoContainer: FrameLayout {
    var tvTitle: TextView? = null
    var flOption: FrameLayout? = null
    var tvSubtitles: Array<TextView>? = null
    var tvValues: Array<TextView>? = null

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_titled_info_container, this, false)
        tvTitle = rootView.findViewById(R.id.tvInfoContainerTitle)
        flOption = rootView.findViewById(R.id.flInfoContainerOption)
        tvSubtitles = Array<TextView>(3, {i -> TextView(context)})
        tvValues = Array<TextView>(3, {i -> TextView(context)})

        tvSubtitles!![0] = rootView.findViewById(R.id.tvInfoContainerSubTitle1)
        tvValues!![0] = rootView.findViewById(R.id.tvInfoContainerValue1)
        tvSubtitles!![1] = rootView.findViewById(R.id.tvInfoContainerSubTitle2)
        tvValues!![1] = rootView.findViewById(R.id.tvInfoContainerValue2)
        tvSubtitles!![2] = rootView.findViewById(R.id.tvInfoContainerSubTitle3)
        tvValues!![2] = rootView.findViewById(R.id.tvInfoContainerValue3)

        addView(rootView)
    }

    internal fun setTitle(title: String){
        tvTitle?.text = title
    }

    internal fun setData(subTitles: ArrayList<String>, values: ArrayList<String>){
        for(idx in 0..subTitles.lastIndex){
            tvSubtitles!![idx].text = subTitles[idx]
            tvValues!![idx].text = values[idx]
        }
    }

    internal fun setContainerShort(){
        tvSubtitles!![2].visibility = View.GONE
        tvValues!![2].visibility = View.GONE
    }

    internal fun showOptionButton(){
        flOption?.visibility = View.VISIBLE
    }
}
package vdream.vd.com.vdream.view.component

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import vdream.vd.com.vdream.R

class ComplexSelectView: FrameLayout {
    var tvTitle: TextView? = null
    var clSelector: ConstraintLayout? = null
    var tvSelectorTitle: TextView? = null
    var etContent: EditText? = null
    var switchOption: Switch? = null
    var ivSelectorMark: ImageView? = null
    var selectedValue = ""
    var isOptionSwitchOn = false

    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_complex_select, this, false)
        tvTitle = rootView.findViewById(R.id.tvComplexTitle)
        clSelector = rootView.findViewById(R.id.clSelectorContainer)
        tvSelectorTitle = rootView.findViewById(R.id.tvComplexSelectorTitle)
        etContent = rootView.findViewById(R.id.etSecondContent)
        switchOption = rootView.findViewById(R.id.swComplexOption)
        ivSelectorMark = rootView.findViewById(R.id.ivComplexSelectorMark)

        switchOption?.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                isOptionSwitchOn = isChecked
            }
        })

        addView(rootView)
    }

    internal fun setTitle(title: String){
        tvTitle?.setText(title)
    }

    internal fun setSelectorTitle(title: String){
        tvSelectorTitle?.setText(title)
    }

    internal fun setSelectorMark(mark: Int){
        ivSelectorMark?.setImageResource(mark)
    }

    internal fun setSelectorClickListener(listener: OnClickListener){
        clSelector?.setOnClickListener(listener)
    }

    internal fun showSelectorOptionSwitch(){
        switchOption?.visibility = View.VISIBLE
    }

    internal fun setValue(selectedValue: String){
        this.selectedValue = selectedValue
        tvSelectorTitle?.text = selectedValue
    }

    internal fun showSecondeInputView(){
        etContent?.visibility = View.VISIBLE
        etContent?.requestFocus()
    }

    internal fun getSecondContentValue(): String {
        return etContent?.text.toString()
    }

    internal fun setTitleColor(color: Int){
        tvTitle?.setTextColor(color)
    }
}
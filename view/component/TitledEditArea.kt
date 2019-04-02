package vdream.vd.com.vdream.view.component

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import vdream.vd.com.vdream.R

class TitledEditArea: FrameLayout {
    var tvTitle: TextView? = null
    var etContent: EditText? = null
    var expandSize = 0

    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_titled_edittext_area, this, false)
        tvTitle = rootView.findViewById(R.id.tvAreaTitle)
        etContent = rootView.findViewById(R.id.etAreaContent)

        addView(rootView)

        etContent?.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                etContent?.setTextColor(ContextCompat.getColor(context, R.color.mainColor))
                etContent?.minLines = expandSize
                etContent?.maxLines = expandSize
            }else{
                etContent?.setTextColor(ContextCompat.getColor(context, R.color.lightGray))
                if(etContent?.lineCount == 1){
                    etContent?.minLines = 1
                    etContent?.maxLines = 1
                }
            }
        }
    }

    internal fun setTitle(title: String) {
        tvTitle?.text = title
    }

    internal fun setContentHint(hint: String) {
        etContent?.hint = hint
    }

    internal fun setValue(value: String) {
        etContent?.text = SpannableStringBuilder(value)
    }

    internal fun getValue(): String {
        return etContent!!.text.toString()
    }

    internal fun setImeOptionNext(){
        etContent?.imeOptions = EditorInfo.IME_ACTION_NEXT
    }

    internal fun setMaxContentLine(num: Int){
        expandSize = num
    }
}
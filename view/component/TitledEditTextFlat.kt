package vdream.vd.com.vdream.view.component

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import vdream.vd.com.vdream.R

class TitledEditTextFlat: FrameLayout {
    var tvTitle: TextView? = null
    var etContent: EditText? = null
    var ivUnderline: ImageView? = null

    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_titled_edittext_flat, this, false)
        tvTitle = rootView.findViewById(R.id.tvFlatTitle)
        etContent = rootView.findViewById(R.id.etFlatContent)
        ivUnderline = rootView.findViewById(R.id.ivFlatUnderline)

        addView(rootView)

        etContent?.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                etContent?.setTextColor(ContextCompat.getColor(context, R.color.mainColor))
                ivUnderline?.setBackgroundColor(ContextCompat.getColor(context, R.color.mainColor))
            }else{
                etContent?.setTextColor(ContextCompat.getColor(context, R.color.lightGray))
                ivUnderline?.setBackgroundColor(ContextCompat.getColor(context, R.color.lightlightGray))
            }
        }
    }

    internal fun setTitle(title: String) {
        tvTitle?.text = title
    }

    internal fun setContentHint(hint: String) {
        etContent?.hint = hint
    }

    internal fun setNumericInput(){
        etContent?.inputType = InputType.TYPE_CLASS_NUMBER
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
        etContent?.minLines = num
        etContent?.maxLines = num
    }
}
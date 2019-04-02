package vdream.vd.com.vdream.view.component

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.FrameMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import vdream.vd.com.vdream.R

class TitledEditText: FrameLayout {
    var tvTitle: TextView? = null
    var tvExplain: TextView? = null
    var etContent: EditText? = null
    var tvOption: TextView? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_titled_edittext, this, false)
        tvTitle = rootView.findViewById(R.id.tvTitle)
        tvExplain = rootView.findViewById(R.id.tvExplain)
        etContent = rootView.findViewById(R.id.etContent)
        tvOption = rootView.findViewById(R.id.tvOptionalBtn)

        addView(rootView)
    }

    internal fun setTitle(title: String){
        tvTitle?.setText(title)
    }

    internal fun setExplain(explain: String){
        tvExplain?.setText(explain)
    }

    internal fun setTitleColor(color: Int){
        tvTitle?.setTextColor(color)
    }

    internal fun setExplainColor(color: Int){
        tvExplain?.setTextColor(color)
    }

    internal fun setInputType(type: Int){
        etContent?.inputType = type

        if(type == InputType.TYPE_TEXT_VARIATION_PASSWORD){
            etContent?.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    internal fun setEditTextHint(hint: String){
        etContent?.hint = hint
    }

    internal fun setText(text: String) {
        etContent?.text = SpannableStringBuilder(text)
    }

    internal fun getText(): String{
        return etContent?.text.toString()
    }

    internal fun showExplain(){
        tvExplain?.visibility = View.VISIBLE
    }

    internal fun hideExplain(){
        tvExplain?.visibility = View.INVISIBLE
    }

    internal fun setOptionText(option: String){
        tvOption?.text = option
    }

    internal fun showOptionalButton(){
        tvOption?.visibility = View.VISIBLE
    }

    internal fun setOptionButtonClickListener(listener: OnClickListener){
        tvOption?.setOnClickListener(listener)
    }

    internal fun requestTetFocus(){
        etContent?.requestFocus()
    }

    internal fun setImeOptionNext(){
        etContent?.imeOptions = EditorInfo.IME_ACTION_NEXT
    }

    internal fun setTextChangeListener(watcher: TextWatcher){
        etContent?.addTextChangedListener(watcher)
    }
}
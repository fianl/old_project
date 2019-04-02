package vdream.vd.com.vdream.view.component

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.interfaces.AnnounceImageChangeCallback

class RecordOptionView: FrameLayout {
    var ivOptionType: ImageView? = null
    var tvOptionType: TextView? = null
    var llContainer: LinearLayout? = null
    var contentList = ArrayList<View>()
    var etContent: EditText? = null
    var announceImageCallback: AnnounceImageChangeCallback? = null

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_record_option, this, false)
        ivOptionType = rootView.findViewById(R.id.ivRecordOptType)
        tvOptionType = rootView.findViewById(R.id.tvRecordOptType)
        llContainer = rootView.findViewById(R.id.llRecordOptContents)
        etContent = rootView.findViewById(R.id.etContent)

        addView(rootView)
    }

    internal fun setType(type: String){
        tvOptionType?.text = type
        ivOptionType?.setImageResource(getOptionImageResource(type))
    }

    internal fun setCallback(callback: AnnounceImageChangeCallback){
        announceImageCallback = callback
    }

    private fun getOptionImageResource(type: String): Int{
        var res = 0

        when(type){
            context.getString(R.string.record_option_tag) -> res = R.drawable.attach_tag
            context.getString(R.string.record_option_picture) -> res = R.drawable.attach_camera
            context.getString(R.string.record_option_location) -> res = R.drawable.attach_location
            context.getString(R.string.record_option_video) -> res = R.drawable.attach_video
        }

        return res
    }

    internal fun addContent(view: View){
        llContainer?.addView(view)
        contentList.add(view)

        if(view is ImageView){
            view.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(v: View?): Boolean {
                    announceImageCallback?.imageDeleted(view.getTag(R.id.record_added_iv) as Int)
                    llContainer?.removeView(view)
                    return true
                }
            })
        }
    }

    internal fun getContextText(): String {
        if(etContent != null){
            return etContent!!.text.toString()
        }else{
            return ""
        }
    }

    internal fun setTextInputEnable(){
        etContent?.visibility = View.VISIBLE
    }

    internal fun setValue(value: String){
        etContent?.text = SpannableStringBuilder(value)
    }

    internal fun setFocus() {
        etContent?.requestFocus()
    }
}
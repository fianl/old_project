package vdream.vd.com.vdream.view.component

import android.content.Context
import android.provider.Telephony
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.interfaces.AddressRequestCallback

class TitledLocationInputForm: FrameLayout {
    var tvTitle: TextView? = null
    var tvBigAddress: TextView? = null
    var ivIcon: ImageView? = null
    var etDetailAddress: EditText? = null
    var bigAddress = ""
    var addressReqCallback: AddressRequestCallback? = null

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr) {
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_titled_location_input, this, false)
        tvTitle = rootView.findViewById(R.id.tvLocationTitle)
        tvBigAddress = rootView.findViewById(R.id.tvLocationBigAddress)
        ivIcon = rootView.findViewById(R.id.ivLocationIcon)
        etDetailAddress = rootView.findViewById(R.id.etLocationDetailAddress)

        addView(rootView)

        ivIcon?.setOnClickListener({
            addressReqCallback?.requestAddress()
        })
    }

    internal fun setTitle(title: String) {
        tvTitle?.text = title
    }

    internal fun setBigAddress(address: String) {
        bigAddress = address
        tvBigAddress?.text = address
    }

    internal fun setDetailAddress(address: String) {
        etDetailAddress?.text = SpannableStringBuilder(address)
    }

    internal fun getDetailAddress(): String{
        return etDetailAddress!!.text.toString()
    }

    internal fun setAddressRequestCallback(callback: AddressRequestCallback){
        addressReqCallback = callback
    }
}
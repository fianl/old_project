package vdream.vd.com.vdream.view.component

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.CropCircleTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.ReviewData
import vdream.vd.com.vdream.interfaces.ReviewChangeCallback
import vdream.vd.com.vdream.utils.CommonUtils

class ReviewItmeView: FrameLayout {
    var ivProfile: ImageView? = null
    var flMenu: FrameLayout? = null
    var tvNickname: TextView? = null
    var tvDate: TextView? = null
    var tvContent: TextView? = null
    var reviewData: ReviewData? = null
    var reviewChangeCallback: ReviewChangeCallback? = null

    constructor(context: Context, reviewChangeCallback: ReviewChangeCallback): super(context) {
        this.reviewChangeCallback = reviewChangeCallback
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_review_item, this, false)
        ivProfile = rootView.findViewById(R.id.ivReviewProfile)
        flMenu = rootView.findViewById(R.id.flReviewMenu)
        tvNickname = rootView.findViewById(R.id.tvReviewNickname)
        tvDate = rootView.findViewById(R.id.tvReviewDate)
        tvContent = rootView.findViewById(R.id.tvReviewContent)

        addView(rootView)

        flMenu?.setOnClickListener({
            createOptionDialog()
        })
    }

    internal fun setData(data: ReviewData) {
        this.reviewData = data

        if(data.profile_img == context.getString(R.string.default_text))
            ivProfile?.setImageResource(R.drawable.default_profile)
        else
            Glide.with(context).load(CommonUtils.getThumbnailLinkPath(context, data.profile_img))
                    .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                    .into(ivProfile!!)

        tvNickname?.text = data.nickname
        tvDate?.text = data.created_at.split(" ")[0].replace("-", ".")
        tvContent?.text = data.content

        if(data.is_mine == "Y")
            flMenu?.visibility = View.VISIBLE
    }

    private fun createOptionDialog(){
        var edit = Dialog(context)
        edit.window.requestFeature(Window.FEATURE_NO_TITLE)
        edit.setContentView(R.layout.dialog_annouce_edit_option)
        edit.window.setLayout((resources.displayMetrics.widthPixels * 0.8f).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        edit.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var tvModify = edit.findViewById<TextView>(R.id.tvAnnounceEditOptModify)
        var tvDelete = edit.findViewById<TextView>(R.id.tvAnnounceEditOptDelete)

        tvModify.setOnClickListener({
            reviewChangeCallback?.onRequestUpdate(reviewData!!)
            edit.dismiss()
        })

        tvDelete.setOnClickListener({
            reviewChangeCallback?.onRequestDelete(reviewData!!.idx)
            edit.dismiss()
        })

        edit.show()
    }
}
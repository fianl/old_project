package vdream.vd.com.vdream.view.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.View
import android.view.ViewParent
import android.view.Window
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.BlurTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.ExperienceRequestData
import vdream.vd.com.vdream.data.FeedDetailData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils
import vdream.vd.com.vdream.view.component.ExperienceDetailExplainView
import vdream.vd.com.vdream.view.component.ExperienceDetailPlaceTimeView
import vdream.vd.com.vdream.view.component.ExperienceDetailReViewView
import java.text.NumberFormat
import java.util.*

class ExperienceDetailActivity: BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flExpDetailBack -> {
                sendAppEvent("체험상세_헤더_백버튼")
                finish()
            }
            R.id.tvExpDetailExplain -> {
                sendAppEvent("체험상세_보드_체험소개")
                setTopMenuUi(0)
            }
            R.id.tvExpDetailPlaceTime -> {
                sendAppEvent("체험상세_보드_장소시간")
                setTopMenuUi(1)
            }
            R.id.tvExpDetailReview -> {
                sendAppEvent("체험상세_보드_리뷰")
                setTopMenuUi(2)
            }
            R.id.ivExpDetailToTop -> {
                sendAppEvent("체험상세_보드_콘텐츠_맨위로")
                svContainer?.smoothScrollTo(0, 0)
            }
            R.id.tvExpDetailApply -> {
                sendAppEvent("체험상세_구매하기")
                if(feedData!!.is_secure == "Y"){
                    createSecureCodeDialog()
                }else {/*
                    if(feedData!!.price > 0) {
                        moveToPayment()
                    }else{*/
                        requestExperience()
                   // }
                }
            }
        }
    }

    var feedData: FeedDetailData? = null

    var clProfileBg: ConstraintLayout? = null
    var flBack: FrameLayout? = null
    var tvTitle: TextView? = null
    var flLike: FrameLayout? = null
    var ivLike: ImageView? = null
    var tvCost: TextView? = null
    var tvShortExplain: TextView? = null
    var tvTag: TextView? = null
    var tvLikeCnt: TextView? = null
    var tvSubscribeCnt: TextView? = null
    var tvTopMenu: Array<TextView>? = null
    var svContainer: ScrollView? = null
    var llDetailViewContainer: LinearLayout? = null
    var tvApply: TextView? = null
    var ivToTop: ImageView? = null

    var explainView: ExperienceDetailExplainView? = null
    var placeTimeView: ExperienceDetailPlaceTimeView? = null
    var reView: ExperienceDetailReViewView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_experience_detail)

        feedData = intent.getSerializableExtra(getString(R.string.intent_key_name_feeddata)) as FeedDetailData

        clProfileBg = findViewById(R.id.clProfileContainer)
        flBack = findViewById(R.id.flExpDetailBack)
        tvTitle = findViewById(R.id.tvExpDetailTitle)
        flLike = findViewById(R.id.flExpDetailLike)
        ivLike = findViewById(R.id.ivExpDetailLike)
        tvCost = findViewById(R.id.tvExpDetailCost)
        tvShortExplain = findViewById(R.id.tvExpDetailSummary)
        tvTag = findViewById(R.id.tvExpDetailTag)
        tvLikeCnt = findViewById(R.id.tvExpDetailLikeCnt)
        tvSubscribeCnt = findViewById(R.id.tvExpDetailSubscriptCnt)
        svContainer = findViewById(R.id.svExpDetailContents)
        llDetailViewContainer = findViewById(R.id.llExpDetailContentContainer)

        tvApply = findViewById(R.id.tvExpDetailApply)
        ivToTop = findViewById(R.id.ivExpDetailToTop)

        tvTopMenu = arrayOf(findViewById(R.id.tvExpDetailExplain), findViewById(R.id.tvExpDetailPlaceTime), findViewById(R.id.tvExpDetailReview))
        for(menu in tvTopMenu!!)
            menu.setOnClickListener(this)

        flBack?.setOnClickListener(this)
        ivToTop?.setOnClickListener(this)
        tvApply?.setOnClickListener(this)

        setDataToUi()
    }

    private fun setDataToUi(){
        tvTitle?.text = feedData!!.title

        if(feedData!!.is_like == "Y")
            ivLike?.setImageResource(R.drawable.icon_like)

        tvShortExplain?.text = feedData!!.summary
        tvShortExplain?.isSelected = true
        if(feedData!!.tags != null)
            tvTag?.text = CommonUtils.convertTagsToString(feedData!!.tags!!)

        var numberForm = NumberFormat.getCurrencyInstance(Locale.KOREA)
        var costText = numberForm.format(feedData!!.price) + "원"
        var builder = SpannableStringBuilder(costText)
        builder.setSpan(AbsoluteSizeSpan(16, true), costText.length-1, costText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvCost?.text = builder

        if(feedData!!.files != null && feedData!!.files!!.isNotEmpty()){
            var image = feedData!!.files!![0].uploaded_path

            if(image == getString(R.string.default_text)){
                clProfileBg?.setBackgroundResource(R.drawable.default_bg)
            }else{
                var bitmap = ImageCacheUtils.getBitmap(image)

                if(bitmap == null) {
                    Glide.with(this)
                            .asBitmap()
                            .load(CommonUtils.getBigImageLinkPath(this, image))
                            .apply(RequestOptions.bitmapTransform(BlurTransformation()))
                            .into(object : ViewTarget<ConstraintLayout, Bitmap>(clProfileBg!!) {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    ImageCacheUtils.putBitmap(image, resource)
                                    clProfileBg?.background = BitmapDrawable(resources, resource)
                                }
                            })
                }else{
                    clProfileBg?.background = BitmapDrawable(resources, bitmap)
                }

            }
        }else{
            clProfileBg?.setBackgroundResource(R.drawable.default_bg)
        }

        tvLikeCnt?.text = feedData!!.like_count.toString()
        tvSubscribeCnt?.text = 1!!.toString()

        tvTopMenu!![0].performClick()
    }

    private fun setTopMenuUi(order: Int){
        for(idx in 0..tvTopMenu!!.lastIndex){
            if(idx == order) {
                tvTopMenu!![idx].setBackgroundResource(R.drawable.rectangle_200dp_rounded_maincolor_all)
                tvTopMenu!![idx].setTextColor(Color.WHITE)
            }else {
                tvTopMenu!![idx].setBackgroundColor(Color.TRANSPARENT)
                tvTopMenu!![idx].setTextColor(ContextCompat.getColor(this, R.color.text_gray))
            }
        }

        when(order) {
            0 -> {
                if(explainView == null) {
                    explainView = ExperienceDetailExplainView(this)
                    explainView!!.setData(feedData!!.content, feedData!!.files!!.toCollection(ArrayList()))
                }

                if(llDetailViewContainer?.childCount!! > 0)
                    llDetailViewContainer?.removeAllViews()

                llDetailViewContainer?.addView(explainView)
            }

            1 -> {
                if(placeTimeView == null){
                    placeTimeView = ExperienceDetailPlaceTimeView(this)
                    placeTimeView!!.setData(feedData!!.title, feedData!!.lat.toDouble(), feedData!!.lng.toDouble(), feedData!!.address_1, feedData!!.address_2, feedData!!.opened_at)
                }

                if(llDetailViewContainer?.childCount!! > 0)
                    llDetailViewContainer?.removeAllViews()

                llDetailViewContainer?.addView(placeTimeView)
            }

            2 -> {
                if(reView == null){
                    reView = ExperienceDetailReViewView(this)
                    reView!!.setData(feedData!!.idx)
                }

                if(llDetailViewContainer?.childCount!! > 0)
                    llDetailViewContainer?.removeAllViews()

                llDetailViewContainer?.addView(reView)
            }
        }
    }

    private fun requestExperience(){
        var requstData = ExperienceRequestData()
        if(feedData!!.price > 0){
            requstData.status = "WAIT"
        }
        var apiService = ApiManager.getInstance().apiService
        apiService.requestExperience(feedData!!.idx, requstData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        requestSuccessDialog()
                    }else{
                        Log.e("REQ_EXP", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_register_experience), Toast.LENGTH_SHORT).show()
                    }
                    tvApply?.isClickable = true
                }, { err ->
                    Log.e("REQ_EXP", err.toString())
                    Toast.makeText(this, getString(R.string.fail_to_register_experience), Toast.LENGTH_SHORT).show()
                })
    }

    private fun requestSuccessDialog(){
        var success = AlertDialog.Builder(this)
        success.setTitle(getString(R.string.update_alert_title))
        success.setMessage(getString(R.string.success_to_register_experience))
        success.setNeutralButton(getString(R.string.confirm), null)

        success.create().show()
    }

    private fun createSecureCodeDialog(){
        sendAppEvent("체험상세_구매하기_인증번호확인")
        var secure = Dialog(this)
        secure.window.requestFeature(Window.FEATURE_NO_TITLE)
        secure.setContentView(R.layout.dialog_experience_secure_code)
        secure.window.setLayout((resources.displayMetrics.widthPixels * 0.9f).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        secure.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var etSecureCode = secure.findViewById<EditText>(R.id.etSecureCode)
        var tvError = secure.findViewById<TextView>(R.id.tvSecureCodeError)
        var tvConfirm = secure.findViewById<TextView>(R.id.tvSecureCodeConfirm)

        tvConfirm.setOnClickListener {
            if(etSecureCode.text.toString() == ""){
                Toast.makeText(this, getString(R.string.input_secure_code_before_confirm), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var apiService = ApiManager.getInstance().apiService
            apiService.authExperienceSecureCode(feedData!!.idx, etSecureCode.text.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe({ result ->
                        if(result.status == "Y"){
                            requestExperience()
                            secure.dismiss()
                        }else{
                            Log.e("AUTH_EXP", result.error)
                            tvError.visibility = View.VISIBLE
                        }
                    }, { err ->
                        Log.e("AUTH_EXP", err.toString())
                        tvError.visibility = View.VISIBLE
                    })
        }

        secure.show()
    }

    private fun moveToPayment(){
        var intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(getString(R.string.intent_key_name_feeddata), feedData)
        startActivity(intent)
    }
}
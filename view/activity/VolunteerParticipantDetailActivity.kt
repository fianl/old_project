package vdream.vd.com.vdream.view.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.VolunteerPartDetailData
import vdream.vd.com.vdream.network.VolunteerApiManager
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.KakaoLinkUtils

class VolunteerParticipantDetailActivity: BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flVolunteerPartBack -> finish()
            R.id.flVolunteerPartShare -> {
                KakaoLinkUtils.sendNormalText(this, detailData!!.progrmSj!!, detailData!!.progrmCn!!)
            }
        }
    }

    var flBack: FrameLayout? = null
    var flShare: FrameLayout? = null
    var tvTitle: TextView? = null
    var tvPeriod: TextView? = null
    var tvTime: TextView? = null
    var tvRecruitPeriod: TextView? = null
    var tvRecruitNumber: TextView? = null
    var tvDay: TextView? = null
    var tvApplyNumber: TextView? = null
    var tvField: TextView? = null
    var tvType: TextView? = null
    var tvRecruitAgency: TextView? = null
    var tvRegisterAgency: TextView? = null
    var tvPlace: TextView? = null
    var tvTarget: TextView? = null
    var tvContent: TextView? = null
    var detailData: VolunteerPartDetailData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var prgrmNo = intent.getIntExtra(getString(R.string.intent_key_name_volunteer_number), 0)

        setContentView(R.layout.activity_volunteer_participant_detail)

        flBack = findViewById(R.id.flVolunteerPartBack)
        flShare = findViewById(R.id.flVolunteerPartShare)
        tvTitle = findViewById(R.id.tvVolunteerPartDetailTitle)
        tvPeriod = findViewById(R.id.tvVolunteerPartDetailPeriod)
        tvTime = findViewById(R.id.tvVolunteerPartDetailTime)
        tvRecruitPeriod = findViewById(R.id.tvVolunteerPartDetailRecruitPeriod)
        tvRecruitNumber = findViewById(R.id.tvVolunteerPartDetailRecruitNumber)
        tvDay = findViewById(R.id.tvVolunteerPartDetailDay)
        tvApplyNumber = findViewById(R.id.tvVolunteerPartDetailApplyNumber)
        tvField = findViewById(R.id.tvVolunteerPartDetailField)
        tvType = findViewById(R.id.tvVolunteerPartDetailType)
        tvRecruitAgency = findViewById(R.id.tvVolunteerPartDetailRecruitAgency)
        tvRegisterAgency = findViewById(R.id.tvVolunteerPartDetailRegisterAgency)
        tvPlace = findViewById(R.id.tvVolunteerPartDetailPlace)
        tvTarget = findViewById(R.id.tvVolunteerPartDetailTarget)
        tvContent = findViewById(R.id.tvVolunteerPartDetailContent)

        flBack?.setOnClickListener(this)
        flShare?.setOnClickListener(this)

        getDetailParticipantInfo(prgrmNo)
    }

    private fun getDetailParticipantInfo(number: Int){
        var apiService = VolunteerApiManager.getInstance().apiService
        apiService.getVolunteerDetailInfo(number, "json")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    detailData = result.response!!.body!!.items!!.item
                    setDataToUi(detailData!!)
                }, { err ->
                    Log.e("PARTICIPANT_DETAIL", err.message)
                })
    }

    private fun setDataToUi(detail: VolunteerPartDetailData){
        tvTitle?.text = convertTitleAndState(detail.progrmSj!!, detail.progrmSttusSe!!)
        tvPeriod?.text = "${CommonUtils.volunteerDateRetouch(detail.progrmBgnde!!)} ~ ${CommonUtils.volunteerDateRetouch(detail.progrmEndde!!)}"
        tvTime?.text = "${convertTime(detail.actBeginTm!!)} ~ ${convertTime(detail.actEndTm!!)}"
        tvRecruitPeriod?.text = "${CommonUtils.volunteerDateRetouch(detail.noticeBgnde!!)} ~ ${CommonUtils.volunteerDateRetouch(detail.noticeEndde!!)}"
        tvRecruitNumber?.text = "${detail.rcritNmpr} 명"
        tvDay?.text = convertVolunteerDay(detail.actWkdy!!)
        tvApplyNumber?.text = "${detail.appTotal} 명"
        tvField?.text = detail.srvcClCode
        tvType?.text = convertVolunteerMemberType(detail)
        tvRecruitAgency?.text = detail.mnnstNm
        tvRegisterAgency?.text = detail.nanmmbyNm
        tvPlace?.text = detail.actPlace
        tvContent?.text = detail.progrmCn
    }

    private fun convertTitleAndState(title: String, state: Int): SpannableStringBuilder {
        var stateString = convertState(state)
        var total = "$title $stateString"
        var builder = SpannableStringBuilder(total)
        builder.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)), total.indexOf(stateString), total.indexOf(stateString) + stateString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return builder
    }

    private fun convertTime(time: Int): String {
        var convert = ""

        if(time < 10) {
            convert = "0$time:00"
        }else{
            convert = "$time:00"
        }

        return convert
    }

    private fun convertState(num: Int): String{
        var state = ""
        when(num){
            1 -> state = "(모집대기)"
            2 -> state = "(모집중)"
            3 -> state = "(모집완료)"
        }

        return state
    }

    private fun convertVolunteerDay(day: String): String{
        var days = StringBuilder()

        if(day == "")
            return ""

        var tempDay = day.toInt()

        if(tempDay/1000000 == 1){
            days.append("월, ")
            tempDay -= 1000000
        }

        if(tempDay/100000 == 1){
            days.append("화, ")
            tempDay -= 100000
        }

        if(tempDay/10000 == 1){
            days.append("수, ")
            tempDay -= 10000
        }

        if(tempDay/1000 == 1){
            days.append("목, ")
            tempDay -= 1000
        }

        if(tempDay/100 == 1){
            days.append("금, ")
            tempDay -= 100
        }

        if(tempDay/10 == 1){
            days.append("토, ")
            tempDay -= 10
        }

        if(tempDay/1 == 1){
            days.append("일, ")
        }

        return days.toString()
    }

    private fun convertVolunteerMemberType(detail: VolunteerPartDetailData): String{
        var types = StringBuilder()

        if(detail.adultPosbleAt == "Y"){
            types.append("성인, ")
        }

        if(detail.yngbgsPosblAt == "Y"){
            types.append("청소년, ")
        }

        if(detail.familyPosblAt == "Y"){
            types.append("가족, ")
        }

        if(detail.pbsvntPosblAt== "Y"){
            types.append("공무원, ")
        }

        if(detail.grpPosblAt == "Y"){
            types.append("단체, ")
        }

        return types.toString()
    }
}
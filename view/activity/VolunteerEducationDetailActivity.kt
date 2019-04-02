package vdream.vd.com.vdream.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.VolunteerEduDetailData
import vdream.vd.com.vdream.network.VolunteerApiManager
import vdream.vd.com.vdream.utils.CommonUtils

class VolunteerEducationDetailActivity: BaseActivity() {
    var flBack: FrameLayout? = null
    var tvTitle: TextView? = null
    var tvRecruitNumber: TextView? = null
    var tvRecruitPeriod: TextView? = null
    var tvEduPeriod: TextView? = null
    var tvCenter: TextView? = null
    var tvTime: TextView? = null
    var tvCharge: TextView? = null
    var tvContact: TextView? = null
    var tvPlace: TextView? = null
    var tvContent: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var eduNo = intent.getIntExtra(getString(R.string.intent_key_name_volunteer_number), 0)
        setContentView(R.layout.activity_volunteer_education_detail)

        flBack = findViewById(R.id.flVolunteerEduBack)
        tvTitle = findViewById(R.id.tvVolunteerEduDetailTitle)
        tvRecruitNumber = findViewById(R.id.tvVolunteerEduDetailRecruitNumber)
        tvRecruitPeriod = findViewById(R.id.tvVolunteerEduDetailRecruitPeriod)
        tvEduPeriod = findViewById(R.id.tvVolunteerEduDetailEduPeriod)
        tvCenter = findViewById(R.id.tvVolunteerEduDetailCenter)
        tvTime = findViewById(R.id.tvVolunteerEduDetailTime)
        tvCharge = findViewById(R.id.tvVolunteerEduDetailCharge)
        tvContact = findViewById(R.id.tvVolunteerEduDetailContact)
        tvPlace = findViewById(R.id.tvVolunteerEduDetailPlace)
        tvContent = findViewById(R.id.tvVolunteerEduDetailContent)

        flBack?.setOnClickListener({
            finish()
        })

        getEducationDetailInfo(eduNo)
    }

    private fun getEducationDetailInfo(num: Int){
        var type = "json"
        var vApiService = VolunteerApiManager.getInstance().apiService
        vApiService.getVounteerEducationDetail(num, type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({result ->
                    setDataToUi(result.response!!.body!!.items!!.item!!)
                }, {
                    err ->
                    Log.e("VOLUNTEER_EDU_DETAIL", err.message)
                })
    }

    private fun setDataToUi(data: VolunteerEduDetailData){
        tvTitle?.text = data.crclmNm
        tvRecruitNumber?.text = "${data.edcNmpr}명"
        tvRecruitPeriod?.text = "${CommonUtils.volunteerDateRetouch(data.edcRceptBgnde!!)} ~ ${CommonUtils.volunteerDateRetouch(data.edcRceptEndde!!)}"
        tvEduPeriod?.text = "${CommonUtils.volunteerDateRetouch(data.edcBgnde!!)} ~ ${CommonUtils.volunteerDateRetouch(data.edcEndde!!)}"
        tvCenter?.text = data.edcMnnstNm
        tvTime?.text = "${convertTime(data.edcBeginTime!!)} ~ ${convertTime(data.edcEndTime!!)}"
        tvCharge?.text = data.edcChargerNm
        tvContact?.text = data.telno
        tvPlace?.text = data.edcplcNm
        tvContent?.text = data.edcCn
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
}
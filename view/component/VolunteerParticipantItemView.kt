package vdream.vd.com.vdream.view.component

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.VolunteerEducationListData
import vdream.vd.com.vdream.data.VolunteerParicipantData

class VolunteerParticipantItemView: FrameLayout {
    var tvTitle: TextView? = null
    var tvPlace: TextView? = null
    var tvPeriod: TextView? = null

    constructor(context: Context): super(context){
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_volunteer_participant_item, this, false)
        tvTitle = rootView.findViewById(R.id.tvVolunteerPItemTitle)
        tvPlace = rootView.findViewById(R.id.tvVolunteerPItemPlace)
        tvPeriod = rootView.findViewById(R.id.tvVolunteerPItemPeriod)

        addView(rootView)
    }

    internal fun setData(data: VolunteerParicipantData){
        tvTitle?.text = data.progrmSj
        tvPlace?.text = data.nanmmbyNm
        tvPeriod?.text = "${CommonUtils.volunteerDateRetouch(data.progrmBgnde!!)} - ${CommonUtils.volunteerDateRetouch(data.progrmEndde!!)}"
    }

    internal fun setData(data: VolunteerEducationListData){
        tvTitle?.text = data.crclmNm
        tvPlace?.text = data.edcMnnstNm
        tvPeriod?.text = "${CommonUtils.volunteerDateRetouch(data.edcBgnde!!)} - ${CommonUtils.volunteerDateRetouch(data.edcEndde!!)}"
    }
}
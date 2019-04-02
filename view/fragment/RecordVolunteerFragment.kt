package vdream.vd.com.vdream.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.RecordRegisterData
import vdream.vd.com.vdream.data.UserDiaryData
import vdream.vd.com.vdream.data.UserRecordData
import vdream.vd.com.vdream.view.component.TitledEditTextFlat
import vdream.vd.com.vdream.view.component.TitledDateTime
import vdream.vd.com.vdream.view.component.TitledEditArea
import vdream.vd.com.vdream.view.component.TitledSelector

class RecordVolunteerFragment: BaseRecordFragment(layoutRes = R.layout.fragment_record_volunteer){
    override fun setDataToUi() {
        if(recordData != null){
            tetfPlace?.setValue(recordData!!.volunteer_place!!)
            var sDateTime = recordData!!.volunteer_started_at!!.split(" ")
            tfdtStartedAt?.setDate(sDateTime[0])
            tfdtStartedAt?.setTime(sDateTime[1])
            var eDateTime = recordData!!.volunteer_ended_at!!.split(" ")
            tfdtEndedAt?.setDate(eDateTime[0])
            tfdtEndedAt?.setTime(eDateTime[1])
            tetfPeriod?.setValue(recordData!!.volunteer_period!!.toString())
            tetfActivity?.setValue(recordData!!.volunteer_activity_content!!)
            teaEffort?.setValue(recordData!!.volunteer_effort!!)
            teaLearning?.setValue(recordData!!.volunteer_learning!!)
        }
    }

    override fun setDiaryData(diary: UserDiaryData) {

    }

    override fun setOldData(oldData: UserRecordData) {
        recordData = oldData
    }

    var tfsKind: TitledSelector? = null
    var tetfPlace: TitledEditTextFlat? = null
    var tfdtStartedAt: TitledDateTime? = null
    var tfdtEndedAt: TitledDateTime? = null
    var tetfPeriod: TitledEditTextFlat? = null
    var tetfActivity: TitledEditTextFlat? = null
    var teaEffort: TitledEditArea? = null
    var teaLearning: TitledEditArea? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun init() {
        tfsKind = rootView!!.findViewById(R.id.tfsVolunteerKind)
        tetfPlace = rootView!!.findViewById(R.id.tetfVolunteerPlace)
        tfdtStartedAt = rootView!!.findViewById(R.id.tfdtVolunteerStartedAt)
        tfdtEndedAt = rootView!!.findViewById(R.id.tfdtVolunteerEndedAt)
        tetfPeriod = rootView!!.findViewById(R.id.tetfVolunteerPeriod)
        tetfActivity = rootView!!.findViewById(R.id.tetfVolunteerActivityContent)
        teaEffort = rootView!!.findViewById(R.id.teaVolunteerEffort)
        teaLearning = rootView!!.findViewById(R.id.teaVolunteerLearning)

        tfsKind?.setTitle(getString(R.string.user_record_volunteer_kind))
        tetfPlace?.setTitle(getString(R.string.user_record_volunteer_place))
        tfdtStartedAt?.setTitle(getString(R.string.user_record_volunteer_started_at))
        tfdtEndedAt?.setTitle(getString(R.string.user_record_volunteer_ended_at))
        tetfPeriod?.setTitle(getString(R.string.user_record_volunteer_period))
        tetfActivity?.setTitle(getString(R.string.user_record_volunteer_activity_content))
        teaEffort?.setTitle(getString(R.string.user_record_volunteer_effort))
        teaLearning?.setTitle(getString(R.string.user_record_volunteer_learning))

        var nameList = arrayOf(getString(R.string.user_record_volunteer_kind_inner), getString(R.string.user_record_volunteer_kind_outer)).toCollection(ArrayList())
        var valueList = arrayOf(getString(R.string.user_record_volunteer_kind_inner_value), getString(R.string.user_record_volunteer_kind_outer_value)).toCollection(ArrayList())
        tfsKind?.setGridColumnLine(2)
        tfsKind?.setItemTitleGravityCenter()
        tfsKind?.setSelectorData(nameList, valueList)

        tetfPeriod?.setNumericInput()
        teaEffort?.setMaxContentLine(4)
        teaLearning?.setMaxContentLine(4)

        setDataToUi()
    }

    override fun checkRegisterData(): String {
        registerData!!.volunteer_kind = tfsKind!!.selectedValue
        registerData!!.volunteer_place = tetfPlace!!.getValue()
        registerData!!.volunteer_started_at = tfdtStartedAt!!.getDate() + " " + tfdtStartedAt!!.getTime()
        registerData!!.volunteer_ended_at = tfdtEndedAt!!.getDate() + " " + tfdtEndedAt!!.getTime()
        if(tetfPeriod!!.getValue() != "")
            registerData!!.volunteer_period = tetfPeriod!!.getValue().toInt()
        registerData!!.volunteer_activity_content = tetfActivity!!.getValue()
        registerData!!.volunteer_effort = teaEffort!!.getValue()
        registerData!!.volunteer_learning = teaLearning!!.getValue()

        var err = ""

        if(registerData!!.volunteer_place == ""){
            err = getString(R.string.user_record_volunteer_input_place)
        }else if(tfdtStartedAt!!.getDate() == "" || tfdtStartedAt!!.getTime() == ""){
            err = getString(R.string.user_record_volunteer_input_started_at)
        }else if(tfdtEndedAt!!.getDate() == "" || tfdtEndedAt!!.getTime() == ""){
            err = getString(R.string.user_record_volunteer_input_ended_at)
        }else if(registerData!!.volunteer_period == 0) {
            err = getString(R.string.user_record_volunteer_input_period)
        }else if(registerData!!.volunteer_activity_content == "") {
            err = getString(R.string.user_record_volunteer_input_activity_content)
        }else if(registerData!!.volunteer_effort == "") {
            err = getString(R.string.user_record_volunteer_input_effort)
        }else if(registerData!!.volunteer_learning == ""){
            err = getString(R.string.user_record_volunteer_input_learning)
        }

        return err
    }
    override fun getRecordData(): RecordRegisterData {
        return registerData
    }

}
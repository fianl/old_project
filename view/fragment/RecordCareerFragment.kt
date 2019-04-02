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

class RecordCareerFragment: BaseRecordFragment(layoutRes = R.layout.fragment_record_career){
    override fun setDataToUi() {
        if(recordData != null){
            var dateTime = recordData!!.career_activated_at!!.split(" ")
            tfdtActiviatedAt?.setDate(dateTime[0])
            tfdtActiviatedAt?.setTime(dateTime[1])
            teaEffort?.setValue(recordData!!.career_effort!!)
            teaLearning?.setValue(recordData!!.career_learning!!)
        }
    }

    override fun setDiaryData(diary: UserDiaryData) {

    }

    override fun setOldData(oldData: UserRecordData) {
        recordData = oldData
    }

    var tfdtActiviatedAt: TitledDateTime? = null
    var teaEffort: TitledEditArea? = null
    var teaLearning: TitledEditArea? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun init() {
        tfdtActiviatedAt = rootView!!.findViewById(R.id.tfdtCareerTime)
        teaEffort = rootView!!.findViewById(R.id.teaCareerEffort)
        teaLearning = rootView!!.findViewById(R.id.teaCareerLearning)

        tfdtActiviatedAt?.setTitle(getString(R.string.user_record_career_activated_at))
        teaEffort?.setTitle(getString(R.string.user_record_career_effort))
        teaLearning?.setTitle(getString(R.string.user_record_career_learning))

        tfdtActiviatedAt?.hideTime()
        teaEffort?.setMaxContentLine(4)
        teaLearning?.setMaxContentLine(4)

        setDataToUi()
    }

    override fun checkRegisterData(): String {
        registerData!!.career_activated_at = tfdtActiviatedAt!!.getDate()
        registerData!!.career_effort = teaEffort!!.getValue()
        registerData!!.career_learning = teaLearning!!.getValue()

        var err = ""

        if(registerData!!.career_activated_at == null || registerData!!.career_activated_at == ""){
            err = getString(R.string.user_record_career_input_activated_at)
        }else if(registerData!!.career_effort == ""){
            err = getString(R.string.user_record_career_input_effort)
        }else if(registerData!!.career_learning == ""){
            err = getString(R.string.user_record_career_input_learning)
        }

        return err
    }

    override fun getRecordData(): RecordRegisterData {
        return registerData
    }

}
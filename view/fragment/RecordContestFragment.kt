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

class RecordContestFragment: BaseRecordFragment(layoutRes = R.layout.fragment_record_contest) {
    override fun setDataToUi() {
        if(recordData != null){
            var dateTime = recordData!!.contest_activated_at!!.split(" ")
            tfdtActiviatedAt?.setDate(dateTime[0])
            tfdtActiviatedAt?.setTime(dateTime[1])
            teaEffort?.setValue(recordData!!.contest_effort!!)
            teaLearning?.setValue(recordData!!.contest_learning!!)
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
        tfdtActiviatedAt = rootView!!.findViewById(R.id.tfdtContestTime)
        teaEffort = rootView!!.findViewById(R.id.teaContestEffort)
        teaLearning = rootView!!.findViewById(R.id.teaContestLearning)

        tfdtActiviatedAt?.setTitle(getString(R.string.user_record_career_activated_at))
        teaEffort?.setTitle(getString(R.string.user_record_career_effort))
        teaLearning?.setTitle(getString(R.string.user_record_career_learning))

        tfdtActiviatedAt?.hideTime()
        teaEffort?.setMaxContentLine(4)
        teaLearning?.setMaxContentLine(4)

        setDataToUi()
    }

    override fun checkRegisterData(): String {
        registerData!!.contest_activated_at = tfdtActiviatedAt!!.getDate()
        registerData!!.contest_effort = teaEffort!!.getValue()
        registerData!!.contest_learning = teaLearning!!.getValue()

        var err = ""

        if(registerData!!.contest_activated_at == null || registerData!!.contest_activated_at == ""){
            err = getString(R.string.user_record_career_input_activated_at)
        }else if(registerData!!.contest_effort == ""){
            err = getString(R.string.user_record_career_input_effort)
        }else if(registerData!!.contest_learning == ""){
            err = getString(R.string.user_record_career_input_learning)
        }

        return err
    }

    override fun getRecordData(): RecordRegisterData {
        return registerData
    }
}
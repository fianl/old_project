package vdream.vd.com.vdream.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
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

class RecordClubFragment: BaseRecordFragment(layoutRes = R.layout.fragment_record_club) {
    override fun setDataToUi() {
        if(recordData != null){
            tetfTopic?.setValue(recordData!!.club_topic!!)
            var dateTime = recordData!!.club_activated_at!!.split(" ")
            tfdtActivatedAt?.setDate(dateTime[0])
            tfdtActivatedAt?.setTime(dateTime[1])
            tetfParticipants?.setValue(recordData!!.club_participants!!)
            teaMotivation?.setValue(recordData!!.club_motivation!!)
            teaPrinciple?.setValue(recordData!!.club_principle!!)
            teaLearning?.setValue(recordData!!.club_learning!!)
        }
    }

    override fun setDiaryData(diary: UserDiaryData) {

    }

    override fun setOldData(oldData: UserRecordData) {
        recordData = oldData
    }

    var tetfTopic: TitledEditTextFlat? = null
    var tfdtActivatedAt: TitledDateTime? = null
    var tetfParticipants: TitledEditTextFlat? = null
    var teaMotivation: TitledEditArea? = null
    var teaPrinciple: TitledEditArea? = null
    var teaLearning: TitledEditArea? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun init() {
        tetfTopic = rootView!!.findViewById(R.id.tetfClubTopic)
        tfdtActivatedAt = rootView!!.findViewById(R.id.tfdtTime)
        tetfParticipants = rootView!!.findViewById(R.id.tetfClubParticipants)
        teaMotivation = rootView!!.findViewById(R.id.teaClubMotivation)
        teaPrinciple = rootView!!.findViewById(R.id.teaClubPrinciple)
        teaLearning = rootView!!.findViewById(R.id.teaClubLearning)

        tetfTopic!!.setTitle(getString(R.string.user_record_club_topic))
        tfdtActivatedAt!!.setTitle(getString(R.string.user_record_club_activated_at))
        tetfParticipants!!.setTitle(getString(R.string.user_record_club_participants))
        teaMotivation!!.setTitle(getString(R.string.user_record_club_motivation))
        teaMotivation!!.setMaxContentLine(2)
        teaPrinciple!!.setTitle(getString(R.string.user_record_club_principle))
        teaPrinciple!!.setMaxContentLine(4)
        teaLearning!!.setTitle(getString(R.string.user_record_club_learning))
        teaLearning!!.setMaxContentLine(4)

        setDataToUi()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            var speechResult = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            teaLearning?.setValue(speechResult[0])
        }
    }

    override fun checkRegisterData(): String {
        registerData!!.club_topic = tetfTopic!!.getValue()
        registerData!!.club_activated_at = tfdtActivatedAt!!.getDate() + " " + tfdtActivatedAt!!.getTime()
        registerData!!.club_participants = tetfParticipants!!.getValue()
        registerData!!.club_motivation = teaMotivation!!.getValue()
        registerData!!.club_principle = teaPrinciple!!.getValue()
        registerData!!.club_learning = teaLearning!!.getValue()

        var err = ""

        if(tetfTopic!!.getValue() == "") {
            err = getString(R.string.user_record_club_input_topic)
        }else if(tfdtActivatedAt!!.getDate() == ""){
            err = getString(R.string.user_record_club_input_activated_at)
        }else if(tetfParticipants!!.getValue() == "") {
            err = getString(R.string.user_record_club_input_participants)
        }else if(teaPrinciple!!.getValue() == ""){
            err = getString(R.string.user_record_club_input_principle)
        }else if(teaLearning!!.getValue() == "" ){
            err = getString(R.string.user_record_club_input_learning)
        }

        return err
    }

    override fun getRecordData(): RecordRegisterData {
        return registerData!!
    }
}
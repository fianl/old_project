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
import vdream.vd.com.vdream.view.component.TitledEditArea
import vdream.vd.com.vdream.view.component.TitledEditTextFlat

class RecordClassFragment: BaseRecordFragment(layoutRes = R.layout.fragment_record_class) {
    override fun setDataToUi() {
        if(recordData != null){
            tetfUnit?.setValue(recordData!!.class_unit!!)
            teaLearning?.setValue(recordData!!.class_leading_learning!!)
            teaLeading?.setValue(recordData!!.class_leading_learning!!)
        }
    }

    override fun setDiaryData(diary: UserDiaryData) {

    }

    override fun setOldData(oldData: UserRecordData) {
        recordData = oldData
    }

    var tetfUnit: TitledEditTextFlat? = null
    var teaLearning: TitledEditArea? = null
    var teaLeading: TitledEditArea? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun init() {
        tetfUnit = rootView!!.findViewById(R.id.tetfClassUnit)
        teaLearning = rootView!!.findViewById(R.id.teaClassLearning)
        teaLeading = rootView!!.findViewById(R.id.teaClassLeading)

        tetfUnit!!.setTitle(getString(R.string.user_record_class_unit))
        teaLearning!!.setTitle(getString(R.string.user_record_class_learning))
        teaLeading!!.setTitle(getString(R.string.user_record_class_leading))

        teaLearning!!.setMaxContentLine(3)
        teaLeading!!.setMaxContentLine(6)

        teaLeading!!.setContentHint(getString(R.string.user_record_class_leading_hint))

        setDataToUi()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            var speechResult = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            teaLeading?.setValue(speechResult[0])
        }
    }

    override fun checkRegisterData(): String {
        registerData!!.class_unit = tetfUnit!!.getValue()
        registerData!!.class_learning_content = teaLearning!!.getValue()
        registerData!!.class_leading_learning = teaLeading!!.getValue()

        var err = ""

        if(tetfUnit!!.getValue() == "") {
            err = getString(R.string.user_record_class_input_unit)
        }else if(teaLearning!!.getValue() == "") {
            err = getString(R.string.user_record_class_input_learning)
        }

        return err
    }

    override fun getRecordData(): RecordRegisterData {
        return registerData!!
    }
}
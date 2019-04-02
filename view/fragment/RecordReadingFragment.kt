package vdream.vd.com.vdream.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.RecordRegisterData
import vdream.vd.com.vdream.data.UserDiaryData
import vdream.vd.com.vdream.data.UserRecordData
import vdream.vd.com.vdream.view.component.TitledEditArea
import vdream.vd.com.vdream.view.component.TitledEditTextFlat

class RecordReadingFragment: BaseRecordFragment(layoutRes = R.layout.fragment_record_reading){
    override fun setDataToUi() {
        if(recordData != null) {
            tetfBookName?.setValue(recordData!!.reading_book_name!!)
            tetfAuthor?.setValue(recordData!!.reading_author!!)
            teaMotivation?.setValue(recordData!!.reading_motivation!!)
            teaSummary?.setValue(recordData!!.reading_summary!!)
            teaLearning?.setValue(recordData!!.reading_learning!!)
        }
    }

    override fun setDiaryData(diary: UserDiaryData) {

    }

    override fun setOldData(oldData: UserRecordData) {
        recordData = oldData
    }

    var tetfBookName: TitledEditTextFlat? = null
    var tetfAuthor: TitledEditTextFlat? = null
    var teaMotivation: TitledEditArea? = null
    var teaSummary: TitledEditArea? = null
    var teaLearning: TitledEditArea? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun init() {
        tetfBookName = rootView!!.findViewById(R.id.tetfReadingBookName)
        tetfAuthor = rootView!!.findViewById(R.id.tetfReadingAuthor)
        teaMotivation = rootView!!.findViewById(R.id.teaReadingMotivation)
        teaSummary = rootView!!.findViewById(R.id.teaReadingSummary)
        teaLearning = rootView!!.findViewById(R.id.teaReadingLearning)

        tetfBookName?.setTitle(getString(R.string.user_record_reading_book_name))
        tetfAuthor?.setTitle(getString(R.string.user_record_reading_author))
        teaMotivation?.setTitle(getString(R.string.user_record_reading_motivation))
        teaSummary?.setTitle(getString(R.string.user_record_reading_summary))
        teaLearning?.setTitle(getString(R.string.user_record_reading_learning))

        teaMotivation?.setMaxContentLine(3)
        teaSummary?.setMaxContentLine(8)
        teaLearning?.setMaxContentLine(6)

        setDataToUi()
    }

    override fun checkRegisterData(): String {
        registerData!!.reading_book_name = tetfBookName!!.getValue()
        registerData!!.reading_author = tetfAuthor!!.getValue()
        registerData!!.reading_motivation = teaMotivation!!.getValue()
        registerData!!.reading_summary = teaSummary!!.getValue()
        registerData!!.reading_learning = teaLearning!!.getValue()

        var err = ""

        if(registerData!!.reading_book_name == ""){
            err = getString(R.string.user_record_reading_input_book_name)
        }else if(registerData!!.reading_motivation == ""){
            err = getString(R.string.user_record_reading_input_motivation)
        }else if(registerData!!.reading_summary == ""){
            err = getString(R.string.user_record_reading_input_summary)
        }else if(registerData!!.reading_learning == ""){
            err = getString(R.string.user_record_reading_input_learning)
        }

        return err
    }

    override fun getRecordData(): RecordRegisterData {
        return registerData
    }

}
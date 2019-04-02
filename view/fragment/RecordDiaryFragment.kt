package vdream.vd.com.vdream.view.fragment

import android.text.SpannableStringBuilder
import android.widget.EditText
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.RecordRegisterData
import vdream.vd.com.vdream.data.UserDiaryData
import vdream.vd.com.vdream.data.UserRecordData

class RecordDiaryFragment: BaseRecordFragment(layoutRes = R.layout.fragment_record_diary){
    var userDiaryData: UserDiaryData? = null

    override fun setDataToUi() {
        if(userDiaryData != null){
            etContent?.text = SpannableStringBuilder(userDiaryData!!.content)
        }
    }

    override fun setDiaryData(diary: UserDiaryData) {
        userDiaryData = diary
    }

    override fun setOldData(oldData: UserRecordData) {

    }

    var etContent: EditText? = null

    override fun init() {
        etContent = rootView!!.findViewById(R.id.etDiaryContent)
        setDataToUi()
    }

    override fun checkRegisterData(): String {
        registerData.content = etContent!!.text.toString()
        return ""
    }

    override fun getRecordData(): RecordRegisterData {
        return registerData
    }

}
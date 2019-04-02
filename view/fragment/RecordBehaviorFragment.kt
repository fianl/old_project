package vdream.vd.com.vdream.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.RecordRegisterData
import vdream.vd.com.vdream.data.UserDiaryData
import vdream.vd.com.vdream.data.UserRecordData
import vdream.vd.com.vdream.view.component.TitledEditArea
import vdream.vd.com.vdream.view.component.TitledEditTextFlat
import vdream.vd.com.vdream.view.component.TitledSelector

class RecordBehaviorFragment: BaseRecordFragment(layoutRes = R.layout.fragment_record_behavior){
    override fun setDataToUi() {
        if(recordData != null){
            tfsBehaviorKind?.setDataSelection(getValueOrder(recordData!!.behavior_kind!!))
            teaBehaviorCase?.setValue(recordData!!.behavior_case!!)
        }
    }

    override fun setDiaryData(diary: UserDiaryData) {

    }

    override fun setOldData(oldData: UserRecordData) {
        recordData = oldData
    }

    var tfsBehaviorKind: TitledSelector? = null
    var teaBehaviorCase: TitledEditArea? = null
    var nameList = ArrayList<String>()
    var valueList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun init() {
        tfsBehaviorKind = rootView!!.findViewById(R.id.tfsBehaviorKind)
        teaBehaviorCase = rootView!!.findViewById(R.id.teaBehaviorCase)

        tfsBehaviorKind?.setTitle(getString(R.string.user_record_behavior_kind))
        teaBehaviorCase?.setTitle(getString(R.string.user_record_behavior_case))

        nameList = arrayListOf<String>(getString(R.string.user_record_behavior_kind_consideration), getString(R.string.user_record_behavior_kind_sharing),
                getString(R.string.user_record_behavior_kind_cooperation), getString(R.string.user_record_behavior_kind_respect),
                getString(R.string.user_record_behavior_kind_conflict), getString(R.string.user_record_behavior_kind_diligence),
                getString(R.string.user_record_behavior_kind_responsibility), getString(R.string.user_record_behavior_kind_instance),
                getString(R.string.user_record_behavior_kind_creativity), getString(R.string.user_record_behavior_kind_leadership),
                getString(R.string.user_record_behavior_kind_etc))

        valueList = arrayListOf<String>(getString(R.string.user_record_behavior_kind_consideration_value), getString(R.string.user_record_behavior_kind_sharing_value),
                getString(R.string.user_record_behavior_kind_cooperation_value), getString(R.string.user_record_behavior_kind_respect_value),
                getString(R.string.user_record_behavior_kind_conflict_value), getString(R.string.user_record_behavior_kind_diligence_value),
                getString(R.string.user_record_behavior_kind_responsibility_value), getString(R.string.user_record_behavior_kind_instance_value),
                getString(R.string.user_record_behavior_kind_creativity_value), getString(R.string.user_record_behavior_kind_leadership_value),
                getString(R.string.user_record_behavior_kind_etc_value))

        tfsBehaviorKind?.setGridColumnLine(3)
        tfsBehaviorKind?.setSelectorHeightLong()
        tfsBehaviorKind?.setSelectorData(nameList, valueList)
        teaBehaviorCase?.setMaxContentLine(4)

        setDataToUi()
    }

    override fun checkRegisterData(): String {
        registerData.behavior_kind = tfsBehaviorKind!!.selectedValue
        registerData.behavior_case = teaBehaviorCase!!.getValue()

        var err = ""

        if(registerData.behavior_case == "")
            err = getString(R.string.user_record_behavior_input_case)
        else if(registerData.behavior_kind == "")
            err = getString(R.string.user_record_behavior_input_kind)

        return err
    }

    override fun getRecordData(): RecordRegisterData {
        return registerData
    }

    private fun getValueOrder(value: String): Int {
        var order = -1

        for(idx in 0..valueList.lastIndex){
            if(valueList[idx] == value)
                order = idx
        }

        return order
    }
}
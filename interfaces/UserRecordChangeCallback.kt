package vdream.vd.com.vdream.interfaces

import vdream.vd.com.vdream.data.UserDiaryData
import vdream.vd.com.vdream.data.UserRecordData

interface UserRecordChangeCallback {
    fun onChanged(type: String)
    fun requestModifyRecord(record: UserRecordData)
    fun requesModifyDiary(diary: UserDiaryData)

}
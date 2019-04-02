package vdream.vd.com.vdream.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.AnnounceData
import vdream.vd.com.vdream.data.AttachedFileData
import vdream.vd.com.vdream.data.ClassSummaryData
import vdream.vd.com.vdream.view.component.BaseAnnounceView
import vdream.vd.com.vdream.view.component.ClassesViewGroup

class MyInterestFragment: Fragment() {
    var cvgMyRegister: ClassesViewGroup? = null
    var llMyContents: LinearLayout? = null
    var registerClasses: ArrayList<ClassSummaryData> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, instance: Bundle?): View {
        var rootView = inflater.inflate(R.layout.fragment_my_interest, container, false)
        cvgMyRegister = rootView.findViewById(R.id.cvgMyRegister)
        llMyContents = rootView.findViewById(R.id.llMyContentsContainer)

        getRegisterClassesInfo()
        getMyInterestsInfo()

        return rootView
    }

    internal fun getRegisterClassesInfo(){
        for(x in 0..3){
            registerClasses.add(ClassSummaryData(x, "https://scontent-sea1-1.cdninstagram.com/t51.2885-15/s480x480/e35/c231.0.618.618/22071305_118373932190401_4710088671276040192_n.jpg?ig_cache_key=MTYxNDczMDk0NDcxNDc5NzAxNQ%3D%3D.2.c", "바른글쓰기", "#글씨#캘리그라피#악필"))
        }

        cvgMyRegister?.setType(getString(R.string.type_my))
        cvgMyRegister?.setMyRegisterClassData(registerClasses)
        cvgMyRegister?.setClassMarkNone()
    }

    internal fun getMyInterestsInfo(){
        for(x in 0..6) {
            var announceData = AnnounceData()
            announceData.writerImgUrl = "https://pbs.twimg.com/profile_images/378800000519114526/f82c3f0107e535a9c8090bd31ba9da50_400x400.jpeg"
            announceData.title = "무지개 어린이집"
            announceData.writerName = "클래스매니저"
            announceData.time = "6시간전"
            announceData.textContent = "[공지사항] 2018년도 신입생 안내문\n\n2018년도 신입 교육 대상자 첨부파일 확인바랍니다\n"
            var attchedContent = AttachedFileData()
            attchedContent.filePath = ""
            attchedContent.fileName = "2018년도 신입 교육 대상자.hwp"
            attchedContent.fileSize = 72
            announceData.attchUrlList = arrayOf(attchedContent)

            var announceView = BaseAnnounceView(context!!)
            announceView.setData(announceData)

            llMyContents?.addView(announceView)
        }
    }
}
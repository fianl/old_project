package vdream.vd.com.vdream.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import vdream.vd.com.vdream.R

class StrongSmallCompanyFragment: Fragment() {
    var lvCompnaies: LinearLayout? = null
    var compnayList = ArrayList<SmallCompnay>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_strong_small_company, container, false)
        lvCompnaies = rootView.findViewById(R.id.lvCompanyList)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(compnayList.isEmpty()) {
            compnayList.add(SmallCompnay("(주)디엔디이", "디지털 사이니지 개발"))
            compnayList.add(SmallCompnay("(주)리컨벤션", "MICE 발전을 위한 신규컨벤션 기획"))
            compnayList.add(SmallCompnay("(주)동호전자", "VR콘텐츠와 4D시뮬레이션이 결합된 MAXRIDER 시뮬레이터 개발"))
            compnayList.add(SmallCompnay("(주)블레싱에코디자인", "ICT 기술기반의 다목적 드론 디자인 개발"))
            compnayList.add(SmallCompnay("(주)마로인", "콘텐츠 웨어 하우스 개발"))
            compnayList.add(SmallCompnay("(주)인타운", "다중입출력 IoT 허브용 산업용 게이트웨이 개발"))
            compnayList.add(SmallCompnay("(주)에스위너스", "원격자산관리 솔루션 개발"))
            compnayList.add(SmallCompnay("(주)파크이에스엠", "게임개발"))
            compnayList.add(SmallCompnay("수상에스티(주)", "감성 교감형 봉제지능 로봇 및 콘텐츠 개발"))
            compnayList.add(SmallCompnay("한국컴포짓(주)", "유선드론 전원공급장치 시제품 개발"))
            compnayList.add(SmallCompnay("(주)펠릭스테크", "단조플랜지 제조기술 개발"))
            compnayList.add(SmallCompnay("(주)동화뉴텍", "LNG추진선용 BOG 압축기 핵심 부품 및 제어 모니터링 시스템 개발"))
            compnayList.add(SmallCompnay("에스피엑스플로우테크놀로지(주)", "에너지 절감형 대형 에어드라이어 개발"))
            compnayList.add(SmallCompnay("(주)늘푸른바다", "자동차 Shift Motor 제어용 Brush CardAss'y 개발"))
            compnayList.add(SmallCompnay("동연스틸(주)", "고강성 스프링강 스테빌라이져 Tube 제작 기술개발"))
            compnayList.add(SmallCompnay("(주)동성코퍼레이션", "고내열성 고내구성 열가소성 폴리우레탄 패킹씰 소재 개발"))
        }


        lvCompnaies?.removeAllViews()
        for(data in compnayList){
            var companyView = LayoutInflater.from(context).inflate(R.layout.view_item_small_company, lvCompnaies, false)
            var tvName = companyView.findViewById<TextView>(R.id.tvSmallCompanyName)
            var tvField = companyView.findViewById<TextView>(R.id.tvSmallCompanyField)

            lvCompnaies?.addView(companyView)

            tvName.text = data.name
            tvField.text = data.field
        }
    }

    inner class SmallCompnay {
        constructor(name: String, field: String) {
            this.name = name
            this.field = field
        }

        var name = ""
        var field = ""
    }
}
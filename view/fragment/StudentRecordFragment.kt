package vdream.vd.com.vdream.view.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.view.activity.*
import vdream.vd.com.vdream.view.component.RecordTypeItemView

class StudentRecordFragment: Fragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        var userInfo = MyInfoStore.myInfo
        if(userInfo!!.nickname == null || userInfo!!.nickname == ""){
            userInfoSetDialog()
            return
        }

        when(v?.id){
            R.id.rtivClass -> moveToCreateRecord(getString(R.string.user_record_type_class))
            R.id.rtivClub -> moveToCreateRecord(getString(R.string.user_record_type_club))
            R.id.rtivCareer -> moveToCreateRecord(getString(R.string.user_record_type_career))
            R.id.rtivContest -> moveToCreateRecord(getString(R.string.user_record_type_contest))
            R.id.rtivVolunteer -> moveToCreateRecord(getString(R.string.user_record_type_volunteer))
            R.id.rtivBehavior-> moveToCreateRecord(getString(R.string.user_record_type_behavior))
            R.id.rtivReading -> moveToCreateRecord(getString(R.string.user_record_type_reading))
            R.id.rtivOtherRecord -> moveToCreateRecord("")
            R.id.rtivMyRecord -> {
                var intent = Intent(context!!, MypageAcitivity::class.java)
                startActivity(intent)
            }
            R.id.tvStudentRecordPortfolio -> {
                var intent = Intent(context!!, CreatePortfolioActivity::class.java)
                startActivity(intent)
            }
        }
    }

    var ivBanner: ImageView? = null
    var rtivClass: RecordTypeItemView? = null
    var rtivClub: RecordTypeItemView? = null
    var rtivCareer: RecordTypeItemView? = null
    var rtivContest: RecordTypeItemView? = null
    var rtivVolunteer: RecordTypeItemView? = null
    var rtivBehavior: RecordTypeItemView? = null
    var rtivReading: RecordTypeItemView? = null
    var rtivOther: RecordTypeItemView? = null
    var rtivMyRecord: RecordTypeItemView? = null
    var tvPortfolio: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_student_record, container, false)
        ivBanner = rootView.findViewById(R.id.ivStudentRecordBanner)
        rtivClass = rootView.findViewById(R.id.rtivClass)
        rtivClub = rootView.findViewById(R.id.rtivClub)
        rtivCareer = rootView.findViewById(R.id.rtivCareer)
        rtivContest = rootView.findViewById(R.id.rtivContest)
        rtivVolunteer = rootView.findViewById(R.id.rtivVolunteer)
        rtivBehavior = rootView.findViewById(R.id.rtivBehavior)
        rtivReading = rootView.findViewById(R.id.rtivReading)
        rtivOther = rootView.findViewById(R.id.rtivOtherRecord)
        rtivMyRecord = rootView.findViewById(R.id.rtivMyRecord)
        tvPortfolio = rootView.findViewById(R.id.tvStudentRecordPortfolio)

        tvPortfolio?.setOnClickListener(this)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initTypeView()
    }

    private fun initTypeView(){
        rtivClass?.setTypeData(getString(R.string.user_record_kor_type_class), R.drawable.icon_class)
        rtivClub?.setTypeData(getString(R.string.user_record_kor_type_club), R.drawable.icon_class)
        rtivCareer?.setTypeData(getString(R.string.user_record_kor_type_career), R.drawable.icon_class)
        rtivContest?.setTypeData(getString(R.string.user_record_kor_type_contest), R.drawable.icon_class)
        rtivVolunteer?.setTypeData(getString(R.string.user_record_kor_type_volunteer), R.drawable.icon_class)
        rtivBehavior?.setTypeData(getString(R.string.user_record_kor_type_behavior), R.drawable.icon_class)
        rtivReading?.setTypeData(getString(R.string.user_record_kor_type_reading), R.drawable.icon_class)
        rtivOther?.setTypeData(getString(R.string.board_mypage_activity_record), R.drawable.icon_class)
        rtivMyRecord?.setTypeData(getString(R.string.board_mypage_my_record), R.drawable.icon_class)

        rtivClass?.setOnClickListener(this)
        rtivClub?.setOnClickListener(this)
        rtivCareer?.setOnClickListener(this)
        rtivContest?.setOnClickListener(this)
        rtivVolunteer?.setOnClickListener(this)
        rtivBehavior?.setOnClickListener(this)
        rtivReading?.setOnClickListener(this)
        rtivOther?.setOnClickListener(this)
        rtivMyRecord?.setOnClickListener(this)
    }

    private fun moveToCreateRecord(type: String){
        var intent = Intent(context!!, RecordActivity::class.java)
        intent.putExtra(getString(R.string.intent_key_name_record_kind), type)
        startActivityForResult(intent, MainActivity.USER_EDIT)
    }

    private fun userInfoSetDialog() {
        var alert = AlertDialog.Builder(context)
        alert.setTitle(getString(R.string.update_alert_title))
        alert.setMessage(getString(R.string.input_additional_info_before_write))
        alert.setPositiveButton(getString(R.string.set_userInfo)) { dialog, which ->
            var intent = Intent(context!!, UserInfoSetActivity::class.java)
            startActivity(intent)
        }
        alert.setNegativeButton(getString(R.string.update_alert_negative_button_close), null)
        alert.create().show()
    }
}
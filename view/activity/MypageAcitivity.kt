package vdream.vd.com.vdream.view.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropCircleTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.*
import vdream.vd.com.vdream.interfaces.CommentClickCallback
import vdream.vd.com.vdream.interfaces.UserRecordChangeCallback
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils
import vdream.vd.com.vdream.view.component.CustomTabView
import vdream.vd.com.vdream.view.component.MypageClassItemView
import vdream.vd.com.vdream.view.component.MypageExpItemView
import vdream.vd.com.vdream.view.component.UserRecordAnnounceView
import java.util.*

class MypageAcitivity: BaseActivity(), View.OnClickListener, CommentClickCallback, UserRecordChangeCallback {
    override fun requestModifyRecord(record: UserRecordData) {
        sendAppEvent("마이페이지_활동기록_수정")

        if(record.kind == getString(R.string.user_record_type_diary)) {
            var diary = UserDiaryData()
            diary.idx = record.idx
            diary.is_mine = record.is_mine
            diary.is_like = record.is_like
            diary.nickname = record.nickname
            diary.profile_img = record.profile_img
            diary.status = record.status
            diary.content = record.content!!
            diary.tags = record.tags
            diary.comments = record.comments
            diary.files = record.files
            diary.like_count = record.like_count
            diary.created_at = record.created_at

            requesModifyDiary(diary)
            return
        }

        var intent = Intent(applicationContext, RecordActivity::class.java)
        intent.putExtra(getString(R.string.intent_key_name_record_kind), record.kind)
        intent.putExtra(getString(R.string.intent_key_name_record_data), record)
        startActivityForResult(intent, USER_EDIT)
    }

    override fun requesModifyDiary(diary: UserDiaryData) {
        sendAppEvent("마이페이지_일기장_수정")
        var intent = Intent(applicationContext, RecordActivity::class.java)
        intent.putExtra(getString(R.string.intent_key_name_record_kind), "")
        intent.putExtra(getString(R.string.intent_key_name_record_data), diary)
        startActivityForResult(intent, USER_EDIT)
    }

    override fun onChanged(type: String) {
        if(type == getString(R.string.user_record_type_diary)){
            diaryList.clear()
            diaryPage = 1
            getUserDiaryList(true)
        }else{
            recordList.clear()
            recordPage = 1
            getUserRecordList(recordType[0])
        }
    }

    override fun onClick(idx: Int) {
        sendAppEvent("마이페이지_댓글")
        var intent = Intent(this, CommentActivity::class.java)
        intent.putExtra(getString(R.string.intent_key_name_article_index), idx)
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flMyPageBack -> {
                sendAppEvent("마이페이지_헤더_뒤로가기")
                finish()
            }
            R.id.ivMyPageEdit -> {
                sendAppEvent("마이페이지_유저정보수정")
                var intent = Intent(this, UserInfoSetActivity::class.java)
                startActivityForResult(intent, USER_EDIT)
            }
            R.id.flMyPageWriteContainer -> {
                sendAppEvent("마이페이지_생기부작성")
                if(userInfo!!.nickname == null || userInfo!!.nickname.equals("")){
                    Toast.makeText(this, getString(R.string.input_additional_info_before_write), Toast.LENGTH_SHORT).show()
                    return
                }

                createWriteTypeDialog()
            }
        }
    }

    val USER_EDIT = 0
    var userInfo: UserInfoData? = null
    var recordList = ArrayList<UserRecordData>()
    var diaryList = ArrayList<UserDiaryData>()
    var madeClassList = ArrayList<ClassroomBaseData>()
    var joinedClassList = ArrayList<ClassroomBaseData>()
    var madeExpList = ArrayList<ExperienceData>()
    var requestExpList = ArrayList<ExperienceData>()
    var recordPage = 1
    var diaryPage = 1
    var madeClassPage = 0
    var joinedClassPage = 0
    var recordType = arrayOf("all", "class", "experience", "read", "school", "certificate", "license")
    var curBoard = ""

    var svMain: ScrollView? = null
    var clProfileBg: ConstraintLayout? = null
    var flMypageBack: FrameLayout? = null
    var tvMyPageTitle: TextView? = null
    var flMypageWrite: FrameLayout? = null
    var ivProfileImg: ImageView? = null
    var ivEdit: ImageView? = null
    var tvNickname: TextView? = null
    var ivOpenClose: ImageView? = null
    var ivFavorite: ImageView? = null
    var tvFeedCnt: TextView? = null
    var tvSubscriptCnt: TextView? = null
    var llMyPageBoardContainer: LinearLayout? = null
    var llBoardList: LinearLayout? = null
    var llBoardContainer: LinearLayout? = null
    var boardViewList= ArrayList<CustomTabView>()


    var boardViewLeftMargin = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)
        userInfo = MyInfoStore.myInfo

        svMain = findViewById(R.id.svMyPageMain)
        clProfileBg = findViewById(R.id.clProfileContainer)
        flMypageBack = findViewById(R.id.flMyPageBack)
        tvMyPageTitle = findViewById(R.id.tvMyPageTitle)
        flMypageWrite = findViewById(R.id.flMyPageWriteContainer)
        ivProfileImg = findViewById(R.id.ivMyPageProfile)
        ivEdit = findViewById(R.id.ivMyPageEdit)
        tvNickname = findViewById(R.id.tvMyPageNickname)
        ivOpenClose = findViewById(R.id.ivMyPageLock)
        ivFavorite = findViewById(R.id.ivMyPageFavorite)
        tvFeedCnt = findViewById(R.id.tvMyPageFeedCnt)
        tvSubscriptCnt = findViewById(R.id.tvMyPageSubscriptCnt)
        llMyPageBoardContainer = findViewById(R.id.llMyPageBoardContainer)
        llBoardList = findViewById(R.id.llBoardList)
        llBoardContainer = findViewById(R.id.llBoardContainer)

        uiInit()
        boardListInit()
        getUserRecordList(recordType[0])
        getUserDiaryList(false)
        getMadeClassList()
        getJoinedClassList()
        getMadeExperienceList()
        getRequestExperienceList()

        flMypageBack?.setOnClickListener(this)
        flMypageWrite?.setOnClickListener(this)
        ivEdit?.setOnClickListener(this)
        ivFavorite?.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                USER_EDIT -> {
                    userInfo = MyInfoStore.myInfo
                    uiInit()
                    getUserRecordList(recordType[0])
                    getUserDiaryList(false)
                }
            }
        }
    }

    private fun uiInit(){
        clProfileBg?.setBackgroundResource(R.drawable.default_bg)

        if(userInfo!!.profile_img.equals(getString(R.string.default_text))){
            ivProfileImg?.setImageResource(R.drawable.default_profile)
        }else{
            var bitmap = ImageCacheUtils.getBitmap(userInfo!!.profile_img)

            if(bitmap == null) {
                Glide.with(this)
                        .asBitmap()
                        .load(CommonUtils.getThumbnailLinkPath(this, userInfo!!.profile_img))
                        .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                        .into(object : ViewTarget<ImageView, Bitmap>(ivProfileImg!!){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                ImageCacheUtils.putBitmap(userInfo!!.profile_img, resource)
                                ivProfileImg?.setImageBitmap(resource)
                            }
                        })
            }else{
                ivProfileImg?.setImageBitmap(bitmap)
            }
        }

        if(userInfo!!.nickname == null || userInfo!!.nickname.equals(""))
            tvNickname?.text = getString(R.string.no_name)
        else
            tvNickname?.text = userInfo!!.nickname

        svMain?.viewTreeObserver!!.addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
            var scroll = svMain!!.scrollY

            if(scroll > resources.displayMetrics.density * 240)
                scroll = (resources.displayMetrics.density * 240).toInt()

            llMyPageBoardContainer!!.translationY = -scroll.toFloat()
        })
    }

    private fun boardListInit(){
        var boardList = ArrayList<String>()
        boardList.add(getString(R.string.board_mypage_record))
        boardList.add(getString(R.string.board_mypage_activity_record))
        boardList.add(getString(R.string.board_mypage_myclass))
        boardList.add(getString(R.string.board_mypage_registered_class))
        boardList.add(getString(R.string.board_mypage_my_experience))
        boardList.add(getString(R.string.board_mypage_apply_experience))

        when(boardList.size){
            2 -> boardViewLeftMargin = resources.getDimension(R.dimen.pan_left_margin_list_size_2)
            3 -> boardViewLeftMargin = resources.getDimension(R.dimen.pan_left_margin_list_size_3)
            4 -> boardViewLeftMargin = resources.getDimension(R.dimen.pan_left_margin_list_size_4)
            else -> boardViewLeftMargin = resources.getDimension(R.dimen.pan_left_margin_list_size_4_more)
        }

        for(board in boardList){
            var tab = CustomTabView(this)
            tab.setTitle(board)
            tab.setTabColor(R.color.gray, R.color.mainColor, R.color.transparent, R.color.mainColor)
            boardViewList.add(tab)

            tab.setOnClickListener(View.OnClickListener { view ->
                tab.setTabSelected(true)

                for(other in boardViewList){
                    if(tab != other)
                        other.setTabSelected(false)
                }

                curBoard = board

                when(board){
                    getString(R.string.board_mypage_record) -> setActivityRecordList(recordList)
                    getString(R.string.board_mypage_activity_record) -> setActivityDiaryList(diaryList)
                    getString(R.string.board_mypage_myclass) -> setMadeClassList()
                    getString(R.string.board_mypage_registered_class) -> setJoinedClassList()
                    getString(R.string.board_mypage_my_experience) -> setMadeExpList()
                    getString(R.string.board_mypage_apply_experience) -> setJoinExpList()
                }

                sendAppEvent("마이페이지_보드_$board")
            })

            var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.leftMargin = boardViewLeftMargin.toInt()
            tab.layoutParams = params
            llBoardList?.addView(tab)
        }

        (llBoardList?.getChildAt(0) as CustomTabView).performClick()
    }

    private fun getUserRecordList(type: String) {
        var apiService = ApiManager.getInstance().apiService
        apiService.getUserRecord(userInfo!!.uuid!!, type, recordPage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        recordPage = result.result!!.page
                        if (recordPage == 1)
                            recordList = result.result!!.data!!.toCollection(ArrayList<UserRecordData>())
                        else {
                            if (result.result!!.data != null) {
                                for (record in result.result!!.data!!) {
                                    recordList.add(record)
                                }
                            }
                        }
                        setActivityRecordList(result.result!!.data!!.toCollection(ArrayList<UserRecordData>()))
                    }else{
                        Log.e("RECORD_LIST", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_load_record), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("RECORD_LIST", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_load_record), Toast.LENGTH_SHORT).show()
                })
    }

    private fun getUserDiaryList(isAddUi: Boolean) {
        var apiService = ApiManager.getInstance().apiService
        apiService.getUserDiary(userInfo!!.uuid!!, diaryPage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        diaryPage = result.result!!.page
                        diaryList.addAll(result.result!!.data!!.toCollection(ArrayList()))

                        if(isAddUi)
                            setActivityDiaryList(diaryList)
                    }else{
                        Log.e("DIARY_LIST", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_load_record), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("DIARY_LIST", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_load_record), Toast.LENGTH_SHORT).show()
                })
    }


    private fun setActivityRecordList(dataList: ArrayList<UserRecordData>) {
        if(llBoardContainer!!.childCount > 0)
            llBoardContainer?.removeAllViews()

        for(record in dataList){
            var recordAnnouceView = UserRecordAnnounceView(this, this, this)

            llBoardContainer?.addView(recordAnnouceView)
            recordAnnouceView.setData(record)
        }
    }

    private fun setActivityDiaryList(dataList: ArrayList<UserDiaryData>) {
        if(llBoardContainer!!.childCount > 0)
            llBoardContainer?.removeAllViews()

        for(record in dataList){
            var recordAnnouceView = UserRecordAnnounceView(this, this, this)

            llBoardContainer?.addView(recordAnnouceView)
            recordAnnouceView.setData(record)
        }
    }

    private fun setMadeClassList(){
        if(llBoardContainer!!.childCount > 0)
            llBoardContainer?.removeAllViews()

        for(classData in madeClassList) {
            var madeClass = MypageClassItemView(this)
            madeClass.setData(classData, getString(R.string.mypage_class_list_type_made))
            llBoardContainer?.addView(madeClass)
        }
    }

    private fun setJoinedClassList() {
        if(llBoardContainer!!.childCount > 0)
            llBoardContainer?.removeAllViews()

        for(classData in joinedClassList) {
            var madeClass = MypageClassItemView(this)
            madeClass.setData(classData, getString(R.string.mypage_class_list_type_joined))
            llBoardContainer?.addView(madeClass)
        }
    }

    private fun setMadeExpList(){
        if(llBoardContainer!!.childCount > 0)
            llBoardContainer?.removeAllViews()

        for(expData in madeExpList) {
            var madeClass = MypageExpItemView(this)
            madeClass.setData(expData)
            llBoardContainer?.addView(madeClass)
        }
    }

    private fun setJoinExpList(){
        if(llBoardContainer!!.childCount > 0)
            llBoardContainer?.removeAllViews()

        var paymentInfo = TextView(this)
        var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        paymentInfo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        paymentInfo.setTextColor(ContextCompat.getColor(this, R.color.white))
        paymentInfo.setPadding((16 * resources.displayMetrics.density).toInt(), (16 * resources.displayMetrics.density).toInt(),
                (16 * resources.displayMetrics.density).toInt(), (16 * resources.displayMetrics.density).toInt())
        paymentInfo.setBackgroundColor(ContextCompat.getColor(this, R.color.mainLightColor))
        paymentInfo.setSingleLine(true)
        //paymentInfo.ellipsize = TextUtils.TruncateAt.MARQUEE
        //paymentInfo.marqueeRepeatLimit = 1000
        paymentInfo.text = getString(R.string.payment_bank_info)
        //paymentInfo.isSelected = true
        llBoardContainer?.addView(paymentInfo)

        for(expData in requestExpList) {
            var joinedClass = MypageExpItemView(this)
            joinedClass.setNoMember()
            joinedClass.setData(expData)
            llBoardContainer?.addView(joinedClass)
        }
    }

    private fun createWriteTypeDialog(){
        var write = Dialog(this)
        write.window.requestFeature(Window.FEATURE_NO_TITLE)
        write.setContentView(R.layout.dialog_record_write_type)
        write.window.setLayout((resources.displayMetrics.widthPixels * 0.8f).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        write.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var tvClass = write.findViewById<TextView>(R.id.tvRecordWriteTypeClass)
        var tvClub = write.findViewById<TextView>(R.id.tvRecordWriteTypeClub)
        var tvCareer = write.findViewById<TextView>(R.id.tvRecordWriteTypeCareer)
        var tvContest = write.findViewById<TextView>(R.id.tvRecordWriteTypeContest)
        var tvVolunteer = write.findViewById<TextView>(R.id.tvRecordWriteTypeVolunteer)
        var tvBehavior = write.findViewById<TextView>(R.id.tvRecordWriteTypeBehavior)
        var tvReading = write.findViewById<TextView>(R.id.tvRecordWriteTypeReading)
        var tvDiary = write.findViewById<TextView>(R.id.tvRecordWriteTypeDiary)

        var clickListener = object : View.OnClickListener{
            override fun onClick(v: View?) {
                var kind: String? = ""

                when(v?.id){
                    R.id.tvRecordWriteTypeClass -> kind = getString(R.string.user_record_type_class)
                    R.id.tvRecordWriteTypeClub -> kind = getString(R.string.user_record_type_club)
                    R.id.tvRecordWriteTypeCareer -> kind = getString(R.string.user_record_type_career)
                    R.id.tvRecordWriteTypeContest -> kind = getString(R.string.user_record_type_contest)
                    R.id.tvRecordWriteTypeVolunteer -> kind = getString(R.string.user_record_type_volunteer)
                    R.id.tvRecordWriteTypeBehavior-> kind = getString(R.string.user_record_type_behavior)
                    R.id.tvRecordWriteTypeReading -> kind = getString(R.string.user_record_type_reading)
                }

                var intent = Intent(applicationContext, RecordActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_record_kind), kind)
                startActivityForResult(intent, USER_EDIT)
                write.dismiss()
            }
        }

        tvClass.setOnClickListener(clickListener)
        tvClub.setOnClickListener(clickListener)
        tvCareer.setOnClickListener(clickListener)
        tvContest.setOnClickListener(clickListener)
        tvVolunteer.setOnClickListener(clickListener)
        tvBehavior.setOnClickListener(clickListener)
        tvReading.setOnClickListener(clickListener)
        tvDiary.setOnClickListener(clickListener)

        write.show()
    }

    private fun getMadeClassList() {
        var apiService = ApiManager.getInstance().apiService
        apiService.getMadeClassList(madeClassPage + 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        madeClassPage = result.result!!.page
                        madeClassList.addAll(result.result!!.data!!.toCollection(ArrayList()))
                    }else{
                        Log.e("GET_MADE_CLASS", result.error)
                        Toast.makeText(applicationContext, getString(R.string.fail_to_get_made_class_list), Toast.LENGTH_SHORT).show()
                    }
                }, { err ->
                    Log.e("GET_MADE_CLASS", err.toString())
                    Toast.makeText(applicationContext, getString(R.string.fail_to_get_made_class_list), Toast.LENGTH_SHORT).show()
                })
    }

    private fun getJoinedClassList(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getJoinedClassList(joinedClassPage + 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        joinedClassPage = result.result!!.page
                        joinedClassList.addAll(result.result!!.data!!.toCollection(ArrayList()))
                    }else{
                        Log.e("GET_JOINED_CLASS", result.error)
                        Toast.makeText(applicationContext, getString(R.string.fail_to_get_registered_class_list), Toast.LENGTH_SHORT).show()
                    }
                }, { err ->
                    Log.e("GET_JOINED_CLASS", err.toString())
                    Toast.makeText(applicationContext, getString(R.string.fail_to_get_registered_class_list), Toast.LENGTH_SHORT).show()
                })
    }

    private fun getMadeExperienceList() {
        var apiService = ApiManager.getInstance().apiService
        apiService.getMadeExperienceList(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        madeExpList = result!!.result!!.data!!.toCollection(ArrayList())
                    }
                }, { err ->

                })
    }

    private fun getRequestExperienceList() {
        var apiService = ApiManager.getInstance().apiService
        apiService.getRequestExperienceList(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y")
                        requestExpList = result!!.result!!.data!!.toCollection(ArrayList())
                }, { err ->

                })
    }
}
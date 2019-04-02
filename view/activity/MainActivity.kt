package vdream.vd.com.vdream.view.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.LoaderManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.animation.*
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.json.JSONObject
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.UpdateUserData
import vdream.vd.com.vdream.data.UserInfoData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils
import vdream.vd.com.vdream.view.component.CustomScrollView
import vdream.vd.com.vdream.view.component.CustomTabView
import vdream.vd.com.vdream.view.component.TitledSwitch
import vdream.vd.com.vdream.view.dialog.QuestionAddDialog
import vdream.vd.com.vdream.view.fragment.*
import java.util.*

/**
 * Created by SHINLIB on 2018-03-19.
 */
class MainActivity: BaseActivity(), View.OnClickListener {
    var receiveIdx = -1
    companion object {
        var locationManager: LocationManager? = null
        val USER_EDIT = 0
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flMenuContainer -> {
                sendAppEvent("메인화면_헤더_메뉴")
                dlMain!!.openDrawer(Gravity.LEFT)
            }
            R.id.flHomeContainer -> {
                sendAppEvent("메인화면_판_홈")
                for(pan in panArray){
                    pan.setTabSelected(false)
                }
                setFragmentToContainer(getString(R.string.pan_main))
            }
            R.id.fabRecord -> {
                if(clFloatingBtnGroup!!.visibility == View.VISIBLE) {
                    sendAppEvent("메인화면_플로팅버튼그룹_숨기기")
                    floatingButtonGroupHide()
                }else {
                    sendAppEvent("메인화면_플로팅버튼그룹_열기")
                    floatingButtonGroupShow()
                }
            }
            R.id.flSearchContainer -> {
                sendAppEvent("메인화면_헤더_검색")
                var intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
            R.id.clRecordBtnGroup -> {
                sendAppEvent("메인화면_생기부작성버튼클릭")
                if(userInfo!!.nickname == null || userInfo!!.nickname.equals("")){
                    userInfoSetDialog()
                    return
                }

                createWriteTypeDialog()
                floatingButtonGroupHide()
            }
            R.id.clCreateClassBtnGroup -> {
                sendAppEvent("메인화면_클래스생성버튼클릭")
                var intent = Intent(this, ClassRegisterActivity::class.java)
                startActivity(intent)
                floatingButtonGroupHide()
            }

            R.id.ivSideMenuProfileImage -> {sendAppEvent("메인화면_사이드메뉴_프로필이미지")}
            R.id.clSideMenuProfile -> {sendAppEvent("메인화면_사이드메뉴_백그라운드이미지")}
            R.id.ivSideMenuClose -> {
                sendAppEvent("메인화면_사이드메뉴_닫기버튼")
                dlMain!!.closeDrawer(Gravity.LEFT)
            }
            R.id.ivSideMenuEdit -> {
                sendAppEvent("메인화면_사이드메뉴_유저정보수정")
                var intent = Intent(this, UserInfoSetActivity::class.java)
                startActivityForResult(intent, USER_EDIT)
            }
            R.id.llSideMenuNewWrite -> {
                sendAppEvent("메인화면_사이드메뉴_생기부작성")
                if(userInfo!!.nickname == null || userInfo!!.nickname.equals("")){
                    userInfoSetDialog()
                    return
                }

                createWriteTypeDialog()
                floatingButtonGroupHide()
            }
            R.id.llSideMenuMypage -> {
                sendAppEvent("메인화면_사이드메뉴_마이페이지")
                var intent = Intent(this, MypageAcitivity::class.java)
                startActivity(intent)
            }
            R.id.clSideMenuCustomerSupport -> {
                sendAppEvent("메인화면_사이드메뉴_문의하기")
                var questionDialog = QuestionAddDialog(this)
                questionDialog.show()
            }
            R.id.tvSideMenuClause -> {
                var intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_url), getString(R.string.user_clause_url))
                startActivity(intent)
            }
            R.id.tvSideMenuLogout -> {
                v?.isClickable = false
                logout()
            }
        }
    }

    var dlMain: DrawerLayout? = null
    var svMain: CustomScrollView? = null
    var flMenu: FrameLayout? = null
    var flSearch: FrameLayout? = null
    var flHomeContainer: FrameLayout? = null
    var hsvPan: HorizontalScrollView? = null
    var llPanContainer: LinearLayout? = null
    var clPanTab: ConstraintLayout? = null
    var fabRecord: ImageView? = null
    var clFloatingBtnGroup: ConstraintLayout? = null
    var clRecordButton: ConstraintLayout? = null
    var clCreateClass: ConstraintLayout ? = null

    var clSideMenuProfile: ConstraintLayout? = null
    var ivSideMenuProfile: ImageView? = null
    var ivSideMenuEdit: ImageView? = null
    var ivSideMenuClose: ImageView? = null
    var ivSideMenuNoti: ImageView? = null
    var tvSideMenuNick: TextView? = null
    var tvSideMenuSchool: TextView? = null
    var tvSideMenuFeedLikeCnt: TextView? = null
    var tvSideMenuFollowingCnt: TextView? = null
    var tvSideMenuFollowCnt: TextView? = null
    var llSideMenuNewWrite: LinearLayout? = null
    var llSideMenuMypage: LinearLayout? = null
    var llSideMenuConnectChild: LinearLayout? = null
    var clSideMenuPrivateSetting: ConstraintLayout? = null
    var clSideMenuAppSetting: ConstraintLayout? = null
    var clSideMenuCustomerSupport: ConstraintLayout? = null
    var tswPush: TitledSwitch? = null
    var tvClause: TextView? = null
    var tvLogout: TextView? = null

    var panArray = ArrayList<CustomTabView>()
    var panLeftMargin = 0f
    var userInfo: UserInfoData? = null
    var backPressFirstTime: Long = 0

    var homefragment: HomeFragment? = null
    var companyFragment: CompanyOnMapFragment? = null
    var classFragment: ClassListFragment? = null
    var newsFragment: NewsFragment? = null
    var recordFragment: StudentRecordFragment? = null
    var volunteerFragment: VolunteerFragment? = null
    var expFragment: ExperienceFragment? = null
    var airbusnaFragment: AirBusanFragment? = null
    var strongSmallCompanyFragment: StrongSmallCompanyFragment? = null

    override fun onCreate(instance: Bundle?) {
        super.onCreate(instance)
        Fabric.with(this, Crashlytics())
        receiveIdx = intent.getIntExtra(getString(R.string.intent_key_name_index), -1)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        setContentView(R.layout.activity_main)

        if(receiveIdx != -1){
            moveToReceiveClass()
        }

        dlMain = findViewById(R.id.dlMain)
        svMain = findViewById(R.id.svMain)
        flMenu = findViewById(R.id.flMenuContainer)
        flSearch = findViewById(R.id.flSearchContainer)
        flHomeContainer = findViewById(R.id.flHomeContainer)
        hsvPan = findViewById(R.id.hsvPanList)
        llPanContainer = findViewById(R.id.llPanContainer)
        clPanTab = findViewById(R.id.clPanTab)
        fabRecord = findViewById(R.id.fabRecord)
        clFloatingBtnGroup = findViewById(R.id.clFloatingButtonGroup)
        clRecordButton = findViewById(R.id.clRecordBtnGroup)
        clCreateClass = findViewById(R.id.clCreateClassBtnGroup)

        //Side Menu Ui
        clSideMenuProfile = findViewById(R.id.clSideMenuProfile)
        ivSideMenuProfile = findViewById(R.id.ivSideMenuProfileImage)
        ivSideMenuEdit = findViewById(R.id.ivSideMenuEdit)
        ivSideMenuClose = findViewById(R.id.ivSideMenuClose)
        ivSideMenuNoti = findViewById(R.id.ivSideMenuNotification)
        tvSideMenuNick = findViewById(R.id.tvSideMenuNickname)
        tvSideMenuSchool = findViewById(R.id.tvSideMenuSchool)
        tvSideMenuFeedLikeCnt = findViewById(R.id.tvSideMenuFeedLikeCnt)
        tvSideMenuFollowingCnt = findViewById(R.id.tvSideMenuFollowimgCnt)
        tvSideMenuFollowCnt = findViewById(R.id.tvSideMenuFollowCnt)
        llSideMenuNewWrite = findViewById(R.id.llSideMenuNewWrite)
        llSideMenuMypage = findViewById(R.id.llSideMenuMypage)
        llSideMenuConnectChild = findViewById(R.id.llSideMenuConnectChild)
        clSideMenuPrivateSetting = findViewById(R.id.clSideMenuPrivateSetting)
        clSideMenuAppSetting = findViewById(R.id.clSideMenuAppSetting)
        clSideMenuCustomerSupport = findViewById(R.id.clSideMenuCustomerSupport)
        tswPush = findViewById(R.id.tswSideMenuPushEnable)
        tvClause = findViewById(R.id.tvSideMenuClause)
        tvLogout = findViewById(R.id.tvSideMenuLogout)

        panListInit()

        setFragmentToContainer(getString(R.string.pan_main))

        svMain?.viewTreeObserver!!.addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
            var scroll = svMain!!.scrollY

            if(scroll > resources.displayMetrics.density * 49)
                scroll = (resources.displayMetrics.density * 49).toInt()

            clPanTab!!.translationY = -scroll.toFloat()

            if(!svMain!!.canScrollVertically(1)){
                if(newsFragment != null && newsFragment!!.isVisible)
                    newsFragment!!.getNewsFeed()
            }
        })

        tswPush?.setTextSize(14f)
        tswPush?.setTitle(getString(R.string.setting_push))
        tswPush?.setCheckChangedListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                setPushEnable("Y")
            }else{
                setPushEnable("N")
            }
        })

        flMenu?.setOnClickListener(this)
        flHomeContainer?.setOnClickListener(this)
        fabRecord?.setOnClickListener(this)
        flSearch?.setOnClickListener(this)
        clRecordButton?.setOnClickListener(this)
        clCreateClass?.setOnClickListener(this)
        tvClause?.setOnClickListener(this)
        tvLogout?.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        sideMenuInit()
    }

    override fun onBackPressed() {
        if(clFloatingBtnGroup!!.visibility == View.VISIBLE) {
            floatingButtonGroupHide()
            return
        }

        var curPressTime = Date().time

        if(backPressFirstTime == 0L){
            backPressFirstTime = curPressTime
            Toast.makeText(this, getString(R.string.app_finish_notification), Toast.LENGTH_SHORT).show()
        }else{
            var term = curPressTime - backPressFirstTime
            if(term < 3000)
                finish()
            else
                backPressFirstTime = 0
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                USER_EDIT -> {
                    sideMenuInit()
                }
            }
        }
    }

    private fun setFragmentToContainer(type: String){
        var fr: Fragment? = null
        var tr = supportFragmentManager.beginTransaction()

        fabRecord?.visibility = View.VISIBLE
        when(type){
            getString(R.string.pan_main) -> {
                if(homefragment == null)
                    homefragment = HomeFragment()

                fr = homefragment
            }
            getString(R.string.pan_company) -> {
                if(companyFragment == null)
                    companyFragment = CompanyOnMapFragment()

                fr = companyFragment
                fabRecord?.visibility = View.GONE
            }
            getString(R.string.pan_class) -> {
                if(classFragment == null)
                    classFragment = ClassListFragment()

                fr = classFragment
            }
            getString(R.string.pan_news) -> {
                if(newsFragment == null)
                    newsFragment = NewsFragment()

                fr = newsFragment
            }
            getString(R.string.pan_student_record) -> {
                if(recordFragment == null)
                    recordFragment = StudentRecordFragment()

                fr = recordFragment
            }
            getString(R.string.pan_volunteer) -> {
                if(volunteerFragment == null)
                    volunteerFragment = VolunteerFragment()

                fr = volunteerFragment
            }
            getString(R.string.pan_experience) -> {
                if(expFragment == null)
                    expFragment = ExperienceFragment()

                fr = expFragment
            }
            getString(R.string.pan_airbusan) -> {
                if(airbusnaFragment == null)
                    airbusnaFragment = AirBusanFragment()

                fr = airbusnaFragment
            }
            getString(R.string.pan_strong_small) -> {
                if(strongSmallCompanyFragment == null)
                    strongSmallCompanyFragment = StrongSmallCompanyFragment()

                fr = strongSmallCompanyFragment
            }
        }


        tr?.replace(R.id.flFragmentContainer, fr, type)
        tr?.commit()

        if(fr is CompanyOnMapFragment)
            svMain?.setScrollable(false)
        else
            svMain?.setScrollable(true)
    }

    private fun panListInit(){
        var panList = ArrayList<String>()
        panList.add(getString(R.string.news))
        if(ApiManager.getInstance().token != null) {
            panList.add(getString(R.string.student_record))
        }
        panList.add(getString(R.string.strong_small))
        panList.add(getString(R.string.volunteer))
        panList.add(getString(R.string.experience))
        panList.add(getString(R.string.classes))

        //panList.add(getString(R.string.airbusan))

        panLeftMargin = when(panList.size){
            2 -> resources.getDimension(R.dimen.pan_left_margin_list_size_2)
            3 -> resources.getDimension(R.dimen.pan_left_margin_list_size_3)
            4 -> resources.getDimension(R.dimen.pan_left_margin_list_size_4)
            else -> resources.getDimension(R.dimen.pan_left_margin_list_size_4_more)
        }

        for(idx in 0..panList.lastIndex){
            var pan = panList[idx]
            var tab = CustomTabView(this)
            tab.setTitle(pan)
            tab.setTabColor(R.color.gray, R.color.mainColor, R.color.transparent, R.color.mainColor)
            panArray.add(tab)

            /*if(pan == getString(R.string.airbusan)){
                tab.setImageInPlaceOfText(R.drawable.img_airbusan_ai_01)
            }*/

            tab.setOnClickListener(View.OnClickListener { view ->
                tab.setTabSelected(true)

                for(other in panArray){
                    if(tab != other)
                        other.setTabSelected(false)
                }

                when(pan){
                    getString(R.string.news) -> {
                        sendAppEvent("메인화면_판_뉴스")
                        setFragmentToContainer(getString(R.string.pan_news))
                    }
                    getString(R.string.companies) -> {
                        sendAppEvent("메인화면_판_체험처")
                        setFragmentToContainer(getString(R.string.pan_company))
                    }
                    getString(R.string.classes) -> {
                        sendAppEvent("메인화면_판_클래스")
                        setFragmentToContainer(getString(R.string.pan_class))
                    }
                    getString(R.string.experience) -> {
                        sendAppEvent("메인화면_판_클래스")
                        setFragmentToContainer(getString(R.string.pan_experience))
                    }
                    getString(R.string.student_record) -> {
                        sendAppEvent("메인화면_판_생기부")
                        setFragmentToContainer(getString(R.string.pan_student_record))
                    }
                    getString(R.string.volunteer) -> {
                        sendAppEvent("메인화면_판_자원봉사")
                        setFragmentToContainer(getString(R.string.pan_volunteer))
                    }
                    getString(R.string.airbusan) -> {
                        sendAppEvent("메인화면_판_에어부산")
                        setFragmentToContainer(getString(R.string.pan_airbusan))
                    }
                    getString(R.string.strong_small) -> {
                        sendAppEvent("메인화면_판_강소기업")
                        setFragmentToContainer(getString(R.string.pan_strong_small))
                    }
                }
            })

            var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.leftMargin = panLeftMargin.toInt()
            if(idx == panList.lastIndex)
                params.rightMargin = resources.getDimension(R.dimen.pan_left_margin_list_size_4_more).toInt()
            tab.layoutParams = params
            llPanContainer?.addView(tab)
        }
    }

    private fun floatingButtonGroupShow(){
        clFloatingBtnGroup?.visibility = View.VISIBLE
        var recordAni = AnimationUtils.loadAnimation(this, R.anim.record_button_show)
        recordAni.interpolator = OvershootInterpolator()
        clRecordButton?.startAnimation(recordAni)

        var classAni = AnimationUtils.loadAnimation(this, R.anim.class_button_show)
        classAni.interpolator = OvershootInterpolator()
        clCreateClass?.startAnimation(classAni)

        var rotAni = RotateAnimation(0f, 45f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotAni.interpolator = OvershootInterpolator()
        rotAni.fillAfter = true
        rotAni.duration = 300

        fabRecord?.startAnimation(rotAni)
    }

    private fun floatingButtonGroupHide(){
        var recordAni = AnimationUtils.loadAnimation(this, R.anim.record_button_hide)
        recordAni.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                clFloatingBtnGroup?.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {}

        })
        clRecordButton?.startAnimation(recordAni)

        var classAni = AnimationUtils.loadAnimation(this, R.anim.class_button_hide)
        clCreateClass?.startAnimation(classAni)

        var rotAni = RotateAnimation(45f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotAni.interpolator = OvershootInterpolator()
        rotAni.fillAfter = true
        rotAni.duration = 300

        fabRecord?.startAnimation(rotAni)
    }

    private fun sideMenuInit(){
        userInfo = MyInfoStore.myInfo

        if(userInfo == null)
            return

        if(userInfo!!.background_img == null || userInfo!!.background_img.equals(getString(R.string.default_text))){
            clSideMenuProfile?.setBackgroundResource(R.drawable.default_bg)
        }else{
            Glide.with(this)
                    .load(R.drawable.default_bg)
                    .into(object : ViewTarget<ConstraintLayout, Drawable>(clSideMenuProfile!!){
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            clSideMenuProfile?.background = resource
                        }
                    })
        }

        if(userInfo!!.profile_img == null || userInfo!!.profile_img.equals(getString(R.string.default_text))){
            ivSideMenuProfile?.setImageResource(R.drawable.default_profile)
        }else{
            var bitmap = ImageCacheUtils.getBitmap(userInfo!!.profile_img)

            if(bitmap == null) {
                Glide.with(this)
                        .asBitmap()
                        .load(CommonUtils.getThumbnailLinkPath(this, userInfo!!.profile_img))
                        .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                        .into(object : ViewTarget<ImageView, Bitmap>(ivSideMenuProfile!!){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                ImageCacheUtils.putBitmap(userInfo!!.profile_img, resource)
                                ivSideMenuProfile?.setImageBitmap(resource)
                            }
                        })
            }else{
                ivSideMenuProfile!!.setImageBitmap(bitmap)
            }
        }

        ivSideMenuClose?.setOnClickListener(this)
        ivSideMenuNoti?.setOnClickListener(this)

        if(userInfo!!.nickname == null || userInfo!!.nickname.equals(""))
            tvSideMenuNick?.text = getString(R.string.no_name)
        else
            tvSideMenuNick?.text = userInfo!!.nickname

        /*if(userInfo!!.school == null || userInfo!!.school.equals(""))
            tvSideMenuSchool?.text = "no_school"
        else
            tvSideMenuSchool?.text = userInfo!!.school*/

        ivSideMenuEdit?.setOnClickListener(this)
        ivSideMenuProfile?.setOnClickListener(this)
        clSideMenuProfile?.setOnClickListener(this)
        llSideMenuNewWrite?.setOnClickListener(this)
        llSideMenuMypage?.setOnClickListener(this)
        llSideMenuConnectChild?.setOnClickListener(this)
        clSideMenuPrivateSetting?.setOnClickListener(this)
        clSideMenuAppSetting?.setOnClickListener(this)
        clSideMenuCustomerSupport?.setOnClickListener(this)
        /*
        var tvSideMenuFeedLikeCnt: TextView? = null
        var tvSideMenuFollowingCnt: TextView? = null
        var tvSideMenuFollowCnt: TextView? = null*/
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

        var clickListener = View.OnClickListener { v ->
            var kind: String? = ""

            when(v?.id){
                R.id.tvRecordWriteTypeClass -> {kind = getString(R.string.user_record_type_class)}
                R.id.tvRecordWriteTypeClub -> {kind = getString(R.string.user_record_type_club)}
                R.id.tvRecordWriteTypeCareer -> {kind = getString(R.string.user_record_type_career)}
                R.id.tvRecordWriteTypeContest -> {kind = getString(R.string.user_record_type_contest)}
                R.id.tvRecordWriteTypeVolunteer -> {kind = getString(R.string.user_record_type_volunteer)}
                R.id.tvRecordWriteTypeBehavior-> {kind = getString(R.string.user_record_type_behavior)}
                R.id.tvRecordWriteTypeReading -> {kind = getString(R.string.user_record_type_reading)}
            }

            sendAppEvent("메인화면_생기부작성_" + (v as TextView).text.toString())

            var intent = Intent(applicationContext, RecordActivity::class.java)
            intent.putExtra(getString(R.string.intent_key_name_record_kind), kind)
            startActivityForResult(intent, USER_EDIT)
            write.dismiss()
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

    private fun userInfoSetDialog() {
        var alert = AlertDialog.Builder(this)
        alert.setTitle(getString(R.string.update_alert_title))
        alert.setMessage(getString(R.string.input_additional_info_before_write))
        alert.setPositiveButton(getString(R.string.set_userInfo)) { dialog, which ->
            var intent = Intent(this, UserInfoSetActivity::class.java)
            startActivity(intent)
        }
        alert.setNegativeButton(getString(R.string.update_alert_negative_button_close), null)
        alert.create().show()
    }

    private fun moveToReceiveClass(){
        var intent = Intent(this, ClassDetailAcitivity::class.java)
        intent.putExtra(getString(R.string.intent_key_name_index), receiveIdx)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        ImageCacheUtils.cacheClear()
    }

    open fun requestScrollTo(yPos: Int){
        var containerPos = intArrayOf(0, 0)
        llPanContainer!!.getLocationOnScreen(containerPos)
        var moveHeight = yPos - (containerPos[1] + (resources.displayMetrics.density*48).toInt())
        svMain?.smoothScrollTo(0, yPos)
    }

    private fun setPushEnable(isEnable: String){
        var requestData = UpdateUserData()
        requestData.is_pushable = isEnable
        var apiService = ApiManager.getInstance().apiService
        apiService.updateUserInfo(requestData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({result ->
                    if(result.status == "Y"){
                        Toast.makeText(this, getString(R.string.user_info_modified), Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, getString(R.string.fail_to_user_info_set), Toast.LENGTH_SHORT).show()
                        Log.e("PUSH_ENABLE", result.error)
                    }
                }, {err ->
                    Toast.makeText(this, getString(R.string.fail_to_user_info_set), Toast.LENGTH_SHORT).show()
                    Log.e("PUSH_ENABLE", err.message)
                })
    }

    private fun logout() {
        var apiService = ApiManager.getInstance().apiService
        apiService.logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({result ->
                    tvLogout?.isClickable = true
                    if(result.status == "Y"){
                        Toast.makeText(this, getString(R.string.success_to_logout), Toast.LENGTH_SHORT).show()
                        var intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }else{
                        Log.e("LOG_OUT", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_logout), Toast.LENGTH_SHORT)
                    }
                }, {err ->
                    tvLogout?.isClickable = true
                    Log.e("LOG_OUT", err.message)
                    Toast.makeText(this, getString(R.string.fail_to_logout), Toast.LENGTH_SHORT)
                })
    }
}
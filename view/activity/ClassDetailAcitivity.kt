package vdream.vd.com.vdream.view.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropCircleTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.ClassDetailData
import vdream.vd.com.vdream.data.FeedDetailData
import vdream.vd.com.vdream.data.ImageData
import vdream.vd.com.vdream.interfaces.AnnounceChangeCallback
import vdream.vd.com.vdream.interfaces.CommentClickCallback
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils
import vdream.vd.com.vdream.view.component.AlbumLayoutView
import vdream.vd.com.vdream.view.component.ClassAnnounceView
import vdream.vd.com.vdream.view.component.ClassExperienceAnnounceVIew
import vdream.vd.com.vdream.view.component.CustomTabView
import kotlin.collections.ArrayList

class ClassDetailAcitivity: YouTubeBaseActivity(), View.OnClickListener, CommentClickCallback, AnnounceChangeCallback {
    override fun requestUpdate(type: String, idx: Int) {
        var oldData: FeedDetailData? = null
        var intent: Intent? = null

        if(type == getString(R.string.board_type_of_feed_board)){
            for(data in feedList){
                if(data.idx == idx) {
                    oldData = data
                    break
                }
            }

            intent = Intent(this, WriteFeedActivity::class.java)
            intent.putExtra(getString(R.string.intent_key_name_is_notice), "N")
            sendAppEvent("클래스상세_피드_수정")
        }else if(type == getString(R.string.board_type_of_feed_exp)){
            for(data in expList){
                if(data.idx == idx) {
                    oldData = data
                    break
                }
            }

            intent = Intent(this, WriteExperienceActivity::class.java)
            sendAppEvent("클래스상세_체험_수정")
        }else {
            for(data in detailData!!.notices!!.data!!){
                if(data.idx == idx) {
                    oldData = data
                    break
                }
            }

            intent = Intent(this, WriteFeedActivity::class.java)
            intent.putExtra(getString(R.string.intent_key_name_is_notice), "Y")
            sendAppEvent("클래스상세_공자_수정")
        }

        intent.putExtra(getString(R.string.intent_key_name_index), classIdx)
        intent.putExtra(getString(R.string.intent_key_name_feeddata), oldData)
        startActivityForResult(intent, CLASS_EDIT)
    }

    override fun announceDeleted() {
        getClassDetailInfo(classIdx)
        sendAppEvent("클래스상세_게시물삭제")
    }

    override fun onClick(idx: Int) {
        sendAppEvent("클래스상세_댓글")
        for(feedData in detailData!!.feeds!!.data!!){
            if(idx == feedData.idx){
                var intent = Intent(this, CommentActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_index), classIdx)
                intent.putExtra(getString(R.string.intent_key_name_article_index), feedData.idx)
                intent.putExtra(getString(R.string.intent_key_name_reference_index), idx)
                startActivityForResult(intent, CLASS_EDIT)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flDetailBackContainer -> {
                sendAppEvent("클래스상세_헤더_백버튼")
                finish()
            }
            R.id.flDetailWriteContainer -> {
                if(detailData!!.is_joined.equals("N")){
                    sendAppEvent("클래스상세_글쓰기_비가입")
                    Toast.makeText(this, getString(R.string.join_class_before_write), Toast.LENGTH_SHORT).show()
                    return
                }

                if(MyInfoStore.myInfo!!.nickname == null || MyInfoStore.myInfo!!.nickname.equals("")){
                    sendAppEvent("클래스상세_글쓰기_회원정보_미기입")
                    createNoUserInfoAlert()
                    return
                }

                if(detailData!!.is_mine.equals("Y")) {
                    sendAppEvent("클래스상세_글쓰기_마스터")
                    writeTypeDialog()
                }else {
                    sendAppEvent("클래스상세_글쓰기_멤버")
                    var intent = Intent(this, WriteFeedActivity::class.java)
                    intent.putExtra(getString(R.string.intent_key_name_is_notice), "N")
                    intent.putExtra(getString(R.string.intent_key_name_index), classIdx)
                    startActivityForResult(intent, CLASS_EDIT)
                }
            }
            R.id.ivDetailEdit -> {
                sendAppEvent("클래스상세_클래스정보수정")
                var intent = Intent(this, ClassRegisterActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_index), classIdx)
                intent.putExtra(getString(R.string.intent_key_name_classroom), detailData!!.classroom)
                startActivityForResult(intent, CLASS_EDIT)
            }
            R.id.ivDetailFavorite -> {
                ivFavorite?.isClickable = false
                if(detailData!!.is_subscribed == "Y") {
                    sendAppEvent("클래스상세_클래스_좋아요_취소")
                    classSubscribeCancel()
                }else {
                    sendAppEvent("클래스상세_클래스_좋아요")
                    classSubscribe()
                }
            }
        }
    }

    val CLASS_EDIT = 0
    var svMain: ScrollView? = null
    var clProfileBg: ConstraintLayout? = null
    var flDetailBackContainer: FrameLayout? = null
    var tvClassTitle: TextView? = null
    var flDetailWriteContainer: FrameLayout? = null
    var ivProfileImg: ImageView? = null
    var ivEdit: ImageView? = null
    var tvTag: TextView? = null
    var ivOpenClose: ImageView? = null
    var ivFavorite: ImageView? = null
    var tvFeedCnt: TextView? = null
    var tvSubscriptCnt: TextView? = null
    var llClassDetailBoardContainer: LinearLayout? = null
    var llBoardList: LinearLayout? = null
    var llBoardContainer: LinearLayout? = null
    var boardViewList= ArrayList<CustomTabView>()
    var albumLayout: AlbumLayoutView? = null

    var analytics: FirebaseAnalytics? = null
    var classIdx = 0
    var boardViewLeftMargin = 0f
    var curBoard = ""
    var feedPage = 1
    var noticePage = 1
    var expPage = 1
    var albumPage = 1
    var feedList = ArrayList<FeedDetailData>()
    var noticeList = ArrayList<FeedDetailData>()
    var expList = ArrayList<FeedDetailData>()
    var albumList = ArrayList<ImageData>()

    companion object {
        var detailData: ClassDetailData? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_detail)

        svMain = findViewById(R.id.svClassDetailMain)
        clProfileBg = findViewById(R.id.clProfileContainer)
        flDetailBackContainer = findViewById(R.id.flDetailBackContainer)
        tvClassTitle = findViewById(R.id.tvDetailTitle)
        flDetailWriteContainer = findViewById(R.id.flDetailWriteContainer)
        ivProfileImg = findViewById(R.id.ivDetailProfile)
        ivEdit = findViewById(R.id.ivDetailEdit)
        tvTag = findViewById(R.id.tvDetailTag)
        ivOpenClose = findViewById(R.id.ivDetailLock)
        ivFavorite = findViewById(R.id.ivDetailFavorite)
        tvFeedCnt = findViewById(R.id.tvDetailFeedCnt)
        tvSubscriptCnt = findViewById(R.id.tvDetailSubscriptCnt)
        llClassDetailBoardContainer = findViewById(R.id.llClassDetailBoardContainer)
        llBoardList = findViewById(R.id.llBoardList)
        llBoardContainer = findViewById(R.id.llBoardContainer)

        classIdx = intent.getIntExtra(getString(R.string.intent_key_name_index), 13)

        if (classIdx > 0)
            getClassDetailInfo(classIdx)

        boardListInit()

        flDetailBackContainer?.setOnClickListener(this)
        flDetailWriteContainer?.setOnClickListener(this)
        ivEdit?.setOnClickListener(this)
        ivFavorite?.setOnClickListener(this)

        svMain!!.viewTreeObserver.addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
            var scroll = svMain!!.scrollY

            if(scroll > resources.displayMetrics.density * 240)
                scroll = (resources.displayMetrics.density * 240).toInt()

            llClassDetailBoardContainer!!.translationY = -scroll.toFloat()

            if(!svMain!!.canScrollVertically(1)){
                when(curBoard){
                    getString(R.string.board_feed) -> getNextFeeds()
                    getString(R.string.board_notice) -> getNextNotice()
                    getString(R.string.board_album) -> getNextAlbum()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                CLASS_EDIT -> getClassDetailInfo(classIdx)
            }
        }
    }

    private fun boardListInit(){
        var boardList = ArrayList<String>()
        boardList.add(getString(R.string.board_feed))
        boardList.add(getString(R.string.board_notice))
        boardList.add(getString(R.string.board_album))
        boardList.add(getString(R.string.board_experience))

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

                if(curBoard == board)
                    return@OnClickListener

                curBoard = board

                for(other in boardViewList){
                    if(tab != other)
                        other.setTabSelected(false)
                }

                when(curBoard){
                    getString(R.string.board_feed) -> {
                        sendAppEvent("클래스상세_보드_피드")
                        setFeedList()
                    }
                    getString(R.string.board_notice) -> {
                        sendAppEvent("클래스상세_보드_공지")
                        setNoticeList()
                    }
                    getString(R.string.board_album) -> {
                        sendAppEvent("클래스상세_보드_앨범")
                        setAlbumList()
                    }
                    getString(R.string.board_experience) -> {
                        sendAppEvent("클래스상세_보드_체험")
                        setExperienceList()
                    }
                }
            })

            var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.leftMargin = boardViewLeftMargin.toInt()
            tab.layoutParams = params
            llBoardList?.addView(tab)
        }
    }

    private fun getClassDetailInfo(idx: Int){
        var apiService = ApiManager.getInstance().apiService
        apiService.getClassDetailInfo(idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        detailData = result.result!!

                        if (detailData!!.classroom!!.profile_img.equals(getString(R.string.default_text))) {
                            ivProfileImg?.setImageResource(R.drawable.default_profile)
                        } else {
                            var bitmap = ImageCacheUtils.getBitmap(detailData!!.classroom!!.profile_img)

                            if(bitmap == null) {
                                Glide.with(this)
                                        .asBitmap()
                                        .load(CommonUtils.getBigImageLinkPath(this, detailData!!.classroom!!.profile_img))
                                        .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                                        .into(object : ViewTarget<ImageView, Bitmap>(ivProfileImg!!){
                                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                                ImageCacheUtils.putBitmap(detailData!!.classroom!!.profile_img, resource)
                                                ivProfileImg?.setImageBitmap(resource)
                                            }
                                        })
                            }else{
                                ivProfileImg!!.setImageBitmap(bitmap)
                            }
                        }


                        if (detailData!!.classroom!!.background_img.equals(getString(R.string.default_text))) {
                            clProfileBg?.setBackgroundResource(R.drawable.default_bg)
                        } else {
                            var bitmap = ImageCacheUtils.getBitmap(detailData!!.classroom!!.background_img)

                            if(bitmap == null) {
                                Glide.with(this)
                                        .asBitmap()
                                        .load(CommonUtils.getBigImageLinkPath(this, detailData!!.classroom!!.background_img))
                                        .into(object : ViewTarget<ConstraintLayout, Bitmap>(clProfileBg!!) {
                                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                                ImageCacheUtils.putBitmap(detailData!!.classroom!!.background_img, resource)
                                                clProfileBg?.background = BitmapDrawable(resources, resource)
                                            }
                                        })
                            }else{
                                clProfileBg?.background = BitmapDrawable(resources, bitmap)
                            }
                        }

                        if (detailData!!.is_mine.equals("Y"))
                            ivEdit?.visibility = View.VISIBLE

                        tvClassTitle?.text = detailData!!.classroom!!.title
                        tvTag?.text = CommonUtils.convertTagsToString(detailData!!.classroom!!.tags!!)

                        if (detailData!!.classroom!!.is_public.equals("Y"))
                            ivOpenClose?.setImageResource(R.drawable.icon_lock)
                        else
                            ivOpenClose?.setImageResource(R.drawable.icon_unlock)

                        ivOpenClose?.visibility = View.VISIBLE

                        if (detailData!!.classroom!!.like_count > 999) {
                            var lCnt = detailData!!.classroom!!.like_count.toFloat() / 1000f
                            tvFeedCnt?.text = lCnt.toString() + "K"
                        } else {
                            tvFeedCnt?.text = detailData!!.classroom!!.like_count.toString()
                        }

                        if (detailData!!.classroom!!.subscribe_count > 999) {
                            var sCnt = detailData!!.classroom!!.subscribe_count.toFloat() / 1000f
                            tvSubscriptCnt?.text = sCnt.toString() + "K"
                        } else {
                            tvSubscriptCnt?.text = detailData!!.classroom!!.subscribe_count.toString()
                        }

                        if (detailData!!.is_subscribed.equals("Y"))
                            ivFavorite?.setImageResource(R.drawable.icon_follow)

                        if(detailData!!.feeds!!.data!!.isNotEmpty()){
                            feedList = detailData!!.feeds!!.data!!.toCollection(ArrayList())
                            albumList = detailData!!.albums!!.data!!.toCollection(ArrayList())

                            for(data in feedList){
                                if(data.kind == "EXPERIENCE"){
                                    expList.add(data)
                                }else if(data.is_notice == "Y"){
                                    noticeList.add(data)
                                }
                            }
                        }

                        when(curBoard){
                            getString(R.string.board_feed) -> llBoardList?.getChildAt(0)?.performClick()
                            getString(R.string.board_notice) -> llBoardList?.getChildAt(1)?.performClick()
                            getString(R.string.board_album) -> llBoardList?.getChildAt(2)?.performClick()
                            getString(R.string.board_experience) -> llBoardList?.getChildAt(3)?.performClick()
                            else -> llBoardList?.getChildAt(0)?.performClick()
                        }

                        if(detailData!!.is_mine == "N")
                            ivFavorite!!.visibility = View.VISIBLE

                    } else {
                        Log.e("CLASS_DETAIL", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_class_detail_info), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("CLASS_DETAIL", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_class_detail_info), Toast.LENGTH_SHORT).show()
                })
    }

    private fun setFeedList() {
        if(llBoardContainer!!.childCount > 0){
            llBoardContainer?.removeAllViews()
        }

        for(data in feedList) {
            if(data.kind == "BOARD"){
                var announceView = ClassAnnounceView(this, this, this)
                announceView.setData(detailData!!.classroom!!.title, data, classIdx, detailData!!.is_mine)
                llBoardContainer?.addView(announceView)
            }else{
                var expView = ClassExperienceAnnounceVIew(this, this)
                expView.setData(data)
                llBoardContainer?.addView(expView)
            }
        }
    }

    private fun setExperienceList() {
        if (llBoardContainer!!.childCount > 0) {
            llBoardContainer?.removeAllViews()
        }

        for (data in expList) {
            var announceView = ClassExperienceAnnounceVIew(this, this)
            announceView.setData(data)
            llBoardContainer?.addView(announceView)
            announceView.setOnClickListener {
                var intent = Intent(this, ExperienceDetailActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_index), classIdx)
                intent.putExtra(getString(R.string.intent_key_name_feeddata), data)
                startActivity(intent)
            }
        }
    }

    private fun setNoticeList() {
        if(llBoardContainer!!.childCount > 0){
            llBoardContainer?.removeAllViews()
        }

        for(data in noticeList){
            var announceView = ClassAnnounceView(this, this, this)
            announceView.setData(detailData!!.classroom!!.title, data, classIdx, detailData!!.is_mine)

            llBoardContainer?.addView(announceView)
        }
    }

    private fun setAlbumList() {
        if(llBoardContainer!!.childCount > 0){
            llBoardContainer?.removeAllViews()
        }

        if(detailData!!.albums != null){
            if(albumLayout == null)
                albumLayout = AlbumLayoutView(this)

            albumLayout?.setData(albumList)
            llBoardContainer?.addView(albumLayout)
        }
    }

    private fun getNextFeeds(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getNextFeeds(classIdx, feedPage+1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        feedPage = result.result!!.page
                        feedList.addAll(result.result!!.data!!.toCollection(ArrayList()))
                        addFeedsList(result.result!!.data!!)
                    }else{
                        Log.e("NEXT_FEEDS", result.error)
                    }
                }, { error ->
                    Log.e("NEXT_FEEDS", error.toString())
                })
    }

    private fun getNextNotice(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getNextNotices(classIdx, noticePage+1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        noticePage = result.result!!.page
                        noticeList.addAll(result.result!!.data!!.toCollection(ArrayList()))
                        addFeedsList(result.result!!.data!!)
                    }else{
                        Log.e("NEXT_FEEDS", result.error)
                    }
                }, { error ->
                    Log.e("NEXT_FEEDS", error.toString())
                })
    }

    private fun getNextAlbum(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getNextAlbums(classIdx, albumPage + 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        if(result.result!!.data!!.isNotEmpty()) {
                            albumPage = result.result!!.page
                            albumList.addAll(result.result!!.data!!.toCollection(ArrayList()))
                            albumLayout?.setData(result.result!!.data!!.toCollection(ArrayList()))
                        }
                    }else{
                        Log.e("NEXT_ALBUM", result.error)
                    }
                }, { error ->
                    Log.e("NEXT_ALBUM", error.toString())
                })
    }

    private fun addFeedsList(moreList: Array<FeedDetailData>){
        for(data in moreList){
            var announceView = ClassAnnounceView(this)
            announceView.setData(detailData!!.classroom!!.title, data, classIdx, detailData!!.is_mine)

            llBoardContainer?.addView(announceView)
        }
    }

    private fun classSubscribe(){
        var apiSuccessCnt = 0
        var apiService = ApiManager.getInstance().apiService
        var subscribe = apiService.classSubscribe(classIdx)
        var classJoin = apiService.classJoin(classIdx)
        var totalResult = Observable.merge(subscribe, classJoin)
        totalResult.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        apiSuccessCnt++

                        if(apiSuccessCnt == 2) {
                            detailData!!.is_subscribed = "Y"
                            detailData!!.is_joined = "Y"
                            ivFavorite?.setImageResource(R.drawable.icon_follow)
                            Toast.makeText(this, getString(R.string.class_joined), Toast.LENGTH_SHORT).show()
                            ivFavorite?.isClickable = true
                        }
                    }else{
                        Log.e("CLASS_SUBSCRIBE", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_class_join), Toast.LENGTH_SHORT).show()
                        ivFavorite?.isClickable = true
                    }
                }, { error ->
                    Log.e("CLASS_SUBSCRIBE", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_class_join), Toast.LENGTH_SHORT).show()
                    ivFavorite?.isClickable = true
                })
    }

    private fun classSubscribeCancel(){
        var apiSuccessCnt = 0
        var apiService = ApiManager.getInstance().apiService
        var subscribeCancel = apiService.classSubscribeCancel(classIdx)
        var classLeave = apiService.classLeave(classIdx)
        var totalResult = Observable.merge(subscribeCancel, classLeave)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        apiSuccessCnt++

                        if(apiSuccessCnt == 2) {
                            detailData!!.is_subscribed = "N"
                            detailData!!.is_joined = "N"
                            ivFavorite?.setImageResource(R.drawable.icon_unfollow)
                            Toast.makeText(this, getString(R.string.class_leave), Toast.LENGTH_SHORT).show()
                            ivFavorite?.isClickable = true
                        }
                    }else{
                        Log.e("SUBSCRIBE_CANCEL", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_class_leave), Toast.LENGTH_SHORT).show()
                        ivFavorite?.isClickable = true
                    }
                }, { error ->
                    Log.e("SUBSCRIBE_CANCEL", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_class_leave), Toast.LENGTH_SHORT).show()
                    ivFavorite?.isClickable = true
                })
    }

    private fun createNoUserInfoAlert(){
        var noInfo = AlertDialog.Builder(this)
        noInfo.setTitle(getString(R.string.update_alert_title))
        noInfo.setMessage(getString(R.string.input_additional_info_before_write))
        noInfo.setPositiveButton(getString(R.string.update_alert_positiv_button), object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                var intent = Intent(applicationContext, UserInfoSetActivity::class.java)
                startActivity(intent)
            }
        })
        noInfo.setNegativeButton(getString(R.string.update_alert_negative_button_close), null)
        noInfo.create().show()
    }

    private fun writeTypeDialog() {
        var write = Dialog(this)
        write.window.requestFeature(Window.FEATURE_NO_TITLE)
        write.setContentView(R.layout.dialog_class_notice_write)
        write.window.setLayout((resources.displayMetrics.widthPixels * 0.8f).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        write.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var tvFeed = write.findViewById<TextView>(R.id.tvClassWriteFeed)
        var tvNotice = write.findViewById<TextView>(R.id.tvClassWriteNotice)
        var tvExperience = write.findViewById<TextView>(R.id.tvClassWriteExperience)

        tvFeed.setOnClickListener {
            var intent = Intent(this, WriteFeedActivity::class.java)
            intent.putExtra(getString(R.string.intent_key_name_is_notice), "N")
            intent.putExtra(getString(R.string.intent_key_name_index), classIdx)
            startActivityForResult(intent, CLASS_EDIT)
            write.dismiss()
        }

        tvNotice.setOnClickListener {
            var intent = Intent(this, WriteFeedActivity::class.java)
            intent.putExtra(getString(R.string.intent_key_name_is_notice), "Y")
            intent.putExtra(getString(R.string.intent_key_name_index), classIdx)
            startActivityForResult(intent, CLASS_EDIT)
            write.dismiss()
        }

        tvExperience.setOnClickListener {
            var intent = Intent(this, WriteExperienceActivity::class.java)
            intent.putExtra(getString(R.string.intent_key_name_index), classIdx)
            startActivityForResult(intent, CLASS_EDIT)
            write.dismiss()
        }

        write.show()
    }

    fun sendAppEvent(name: String) {
        var bundle = Bundle()
        var user = MyInfoStore.myInfo?.uuid

        if(user == null)
            bundle.putString(getString(R.string.firebase_event_user_id), getString(R.string.firebase_event_user_no_account))
        else
            bundle.putString(getString(R.string.firebase_event_user_id), user)

        bundle.putString(getString(R.string.firebase_event_activity), javaClass.simpleName)

        analytics?.logEvent(name, bundle)
    }
}
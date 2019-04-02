package vdream.vd.com.vdream.view.component

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropCircleTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.FeedDetailData
import vdream.vd.com.vdream.data.FileData
import vdream.vd.com.vdream.interfaces.AnnounceChangeCallback
import vdream.vd.com.vdream.interfaces.CommentClickCallback
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils
import vdream.vd.com.vdream.utils.KakaoLinkUtils
import vdream.vd.com.vdream.view.activity.WriteFeedActivity

class ClassAnnounceView: FrameLayout, View.OnClickListener, YouTubePlayer.OnInitializedListener {
    override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?, p2: Boolean) {
        Log.d("YOUTUBE", "INIT_SUCCESS")
        if(feedData != null){
            if(feedData!!.video != null){
                Log.d("YOUTUBE", feedData!!.video!!.split("/")[3])
                p1?.cueVideo(feedData!!.video!!.split("/")[3])
            }
        }
    }

    override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
        Log.e("YOUTUBE", "INIT_FAIL")
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.clClassFeedLike -> {
                if(feedData!!.is_like == "Y")
                    setAnnounceLikeCancel()
                else
                    setAnnounceLike()
            }
            R.id.clClassFeedComment -> commentClickCallBack?.onClick(feedData!!.idx)
            R.id.clClassFeedShare -> {
                if(feedData!!.files!!.isNotEmpty()) {
                    KakaoLinkUtils.sendKakaoFeedTemplete(context, context.getString(R.string.app_name), CommonUtils.getThumbnailLinkPath(context, feedData!!.files!![0].uploaded_path),
                            feedData!!.content, "http://www.naver.com", classIdx.toString())
                }else{
                    KakaoLinkUtils.sendKakaoTextTemplete(context, context.getString(R.string.app_name) + "\n" + feedData!!.content, null)
                }
            }
        }
    }

    var classIdx = 0
    var annouceIdx = 0
    var feedData: FeedDetailData? = null
    var classIsMine = ""

    var commentClickCallBack: CommentClickCallback? = null
    var changeCallback: AnnounceChangeCallback? = null

    var ivProfile: ImageView? = null
    var tvTitle: TextView? = null
    var tvWritet: TextView? = null
    var tvTime: TextView? = null
    var flMenu: FrameLayout? = null
    var tvContent: TextView? = null
    var tvTag: TextView? = null
    var vpImages: ViewPager? = null
    var tvLikeCnt: TextView? = null
    var ivDivider: ImageView? = null
    var clLike: ConstraintLayout? = null
    var ivLike: ImageView? = null
    var clComment: ConstraintLayout? = null
    var clShare: ConstraintLayout? = null
    var youtube: YouTubePlayerView? = null

    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, callback: CommentClickCallback, changeCallback: AnnounceChangeCallback?): super(context) {
        commentClickCallBack = callback
        this.changeCallback = changeCallback
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_class_feed_item, this, false)
        ivProfile = rootView.findViewById(R.id.ivClassFeedProfile)
        tvTitle = rootView.findViewById(R.id.tvClassFeedTitle)
        tvWritet = rootView.findViewById(R.id.tvClassFeedWriter)
        tvTime = rootView.findViewById(R.id.tvClassFeedTime)
        flMenu = rootView.findViewById(R.id.flClassFeedMenu)
        tvContent = rootView.findViewById(R.id.tvClassFeedContent)
        tvTag = rootView.findViewById(R.id.tvClassFeedTag)
        vpImages = rootView.findViewById(R.id.vpClassFeedImages)
        tvLikeCnt = rootView.findViewById(R.id.tvClassFeedLikeCount)
        clLike = rootView.findViewById(R.id.clClassFeedLike)
        ivLike = rootView.findViewById(R.id.ivClassFeedLike)
        clComment = rootView.findViewById(R.id.clClassFeedComment)
        clShare = rootView.findViewById(R.id.clClassFeedShare)
        youtube = rootView.findViewById(R.id.ytvVideo)

        addView(rootView)

        clLike?.setOnClickListener(this)
        clComment?.setOnClickListener(this)
        clShare?.setOnClickListener(this)
    }

    internal fun setData(classTitle: String, data: FeedDetailData, classIdx: Int, classIsMine: String){
        this.classIdx = classIdx
        this.feedData = data
        this.classIsMine = classIsMine

        annouceIdx = data.idx

        if(data.profile_img.equals(context.getString(R.string.default_text))) {
            ivProfile?.setImageResource(R.drawable.default_profile)
        } else {
            var bitmap = ImageCacheUtils.getBitmap(data.profile_img)

            if(bitmap == null) {
                Glide.with(context)
                        .asBitmap()
                        .load(CommonUtils.getThumbnailLinkPath(context, data.profile_img))
                        .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                        .into(object : ViewTarget<ImageView, Bitmap>(ivProfile!!){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                ImageCacheUtils.putBitmap(data.profile_img, resource)
                                ivProfile?.setImageBitmap(resource)
                            }
                        })
            }else{
                ivProfile!!.setImageBitmap(bitmap)
            }
        }

        tvTitle?.text = classTitle
        tvWritet?.setText(data.nickname)
        tvTime?.text = "・" + CommonUtils.calculateTimeFromCreated(data.created_at)
        tvContent?.setText(data.content)

        var imageList = ArrayList<FileData>()
        var fileList = ArrayList<FileData>()

        if(data.files != null){
            for(file in data.files!!){
                if(file.kind.equals("FILE"))
                    fileList.add(file)
                else
                    imageList.add(file)
            }
        }

        if(imageList.size > 0){
            var imgList = ArrayList<String>()
            for(imgFile in imageList){
                imgList.add(imgFile.uploaded_path)
            }

            setImageResource(imgList)
        }

        if(data.video != null){
            youtube?.visibility = View.VISIBLE
            youtube?.initialize("AIzaSyCs0Jut6MXKS_-giCfjxh_abmln_exDIpk", this)
        }

        if(data.is_like == "Y")
            ivLike?.setImageResource(R.drawable.reply_like_on)

        if(data.is_mine == "Y" || classIsMine == "Y"){
            flMenu?.visibility = View.VISIBLE

            flMenu?.setOnClickListener {
                if(data.is_mine == "Y"){
                    createOptionDialog()
                }else{
                    createDeleteConfirmDialog()
                }
            }
        }
    }

    private fun setAnnounceLike(){
        var apiService = ApiManager.getInstance().apiService
        apiService.noticeLike(classIdx, annouceIdx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        feedData!!.is_like = "Y"
                        ivLike?.setImageResource(R.drawable.reply_like_on)
                    }else{
                        Log.e("NOTICE_LIKE", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_like), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("NOTICE_LIKE", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_like), Toast.LENGTH_SHORT).show()
                })
    }

    private fun setAnnounceLikeCancel() {
        var apiService = ApiManager.getInstance().apiService
        apiService.noticeLikeCancel(classIdx, annouceIdx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        feedData!!.is_like = "N"
                        ivLike?.setImageResource(R.drawable.reply_like)
                    }else{
                        Log.e("LIKE_CANCEL", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_unlike), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("LIKE_CANCEL", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_unlike), Toast.LENGTH_SHORT).show()
                })
    }

    private fun createOptionDialog(){
        var edit = Dialog(context)
        edit.window.requestFeature(Window.FEATURE_NO_TITLE)
        edit.setContentView(R.layout.dialog_annouce_edit_option)
        edit.window.setLayout((resources.displayMetrics.widthPixels * 0.8f).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        edit.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var tvModify = edit.findViewById<TextView>(R.id.tvAnnounceEditOptModify)
        var tvDelete = edit.findViewById<TextView>(R.id.tvAnnounceEditOptDelete)

        tvModify.setOnClickListener {
            changeCallback?.requestUpdate(feedData!!.kind, feedData!!.idx)
            edit.dismiss()
        }

        tvDelete.setOnClickListener {
            createDeleteConfirmDialog()
            edit.dismiss()
        }

        edit.show()
    }

    private fun setImageResource(imgList: ArrayList<String>) {
        vpImages?.visibility = View.VISIBLE
        vpImages?.adapter = ImagePagerAdapter(context, imgList)
    }

    private fun createDeleteConfirmDialog(){
        var deleteBuilder = AlertDialog.Builder(context)
        deleteBuilder.setTitle(context.getString(R.string.update_alert_title))
        deleteBuilder.setMessage(context.getString(R.string.dialog_delete_confirm_content))
        deleteBuilder.setPositiveButton(context.getString(R.string.update_alert_positiv_button), object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                deleteFeed()
            }
        })
        deleteBuilder.setNegativeButton(context.getString(R.string.update_alert_negative_button_close), null)
        deleteBuilder.create().show()
    }

    private fun deleteFeed(){
        var apiService = ApiManager.getInstance().apiService
        apiService.deleteAnnounce(classIdx, annouceIdx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        changeCallback?.announceDeleted()
                    }else{
                        Log.e("DELETE_ANNOUNCE", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_delete_announce), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("DELETE_ANNOUNCE", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_delete_announce), Toast.LENGTH_SHORT).show()
                })
    }
}
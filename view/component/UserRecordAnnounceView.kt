package vdream.vd.com.vdream.view.component

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropCircleTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.FileData
import vdream.vd.com.vdream.data.UserDiaryData
import vdream.vd.com.vdream.data.UserRecordData
import vdream.vd.com.vdream.interfaces.CommentClickCallback
import vdream.vd.com.vdream.interfaces.UserRecordChangeCallback
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils
import vdream.vd.com.vdream.view.activity.RecordActivity

class UserRecordAnnounceView: FrameLayout, View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flUserRecordMenu -> createOptionDialog()
            R.id.clUserRecordLike -> {
                if(recordData != null){
                    if(recordData!!.is_like == "Y")
                        setRecordUnLike()
                    else
                        setRecordLike()
                }else{
                    if(diaryData!!.is_like == "Y")
                        setRecordUnLike()
                    else
                        setRecordLike()
                }
            }
            R.id.clUserRecordComment -> {
                commentClickCallBack?.onClick(recordIdx)
            }
        }
    }

    var commentClickCallBack: CommentClickCallback? = null
    var recordChangeCallback: UserRecordChangeCallback? = null
    var recordData: UserRecordData? = null
    var diaryData: UserDiaryData? = null
    var recordIdx = 0

    var ivProfile: ImageView? = null
    var tvNickName: TextView? = null
    var tvSchool: TextView? = null
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

    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, commentClickCallback: CommentClickCallback, recordChangeCallback: UserRecordChangeCallback): super(context){
        commentClickCallBack = commentClickCallback
        this.recordChangeCallback = recordChangeCallback
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_user_record_item, this, false)
        ivProfile = rootView.findViewById(R.id.ivUserRecordProfile)
        tvNickName = rootView.findViewById(R.id.tvUserRecordNickname)
        tvSchool = rootView.findViewById(R.id.tvUserRecordSchool)
        tvTime = rootView.findViewById(R.id.tvUserRecordTime)
        flMenu = rootView.findViewById(R.id.flUserRecordMenu)
        tvContent = rootView.findViewById(R.id.tvUserRecordContent)
        tvTag = rootView.findViewById(R.id.tvUserRecordTag)
        vpImages = rootView.findViewById(R.id.vpUserRecordImages)
        tvLikeCnt = rootView.findViewById(R.id.tvUserRecordLikeCount)
        clLike = rootView.findViewById(R.id.clUserRecordLike)
        ivLike = rootView.findViewById(R.id.ivUserRecordLike)
        clComment = rootView.findViewById(R.id.clUserRecordComment)

        addView(rootView)

        flMenu?.setOnClickListener(this)
        clLike?.setOnClickListener(this)
        clComment?.setOnClickListener(this)
    }

    internal fun setData(data: UserRecordData){
        recordData = data
        recordIdx = data.idx

        if(data.profile_img.equals(context.getString(R.string.default_text))){
            ivProfile?.setImageResource(R.drawable.default_profile)
        }else {
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
                ivProfile?.setImageBitmap(bitmap)
            }
        }

        if(data.nickname == null || data.nickname.equals(""))
            tvNickName?.text = context.getString(R.string.no_name)
        else
            tvNickName?.text = data.nickname

        tvTime?.text = CommonUtils.calculateTimeFromCreated(data.created_at)

        if(data.kind == context.getString(R.string.user_record_type_diary))
            tvContent?.text = data.content
        else
            tvContent?.text = getTextContentFromData(data.kind)

        if(data.tags != null)
            tvTag?.text = CommonUtils.convertTagsToString(data.tags!!)

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

        tvLikeCnt?.text = setLikeCountText(data.like_count)

        if(data.is_like == "Y")
            ivLike?.setImageResource(R.drawable.reply_like_on)
    }

    internal fun setData(data: UserDiaryData){
        diaryData = data
        recordIdx = data.idx

        if(data.profile_img.equals(context.getString(R.string.default_text))){
            Glide.with(context).load(R.drawable.default_profile)
                    .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                    .into(ivProfile!!)
        }else {
            Glide.with(context).load(CommonUtils.getThumbnailLinkPath(context, data.profile_img))
                    .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                    .into(ivProfile!!)
        }

        if(data.nickname == null || data.nickname.equals(""))
            tvNickName?.text = context.getString(R.string.no_name)
        else
            tvNickName?.text = data.nickname

        tvTime?.text = CommonUtils.calculateTimeFromCreated(data.created_at)

        tvContent?.text = data.content

        if(data.tags != null)
            tvTag?.text = CommonUtils.convertTagsToString(data.tags!!)

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

        tvLikeCnt?.text = setLikeCountText(data.like_count)

        if(data.is_like.equals("Y"))
            ivLike?.setImageResource(R.drawable.reply_like_on)
    }

    private fun setRecordLike(){
        var idx = 0
        if(recordData != null)
            idx = recordData!!.idx
        else
            idx = diaryData!!.idx

        var apiService = ApiManager.getInstance().apiService
        apiService.recordLike(idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        if(recordData != null) {
                            recordData!!.is_like = "Y"
                            recordData!!.like_count += 1
                            tvLikeCnt?.text = setLikeCountText(recordData!!.like_count)
                        }else {
                            diaryData!!.is_like = "Y"
                            diaryData!!.like_count += 1
                            tvLikeCnt?.text = setLikeCountText(diaryData!!.like_count)
                        }

                        ivLike?.setImageResource(R.drawable.reply_like_on)

                    }else{
                        Log.e("RECORD_LIKE", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_like), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("RECORD_LIKE", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_like), Toast.LENGTH_SHORT).show()
                })
    }

    private fun setRecordUnLike(){
        var apiService = ApiManager.getInstance().apiService
        apiService.recordUnlike(recordData!!.idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        if(recordData != null) {
                            recordData!!.is_like = "N"
                            recordData!!.like_count -= 1
                            tvLikeCnt?.text = setLikeCountText(recordData!!.like_count)
                        }else {
                            diaryData!!.is_like = "N"
                            diaryData!!.like_count -= 1
                            tvLikeCnt?.text = setLikeCountText(diaryData!!.like_count)
                        }

                        ivLike?.setImageResource(R.drawable.reply_like)
                    }else{
                        Log.e("RECORD_UNLIKE", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_unlike), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("RECORD_UNLIKE", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_unlike), Toast.LENGTH_SHORT).show()
                })
    }

    private fun setImageResource(imgList: ArrayList<String>) {
        vpImages?.visibility = View.VISIBLE
        vpImages?.adapter = ImagePagerAdapter(context, imgList)
    }

    private fun setLikeCountText(count: Int): SpannableStringBuilder {
        var wholeText = count.toString() + context.getString(R.string.like_count_comment)
        var builder = SpannableStringBuilder(wholeText)
        builder.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.mainColor)), 0, count.toString().length+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(StyleSpan(Typeface.BOLD), 0, count.toString().length+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return builder
    }

    private fun convertKindEngToKor(kind: String): String {
        var kor = ""

        when(kind){
            context.getString(R.string.user_record_type_class) -> kor = context.getString(R.string.user_record_kor_type_class)
            context.getString(R.string.user_record_type_club) -> kor = context.getString(R.string.user_record_kor_type_club)
            context.getString(R.string.user_record_type_career) -> kor = context.getString(R.string.user_record_kor_type_career)
            context.getString(R.string.user_record_type_contest) -> kor = context.getString(R.string.user_record_kor_type_contest)
            context.getString(R.string.user_record_type_volunteer) -> kor = context.getString(R.string.user_record_kor_type_volunteer)
            context.getString(R.string.user_record_type_behavior) -> kor = context.getString(R.string.user_record_kor_type_behavior)
            context.getString(R.string.user_record_type_reading) -> kor = context.getString(R.string.user_record_kor_type_reading)
        }

        return kor
    }

    private fun getTextContentFromData(kind: String): String {
        var content = ""

        when(kind){
            context.getString(R.string.user_record_type_class) -> content = makeClassTextContent()
            context.getString(R.string.user_record_type_club) -> content = makeClubTextContent()
            context.getString(R.string.user_record_type_career) -> content = makeCareerTextContent()
            context.getString(R.string.user_record_type_contest) -> content = makeContestTextContent()
            context.getString(R.string.user_record_type_volunteer) -> content = makeVolunteerTextContent()
            context.getString(R.string.user_record_type_behavior) -> content = makeBehaviorTextContent()
            context.getString(R.string.user_record_type_reading) -> content = makeReadingTextContent()
        }

        return content
    }

    private fun makeClassTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_class_unit))
        builder.append(" : ")
        builder.append(recordData!!.class_unit)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_class_learning))
        builder.append(" : ")
        builder.append(recordData!!.class_learning_content)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_class_leading_hor))
        builder.append(" :\n")
        builder.append(recordData!!.class_leading_learning)

        return builder.toString()
    }

    private fun makeClubTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_club_topic))
        builder.append(" : ")
        builder.append(recordData!!.club_topic)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_club_activated_at))
        builder.append(" : ")
        builder.append(recordData!!.club_activated_at)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_club_participants))
        builder.append(" : ")
        builder.append(recordData!!.club_participants)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_club_motivation))
        builder.append(" : ")
        builder.append(recordData!!.club_motivation)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_club_principle))
        builder.append(" : ")
        builder.append(recordData!!.club_principle)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_club_learning))
        builder.append(" : ")
        builder.append(recordData!!.club_learning)

        return builder.toString()
    }

    private fun makeCareerTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_career_activated_at))
        builder.append(" : ")
        builder.append(recordData!!.career_activated_at)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_career_effort))
        builder.append(" : ")
        builder.append(recordData!!.career_effort)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_career_learning))
        builder.append(" : ")
        builder.append(recordData!!.career_learning)

        return builder.toString()
    }

    private fun makeContestTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_career_activated_at))
        builder.append(" : ")
        builder.append(recordData!!.contest_activated_at)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_career_effort))
        builder.append(" : ")
        builder.append(recordData!!.contest_effort)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_career_learning))
        builder.append(" : ")
        builder.append(recordData!!.contest_learning)

        return builder.toString()
    }

    private fun makeVolunteerTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_volunteer_kind))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_kind)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_place))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_place)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_started_at))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_started_at)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_ended_at))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_ended_at)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_period))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_period)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_activity_content))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_activity_content)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_effort))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_effort)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_learning))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_learning)

        return builder.toString()
    }

    private fun makeBehaviorTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_behavior_kind))
        builder.append(" : ")
        builder.append(recordData!!.behavior_kind)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_behavior_case))
        builder.append(" : ")
        builder.append(recordData!!.behavior_case)

        return builder.toString()
    }

    private fun makeReadingTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_reading_book_name))
        builder.append(" : ")
        builder.append(recordData!!.reading_book_name)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_reading_author))
        builder.append(" : ")
        builder.append(recordData!!.reading_author)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_reading_motivation))
        builder.append(" : ")
        builder.append(recordData!!.reading_motivation)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_reading_summary))
        builder.append(" : ")
        builder.append(recordData!!.reading_summary)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_reading_learning))
        builder.append(" : ")
        builder.append(recordData!!.reading_learning)

        return builder.toString()
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
            if(recordData != null)
                recordChangeCallback?.requestModifyRecord(recordData!!)
            else
                recordChangeCallback?.requesModifyDiary(diaryData!!)

            edit.dismiss()
        }

        tvDelete.setOnClickListener {
            createDeleteConfirmDialog()
            edit.dismiss()
        }

        edit.show()
    }

    private fun createDeleteConfirmDialog() {
        var deleteBuilder = AlertDialog.Builder(context)
        deleteBuilder.setTitle(context.getString(R.string.update_alert_title))
        deleteBuilder.setMessage(context.getString(R.string.dialog_delete_confirm_content))
        deleteBuilder.setPositiveButton(context.getString(R.string.update_alert_positiv_button), object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                deleteRecord()
            }
        })
        deleteBuilder.setNegativeButton(context.getString(R.string.update_alert_negative_button_close), null)
        deleteBuilder.create().show()
    }

    private fun deleteRecord() {
        var apiService = ApiManager.getInstance().apiService
        apiService.deleteRecord(recordIdx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        Toast.makeText(context, context.getString(R.string.success_to_delete_user_record), Toast.LENGTH_SHORT).show()
                        if(recordData != null)
                            recordChangeCallback?.onChanged(recordData!!.kind)
                        else
                            recordChangeCallback?.onChanged(context.getString(R.string.user_record_type_diary))
                    }else{
                        Log.e("DELETE_RECORD", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_delete_announce), Toast.LENGTH_SHORT).show()
                    }
                }, { err ->
                    Log.e("DELETE_RECORD", err.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_delete_announce), Toast.LENGTH_SHORT).show()
                })
    }
}
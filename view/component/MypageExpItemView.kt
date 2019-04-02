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
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils
import vdream.vd.com.vdream.view.activity.ClassDetailAcitivity
import vdream.vd.com.vdream.view.activity.WriteExperienceActivity
import java.util.*
import kotlin.math.exp

class MypageExpItemView: FrameLayout, View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivMypageExpItemMember -> createMemeberApproveDialog()
            R.id.ivMypageExpItemSetting -> {
                if(ivExpMember?.visibility == View.VISIBLE){
                    createExpOptionDialog()
                }else{
                    createDeleteConfirmDialog()
                }
            }
        }
    }

    var ivExpImage: ImageView? = null
    var tvExpCategory: TextView? = null
    var tvExpName: TextView? = null
    var tvExpTime: TextView? = null
    var ivExpMember: ImageView? = null
    var ivExpMemberNew: ImageView? = null
    var ivExpSetting: ImageView? = null
    var expData: ExperienceData? = null
    var memberList = ArrayList<ExperienceRequestMemberData>()
    var membarListAdapter: ClassMemeberAdapter? = null

    constructor(context: Context): super(context) {
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_mypage_exp_item, this, false)
        ivExpImage = rootView.findViewById(R.id.ivMypageExpItemImage)
        tvExpName = rootView.findViewById(R.id.tvMypageExpItemName)
        tvExpCategory = rootView.findViewById(R.id.tvMypageExpItemCategory)
        tvExpTime = rootView.findViewById(R.id.tvMypageExpItemTime)
        ivExpMember = rootView.findViewById(R.id.ivMypageExpItemMember)
        ivExpMemberNew = rootView.findViewById(R.id.ivMypageExpItemMemberNew)
        ivExpSetting = rootView.findViewById(R.id.ivMypageExpItemSetting)

        addView(rootView)

        ivExpImage?.setOnClickListener(this)
        tvExpCategory?.setOnClickListener(this)
        tvExpName?.setOnClickListener(this)
        ivExpMember?.setOnClickListener(this)
        ivExpSetting?.setOnClickListener(this)
    }

    internal fun setNoMember() {
        ivExpMember?.visibility = View.GONE
    }

    internal fun setData(data: ExperienceData) {
        expData = data

        if(expData!!.feed!!.files != null && expData!!.feed!!.files!!.isNotEmpty()){
            Glide.with(context).load(CommonUtils.getThumbnailLinkPath(context, expData!!.feed!!.files!![0].uploaded_path))
                    .into(ivExpImage!!)
        }

        tvExpName?.text = expData!!.feed!!.title
        tvExpCategory?.text = "[${expData!!.classroom!!.category!!.depth_2!!.title}]"
        tvExpTime?.text = "${expData!!.classroom!!.title}・${dateFormatChange(expData!!.feed!!.opened_at.split(" ")[0])}"
        getRequestMember()
    }

    inner class ClassMemeberAdapter: BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView
            var holder: ClassMemberHolder? = null

            if(convertView == null){
                view = LayoutInflater.from(context).inflate(R.layout.view_class_exp_member_list_item, parent, false)
                holder = ClassMemberHolder()
                holder!!.ivProfile = view!!.findViewById(R.id.ivMemberProfile)
                holder!!.tvNickname = view!!.findViewById(R.id.tvMemberNickname)
                holder!!.tvSchool = view!!.findViewById(R.id.tvMemberSchool)
                holder!!.tvState = view!!.findViewById(R.id.tvMemberState)

                view.tag = holder
            }else{
                holder = view!!.tag as ClassMemberHolder
            }

            var member = memberList[position]

            if(member.profile_img == context.getString(R.string.default_text)){
                holder?.ivProfile?.setImageResource(R.drawable.default_profile)
            }else{
                var bitmap = ImageCacheUtils.getBitmap(member.profile_img)

                if(bitmap == null) {
                    Glide.with(context)
                            .asBitmap()
                            .load(CommonUtils.getThumbnailLinkPath(context, member.profile_img))
                            .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                            .into(object : ViewTarget<ImageView, Bitmap>(holder!!.ivProfile!!){
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    ImageCacheUtils.putBitmap(member.profile_img, resource)
                                    holder!!.ivProfile?.setImageBitmap(resource)
                                }
                            })
                }else{
                    holder!!.ivProfile?.setImageBitmap(bitmap)
                }
            }

            holder!!.tvNickname?.text = member.nickname
            holder!!.tvSchool?.text = ""

            if(member.status == context.getString(R.string.class_user_state_approve)){
                holder!!.tvState?.setTextColor(ContextCompat.getColor(context, R.color.text_dark_gray))
                holder!!.tvState?.setBackgroundResource(R.drawable.rectangle_2dp_rounded_transparent_deepgray_stroke)
            }

            return view
        }

        override fun getItem(position: Int): Any {
            return memberList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return memberList.size
        }

    }

    inner class ClassMemberHolder{
        var ivProfile: ImageView? = null
        var tvNickname: TextView? = null
        var tvSchool: TextView? = null
        var tvState: TextView? = null
    }

    private fun getRequestMember(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getRequestMemberList(expData!!.feed!!.idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ resulit ->
                    if(resulit.status == "Y"){
                        memberList = resulit!!.result!!.toCollection(ArrayList())

                        for(member in memberList){
                            if(member.status == "WAIT" && ivExpMember?.visibility == View.VISIBLE) {
                                ivExpMemberNew?.visibility = View.VISIBLE
                                break
                            }
                        }
                    }else{

                    }
                }, { err ->

                })
    }

    private fun createMemeberApproveDialog(){
        if(memberList.size == 0){
            Toast.makeText(context, context.getString(R.string.no_member_wait_exp), Toast.LENGTH_SHORT).show()
            return
        }

        var members = Dialog(context)
        members.window.requestFeature(Window.FEATURE_NO_TITLE)
        members.setContentView(R.layout.dialog_received_request_exp_member)
        members.window.setLayout((resources.displayMetrics.widthPixels * 0.9f).toInt(), (resources.displayMetrics.heightPixels * 0.6f).toInt())
        members.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var tvTitle = members.findViewById<TextView>(R.id.tvExpJoinRequest)
        var flClose = members.findViewById<FrameLayout>(R.id.flExpJoinClose)
        var lvReceivedList = members.findViewById<ListView>(R.id.lvExpJoinRequest)

        var count =  " (${memberList.size}명)"
        var totalText = context.getString(R.string.exp_joined_member) + count
        var builder = SpannableStringBuilder(totalText)
        builder.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.mainColor)), totalText.indexOf(count), totalText.indexOf(count) + count.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvTitle?.text = builder
        membarListAdapter = ClassMemeberAdapter()
        lvReceivedList.adapter = ClassMemeberAdapter()
        flClose.setOnClickListener({
            members.dismiss()
        })

        members.show()
    }

    private fun dateFormatChange(date: String): String {
        var temp = date.split("-")
        return "${temp[1]}월${temp[2]}일"
    }

    private fun createExpOptionDialog(){
        var option = Dialog(context)
        option.window.requestFeature(Window.FEATURE_NO_TITLE)
        option.setContentView(R.layout.dialog_experience_manage_option)
        option.window.setLayout((resources.displayMetrics.widthPixels * 0.8f).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        option.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var tvSecureCode = option.findViewById<TextView>(R.id.tvExpManageSecureCode)
        var tvModify = option.findViewById<TextView>(R.id.tvExpManageModify)
        var tvCancle = option.findViewById<TextView>(R.id.tvExpManageCancel)

        tvSecureCode.setOnClickListener({
            if(expData!!.feed!!.is_secure == "Y")
                createSecureCodeDialog()
            else
                Toast.makeText(context, context.getString(R.string.exp_no_secure_code), Toast.LENGTH_SHORT).show()
        })

        tvModify.setOnClickListener({
            var intent = Intent(context, WriteExperienceActivity::class.java)
            intent.putExtra(context.getString(R.string.intent_key_name_index), expData!!.classroom!!.idx)
            intent.putExtra(context.getString(R.string.intent_key_name_feeddata), expData!!.feed)
            context.startActivity(intent)

            option.dismiss()
        })

        tvCancle.setOnClickListener({
            if(memberList.isEmpty()){
                createDeleteConfirmDialog()
                option.dismiss()
            }else{
                Toast.makeText(context, context.getString(R.string.can_not_cancel_exp_with_member), Toast.LENGTH_SHORT).show()
            }
        })

        option.show()
    }

    private fun createDeleteConfirmDialog(){
        var deleteBuilder = AlertDialog.Builder(context)
        deleteBuilder.setTitle(context.getString(R.string.update_alert_title))
        deleteBuilder.setMessage("체험학습 신청을 취소하시겠습니까?")
        deleteBuilder.setPositiveButton(context.getString(R.string.update_alert_positiv_button)) { dialog, which -> cancelExpRequest() }
        deleteBuilder.setNegativeButton(context.getString(R.string.update_alert_negative_button_close), null)
        deleteBuilder.create().show()
    }

    private fun createSecureCodeDialog(){
        var code = Dialog(context)
        code.window.requestFeature(Window.FEATURE_NO_TITLE)
        code.setContentView(R.layout.dialog_exp_secure_code_confirm)
        code.window.setLayout((resources.displayMetrics.widthPixels * 0.8f).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)

        var tvCode = code.findViewById<TextView>(R.id.tvSecureCodeConfirmCode)
        var tvConfirm = code.findViewById<TextView>(R.id.tvSecureCodeConfirmConfirm)

        tvCode.text = expData!!.feed!!.secure_code

        tvConfirm.setOnClickListener({
            code.dismiss()
        })

        code.show()
    }

    private fun cancelExpRequest(){
        var apiService = ApiManager.getInstance().apiService
        apiService.cancelRequestExp(expData!!.feed!!.idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        Toast.makeText(context, "체험학습 신청이 취소 되었습니다", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context, "체험학습 신청이 취소 중 오류가 발생했습니다. 잠시 후 시도해 주세요", Toast.LENGTH_SHORT).show()
                        Log.e("EXO_CANCEL", result.error)
                    }
                }, { err ->
                    Toast.makeText(context, "체험학습 신청이 취소 중 오류가 발생했습니다. 잠시 후 시도해 주세요", Toast.LENGTH_SHORT).show()
                    Log.e("EXO_CANCEL", err.toString())
                })
    }
}
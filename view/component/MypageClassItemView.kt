package vdream.vd.com.vdream.view.component

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropCircleTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.BaseResultData
import vdream.vd.com.vdream.data.ClassUserData
import vdream.vd.com.vdream.data.ClassUserListRequestData
import vdream.vd.com.vdream.data.ClassroomBaseData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils
import vdream.vd.com.vdream.view.activity.ClassDetailAcitivity
import vdream.vd.com.vdream.view.activity.ClassRegisterActivity
import java.util.*

class MypageClassItemView: FrameLayout, View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivMypageClassItemMessage -> createMemeberApproveDialog()
            R.id.ivMypageClassItemSetting -> {
                if(classType == context.getString(R.string.mypage_class_list_type_made))
                    moveToClassUpdateActivity()
                else
                    classWithdrawDialog()
            }
            else -> moveToClassDetail()
        }
    }

    var ivClassImage: ImageView? = null
    var flClassImageCover: FrameLayout? = null
    var tvClassCategory: TextView? = null
    var tvClassName: TextView? = null
    var tvClassTag: TextView? = null
    var flClassMessageContainer: FrameLayout? = null
    var ivClassMessage: ImageView? = null
    var ivClassMessageNew: ImageView? = null
    var ivClassSetting: ImageView? = null
    var classData: ClassroomBaseData? = null
    var memberList = ArrayList<ClassUserData>()
    var membarListAdapter: ClassMemeberAdapter? = null

    var classType = ""

    constructor(context: Context): super(context) {
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_mypage_class_item, this, false)
        ivClassImage = rootView.findViewById(R.id.ivMypageClassItemImage)
        flClassImageCover = rootView.findViewById(R.id.flMypageClassItemImageCover)
        tvClassCategory = rootView.findViewById(R.id.tvMypageClassItemCategory)
        tvClassName = rootView.findViewById(R.id.tvMypageClassItemName)
        tvClassTag = rootView.findViewById(R.id.tvMypageClassItemTag)
        flClassMessageContainer = rootView.findViewById(R.id.flMypageClassItemMessageContainer)
        ivClassMessage = rootView.findViewById(R.id.ivMypageClassItemMessage)
        ivClassMessageNew = rootView.findViewById(R.id.ivMypageClassItemMessageNew)
        ivClassSetting = rootView.findViewById(R.id.ivMypageClassItemSetting)

        addView(rootView)

        ivClassImage?.setOnClickListener(this)
        tvClassCategory?.setOnClickListener(this)
        tvClassName?.setOnClickListener(this)
        tvClassTag?.setOnClickListener(this)
        ivClassMessage?.setOnClickListener(this)
        ivClassSetting?.setOnClickListener(this)
    }

    internal fun setData(data: ClassroomBaseData, classType: String) {
        classData = data
        this.classType = classType

        if(data.background_img == context.getString(R.string.default_text)){
            ivClassImage?.setImageResource(R.drawable.default_bg)
        }else{
            var bitmap = ImageCacheUtils.getBitmap(data.background_img)

            if(bitmap == null) {
                Glide.with(context)
                        .asBitmap()
                        .load(CommonUtils.getThumbnailLinkPath(context, data.background_img))
                        .into(object : ViewTarget<ImageView, Bitmap>(ivClassImage!!){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                ImageCacheUtils.putBitmap(data.background_img, resource)
                                ivClassMessage?.setImageBitmap(resource)
                            }
                        })
            }else{
                ivClassImage?.setImageBitmap(bitmap)
            }
        }

        if(data.is_public == "N")
            flClassImageCover!!.visibility = View.VISIBLE

        tvClassCategory?.text = "[${data.category!!.depth_2!!.title}]"
        tvClassName?.text = data.title

        if(data.tags != null) {
            tvClassTag?.text = CommonUtils.convertTagsToString(data.tags!!)
            tvClassTag?.isSelected = true
        }

        tvClassName?.isSelected = true

        if(classType == context.getString(R.string.mypage_class_list_type_made))
            flClassMessageContainer?.visibility = View.VISIBLE

        getClassMemeber(context.getString(R.string.class_user_stat_wait))
    }

    private fun createMemeberApproveDialog(){
        if(memberList.size == 0){
            Toast.makeText(context, context.getString(R.string.no_member_wait), Toast.LENGTH_SHORT).show()
            return
        }

        var members = Dialog(context)
        members.window.requestFeature(Window.FEATURE_NO_TITLE)
        members.setContentView(R.layout.dialog_received_class_join)
        members.window.setLayout((resources.displayMetrics.widthPixels * 0.9f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        members.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var flClose = members.findViewById<FrameLayout>(R.id.flClassJoinClose)
        var lvReceivedList = members.findViewById<ListView>(R.id.lvClassJoinRequest)
        var tvApproveAll = members.findViewById<TextView>(R.id.tvClassJoinApproveAll)

        membarListAdapter = ClassMemeberAdapter()
        lvReceivedList.adapter = ClassMemeberAdapter()
        flClose.setOnClickListener({
            members.dismiss()
        })

        tvApproveAll.setOnClickListener({
            allUserJoin()
            members.dismiss()
        })

        members.show()
    }

    private fun moveToClassUpdateActivity(){
        var intent = Intent(context, ClassRegisterActivity::class.java)
        intent.putExtra(context.getString(R.string.intent_key_name_index), classData!!.idx)
        intent.putExtra(context.getString(R.string.intent_key_name_classroom), classData)
        context.startActivity(intent)
    }

    private fun moveToClassDetail() {
        var intent = Intent(context, ClassDetailAcitivity::class.java)
        intent.putExtra(context.getString(R.string.intent_key_name_index), classData!!.idx)
        context.startActivity(intent)
    }

    private fun getClassMemeber(status: String){
        var requestData = ClassUserListRequestData()
        requestData.status = status
        var apiService = ApiManager.getInstance().apiService
        apiService.getClassUserList(classData!!.idx,requestData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        memberList = result.result!!.toCollection(ArrayList())

                        if(memberList.size > 0)
                            ivClassMessageNew?.visibility = View.VISIBLE
                        else
                            ivClassMessageNew?.visibility = View.GONE
                    }else{
                        Log.e("GET_MEMBER", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_get_class_members), Toast.LENGTH_SHORT).show()
                    }
                }, { err ->
                    Log.e("GET_MEMBER", err.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_get_class_members), Toast.LENGTH_SHORT).show()
                })
    }

    inner class ClassMemeberAdapter: BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView
            var holder: ClassMemberHolder? = null

            if(convertView == null){
                view = LayoutInflater.from(context).inflate(R.layout.view_class_member_list_item, parent, false)
                holder = ClassMemberHolder()
                holder!!.ivProfile = view!!.findViewById(R.id.ivMemberProfile)
                holder!!.tvNickname = view!!.findViewById(R.id.tvMemberNickname)
                holder!!.tvSchool = view!!.findViewById(R.id.tvMemberSchool)
                holder!!.ivApprove = view!!.findViewById(R.id.ivMemberApprove)
                holder!!.ivReject = view!!.findViewById(R.id.ivMemberReject)

                view.tag = holder
            }else{
                holder = view!!.tag as ClassMemberHolder
            }

            var member = memberList[position]

            if(member.profile_img == context.getString(R.string.default_text)){
                holder?.ivProfile?.setImageResource(R.drawable.default_profile)
            }else{
                Glide.with(context).load(CommonUtils.getThumbnailLinkPath(context, member.profile_img))
                        .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                        .into(holder!!.ivProfile!!)
            }

            holder!!.tvNickname?.text = member.nickname
            holder!!.tvSchool?.text = ""

            holder!!.ivApprove?.setOnClickListener({
                userJoinStateUpdate(context.getString(R.string.class_user_state_approve), member)
            })

            holder!!.ivReject?.setOnClickListener({
                userJoinStateUpdate(context.getString(R.string.class_user_stat_return), member)
            })

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
        var ivApprove: ImageView? = null
        var ivReject: ImageView? = null
    }

    private fun userJoinStateUpdate(state: String, user: ClassUserData){
        var requestData = ClassUserListRequestData()
        requestData.status = state

        var apiService = ApiManager.getInstance().apiService
        apiService.updateClassMemberInfo(classData!!.idx, user.uuid, requestData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        if(state == context.getString(R.string.class_user_state_approve))
                            Toast.makeText(context, context.getString(R.string.success_to_class_join_approve), Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(context, context.getString(R.string.success_to_class_join_reject), Toast.LENGTH_SHORT).show()

                        memberList.remove(user)
                        membarListAdapter?.notifyDataSetChanged()
                    }else{
                        Log.e("MEMBER_JOIN_STATE", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_update_members_join_state), Toast.LENGTH_SHORT).show()
                    }
                }, { err ->
                    Log.e("MEMBER_JOIN_STATE", err.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_update_members_join_state), Toast.LENGTH_SHORT).show()
                })
    }

    private fun allUserJoin(){
        var requestData = ClassUserListRequestData()
        requestData.status = context.getString(R.string.class_user_state_approve)
        var apiService = ApiManager.getInstance().apiService
        var totalResult: Observable<BaseResultData>? = null

        for(member in memberList) {
            var sObservable = apiService.updateClassMemberInfo(classData!!.idx, member.uuid, requestData)

            if(totalResult == null)
                totalResult = sObservable
            else
                totalResult = Observable.merge(totalResult, sObservable)
        }

        totalResult!!.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if (result.status == "Y") {
                        Toast.makeText(context, context.getString(R.string.success_to_class_join_approve), Toast.LENGTH_SHORT).show()
                        getClassMemeber(context.getString(R.string.class_user_stat_wait))
                    } else {
                        Log.e("MEMBER_JOIN_ALL", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_update_members_join_state), Toast.LENGTH_SHORT).show()
                    }
                }, { err ->
                    Log.e("MEMBER_JOIN_ALL", err.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_update_members_join_state), Toast.LENGTH_SHORT).show()
                })
    }

    private fun classWithdrawDialog() {
        var withdraw = AlertDialog.Builder(context)
        withdraw.setTitle(R.string.update_alert_title)
        withdraw.setMessage(R.string.do_you_wanna_withdraw_class)
        withdraw.setPositiveButton(context.getString(R.string.update_alert_positiv_button), object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                classWithdraw()
            }
        })
        withdraw.setNegativeButton(context.getString(R.string.update_alert_negative_button_close), null)
        withdraw.create().show()
    }

    private fun classWithdraw(){
        var apiSuccessCnt = 0
        var apiService = ApiManager.getInstance().apiService
        var subscribeCancel = apiService.classSubscribeCancel(classData!!.idx)
        var classLeave = apiService.classLeave(classData!!.idx)
        var totalResult = Observable.merge(subscribeCancel, classLeave)
        totalResult.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        Toast.makeText(context, context.getString(R.string.class_leave), Toast.LENGTH_SHORT).show()
                    }else{
                        Log.e("SUBSCRIBE_CANCEL", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_class_leave), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("SUBSCRIBE_CANCEL", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_class_leave), Toast.LENGTH_SHORT).show()
        })
    }
}
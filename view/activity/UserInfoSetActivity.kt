package vdream.vd.com.vdream.view.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropCircleTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.UpdateUserData
import vdream.vd.com.vdream.data.UserInfoData
import vdream.vd.com.vdream.interfaces.UploadFinishCallback
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.network.S3Uploader
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.view.component.TitledEditText
import vdream.vd.com.vdream.view.component.TitledSwitch
import java.io.File

class UserInfoSetActivity: BaseActivity(), UploadFinishCallback, View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flBackContainer -> {
                sendAppEvent("유저정보설정_헤더_뒤로가기")
                finish()
            }
            R.id.ivUserProfile -> {
                sendAppEvent("유저정보설정_프로필이미지")
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    var intent = Intent(this, ImagePickActivity::class.java)
                    intent.putExtra(getString(R.string.intent_key_name_limit_count), IMAGE_COUNT)
                    startActivityForResult(intent, GALLERY_IMAGE)
                }else{
                    Toast.makeText(this, getString(R.string.permission_external_storage_denied), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.tvModifyUserInfo -> {
                sendAppEvent("유저정보설정_유저정보_설정하기")
                checkUpdateData()
            }
        }
    }

    override fun uploadFinished(uploadPath: ArrayList<String>, filename: ArrayList<String>) {
        updateData!!.profile_img = uploadPath.get(0)
        updateUserData(updateData!!)
    }

    val GALLERY_IMAGE = 0
    val IMAGE_COUNT = 1

    var flBack: FrameLayout? = null
    var clProfileBg: ConstraintLayout? = null
    var ivProfile: ImageView? = null
    var tetNickname: TitledEditText? = null
    var tetEmail: TitledEditText? = null
    var tetPassword: TitledEditText? = null
    var tetPasswordConfirm: TitledEditText? = null
    var tswPush: TitledSwitch? = null
    var tvModify: TextView? = null
    var userInfo: UserInfoData? = null
    var updateData: UpdateUserData? = null
    var imagePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = MyInfoStore.myInfo
        setContentView(R.layout.activity_userinfo_set)

        flBack = findViewById(R.id.flBackContainer)
        clProfileBg = findViewById(R.id.clProfileContainer)
        ivProfile = findViewById(R.id.ivUserProfile)
        tetNickname = findViewById(R.id.tetUserNickname)
        tetEmail = findViewById(R.id.tetUserEmail)
        tetPassword = findViewById(R.id.tetUserPassword)
        tetPasswordConfirm = findViewById(R.id.tetUserPasswordConfirm)
        tswPush = findViewById(R.id.tswUserPush)
        tvModify = findViewById(R.id.tvModifyUserInfo)

        flBack?.setOnClickListener(this)
        ivProfile?.setOnClickListener(this)
        tvModify?.setOnClickListener(this)

        initDetailUI()
        setOldUserData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                GALLERY_IMAGE -> {
                    imagePath = data?.getStringArrayListExtra(getString(R.string.intent_key_name_images))!!.get(0)
                    Glide.with(this).load(File(imagePath)).apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                            .into(ivProfile!!)
                }
            }
        }
    }

    private fun initDetailUI(){
        tetNickname?.setTitle(getString(R.string.nickname))
        tetEmail?.setTitle(getString(R.string.email))
        tetPassword?.setTitle(getString(R.string.password))
        tetPasswordConfirm?.setTitle(getString(R.string.pass_confirm))
        tswPush?.setTitle(getString(R.string.receive_push))

        if(userInfo!!.sns != null && userInfo!!.sns != ""){
            tetPassword?.visibility = View.GONE
            tetPasswordConfirm?.visibility = View.GONE
        }
    }

    private fun setOldUserData(){
        tswPush?.switch!!.isChecked = userInfo!!.is_pushable.equals("Y")

        if(userInfo!!.nickname != null)
            tetNickname?.setText(userInfo!!.nickname)

        tetEmail?.setText(userInfo!!.email)

        if(!userInfo!!.profile_img.equals(getString(R.string.default_text))){
            Glide.with(this).load(CommonUtils.getThumbnailLinkPath(this, userInfo!!.profile_img))
                    .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                    .into(ivProfile!!)
        }
    }

    private fun checkUpdateData(){
        if(tetNickname!!.getText().equals("")){
            Toast.makeText(this, getString(R.string.input_user_nickname), Toast.LENGTH_SHORT).show()
            return
        }

        if(!tetPassword!!.getText().equals(tetPasswordConfirm!!.getText())){
            Toast.makeText(this, getString(R.string.confirm_user_password), Toast.LENGTH_SHORT).show()
            return
        }

        updateData = UpdateUserData()

        if(!tetNickname!!.getText().equals(userInfo!!.nickname))
            updateData!!.nickname = tetNickname!!.getText()

        if(tswPush!!.isSwitchOn)
            updateData!!.is_pushable = "Y"
        else
            updateData!!.is_pushable = "N"

        if(!tetEmail!!.getText().equals("") && !tetEmail!!.getText().equals(userInfo!!.email))
            updateData!!.email = tetEmail!!.getText()

        if(!tetPassword!!.getText().equals(""))
            updateData!!.password = tetPassword!!.getText()

        if(imagePath.equals("")) {
            updateUserData(updateData!!)
        } else{
            var imageArray = ArrayList<String>()
            imageArray.add(imagePath)
            var uploader = S3Uploader(this, imageArray, this)
            uploader.upload()
        }
    }

    private fun updateUserData(data: UpdateUserData){
        var apiService = ApiManager.getInstance().apiService
        apiService.updateUserInfo(data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        Toast.makeText(this, getString(R.string.user_info_modified), Toast.LENGTH_SHORT).show()
                        refreshUserInfo()
                    }else{
                        Log.e("UPDATE_USERINFO", result.error.toString())
                        Toast.makeText(this, getString(R.string.fail_to_user_info_set), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("UPDATE_USERINFO", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_user_info_set), Toast.LENGTH_SHORT).show()
                })
    }

    private fun refreshUserInfo(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getMyInfo().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        MyInfoStore.myInfo = result.result
                        setResult(Activity.RESULT_OK)
                        finish()
                    }else{
                        Log.e("GET_USERINFO", result.error.toString())
                        Toast.makeText(this, getString(R.string.fail_to_user_info_set), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("GET_USERINFO", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_user_info_set), Toast.LENGTH_SHORT).show()
                })
    }
}
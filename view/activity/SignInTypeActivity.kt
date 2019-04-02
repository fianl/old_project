package vdream.vd.com.vdream.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.application.VDreamPreference
import vdream.vd.com.vdream.data.SignUpData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.view.dialog.CommonProgressDialog

class SignInTypeActivity: BaseActivity(), View.OnClickListener {
    var sns: String? = null
    var id: String? = null
    var email: String? = null

    override fun onClick(v: View?) {
        var type = ""
        when(v?.id){
            R.id.flBack-> {
                sendAppEvent("유저타입설정_헤더_뒤로가기")
                finish()
                return
            }
            R.id.tvSignInTeacher -> type = "TEACHER"
            R.id.tvSignInParents -> type = "PARENTS"
            R.id.tvSignInStudent -> type = "STUDENT"
            R.id.tvSignInNormal -> type = "NORMAL"
        }

        sendAppEvent("유저타입설정_유저타입_${(v as TextView).text}")

        if(id == null) {
            var intent = Intent(applicationContext, SignInActivity::class.java)
            intent.putExtra("TYPE", type)
            startActivity(intent)
        }else{
            userSignIn(type)
        }
    }

    var flBack: FrameLayout? = null
    var tvTeacher: TextView? = null
    var tvParents: TextView? = null
    var tvStudent: TextView? = null
    var tvNormal: TextView? = null
    var progressDialog: CommonProgressDialog? = null

    override fun onCreate(instance: Bundle?){
        super.onCreate(instance)
        setContentView(R.layout.activity_signin_type)

        flBack = findViewById(R.id.flBack)
        tvTeacher = findViewById(R.id.tvSignInTeacher)
        tvParents = findViewById(R.id.tvSignInParents)
        tvStudent = findViewById(R.id.tvSignInStudent)
        tvNormal = findViewById(R.id.tvSignInNormal)

        flBack?.setOnClickListener(this)
        tvTeacher?.setOnClickListener(this)
        tvParents?.setOnClickListener(this)
        tvStudent?.setOnClickListener(this)
        tvNormal?.setOnClickListener(this)

        getUserInfo()
    }

    private fun getUserInfo(){
        sns = intent.getStringExtra(getString(R.string.intent_key_name_sns))
        id = intent.getStringExtra(getString(R.string.intent_key_name_id))
        email = intent.getStringExtra(getString(R.string.intent_key_name_email))
    }

    private fun userSignIn(type: String){
        startProgressDialog()
        var apiService = ApiManager.getInstance().apiService
        var signBody = SignUpData()
        signBody.sns = sns!!
        signBody.kind = type
        signBody.username = id!!
        signBody.password = ""
        signBody.email = email!!

        apiService.signIn(signBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        ApiManager.getInstance().setToken(result.result!!.token)
                        VDreamPreference.setUserToken(result.result!!.token)
                        getMyInfo()
                    }else {
                        Log.e("SIGN_IN", result.error)
                        progressDialog?.dismiss()
                        Toast.makeText(this, getString(R.string.fail_to_sign_up), Toast.LENGTH_SHORT).show()
                    }
                }, {
                    error -> Log.e("SIGN_IN", error.toString())
                    progressDialog?.dismiss()
                    Toast.makeText(this, getString(R.string.fail_to_sign_up), Toast.LENGTH_SHORT).show()
                })
    }

    private fun getMyInfo(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getMyInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        progressDialog?.dismiss()
                        MyInfoStore.myInfo = result.result

                        moveToInterestsSet()
                        finish()
                    }else{
                        Log.e("MY_INFO", result.error)
                        progressDialog?.dismiss()
                        Toast.makeText(this, getString(R.string.fail_to_sign_up), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("MY_INFO", error.toString())
                    progressDialog?.dismiss()
                    Toast.makeText(this, getString(R.string.fail_to_sign_up), Toast.LENGTH_SHORT).show()
                })
    }

    private fun moveToInterestsSet(){
        var intent = Intent(this, InterestsSetActivity::class.java)
        intent.putExtra("IS_BACK", false)
        startActivity(intent)
        finish()
    }

    private fun startProgressDialog(){
        if(progressDialog == null)
            progressDialog = CommonProgressDialog(this)

        progressDialog?.show()
    }
}
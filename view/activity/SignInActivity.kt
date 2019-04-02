package vdream.vd.com.vdream.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.application.VDreamPreference
import vdream.vd.com.vdream.data.SignUpData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.view.component.TitledEditText
import vdream.vd.com.vdream.view.dialog.CommonProgressDialog

class SignInActivity: BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnSignIn -> {
                sendAppEvent("회원가입_가입")
                userSignIn(userType)
            }
        }
    }

    var tetID: TitledEditText? = null
    var tetEmail: TitledEditText? = null
    var tetPassword: TitledEditText? = null
    var tetPassConfirm: TitledEditText? = null
    var btnSignIn: Button? = null
    var progressDialog: CommonProgressDialog? = null

    var userType = ""
    var idDuplicated = false
    var emailDuplicated = false

    override fun onCreate(instance: Bundle?){
        super.onCreate(instance)
        setContentView(R.layout.activity_signin)

        userType = intent.getStringExtra("TYPE")
        tetID = findViewById(R.id.tetID)
        tetEmail = findViewById(R.id.tetEmail)
        tetPassword = findViewById(R.id.tetPassword)
        tetPassConfirm = findViewById(R.id.tetPasswordConfirm)
        btnSignIn = findViewById(R.id.btnSignIn)

        btnSignIn!!.setOnClickListener(this)

        tetInit()
    }

    private fun tetInit(){
        tetID?.setTitle(getString(R.string.id))
        tetID?.setExplain(getString(R.string.duplicateId))
        tetID?.setExplainColor(ContextCompat.getColor(this, R.color.red))
        tetID?.setOptionText(getString(R.string.duplicate_confirm))
        tetID?.setOptionButtonClickListener(View.OnClickListener {
            sendAppEvent("회원가입_아이디_중복확인")
            idDuplicatedConfirm()
        })
        tetID?.showOptionalButton()
        tetID?.setInputType(InputType.TYPE_CLASS_TEXT)
        tetID?.setImeOptionNext()
        tetID?.setEditTextHint(getString(R.string.id_hint))

        tetEmail?.setTitle(getString(R.string.email))
        tetEmail?.setExplain(getString(R.string.duplicateEmail))
        tetEmail?.setExplainColor(ContextCompat.getColor(this, R.color.red))
        tetEmail?.showOptionalButton()
        tetEmail?.setOptionText(getString(R.string.duplicate_confirm))
        tetEmail?.setOptionButtonClickListener(View.OnClickListener {
            sendAppEvent("회원가입_이메일_중복인")
            emailDuplicatedConfirm()
        })
        tetEmail?.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        tetEmail?.setEditTextHint(getString(R.string.email_hint))

        tetPassword?.setTitle(getString(R.string.password))
        tetPassword?.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        tetPassConfirm?.setTitle(getString(R.string.pass_confirm))
        tetPassConfirm?.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
    }

    private fun idDuplicatedConfirm(){
        var apiService = ApiManager.getInstance().apiService
        apiService.validateUsername(tetID!!.getText())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        idDuplicated = false
                        tetID?.hideExplain()
                        Toast.makeText(this, getString(R.string.enable_id), Toast.LENGTH_SHORT).show()
                    }else{
                        Log.e("USER_NAME", result.error)
                        idDuplicated = true
                        tetID?.showExplain()
                    }
                }, {
                    error ->
                    if(error is retrofit2.HttpException){
                        if(error.code() == 400){
                            idDuplicated = true
                            tetID?.showExplain()
                        }
                    }
                })
    }

    private fun emailDuplicatedConfirm(){
        var apiService = ApiManager.getInstance().apiService
        apiService.validateEmail(tetEmail!!.getText())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        emailDuplicated = false
                        tetEmail?.hideExplain()
                        Toast.makeText(this, getString(R.string.enable_email), Toast.LENGTH_SHORT).show()
                    }else{
                        emailDuplicated = true
                        tetEmail?.showExplain()
                    }
                }, {
                    error ->
                    if(error is retrofit2.HttpException){
                        if(error.code() == 400){
                            emailDuplicated = true
                            tetEmail?.showExplain()
                        }
                    }
                })
    }

    private fun userSignIn(userType: String){
        if(tetID!!.getText().equals("")){
            Toast.makeText(this, getString(R.string.input_user_id), Toast.LENGTH_SHORT).show()
            tetID?.requestTetFocus()
            return
        }

        if(idDuplicated){
            Toast.makeText(this, getString(R.string.duplicateId), Toast.LENGTH_SHORT).show()
            tetID?.requestTetFocus()
            return
        }

        if(emailDuplicated){
            Toast.makeText(this, getString(R.string.duplicateEmail), Toast.LENGTH_SHORT).show()
            tetEmail?.requestTetFocus()
            return
        }

        if(tetPassword!!.getText().equals("")){
            Toast.makeText(this, getString(R.string.input_user_password), Toast.LENGTH_SHORT).show()
            tetPassword?.requestTetFocus()
            return
        }

        if(tetPassConfirm!!.getText().equals("") || !tetPassConfirm!!.getText().equals(tetPassword!!.getText())){
            Toast.makeText(this, getString(R.string.confirm_user_password), Toast.LENGTH_SHORT).show()
            return
        }

        startProgressDialog()
        var apiService = ApiManager.getInstance().apiService
        var signBody = SignUpData()
        signBody.kind = userType
        signBody.username = tetID!!.getText()
        signBody.password = tetPassword!!.getText()
        signBody.email = tetEmail!!.getText()

        apiService.signIn(signBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        ApiManager.getInstance().setToken(result.result!!.token!!)
                        VDreamPreference.setUserToken(result.result!!.token!!)
                        getMyInfo()
                    }else{
                        Log.e("SIGN_IN", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_sign_up), Toast.LENGTH_SHORT).show()
                    }
                }, {
                    error -> Log.e("SIGN_IN", error.toString())
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
                        Toast.makeText(this, getString(R.string.fail_to_sign_up), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("MY_INFO", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_sign_up), Toast.LENGTH_SHORT).show()
                })
    }

    private fun moveToInterestsSet(){
        var intent = Intent(this, InterestsSetActivity::class.java)
        intent.putExtra("IS_BACK", false)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun startProgressDialog(){
        if(progressDialog == null)
            progressDialog = CommonProgressDialog(this)

        progressDialog?.show()
    }
}
package vdream.vd.com.vdream.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.gson.JsonObject
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.LoginButton
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.usermgmt.response.model.UserProfile
import com.kakao.util.exception.KakaoException
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.application.VDreamPreference
import vdream.vd.com.vdream.data.SignUpData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.view.component.TitledEditText
import vdream.vd.com.vdream.view.dialog.CommonProgressDialog
import java.security.MessageDigest

class LoginActivity: BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnLoginIn -> {
                if(tetID!!.getText().equals("")){
                    Toast.makeText(this, "ID를 입력해주세요", Toast.LENGTH_SHORT).show()
                    tetID?.requestTetFocus()
                    return
                }

                if(tetPassword!!.getText().equals("")){
                    Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                    tetPassword?.requestTetFocus()
                    return
                }
                sendAppEvent("로그인화면_일반로그인")
                normalLogin()
            }
            R.id.tvSignIn -> {
                sendAppEvent("로그인화면_회원가입버튼")
                moveToSignIn()
            }
            R.id.btnGoogleLogin -> {
                sendAppEvent("로그인화면_구글로그인")
                googleSignIn()
            }

            R.id.ivKakaoLoginCover -> {
                sendAppEvent("로그인화면_카카오로그인")
                btnKakaoLogin?.performClick()
            }
            R.id.ivFacebookLoginCover -> {
                sendAppEvent("로그인화면_페이스북로그인")
                btnFacebookLogin?.performClick()
            }
            R.id.ivGoogleLoginCover -> googleSignIn()
        }
    }

    var tetID: TitledEditText? = null
    var tetPassword: TitledEditText? = null
    var btnLogin: Button? = null
    var tvSignIn: TextView? = null
    var btnNaverLogin: OAuthLoginButton? = null
    var btnKakaoLogin: LoginButton? = null
    var btnFacebookLogin: com.facebook.login.widget.LoginButton? = null
    var btnGoogleLogin: SignInButton? = null
    var progressDialog: CommonProgressDialog? = null

    var ivKakaoCover: ImageView? = null
    var ivFacebookCover: ImageView? = null
    var ivGoogleCover: ImageView? = null

    val RC_SIGN_IN = 10
    val RC_KAKAO_SIGN_IN = 0
    val RC_FACEBOOK_SIGN_IN = 64206
    var callback: SessionCallback? = null
    var facebookCallbackManager: CallbackManager? = null

    override fun onCreate(instance: Bundle?){
        super.onCreate(instance)
        setContentView(R.layout.activity_login)

        var reqPer = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(reqPer, 1)

        tetID = findViewById(R.id.tetID)
        tetPassword = findViewById(R.id.tetPassword)
        btnLogin = findViewById(R.id.btnLoginIn)
        tvSignIn = findViewById(R.id.tvSignIn)
        btnNaverLogin = findViewById(R.id.btnNaverLogin)
        btnKakaoLogin = findViewById(R.id.btnKakaoLogin)
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin)
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)

        ivKakaoCover = findViewById(R.id.ivKakaoLoginCover)
        ivFacebookCover = findViewById(R.id.ivFacebookLoginCover)
        ivGoogleCover = findViewById(R.id.ivGoogleLoginCover)

        tetInit()

        var naverOauthHandler = object : OAuthLoginHandler() {
            override fun run(success: Boolean) {
                if (success) {
                    if(VDreamPreference.getTutorialView()) {
                        moveToMain()
                    } else {
                        VDreamPreference.setTutorialView(true)
                        moveToTutorial()
                    }
                } else {
                    Log.e("naver_login", "fail")
                }
            }
        }

        btnNaverLogin?.setOAuthLoginHandler(naverOauthHandler)

        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()

        facebookCallbackManager = CallbackManager.Factory.create()
        btnFacebookLogin?.setReadPermissions("public_profile")
        btnFacebookLogin?.setReadPermissions("email")
        btnFacebookLogin?.registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                Log.d("FACEBOOK_PERMISSION", result?.recentlyGrantedPermissions.toString())
                var request = GraphRequest.newMeRequest(result?.accessToken) { jsonObject, response ->
                    var id = jsonObject?.getString("id")
                    var email = jsonObject?.getString("email")

                    normalLogin(id!!, "", email, getString(R.string.api_sign_in_sns_facebook))
                }

                var parameter = Bundle()
                parameter.putString("fields", "id,name,email,gender,birthday")
                request.parameters = parameter
                request.executeAsync()
            }

            override fun onCancel() {
                Log.e("FACEBOOK_LOGIN", "canceled")
            }

            override fun onError(error: FacebookException?) {
                Log.e("FACEBOOK_LOGIN", error.toString())
            }

        })

        btnGoogleLogin?.setOnClickListener(this)
        btnLogin?.setOnClickListener(this)
        tvSignIn?.setOnClickListener(this)

        ivKakaoCover?.setOnClickListener(this)
        ivFacebookCover?.setOnClickListener(this)
        ivGoogleCover?.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                var signInAccount = task.result as GoogleSignInAccount
                var id = signInAccount.id
                var email = signInAccount.email

                normalLogin(id!!, "", email, getString(R.string.api_sign_in_sns_google))
            } catch (e: Exception) {
                Log.e("GOOGLE_LOGIN", e.toString())
            }
        }else if(requestCode == RC_KAKAO_SIGN_IN){
            if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
                if(resultCode == Activity.RESULT_OK)
                    startProgressDialog()

                return
            }
        }else if(requestCode == RC_FACEBOOK_SIGN_IN){
            facebookCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun tetInit(){
        tetID?.setTitle(getString(R.string.id))
        tetID?.setImeOptionNext()
        tetPassword?.setTitle(getString(R.string.password))
        tetPassword?.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
    }

    private fun googleSignIn(){
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        var signInClient = GoogleSignIn.getClient(this, gso)
        var signInIntent = signInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun moveToMain(){
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun moveToTutorial(){
        val intent = Intent(applicationContext, TutorialActivity::class.java)
        startActivity(intent)
        finish()
    }

    inner class SessionCallback : ISessionCallback {
        override fun onSessionOpened() {
            requestKakaoUserInfo()
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            if(exception != null) {
                Log.e("kakao_session", exception.toString())
                Toast.makeText(applicationContext, "카카오 로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestKakaoUserInfo() {
        UserManagement.getInstance().requestMe(object : MeResponseCallback() {
            override fun onSuccess(result: UserProfile?) {
                var id = result?.id
                var email = result?.email

                normalLogin(id.toString(), "", email, getString(R.string.api_sign_in_sns_kakao))
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.e("KAKAO_LOGIN", errorResult.toString())
            }

            override fun onNotSignedUp() {

            }

        })
    }

    private fun normalLogin(){
        startProgressDialog()
        var apiService = ApiManager.getInstance().apiService
        var loginData = SignUpData()
        loginData.username = tetID!!.getText()
        loginData.password = tetPassword!!.getText()
        apiService.login(loginData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    result ->
                    if(result.status == "Y") {
                        ApiManager.getInstance().setToken(result.result!!.token)
                        VDreamPreference.setUserToken(result.result!!.token)
                        getMyInfo()
                    }else{
                        Log.e("LOGIN_ERROR", result.error)
                        progressDialog?.dismiss()
                        Toast.makeText(this, getString(R.string.fail_to_sign_in), Toast.LENGTH_SHORT).show()
                    }
                }, {error ->
                    Log.e("LOGIN_ERROR", error.toString())
                    progressDialog?.dismiss()
                    Toast.makeText(this, getString(R.string.fail_to_sign_in), Toast.LENGTH_SHORT).show()
                })
    }

    private fun normalLogin(id: String, pw: String, email: String?, sns: String){
        startProgressDialog()
        var apiService = ApiManager.getInstance().apiService
        var loginData = SignUpData()
        loginData.sns = sns
        loginData.username = id
        loginData.password = pw
        apiService.login(loginData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    result ->
                    if(result.status == "Y") {
                        ApiManager.getInstance().setToken(result.result!!.token)
                        VDreamPreference.setUserToken(result.result!!.token)
                        getMyInfo()
                    }else{
                        Log.e("SNS_LOGIN_ERROR", result.error)

                        progressDialog?.dismiss()
                        if(email == null){
                            snsSignUp(sns, id!!.toString(), "")
                        }else {
                            snsSignUp(sns, id!!.toString(), email)
                        }
                    }
                }, { error ->
                    Log.e("SNS_LOGIN_ERROR", error.toString())

                    progressDialog?.dismiss()
                    Toast.makeText(this, getString(R.string.fail_to_login), Toast.LENGTH_SHORT).show()
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

                        if (VDreamPreference.getTutorialView()) {
                            moveToMain()
                        } else {
                            moveToTutorial()
                        }
                        finish()
                    }else{
                        Log.e("MY_INFO", result.error)
                        progressDialog?.dismiss()
                        Toast.makeText(this, getString(R.string.fail_to_sign_in), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("MY_INFO", error.toString())
                    progressDialog?.dismiss()
                    Toast.makeText(this, getString(R.string.fail_to_sign_in), Toast.LENGTH_SHORT).show()
                })
    }

    private fun moveToSignIn(){
        var intent = Intent(this, SignInActivity::class.java)
        intent.putExtra(getString(R.string.intent_key_name_type), "")
        startActivity(intent)
    }

    private fun snsSignUp(sns: String, id: String, email: String){
        startProgressDialog()
        var apiService = ApiManager.getInstance().apiService
        var signBody = SignUpData()
        signBody.sns = sns
        signBody.kind = ""
        signBody.username = id
        signBody.password = ""
        signBody.email = email

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

    private fun startProgressDialog(){
        if(progressDialog == null)
            progressDialog = CommonProgressDialog(this)

        progressDialog?.show()
    }
}
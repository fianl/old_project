package vdream.vd.com.vdream.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.widget.TextView
import com.nhn.android.naverlogin.OAuthLoginHandler
import vdream.vd.com.vdream.R
import com.kakao.auth.ISessionCallback
import com.kakao.util.exception.KakaoException
import com.kakao.auth.Session
import vdream.vd.com.vdream.application.VDreamPreference
import java.security.MessageDigest


/**
 * Created by SHINLIB on 2018-03-15.
 */
class IntroActivity: BaseActivity() {
    var tvLogin: TextView? = null
    var tvSignin: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        tvLogin = findViewById(R.id.tvLogin) as TextView
        tvSignin = findViewById(R.id.tvSignin) as TextView

        tvLogin!!.setOnClickListener({View ->
            moveToLogin()
        })

        tvSignin?.setOnClickListener({View ->
            moveToSiginIn()
        })
    }

    protected fun moveToLogin() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun moveToSiginIn(){
        val intent = Intent(applicationContext, SignInTypeActivity::class.java)
        startActivity(intent)
    }


}
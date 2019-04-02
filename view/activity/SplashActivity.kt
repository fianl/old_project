package vdream.vd.com.vdream.view.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.amazonaws.mobile.auth.core.IdentityManager
import com.amazonaws.mobile.auth.core.StartupAuthResultHandler
import com.amazonaws.mobile.config.AWSConfiguration
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.application.VDreamPreference
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.store.MyInfoStore

/**
 * Created by SHINLIB on 2018-03-21.
 */
class SplashActivity: BaseActivity() {
    var schemeIdx = -1
    override fun onCreate(instance: Bundle?){
        super.onCreate(instance)
        setContentView(R.layout.activity_splash)

        var awsConfig = AWSConfiguration(applicationContext)
        var identityManager = IdentityManager(applicationContext, awsConfig);
        IdentityManager.setDefaultIdentityManager(identityManager);
        identityManager.doStartupAuth(this) {
            if(it.isUserAnonymous){
                Log.d("AWS_AUTH_ANONYMOUS", "ANONYMOUS")
            }

            if(it.isUserSignedIn){
                Log.d("AWS_AUTH_SIGNED", "SIGNED")
            }
        }

        if(intent.scheme != null){
            schemeIdx = intent.dataString.split("=")[1].toInt()
        }

        confirmAppVersion()
    }

    private fun getMyInfo(){
        var token = VDreamPreference.getUserToken()

        if(token.equals(""))
            moveToLogin()
        else {
            ApiManager.getInstance().setToken(token)
            var apiService = ApiManager.getInstance().apiService
            apiService.getMyInfo()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe({ result ->
                        if(result.status == "Y") {
                            MyInfoStore.myInfo = result.result
                            moveToMain()
                        }else{
                            Log.e("MY_INFO", result.error)
                            moveToLogin()
                        }
                    }, { error ->
                        Log.e("MY_INFO", error.toString())
                        moveToLogin()
                    })
        }
    }

    private fun confirmAppVersion(){
        var versionCode = packageManager.getPackageInfo(packageName, 0).versionCode

        var apiService = ApiManager.getInstance().apiService
        apiService.getAppVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        if (versionCode < result.result!!.number && result.result!!.alert > 1)
                            createUpdateDialog(result.result!!.alert)
                        else
                            getMyInfo()
                    }else{
                        Log.e("APP_VERSION", result.error)
                        getMyInfo()
                    }
                }, { error ->
                    Log.e("APP_VERSION", error.toString())
                    getMyInfo()
                })

    }

    private fun moveToMain(){
        var intent = Intent(this, MainActivity::class.java)
        if(schemeIdx != -1)
            intent.putExtra(getString(R.string.intent_key_name_index), schemeIdx)
        startActivity(intent)
        finish()
    }

    private fun moveToLogin(){
        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun createUpdateDialog(alert: Int){
        var update = AlertDialog.Builder(this)
        update.setTitle(getString(R.string.update_alert_title))
        update.setMessage(getString(R.string.update_alert_content))
        update.setPositiveButton(R.string.update_alert_positiv_button, object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                var intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(getString(R.string.app_market_url))
                startActivity(intent)
            }
        })

        if(alert == 2){
            update.setNegativeButton(R.string.update_alert_negative_button_later, object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    getMyInfo()
                }
            })
        }else{
            update.setNegativeButton(R.string.update_alert_negative_button_close, object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if(alert == 3)
                        finish()
                    else
                        getMyInfo()
                }
            })
        }

        update.create().show()
    }

    private fun firebaseTokenAdd() {
        var token = FirebaseInstanceId.getInstance().token

    }
}
package vdream.vd.com.vdream.application

import android.content.Context
import android.support.multidex.MultiDexApplication
import com.amazonaws.mobile.auth.core.IdentityManager
import com.amazonaws.mobile.config.AWSConfiguration
import com.kakao.auth.*
import com.nhn.android.naverlogin.OAuthLogin
import com.kakao.auth.IApplicationConfig


/**
 * Created by SHINLIB on 2018-03-19.
 */
class VDreamApplication: MultiDexApplication() {
    companion object {
        var instance: VDreamApplication? = null

        fun getApplication(): VDreamApplication {
            if(instance == null)
                instance = VDreamApplication()

            return instance as VDreamApplication
        }
    }

    override fun onCreate(){
        super.onCreate()
        instance = this
        awsInitialize()
        naverInitialize()
        KakaoSDK.init(KakaoSDKAdapter())
        VDreamPreference.setContext(applicationContext)
    }

    private fun naverInitialize(){
        var naverOauth = OAuthLogin.getInstance()
        naverOauth.init(applicationContext, "AGSHDGdjRpAY1JdxbRSl", "DXKK3PaGmK", "VDream")
    }

    private fun awsInitialize() {
        var awsConfiguration = AWSConfiguration(applicationContext)
        if (IdentityManager.getDefaultIdentityManager() == null) {
            var identityManager = IdentityManager(applicationContext, awsConfiguration)
            IdentityManager.setDefaultIdentityManager(identityManager)
        }
    }

    inner class KakaoSDKAdapter: KakaoAdapter() {
        override fun getSessionConfig(): ISessionConfig {
            return object  : ISessionConfig {
                override fun isSaveFormData(): Boolean {
                    return true
                }

                override fun getAuthTypes(): Array<AuthType> {
                    return arrayOf(AuthType.KAKAO_LOGIN_ALL)
                }

                override fun isSecureMode(): Boolean {
                    return false
                }

                override fun getApprovalType(): ApprovalType {
                    return ApprovalType.INDIVIDUAL
                }

                override fun isUsingWebviewTimer(): Boolean {
                    return false
                }

            }
        }

        override fun getApplicationConfig(): IApplicationConfig {
            return object : IApplicationConfig {
                override fun getApplicationContext(): Context {
                    return VDreamApplication.getApplication()
                }
            }
        }
    }
}
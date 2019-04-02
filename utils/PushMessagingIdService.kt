package vdream.vd.com.vdream.utils

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.application.VDreamPreference
import vdream.vd.com.vdream.data.UpdateUserData
import vdream.vd.com.vdream.network.ApiManager

class PushMessagingIdService: FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        var token = FirebaseInstanceId.getInstance().token
        Log.d("PUSH_TOKEN", token)
        sendRegisterationToServer(token)
    }

    private fun sendRegisterationToServer(token: String?){
        if(token != null && token != ""){
            var oldToken = VDreamPreference.getPushToken()

            if(oldToken != token) {
                VDreamPreference.setPushToken(token)
                updateUserMessagingId(token)
            }
        }
    }

    private fun updateUserMessagingId(msgId: String){
        var registerData = UpdateUserData()
        registerData.messaging_id = msgId
        var apiService = ApiManager.getInstance().apiService
        apiService.updateUserInfo(registerData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        Log.d("PUSH_TOKEN_UPDATE", "SUCCESS")
                    }else{
                        Log.e("PUSH_TOKEN_UPDATE", result.error)
                    }
                }, { err ->
                    Log.e("PUSH_TOKEN_UPDATE", err.toString())
                })
    }
}
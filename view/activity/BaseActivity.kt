package vdream.vd.com.vdream.view.activity

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric
import vdream.vd.com.vdream.BuildConfig
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.store.MyInfoStore

open class BaseActivity: FragmentActivity() {
    var analytics: FirebaseAnalytics? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = FirebaseAnalytics.getInstance(this)

        if(BuildConfig.DEBUG)
           Fabric.with(this, Crashlytics())
    }

    fun getActivityAnalytics(): FirebaseAnalytics{
        if(analytics == null)
            analytics = FirebaseAnalytics.getInstance(this)

        return analytics!!
    }

    fun sendAppEvent(name: String) {
        var bundle = Bundle()
        var user = MyInfoStore.myInfo?.uuid

        if(user == null)
            bundle.putString(getString(R.string.firebase_event_user_id), getString(R.string.firebase_event_user_no_account))
        else
            bundle.putString(getString(R.string.firebase_event_user_id), user)

        bundle.putString(getString(R.string.firebase_event_activity), javaClass.simpleName)

        analytics?.logEvent(name, bundle)
    }
}
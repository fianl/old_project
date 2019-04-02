package vdream.vd.com.vdream.application

import android.content.Context
import android.content.SharedPreferences
import vdream.vd.com.vdream.R

object VDreamPreference {
    private var context: Context? = null
    private var preference: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    fun setContext(context: Context){
        this.context = context
        preference = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
        editor = preference?.edit()
    }

    fun setTutorialView(isView: Boolean){
        editor?.putBoolean(context!!.getString(R.string.tutorial_view), isView)
        editor?.commit()
    }

    fun getTutorialView(): Boolean {
        return preference!!.getBoolean(context!!.getString(R.string.tutorial_view), false)
    }

    fun setUserToken(token: String){
        editor?.putString(context!!.getString(R.string.user_token), token)
        editor?.commit()
    }

    fun getUserToken(): String{
        return preference!!.getString(context!!.getString(R.string.user_token), "")
    }

    fun setPushToken(token: String) {
        editor?.putString(context!!.getString(R.string.push_token), token)
    }

    fun getPushToken(): String {
        return preference!!.getString(context!!.getString(R.string.push_token), "")
    }
}
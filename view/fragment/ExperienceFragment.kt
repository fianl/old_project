package vdream.vd.com.vdream.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.FeedDetailData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.view.activity.ExperienceDetailActivity
import vdream.vd.com.vdream.view.component.ClassExperienceAnnounceVIew

class ExperienceFragment: Fragment() {
    var llContainer: LinearLayout? = null
    var expList = ArrayList<FeedDetailData>()
    var page = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_experience, container, false)
        llContainer = rootView.findViewById(R.id.llExpContainer)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getExpList()
    }

    private fun getExpList(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getExperiecne(page + 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({result ->
                    if(result.status == "Y"){
                        page = result.result!!.page
                        expList.addAll(result.result!!.data!!.toCollection(ArrayList()))
                        setExpListToContainer(result.result!!.data!!.toCollection(ArrayList()))
                    }else{
                        Log.e("GET_EXP", result.error)
                        Toast.makeText(context!!, getString(R.string.fail_to_get_exp), Toast.LENGTH_SHORT).show()
                    }
                }, {err ->
                    Log.e("GET_EXP", err.toString())
                    Toast.makeText(context!!, getString(R.string.fail_to_get_exp), Toast.LENGTH_SHORT).show()
                })
    }

    private fun setExpListToContainer(list: ArrayList<FeedDetailData>){
        for (data in expList) {
            var announceView = ClassExperienceAnnounceVIew(context!!, null)
            announceView.setData(data)
            llContainer?.addView(announceView)
            announceView.setOnClickListener {
                var intent = Intent(context!!, ExperienceDetailActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_feeddata), data)
                startActivity(intent)
            }
        }
    }
}
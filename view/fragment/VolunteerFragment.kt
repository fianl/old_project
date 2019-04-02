package vdream.vd.com.vdream.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.VolunteerEducationListData
import vdream.vd.com.vdream.data.VolunteerParicipantData
import vdream.vd.com.vdream.network.VolunteerApiManager
import vdream.vd.com.vdream.view.activity.VolunteerEduMoreActivity
import vdream.vd.com.vdream.view.activity.VolunteerEducationDetailActivity
import vdream.vd.com.vdream.view.activity.VolunteerPartMoreActivity
import vdream.vd.com.vdream.view.activity.VolunteerParticipantDetailActivity
import vdream.vd.com.vdream.view.component.VolunteerParticipantItemView

class VolunteerFragment: Fragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvVolunteerParticipantMore -> {
                var intent = Intent(context!!, VolunteerPartMoreActivity::class.java)
                startActivity(intent)
            }
            R.id.tvVolunteerStudyMore -> {
                var intent = Intent(context!!, VolunteerEduMoreActivity::class.java)
                startActivity(intent)
            }
        }
    }

    var vpBanner: ViewPager? = null
    var llIndicator: LinearLayout? = null
    var tvParticipantMore: TextView? = null
    var llParticipantContainer: LinearLayout? = null
    var tvStudyMore: TextView? = null
    var llStudyContainer: LinearLayout? = null
    var participantList = ArrayList<VolunteerParicipantData>()
    var educationList = ArrayList<VolunteerEducationListData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_volunteer, container, false)
        vpBanner = rootView.findViewById(R.id.vpVolunteerBanner)
        llIndicator = rootView.findViewById(R.id.llVolunteerBannerIndicator)
        tvParticipantMore = rootView.findViewById(R.id.tvVolunteerParticipantMore)
        llParticipantContainer = rootView.findViewById(R.id.llVolunteerParticipantInfoContainer)
        tvStudyMore = rootView.findViewById(R.id.tvVolunteerStudyMore)
        llStudyContainer = rootView.findViewById(R.id.llVolunteerStudyInfoContainer)

        vpBanner?.adapter = BannerAdapter()
        tvParticipantMore?.setOnClickListener(this)
        tvStudyMore?.setOnClickListener(this)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getVolunteerParticipantInfo()
        getVolunteerEcucationInfo()
    }

    private fun getVolunteerParticipantInfo(){
        var key = "syrXVaZv9zWaZvewJRWD5Cb2c%2F6PYzUs0g3qjEGPjPy0rrtp677y7VjYvU4GbWLcPB42kqKJrda76f6QRZbaZg%3D%3D"
        var type = "json"
        var rows = 5
        var vApiService = VolunteerApiManager.getInstance().apiService
        vApiService.getVolunteerApplyInfo(key, type, 1, rows)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    participantList = result.response!!.body!!.items!!.item!!.toCollection(ArrayList())
                    setParticipantsData()
                    Log.d("VOLUNTEER", "SUCCESS")
                }, { err ->
                    Log.e("VOLUNTEER", err.toString())
                })
    }

    private fun setParticipantsData(){
        for(data in participantList){
            var itemView = VolunteerParticipantItemView(context!!)
            itemView.setData(data)

            llParticipantContainer?.addView(itemView)

            itemView.setOnClickListener({
                var intent = Intent(context!!, VolunteerParticipantDetailActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_volunteer_number), data.progrmRegistNo)
                startActivity(intent)
            })
        }
    }

    private fun getVolunteerEcucationInfo(){
        var type = "json"
        var rows = 5
        var vApiService = VolunteerApiManager.getInstance().apiService
        vApiService.getVolunteerEducationList(type, 1, rows)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    educationList = result.response!!.body!!.items!!.item!!.toCollection(ArrayList())
                    setEduationData()
                }, { err ->
                    Log.e("VOLUNTEER_EDU", err.message)
                })
    }

    private fun setEduationData(){
        for(data in educationList){
            var itemView = VolunteerParticipantItemView(context!!)
            itemView.setData(data)

            llStudyContainer?.addView(itemView)

            itemView.setOnClickListener({
                var intent = Intent(context!!, VolunteerEducationDetailActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_volunteer_number), data.crclmRegistNo)
                startActivity(intent)
            })
        }
    }

    inner class BannerAdapter: PagerAdapter {
        constructor()

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return obj == view
        }

        override fun getCount(): Int {
            return 1
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var iv = ImageView(context)
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            iv.setImageResource(R.drawable.volunteer_banner)

            container.addView(iv)

            return iv
        }
    }
}
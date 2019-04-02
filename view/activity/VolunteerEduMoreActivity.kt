package vdream.vd.com.vdream.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.VolunteerEducationListData
import vdream.vd.com.vdream.network.VolunteerApiManager
import vdream.vd.com.vdream.view.component.VolunteerParticipantItemView

class VolunteerEduMoreActivity: BaseActivity() {
    var flBack: FrameLayout? = null
    var svMore: ScrollView? = null
    var llContiner: LinearLayout? = null

    var page = 0
    var rows = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer_education_more)

        flBack = findViewById(R.id.flVolunteerEduBack)
        svMore = findViewById(R.id.svVolunteerEduMore)
        llContiner = findViewById(R.id.llVolunteerEduMore)

        flBack?.setOnClickListener({
            finish()
        })

        svMore?.viewTreeObserver!!.addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
            if(!svMore!!.canScrollVertically(1)){
                getVolunteerEcucationInfo()
            }
        })

        getVolunteerEcucationInfo()
    }

    private fun getVolunteerEcucationInfo(){
        var type = "json"
        var vApiService = VolunteerApiManager.getInstance().apiService
        vApiService.getVolunteerEducationList(type, (page+1), rows)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    page = result.response!!.body!!.pageNo!!
                    setEduationData(result.response!!.body!!.items!!.item!!.toCollection(ArrayList()))
                }, { err ->
                    Log.e("VOLUNTEER_EDU", err.message)
                })
    }

    private fun setEduationData(list: ArrayList<VolunteerEducationListData>){
        for(data in list){
            var itemView = VolunteerParticipantItemView(this)
            itemView.setData(data)

            llContiner?.addView(itemView)

            itemView.setOnClickListener({
                var intent = Intent(this, VolunteerEducationDetailActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_volunteer_number), data.crclmRegistNo)
                startActivity(intent)
            })
        }
    }
}
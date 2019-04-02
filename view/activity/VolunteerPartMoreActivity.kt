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
import vdream.vd.com.vdream.data.VolunteerParicipantData
import vdream.vd.com.vdream.network.VolunteerApiManager
import vdream.vd.com.vdream.view.component.VolunteerParticipantItemView

class VolunteerPartMoreActivity: BaseActivity() {
    var flBack: FrameLayout? = null
    var svMore: ScrollView? = null
    var llContiner: LinearLayout? = null

    var page = 0
    var rows = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer_participant_more)

        flBack = findViewById(R.id.flVolunteerEduBack)
        svMore = findViewById(R.id.svVolunteerPartMore)
        llContiner = findViewById(R.id.llVolunteerPartMore)

        flBack?.setOnClickListener({
            finish()
        })

        svMore?.viewTreeObserver!!.addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
            if(!svMore!!.canScrollVertically(1)){
               getVolunteerParticipantInfo()
            }
        })

        getVolunteerParticipantInfo()
    }

    private fun getVolunteerParticipantInfo(){
        var key = "syrXVaZv9zWaZvewJRWD5Cb2c%2F6PYzUs0g3qjEGPjPy0rrtp677y7VjYvU4GbWLcPB42kqKJrda76f6QRZbaZg%3D%3D"
        var type = "json"
        var vApiService = VolunteerApiManager.getInstance().apiService
        vApiService.getVolunteerApplyInfo(key, type, (page + 1), rows)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    page = result.response!!.body!!.pageNo
                    setParticipantsData(result.response!!.body!!.items!!.item!!.toCollection(ArrayList()))
                    Log.d("VOLUNTEER", "SUCCESS")
                }, { err ->
                    Log.e("VOLUNTEER", err.toString())
                })
    }

    private fun setParticipantsData(list: ArrayList<VolunteerParicipantData>){
        for(data in list){
            var itemView = VolunteerParticipantItemView(this)
            itemView.setData(data)

            llContiner?.addView(itemView)

            itemView.setOnClickListener({
                var intent = Intent(this, VolunteerParticipantDetailActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_volunteer_number), data.progrmRegistNo)
                startActivity(intent)
            })
        }
    }
}
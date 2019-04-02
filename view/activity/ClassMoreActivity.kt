package vdream.vd.com.vdream.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.ClassData
import vdream.vd.com.vdream.data.ClassListRequestData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.view.component.ClassVerticalListAdapter

class ClassMoreActivity: BaseActivity(), View.OnClickListener {
    var kind = ""
    var page = 1
    var preScrollstate = 0
    var curScrollstate = 0
    var isScrolled = false
    var dataList = ArrayList<ClassData>()

    var flBack: FrameLayout? = null
    var flRefresh: FrameLayout? = null
    var rcList: RecyclerView? = null
    var layoutManager: LinearLayoutManager? = null
    var adapter: ClassVerticalListAdapter? = null

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flMoreBack -> finish()
            R.id.flMoreRefresh -> {
                dataList.clear()
                getClassList(page)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kind = intent.getStringExtra(getString(R.string.intent_key_name_class_kind))
        kind = kind.toUpperCase()

        setContentView(R.layout.activity_class_more)
        flBack = findViewById(R.id.flMoreBack)
        flRefresh = findViewById(R.id.flMoreRefresh)
        rcList = findViewById(R.id.rcvClassMoreList)

        flBack?.setOnClickListener(this)
        flRefresh?.setOnClickListener(this)

        layoutManager = LinearLayoutManager(this)
        rcList?.layoutManager = layoutManager
        rcList!!.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                preScrollstate = curScrollstate
                curScrollstate = newState

                if(preScrollstate == RecyclerView.SCROLL_STATE_DRAGGING && curScrollstate == RecyclerView.SCROLL_STATE_IDLE){
                    isScrolled = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var firstVisiblePosition = layoutManager!!.findFirstVisibleItemPosition()
                var visibleCount = layoutManager!!.childCount
                var totalCount = layoutManager!!.itemCount

                if((firstVisiblePosition + visibleCount >= totalCount) /*&& isScrolled*/){
                    getClassList(page+1)
                    isScrolled = false
                }
            }
        })

        getClassList(page)
    }

    private fun getClassList(page: Int){
        var requestBody = ClassListRequestData()
        //requestBody.kind = kind
        var apiService = ApiManager.getInstance().apiService
        apiService.getClassList(page, requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        this.page = result.result!!.page
                        dataList.addAll(result.result!!.data!!.toCollection(ArrayList()))

                        if(adapter == null){
                            adapter = ClassVerticalListAdapter(this, dataList, R.layout.view_class_list_item)
                            rcList?.adapter = adapter
                        }else{
                            adapter?.notifyDataSetChanged()
                        }
                    }else{
                        Log.e("GET_CLASS", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_get_class_list), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("GET_CLASS", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_get_class_list), Toast.LENGTH_SHORT).show()
                })
    }
}
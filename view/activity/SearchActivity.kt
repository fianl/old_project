package vdream.vd.com.vdream.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.ClassData
import vdream.vd.com.vdream.data.ClassListRequestData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.view.component.ClassVerticalListAdapter

class SearchActivity: BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flSearch -> {
                sendAppEvent("클래스검색_검색버튼")
                page = 1
                search(etKeyword!!.text.toString(), page)
            }
            R.id.flSearchClear -> {
                sendAppEvent("클래스검색_검색어삭제")
                etKeyword?.text!!.clear()

                if(adapter != null){
                    dataList.clear()
                    adapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    var flSearch: FrameLayout? = null
    var etKeyword: EditText? = null
    var flClear: FrameLayout? = null
    var rcResult: RecyclerView? = null
    var layoutManager: LinearLayoutManager? = null
    var adapter: ClassVerticalListAdapter? = null
    var keyword = ""
    var preScrollstate = 0
    var curScrollstate = 0
    var isScrolled = false

    var dataList = ArrayList<ClassData>()
    var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        flSearch = findViewById(R.id.flSearch)
        etKeyword = findViewById(R.id.etSearchKeyword)
        flClear = findViewById(R.id.flSearchClear)
        rcResult = findViewById(R.id.rcvSearchResult)

        flSearch?.setOnClickListener(this)
        flClear?.setOnClickListener(this)

        layoutManager = LinearLayoutManager(this)
        layoutManager?.orientation = LinearLayoutManager.VERTICAL
        rcResult?.layoutManager = layoutManager

        etKeyword?.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                search(etKeyword!!.text.toString(), page)
            }

            false
        }

        rcResult!!.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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

                if(firstVisiblePosition + visibleCount >= totalCount && isScrolled){
                    search(keyword, (page+1))
                    isScrolled = false
                }
            }
        })
    }

    private fun search(keyword: String, targetPage: Int){
        if(keyword == ""){
            Toast.makeText(this, getString(R.string.input_search_keyword), Toast.LENGTH_SHORT).show()
            return
        }

        this.keyword = keyword

        var requestBody = ClassListRequestData()
        requestBody.kind = getString(R.string.get_class_kind_search)
        requestBody.search_text = keyword
        var apiService = ApiManager.getInstance().apiService
        apiService.getClassList(page, requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        page = result.result!!.page
                        dataList.addAll(result.result!!.data!!.toCollection(ArrayList()))

                        if (adapter == null) {
                            adapter = ClassVerticalListAdapter(this, dataList, R.layout.view_class_list_item)
                            rcResult!!.adapter = adapter
                        } else
                            adapter!!.notifyDataSetChanged()
                    }else{
                        Log.e("CLASS_SEARCH", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_search_class), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("CLASS_SEARCH", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_search_class), Toast.LENGTH_SHORT).show()
                })
    }
}
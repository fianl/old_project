package vdream.vd.com.vdream.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.CategoryData
import vdream.vd.com.vdream.data.InterestUpdateData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.view.component.InterestTileView
import vdream.vd.com.vdream.view.dialog.CommonProgressDialog

class InterestsSetActivity: BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvApplyInterests -> {
                sendAppEvent("관심사등록_등록하기")
                for(interest in interestCategories){
                    if(interest.isSelected)
                        selectedCategories.add(interest.idx)
                }

                if(selectedCategories.size == 0) {
                    Toast.makeText(this, "관심분야를 선택해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    startProgressDialog()
                    registerInterestes()
                }

            }

            R.id.flInterestsSetBack -> {
                sendAppEvent("관심사등록_헤더_뒤로가기")
                onBackPressed()
            }
        }
    }

    var flBack: FrameLayout? = null
    var gvInterests: GridView? = null
    var tvApply: TextView? = null
    var interestCategories = ArrayList<CategoryData>()
    var selectedCategories = ArrayList<Int>()
    var progressDialog: CommonProgressDialog? = null
    var gridItemSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests_set)
        gridItemSize = ((resources.displayMetrics.widthPixels - (20 * resources.displayMetrics.density))/3).toInt()
        init()
        getAllInterests()
    }

    private fun init(){
        flBack = findViewById(R.id.flInterestsSetBack)
        gvInterests = findViewById(R.id.gvInterests)
        tvApply = findViewById(R.id.tvApplyInterests)

        flBack?.setOnClickListener(this)

        gvInterests?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            var item = view as InterestTileView

            if(item.itemSelected) {
                item.itemUnSelect()
                interestCategories[position].isSelected = false
            } else {
                item.itemSelecte()
                interestCategories[position].isSelected = true
            }
        }

        tvApply?.setOnClickListener(this)
    }

    private fun getAllInterests(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getInterestCategory()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        interestCategories = result.result!!.toCollection(ArrayList<CategoryData>())
                        gvInterests?.adapter = InterestAdapter()
                    }else{
                        Log.e("GET_INTEREST", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_category_load), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("GET_INTEREST", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_category_load), Toast.LENGTH_SHORT).show()
                })

    }

    private fun registerInterestes(){
        var updateCategory = InterestUpdateData()
        updateCategory.interests = Array<Int>(selectedCategories.size, {i -> selectedCategories.get(i)})

        var apiService = ApiManager.getInstance().apiService
        apiService.updateMyInterests(updateCategory)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    progressDialog?.dismiss()
                    Toast.makeText(this, getString(R.string.interests_updated), Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, TutorialActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }, { error ->
                    progressDialog?.dismiss()
                    Toast.makeText(this, getString(R.string.fail_to_register_interests), Toast.LENGTH_SHORT).show()
                    Log.e("UPDATE_CATEGORY", error.toString())
                })
    }

    private fun startProgressDialog(){
        if(progressDialog == null)
            progressDialog = CommonProgressDialog(this)

        progressDialog?.show()
    }

    inner class InterestAdapter: BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var itemView = InterestTileView(applicationContext)
            itemView.setImage(interestCategories[position].image)
            var params = FrameLayout.LayoutParams(gridItemSize, gridItemSize)
            itemView.ivImage!!.layoutParams = params
            itemView.ivCover!!.layoutParams = params

            if(interestCategories[position].isSelected)
                itemView.itemSelecte()
            else
                itemView.itemUnSelect()

            return itemView
        }

        override fun getItem(position: Int): Any {
            return interestCategories[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return interestCategories.size
        }

    }
}
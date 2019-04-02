package vdream.vd.com.vdream.view.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import me.crosswall.lib.coverflow.CoverFlow
import me.crosswall.lib.coverflow.core.PagerContainer
import net.daum.mf.map.api.MapPoint
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.CategoryData
import vdream.vd.com.vdream.data.ClassData
import vdream.vd.com.vdream.data.ClassListRequestData
import vdream.vd.com.vdream.data.CompanyData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils
import vdream.vd.com.vdream.view.activity.ClassDetailAcitivity
import vdream.vd.com.vdream.view.activity.MainActivity
import vdream.vd.com.vdream.view.component.ClassTileView
import vdream.vd.com.vdream.view.component.ClassesViewGroup

class ClassListFragment: Fragment() {
    var llCategories: LinearLayout? = null
    var llContainer1: LinearLayout? = null
    var llContainer2: LinearLayout? = null
    var width = 0
    var classList = ArrayList<ClassData>()
    var categories = ArrayList<CategoryData>()
    var tileSize = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_class_list, container, false)

        llCategories = rootView.findViewById(R.id.llCategoryContainer)
        llContainer1 = rootView.findViewById(R.id.llClassContainer1)
        llContainer2 = rootView.findViewById(R.id.llClassContainer2)

        width = context!!.resources.displayMetrics.widthPixels

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tileSize = resources.displayMetrics.widthPixels/2 - (resources.displayMetrics.density*2).toInt()

        categories.add(CategoryData())
        getFirstCategories()
        getClassList("")
    }

    private fun getClassList(keyword: String){
        var requestBody = ClassListRequestData()
        if(keyword != "") {
            requestBody.kind = "SEARCH"
            requestBody.search_text = keyword
        }
        var apiService = ApiManager.getInstance().apiService
        apiService.getClassList(1, requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        classList = result.result!!.data!!.toCollection(ArrayList<ClassData>())
                        setClassTileList()
                    }else{
                        Log.e("INSTITUTE_LIST", result.error)
                    }
                }, { error ->
                    Log.e("INSTITUTE_LIST", error.toString())
                })
    }

    private fun setClassTileList(){
        if(llContainer1?.childCount!! > 0)
            llContainer1?.removeAllViews()

        if(llContainer2?.childCount!! > 0)
            llContainer2?.removeAllViews()

        for(idx in 0..classList.lastIndex){
            var tile = ClassTileView(context!!)
            var params = LinearLayout.LayoutParams(tileSize, tileSize)
            params.bottomMargin = (resources.displayMetrics.density * 4).toInt()
            tile.layoutParams = params
            tile.setData(classList[idx])

            tile.setOnClickListener {
                var intent = Intent(context!!, ClassDetailAcitivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_index), classList[idx].classroom!!.idx)
                startActivity(intent)
            }

            if(idx%2 == 0)
                llContainer1?.addView(tile)
            else
                llContainer2?.addView(tile)
        }
    }

    private fun setCategoryListUi() {
        if(categories == null || categories.isEmpty())
            return

        for(idx in 0..categories.lastIndex){
            var category = categories[idx]
            var view = LayoutInflater.from(context!!).inflate(R.layout.view_class_list_category_item, llCategories!!, false)
            var ivCategory = view.findViewById<ImageView>(R.id.ivCategoryImage)
            var ivCover = view.findViewById<ImageView>(R.id.ivCategoryCover)

            var params = FrameLayout.LayoutParams(tileSize/2, tileSize/2)

            if(idx == 0) {
                params.leftMargin = (resources.displayMetrics.density * 16).toInt()
                ivCover.visibility = View.GONE
            } else
                params.leftMargin = (resources.displayMetrics.density * 8).toInt()

            if(idx == categories.lastIndex)
                params.rightMargin = (resources.displayMetrics.density * 16).toInt()

            ivCategory?.layoutParams = params
            ivCover?.layoutParams = params

            if(category.image == getString(R.string.default_text))
                ivCategory?.setImageResource(R.drawable.default_bg)
            else if (category.image == "")
                ivCategory?.setImageResource(R.drawable.class_all)
            else {
                var bitmap = ImageCacheUtils.getBitmap(category.title)

                if(bitmap == null) {
                    Glide.with(context!!)
                            .asBitmap()
                            .load(CommonUtils.getThumbnailLinkPath(context!!, category.image))
                            .into(object : ViewTarget<ImageView, Bitmap>(ivCategory!!) {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    ImageCacheUtils.putBitmap(category.title, resource)
                                    ivCategory!!.setImageBitmap(resource)
                                }
                            })
                }else{
                    ivCategory!!.setImageBitmap(bitmap)
                }
            }

            view.setOnClickListener {
                getClassList(category.title)

                for(childIdx in 0 until llCategories!!.childCount!!){
                    if(childIdx == idx)
                        llCategories!!.getChildAt(childIdx).findViewById<ImageView>(R.id.ivCategoryCover).visibility = View.GONE
                    else
                        llCategories!!.getChildAt(childIdx).findViewById<ImageView>(R.id.ivCategoryCover).visibility = View.VISIBLE
                }
            }

            llCategories?.addView(view)
        }
    }

    private fun getFirstCategories(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getFirstCategory()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        categories.addAll(result.result!!.toCollection(ArrayList()))
                        setCategoryListUi()
                    }else{
                        Log.e("FIRST_CATEGORY", result.error)
                        Toast.makeText(context!!, getString(R.string.fail_to_category_load), Toast.LENGTH_SHORT).show()
                    }
                },{ error ->
                    Log.e("FIRST_CATEGORY", error.toString())
                    Toast.makeText(context!!, getString(R.string.fail_to_category_load), Toast.LENGTH_SHORT).show()
                })
    }
}
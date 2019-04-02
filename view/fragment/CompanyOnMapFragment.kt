package vdream.vd.com.vdream.view.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.CompanyData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.view.activity.MainActivity

class CompanyOnMapFragment: Fragment() {
    var flMapContainer: FrameLayout? = null
    var tvCompanyCount: TextView? = null
    var rvCompanyList: RecyclerView? = null
    var layoutManager: LinearLayoutManager? = null
    var mapView: MapView? = null
    var locationListener: LocationListener? = null
    var myLocMarker: MapPOIItem? = null
    var companyList = ArrayList<CompanyData>()
    var recyclerViewVisibleItemPosition = 0
    var isDragged = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_company_on_map, container, false)
        flMapContainer = rootView.findViewById(R.id.flMapContainer)
        tvCompanyCount = rootView.findViewById(R.id.tvCompanyListCount)
        rvCompanyList = rootView.findViewById(R.id.rvCompanyList)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, context!!.resources.displayMetrics.heightPixels - (98*resources.displayMetrics.density).toInt())
        flMapContainer?.layoutParams = params

        mapView = MapView(context)
        flMapContainer?.addView(mapView)
        mapView?.setZoomLevel(1, false)

        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location?) {
                if(location != null) {
                    if(myLocMarker == null){
                        myLocMarker = MapPOIItem()
                        myLocMarker?.itemName = "My Location"
                        myLocMarker?.tag = 0
                        myLocMarker?.markerType = MapPOIItem.MarkerType.BluePin
                        myLocMarker?.selectedMarkerType = MapPOIItem.MarkerType.YellowPin
                        myLocMarker?.mapPoint = MapPoint.mapPointWithGeoCoord(location!!.latitude, location!!.longitude)

                        mapView?.addPOIItem(myLocMarker)
                        mapView?.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.latitude, location.longitude), true)
                    }else {
                        myLocMarker?.mapPoint = MapPoint.mapPointWithGeoCoord(location!!.latitude, location!!.longitude)
                    }
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onProviderDisabled(provider: String?) {

            }
        }

        if(ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            MainActivity.locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100.toLong(), 1.toFloat(), locationListener!!)
            MainActivity.locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100.toLong(), 1.toFloat(), locationListener!!)
        }

        layoutManager = LinearLayoutManager(context)
        layoutManager?.orientation = LinearLayoutManager.HORIZONTAL
        rvCompanyList?.layoutManager = layoutManager

        getCompanyList()

        rvCompanyList?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                when(newState){
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        val position = layoutManager!!.findFirstVisibleItemPosition()
                        val nextPosition: Int
                        val view = layoutManager!!.findViewByPosition(position)
                        val half = view.width / 2
                        val x = view.x

                        if (x < 0 && half < Math.abs(x)) {
                            nextPosition = position + 1
                        } else {
                            nextPosition = position
                        }
                        if (nextPosition != recyclerViewVisibleItemPosition) {
                            recyclerViewVisibleItemPosition = nextPosition
                        }

                        recyclerView!!.smoothScrollToPosition(nextPosition)
                        isDragged = false
                        mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(companyList[nextPosition].lat!!.toDouble(), companyList[nextPosition].lng!!.toDouble()),
                                true)
                        tvCompanyCount?.text = "${nextPosition + 1} / ${companyList.size}"
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        if(!isDragged){
                            isDragged = true
                            recyclerViewVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()
                        }
                    }
                }
            }
        })
    }

    inner class CompanyListAdapter: RecyclerView.Adapter<CompanyListViewHolder> {
        var context: Context? = null
        var dataList = ArrayList<CompanyData>()
        var layoutRes = 0

        constructor(context: Context, list: ArrayList<CompanyData>, layoutRes: Int){
            this.context = context
            dataList = list
            this.layoutRes = layoutRes
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CompanyListViewHolder {
            var rootView = LayoutInflater.from(context).inflate(layoutRes, parent, false)
            var viewHolder = CompanyListViewHolder(rootView)

            return viewHolder
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: CompanyListViewHolder?, position: Int) {
            var company = dataList.get(position)

            holder?.tvName?.setText(company.title)
            if(company.category_2 != null)
                holder?.tvCategory?.text = company.category_1 + "/ " + company.category_2
            else
                holder?.tvCategory?.text = company.category_1
            holder?.tvAddress?.text = company.address
            holder?.tvTel?.text = company.phone

            if(company.url == null || company.url == "")
                holder?.ivUrlDivider!!.visibility = View.INVISIBLE
            else
                holder?.tvUrl?.text = company.url

            holder?.ivLocaiontBtn?.setOnClickListener({view ->
                mapView?.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(company.lat!!.toDouble(), company.lng!!.toDouble()), true)
            })
        }

    }

    inner class CompanyListViewHolder: RecyclerView.ViewHolder {
        var tvName:  TextView? = null
        var tvCategory: TextView? = null
        var ivLocaiontBtn: ImageView? = null
        var tvAddress: TextView? = null
        var tvTel: TextView? = null
        var ivUrlDivider: ImageView? = null
        var tvUrl: TextView? = null

        constructor(view: View): super(view) {
            tvName = view.findViewById(R.id.tvCompanyName)
            tvCategory = view.findViewById(R.id.tvCompanyCategory)
            ivLocaiontBtn = view.findViewById(R.id.ivCompanyLocationBtn)
            tvAddress = view.findViewById(R.id.tvCompanyAddress)
            tvTel = view.findViewById(R.id.tvCompanyTel)
            ivUrlDivider = view.findViewById(R.id.ivCardDividerTelUrl)
            tvUrl = view.findViewById(R.id.tvCompanyUrl)
        }
    }

    private fun getCompanyList(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getCompanyList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        companyList = result.result!!.toCollection(ArrayList<CompanyData>())
                        rvCompanyList?.adapter = CompanyListAdapter(context!!, companyList, R.layout.view_comapny_card)
                        tvCompanyCount?.text = "1 / ${companyList.size}"
                        addMarker()
                    } else {
                        Log.e("GET_COMPANY", result.error)
                        Toast.makeText(context!!, getString(R.string.fail_to_compnay_info), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("GET_COMPANY", error.toString())
                    Toast.makeText(context!!, getString(R.string.fail_to_compnay_info), Toast.LENGTH_SHORT).show()
                })
    }

    private fun addMarker() {
        for(company in companyList){
            var marker = MapPOIItem()
            marker?.itemName = company.title!!
            marker?.tag = company.idx
            marker?.markerType = MapPOIItem.MarkerType.CustomImage
            marker?.customImageResourceId = getMarkerImage(company.category_1!!)
            marker?.isCustomImageAutoscale = false
            marker?.setCustomImageAnchor(0.5f, 1.0f)
            marker?.mapPoint = MapPoint.mapPointWithGeoCoord(company.lat!!.toDouble(), company.lng!!.toDouble())

            mapView?.addPOIItem(marker)
        }
    }

    private fun getMarkerImage(category: String): Int {
        var res = 0
        when(category) {
            "음악" -> res = R.drawable.marter_7
            "미술" -> res = R.drawable.marter_7
            "외국어" -> res = R.drawable.marter_1
            "웅변" -> res = R.drawable.marter_1
            "독서실" -> res = R.drawable.marter_5
            "간호" -> res = R.drawable.marter_6
            "기술" -> res = R.drawable.marter_4
            "컴퓨터" -> res = R.drawable.marter_9
            "직업기술" -> res = R.drawable.marter_8
            else -> res = R.drawable.marter_2
        }

        return res
    }
}
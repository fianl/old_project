package vdream.vd.com.vdream.view.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.utils.ImageCacheUtils

class AddAddressActivity: BaseActivity(), MapView.MapViewEventListener, View.OnClickListener {
    var centerPoint: MapPoint? = null
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flAddressBack -> {
                finish()
                sendAppEvent("주소지도화면_헤더_뒤로가기")
            }
            R.id.flAddressComplete -> {
                if(centerPoint != null) {
                    var resultIntent = Intent()
                    resultIntent.putExtra(getString(R.string.intent_key_name_address), address)
                    resultIntent.putExtra(getString(R.string.intent_key_name_lat), centerPoint!!.mapPointGeoCoord.latitude)
                    resultIntent.putExtra(getString(R.string.intent_key_name_lng), centerPoint!!.mapPointGeoCoord.longitude)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }else{
                    Toast.makeText(this, "주소를 설정해 주세요", Toast.LENGTH_SHORT).show()
                }

                sendAppEvent("주소지도화면_완료")
            }
            R.id.ivMyLocation -> {
                var location: Location? = null
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    location = MainActivity.locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                    if(location == null){
                        location = MainActivity.locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    }
                }

                mapView?.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location!!.latitude, location!!.longitude), true)

                sendAppEvent("주소지도화면_내위치")
            }
        }
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
        Log.d("MAPVIEW_EVENT", "DOUBLE_TAP")
    }

    override fun onMapViewInitialized(p0: MapView?) {
        Log.d("MAPVIEW_EVENT", "INIT")
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
        Log.d("MAPVIEW_EVENT", "DRAG_START")
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
        Log.d("MAPVIEW_EVENT", "MOVE_FINISH")
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        Log.d("MAPVIEW_EVENT", "CENTER_POINT")
        centerPoint = p1
        getAddressFromGeoCoder(p1!!)
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
        Log.d("MAPVIEW_EVENT", "DRAG_END")
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        Log.d("MAPVIEW_EVENT", "SINGLE_TAP")
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
        Log.d("MAPVIEW_EVENT", "ZOOM_CHANGE")
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
        Log.d("MAPVIEW_EVENT", "LONG_PRESS")
    }

    var flBack: FrameLayout? = null
    var flComplete: FrameLayout? = null
    var ivMyLocation: ImageView? = null
    var flMapContainer: FrameLayout? = null
    var ivCenter: ImageView? = null
    var tvAddress: TextView? = null
    var mapView: MapView? = null
    var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        flBack = findViewById(R.id.flAddressBack)
        flComplete = findViewById(R.id.flAddressComplete)
        ivMyLocation = findViewById(R.id.ivMyLocation)
        flMapContainer = findViewById(R.id.flAddressMapContainer)
        ivCenter = findViewById(R.id.ivAddressCenterPoint)
        tvAddress = findViewById(R.id.tvAddAddress)

        mapView = MapView(this)
        mapView?.setZoomLevel(1, false)
        mapView?.setMapViewEventListener(this)

        flMapContainer?.addView(mapView)

        flBack?.setOnClickListener(this)
        flComplete?.setOnClickListener(this)
        ivMyLocation?.setOnClickListener(this)

        getCurrentMyLocation()
    }

    private fun getAddressFromGeoCoder(mapPoint: MapPoint) {
        var geoCodingListener = object : MapReverseGeoCoder.ReverseGeoCodingResultListener{
            override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
                Log.e("REVERSE_GEOCODER", "FAIL")
            }

            override fun onReverseGeoCoderFoundAddress(p0: MapReverseGeoCoder?, p1: String?) {
                Log.d("REVERSE_GEOCODER", p1)
                address = p1!!

                tvAddress?.text = address
            }

        }
        var reverseGeoCoder = MapReverseGeoCoder(getString(R.string.kakao_app_key), mapPoint, geoCodingListener, this)
        reverseGeoCoder.startFindingAddress()
    }

    private fun getCurrentMyLocation(){
        var location: Location? = null
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = MainActivity.locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if(location == null){
                location = MainActivity.locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        }

        var myLocMarker = MapPOIItem()
        myLocMarker.itemName = "My Location"
        myLocMarker.tag = 0
        myLocMarker.markerType = MapPOIItem.MarkerType.BluePin
        myLocMarker.selectedMarkerType = MapPOIItem.MarkerType.YellowPin
        myLocMarker.mapPoint = MapPoint.mapPointWithGeoCoord(location!!.latitude, location!!.longitude)

        mapView?.addPOIItem(myLocMarker)
        mapView?.setMapCenterPoint(myLocMarker.mapPoint, true)
    }
}
package vdream.vd.com.vdream.view.component

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import vdream.vd.com.vdream.R

class ExperienceDetailPlaceTimeView: FrameLayout {
    var flMapContainer: FrameLayout? = null
    var mapView: MapView? = null
    var tvAddress: TextView? = null
    var tvDate: TextView? = null
    var tvTime: TextView? = null

    constructor(context: Context): super(context) {
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_experience_detail_placetime, this, false)
        flMapContainer = rootView.findViewById(R.id.flExpDetailPlaceTimeMap)
        tvAddress = rootView.findViewById(R.id.tvExpDetailPlaceTimeAddress)
        tvDate = rootView.findViewById(R.id.tvExpDetailPlaceTimeDate)
        tvTime = rootView.findViewById(R.id.tvExpDetailPlaceTimeTime)

        mapView = MapView(context)
        flMapContainer?.addView(mapView)
        mapView?.setZoomLevel(1, false)

        addView(rootView)
    }

    internal fun setData(title: String, lat: Double, lng: Double, address1: String, address2: String, opened_ad: String){
        var marker = MapPOIItem()
        marker?.itemName = title
        marker?.tag = 0
        marker?.markerType = MapPOIItem.MarkerType.BluePin
        marker?.selectedMarkerType = MapPOIItem.MarkerType.YellowPin
        marker?.mapPoint = MapPoint.mapPointWithGeoCoord(lat, lng)

        mapView?.addPOIItem(marker)
        mapView?.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lng), true)

        tvAddress?.text = "$address1,$address2"

        var dateTime = opened_ad.split(" ")

        tvDate?.text = dateTime[0].replace("-", ".")
        tvTime?.text = convertTimeToAMPM(dateTime[1])
    }

    private fun convertTimeToAMPM(time: String): String {
        var convert = ""

        var tempTime = time.split(":")
        var hour = tempTime[0].toInt()

        if(hour >= 12){
            hour -= 12
            if(hour == 0)
                hour = 12
            convert = "오후 " + String.format("%02d", hour) + ":" + tempTime[1]
        }else{
            convert = "오전 ${tempTime[0]}:${tempTime[1]}"
        }

        return convert
    }
}
package vdream.vd.com.vdream.view.component

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.AlbumData
import vdream.vd.com.vdream.data.ImageData
import vdream.vd.com.vdream.view.dialog.ImageViewDialog

class AlbumLayoutView: FrameLayout {
    var llContainer1: LinearLayout? = null
    var llContainer2: LinearLayout? = null
    constructor(context: Context): super(context) {
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_album_layout, this, false)
        llContainer1 = rootView.findViewById(R.id.llAlbumContainer1)
        llContainer2 = rootView.findViewById(R.id.llAlbumContainer2)

        addView(rootView)
    }

    internal fun setData(data: ArrayList<ImageData>){
        if(llContainer1!!.childCount > 0)
            llContainer1?.removeAllViews()

        if(llContainer2!!.childCount > 0)
            llContainer2?.removeAllViews()

        for(idx in 0 until data.size){
            var random = (Math.random() * 2).toInt() + 1
            var albumView: AlbumItemView? = null

            if(random%2 == 0)
                albumView = AlbumItemView(context, R.layout.view_album_big_item)
            else
                albumView = AlbumItemView(context, R.layout.view_album_item)

            albumView!!.setData(data!![idx])

            albumView.setOnClickListener {
                var ivDialog = ImageViewDialog(context, data!!.get(idx).images!!.toCollection(ArrayList<String>()))
                ivDialog.show()
            }

            if(idx%2 == 0)
                llContainer1?.addView(albumView)
            else
                llContainer2?.addView(albumView)
        }
    }
}
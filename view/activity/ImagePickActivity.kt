package vdream.vd.com.vdream.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.view.component.ImagePickItemView
import android.media.ThumbnailUtils
import android.os.AsyncTask
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import kotlin.math.max

class ImagePickActivity: BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flImagePickBack -> {
                sendAppEvent("이미지선택_헤더_뒤로가기")
                finish()
            }
            R.id.flImagePickComplete -> {
                sendAppEvent("이미지선택_헤더_완료")
                var intent = Intent()
                intent.putExtra(getString(R.string.intent_key_name_images), selectedImageList)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    var flBack: FrameLayout? = null
    var flComplete: FrameLayout? = null
    var gvImages: GridView? = null
    var adapter: CustomGalleryAdapter? = null

    var imageList = ArrayList<String>()
    var imageIdList = ArrayList<InnerImageItem>()
    var selectedImageList = ArrayList<String>()
    var selectedItemList = ArrayList<ImagePickItemView>()
    var limitCnt = 0
    var maxSize: Int = 50
    var imageCache: LruCache<String, Bitmap>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        limitCnt = intent.getIntExtra(getString(R.string.intent_key_name_limit_count), 1)
        setContentView(R.layout.activity_image_pick)

        flBack = findViewById(R.id.flImagePickBack)
        flComplete = findViewById(R.id.flImagePickComplete)
        gvImages = findViewById(R.id.gvImagePick)

        flBack?.setOnClickListener(this)
        flComplete?.setOnClickListener(this)

        var imageLoader = object : AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg params: Void?): Void? {
                var uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                var projection = arrayOf<String>(MediaStore.MediaColumns.DATA, MediaStore.Images.Media._ID)
                var orderBy = MediaStore.Images.Media.DATE_TAKEN

                var cursor = contentResolver.query(uri, projection, null, null, orderBy + " DESC")
                var column_idx = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                var icColumnIdx = cursor.getColumnIndex(projection[1])

                while (cursor.moveToNext()) {
                    imageList.add(cursor.getString(column_idx))
                    imageIdList.add(InnerImageItem(cursor.getString(icColumnIdx)))
                }

                if(imageIdList.size < 50)
                    maxSize = imageIdList.size

                if(imageIdList.size == 0)
                    imageCache = LruCache(1)
                else
                    imageCache = LruCache(imageIdList.size)

                return null
            }


            override fun onPostExecute(result: Void?) {
                if(imageIdList.size > 0)
                    initGridView()
            }
        }

        imageLoader.execute(null)

        gvImages?.setOnScrollListener(object :AbsListView.OnScrollListener{
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if((firstVisibleItem + visibleItemCount) >= totalItemCount && maxSize < imageIdList.size){
                    if(imageIdList.size - maxSize > 50){
                        maxSize += 50
                    }else{
                        if(imageIdList.size - maxSize > 0)
                            maxSize += imageIdList.size - maxSize
                    }

                    if(maxSize <= imageIdList.size) {
                        adapter?.addData(imageIdList.subList(adapter!!.count, maxSize).toCollection(ArrayList()))
                    }
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
            }
        })
    }

    private fun initGridView(){
        var tempList = imageIdList.subList(0, maxSize-1).toCollection(ArrayList())
        adapter = CustomGalleryAdapter(tempList)
        gvImages?.adapter = adapter
        gvImages?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            var clickedView = view as ImagePickItemView

            if(clickedView.selectedOrder == 0) {
                if(selectedImageList.size >= limitCnt){
                    Toast.makeText(applicationContext, String.format(getString(R.string.upload_image_limit_notice), limitCnt), Toast.LENGTH_SHORT).show()
                    return@OnItemClickListener
                }
                imageIdList[position].selectedOrder = selectedImageList.size + 1
                clickedView.setItemSelected(selectedImageList.size + 1)
                selectedImageList.add(imageList.get(position))
                selectedItemList.add(clickedView)
            } else {
                imageIdList[position].selectedOrder = 0
                clickedView.setItemDisSelected()
                selectedImageList.remove(imageList.get(position))
                selectedItemList.remove(clickedView)

                for(idx in 0..selectedItemList.lastIndex){
                    selectedItemList.get(idx).setItemSelected(idx+1)
                }
            }
        }
    }

    inner class CustomGalleryAdapter: BaseAdapter {
        private var list = ArrayList<InnerImageItem>()

        constructor(list: ArrayList<InnerImageItem>){
            this.list = list
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var convertView = ImagePickItemView(applicationContext)
            var bitmap: Bitmap? = imageCache!!.get(list[position].imageId)

            if(bitmap == null) {
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, list[position].imageId!!.toLong(), MediaStore.Images.Thumbnails.MICRO_KIND, null)

                if(bitmap != null) {
                    imageCache?.put(list[position].imageId, bitmap)
                }
            }

            if(list[position].selectedOrder != 0){
                convertView.setItemSelected(list[position].selectedOrder)
            }

            if(bitmap != null)
                convertView.setImage(bitmap!!)

            return convertView
        }

        override fun getItem(position: Int): Any {
            return list[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return list.size
        }

        fun addData(moreList: ArrayList<InnerImageItem>){
            list.addAll(moreList)
            notifyDataSetChanged()
        }
    }

    inner class InnerImageItem {
        var imageId: String? = ""
        var selectedOrder = 0

        constructor(){}
        constructor(id: String){
            imageId = id
        }
    }
}
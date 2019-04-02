package vdream.vd.com.vdream.utils

import android.graphics.Bitmap
import android.util.LruCache

class ImageCacheUtils {
    companion object {
        private var cache = LruCache<String, Bitmap>(100)

        fun putBitmap(id: String, bitmap: Bitmap){
            cache.put(id, bitmap)
        }

        fun getBitmap(id: String): Bitmap? {
            return cache.get(id)
        }

        fun cacheClear(){
            cache.evictAll()
        }
    }
}
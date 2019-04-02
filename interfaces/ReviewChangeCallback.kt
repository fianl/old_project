package vdream.vd.com.vdream.interfaces

import vdream.vd.com.vdream.data.ReviewData

interface ReviewChangeCallback {
    fun onRequestUpdate(data: ReviewData)
    fun onRequestDelete(idx: Int)
}
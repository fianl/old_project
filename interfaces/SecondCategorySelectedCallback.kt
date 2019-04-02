package vdream.vd.com.vdream.interfaces

import vdream.vd.com.vdream.data.CategoryData

interface SecondCategorySelectedCallback {
    fun onSelected(fistCategory: String, secondCategory: CategoryData)
}
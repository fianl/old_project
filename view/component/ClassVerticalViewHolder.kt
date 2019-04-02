package vdream.vd.com.vdream.view.component

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import vdream.vd.com.vdream.R

class ClassVerticalListViewHolder: RecyclerView.ViewHolder {
    var clContainer: ConstraintLayout? = null
    var ivMain: ImageView? = null
    var tvTitle: TextView? = null
    var tvCategory: TextView? = null
    var tvLocation: TextView? = null
    var ivIsMine: ImageView? = null
    var ivIsJoined: ImageView? = null

    constructor(view: View): super(view) {
        clContainer = view.findViewById(R.id.clItemContainer)
        ivMain = view.findViewById(R.id.ivItemMainImage)
        tvTitle = view.findViewById(R.id.tvItemTitle)
        tvCategory = view.findViewById(R.id.tvItemCategory)
        tvLocation = view.findViewById(R.id.tvItemLocation)
        ivIsMine = view.findViewById(R.id.ivItemIsMine)
        ivIsJoined = view.findViewById(R.id.ivItemIsJoined)
    }
}
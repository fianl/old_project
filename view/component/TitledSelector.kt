package vdream.vd.com.vdream.view.component

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import vdream.vd.com.vdream.R

class TitledSelector: FrameLayout {
    var tvTitle: TextView? = null
    var gvSelector: GridView? = null
    var nameList = ArrayList<String>()
    var valueList = ArrayList<String>()
    var selectedValue = ""
    var isItemTitleCenter = false

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr) {
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_titled_selector, this, false)
        tvTitle = rootView.findViewById(R.id.tvSelectorTitle)
        gvSelector = rootView.findViewById(R.id.gvSelector)

        addView(rootView)

        gvSelector?.setOnItemClickListener { parent, view, position, id ->
            for(idx in 0 until parent.childCount) {
                var item = parent.getChildAt(idx) as ItemTitledSelector

                if(idx == position) {
                    item.setItemSelected()
                    selectedValue = valueList.get(position)
                } else
                    item.setItemUnselect()
            }
        }
    }
    
    internal fun setTitle(title: String){
        tvTitle?.text = title
    }

    internal fun setGridColumnLine(line: Int) {
        gvSelector?.numColumns = line
    }

    internal fun setSelectorHeightLong() {
        var params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (resources.displayMetrics.density * 160).toInt())
        params.topMargin = (resources.displayMetrics.density * 16).toInt()
        gvSelector!!.layoutParams = params
    }
    
    internal fun setSelectorData(nameList: ArrayList<String>, valueList: ArrayList<String>){
        this.nameList = nameList
        this.valueList = valueList

        gvSelector?.adapter = GridSelectorAdapter()
    }

    internal fun setItemTitleGravityCenter(){
        isItemTitleCenter = true
    }

    internal fun setDataSelection(idx: Int){
        gvSelector?.getChildAt(idx)?.performClick()
    }

    inner class GridSelectorAdapter: BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var item = ItemTitledSelector(context)
            item.setTitle(nameList[position])

            if(isItemTitleCenter)
                item.setTitleGravityCenter()

            return item
        }

        override fun getItem(position: Int): Any {
            return nameList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return nameList.size
        }

    }
}
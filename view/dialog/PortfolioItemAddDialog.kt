package vdream.vd.com.vdream.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.UserRecordData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.view.component.PortfolioAddingItem

class PortfolioItemAddDialog: Dialog, View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flDialogPortfolioItemAddClose -> dismiss()
            R.id.tvPortfolioItemAdd -> {
                for(idx in 0 until llContainer!!.childCount){
                    var itemView = llContainer!!.getChildAt(idx) as PortfolioAddingItem

                    if(itemView.getIsChecked())
                        selectedList.add(itemView.getRecordData())
                }

                dismiss()
            }
        }
    }

    var recordPage = 0

    var flClose: FrameLayout? = null
    var llContainer: LinearLayout? = null
    var tvAdd: TextView? = null
    var selectedList = ArrayList<UserRecordData>()

    constructor(context: Context): super(context) {
        init()
    }

    private fun init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add_portfolio_item)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        flClose = findViewById(R.id.flDialogPortfolioItemAddClose)
        llContainer = findViewById(R.id.llPortfolioItemContainer)
        tvAdd = findViewById(R.id.tvPortfolioItemAdd)

        flClose?.setOnClickListener(this)
        tvAdd?.setOnClickListener(this)

        getAllRecord()
    }

    private fun getAllRecord(){
        var myInfo = MyInfoStore.myInfo
        var apiService = ApiManager.getInstance().apiService
        apiService.getUserRecord(myInfo!!.uuid!!, "all", recordPage+1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        setAddingItem(result.result!!.data!!.toCollection(ArrayList()))
                    }else{
                        Log.e("RECORD_LIST", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_load_record), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("RECORD_LIST", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_load_record), Toast.LENGTH_SHORT).show()
                })
    }

    private fun setAddingItem(list: ArrayList<UserRecordData>){
        for(data in list){
            var itemView = PortfolioAddingItem(context)
            llContainer?.addView(itemView)
            itemView.setData(data)
        }
    }
}
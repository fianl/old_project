package vdream.vd.com.vdream.view.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.PortfolioRegisterData
import vdream.vd.com.vdream.data.UserInfoData
import vdream.vd.com.vdream.data.UserRecordData
import vdream.vd.com.vdream.interfaces.PortfolioChangeCallback
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.view.component.PortfolioItemView
import vdream.vd.com.vdream.view.dialog.PortfolioItemAddDialog
import java.util.ArrayList

class CreatePortfolioActivity: BaseActivity(), View.OnClickListener, PortfolioChangeCallback {
    override fun portfolioDeleteRequest(idx: Int) {
        portfolioList.remove(portfolioList[idx])
        llContent?.removeView(itemViewList[idx])
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flPortfolioBack -> finish()
            R.id.flPortfolioShare -> {
                var intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, myInfo!!.nickname + getString(R.string.is_portfolio) + getString(R.string.portfolio_base_url) + myInfo!!.uuid)

                var chooser = Intent.createChooser(intent, getString(R.string.share_content))
                startActivity(chooser)
            }
            R.id.clPortfolioLink -> createLinkCopyDialog()
            R.id.clPortfolioModify -> showAddingDialog()
            R.id.tvPortfolioAdd -> showAddingDialog()
            R.id.tvPortfolioSave -> registerPortfolio()
        }
    }

    var myInfo: UserInfoData? = null
    var portfolioList = ArrayList<UserRecordData>()
    var itemViewList = ArrayList<PortfolioItemView>()

    var flBack: FrameLayout? = null
    var flShare: FrameLayout? = null
    var ivProfile: ImageView? = null
    var tvNickname: TextView? = null
    var clLink: ConstraintLayout? = null
    var clModify: ConstraintLayout? = null
    var tvAdd: TextView? = null
    var llContent: LinearLayout? = null
    var tvSave: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_portfolio)
        flBack = findViewById(R.id.flPortfolioBack)
        flShare = findViewById(R.id.flPortfolioShare)
        ivProfile = findViewById(R.id.ivPortfolioProfile)
        tvNickname = findViewById(R.id.tvPortfolioNickname)
        clLink = findViewById(R.id.clPortfolioLink)
        clModify = findViewById(R.id.clPortfolioModify)
        tvAdd = findViewById(R.id.tvPortfolioAdd)
        llContent = findViewById(R.id.llPortfolioContent)
        tvSave = findViewById(R.id.tvPortfolioSave)

        flBack?.setOnClickListener(this)
        flShare?.setOnClickListener(this)
        clLink?.setOnClickListener(this)
        clModify?.setOnClickListener(this)
        tvAdd?.setOnClickListener(this)
        tvSave?.setOnClickListener(this)

        myInfo = MyInfoStore.myInfo

        tvNickname?.text = myInfo?.nickname

        if(myInfo?.profile_img != getString(R.string.default_text)){
            Glide.with(this).load(CommonUtils.getThumbnailLinkPath(this, myInfo!!.profile_img))
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfile!!)
        }

        getExistingPortfolio()
    }

    private fun getExistingPortfolio() {
        var apiService = ApiManager.getInstance().apiService
        apiService.getExistPortfolio(myInfo!!.uuid!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        portfolioList.addAll(result.result!!.feeds!!.toCollection(ArrayList()))

                        if(result.result!!.feeds!!.isNotEmpty()){
                            clModify?.visibility = View.VISIBLE
                            clLink?.visibility = View.VISIBLE
                        }else{
                            tvAdd?.visibility = View.VISIBLE
                        }
                        setExistinPortfolio(result.result!!.feeds!!.toCollection(ArrayList<UserRecordData>()))
                    }else{
                        Log.e("RECORD_LIST", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_load_record), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("RECORD_LIST", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_load_record), Toast.LENGTH_SHORT).show()
                })
    }

    private fun setExistinPortfolio(list: ArrayList<UserRecordData>){
        for((idx, record) in list.withIndex()){
            var portfolioView = PortfolioItemView(this)

            llContent?.addView(portfolioView)
            portfolioView.setData(idx, record, this)
            itemViewList.add(portfolioView)
        }
    }

    private fun showAddingDialog(){
        var adding = PortfolioItemAddDialog(this)
        adding.setOnDismissListener {
            portfolioList.addAll(adding.selectedList)
            setExistinPortfolio(adding.selectedList)
        }
        adding.show()
    }

    private fun registerPortfolio(){
        tvSave?.isClickable = false
        var registerData = PortfolioRegisterData()
        if(portfolioList.isNotEmpty())
           registerData.feeds = Array<Int>(portfolioList.size) { i -> portfolioList[i].idx}

        var apiService = ApiManager.getInstance().apiService
        apiService.registPortfolio(myInfo!!.uuid!!, registerData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({result ->
                    tvSave?.isClickable = true
                    if(result.status == "Y"){
                        Toast.makeText(this, getString(R.string.success_to_register_portfolio), Toast.LENGTH_SHORT).show()
                        createLinkCopyDialog()
                    }else{
                        Log.d("REGIST_PORTFOLIO", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_regist_portfolio), Toast.LENGTH_SHORT).show()
                    }
                }, {err ->
                    tvSave?.isClickable = true
                    Log.d("REGIST_PORTFOLIO", err.toString())
                    Toast.makeText(this, getString(R.string.fail_to_regist_portfolio), Toast.LENGTH_SHORT).show()
                })
    }

    private fun createLinkCopyDialog(){
        var link = Dialog(this)
        link.window.requestFeature(Window.FEATURE_NO_TITLE)
        link.setContentView(R.layout.dialog_portfolio_link)
        link.window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        link.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var flClose = link.findViewById<FrameLayout>(R.id.flLinkClose)
        var tvLink = link.findViewById<TextView>(R.id.tvLinkUrl)
        var tvCopy = link.findViewById<TextView>(R.id.tvLinkCopy)

        tvLink.text = getString(R.string.portfolio_base_url) + myInfo!!.uuid

        flClose.setOnClickListener{
            link.dismiss()
        }

        tvCopy.setOnClickListener{
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                var clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
                clipboard.text = getString(R.string.portfolio_base_url) + myInfo!!.uuid
            } else {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Copied Text", getString(R.string.portfolio_base_url) + myInfo!!.uuid)
                clipboard.primaryClip = clip
            }

            Toast.makeText(this, getString(R.string.link_copied), Toast.LENGTH_SHORT).show()

            link.dismiss()
        }

        link.show()
    }
}
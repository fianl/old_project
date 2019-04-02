package vdream.vd.com.vdream.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.ExperienceRequestData
import vdream.vd.com.vdream.data.FeedDetailData
import vdream.vd.com.vdream.data.UserInfoData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.view.component.TitledEditTextFlat
import vdream.vd.com.vdream.view.component.TitledInfoContainer
import java.text.NumberFormat

class PaymentActivity: BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flPaymentBack -> {
                sendAppEvent("결제정보화면_헤더_뒤로가기")
                finish()
            }
            R.id.clPaymentCredit -> {
                sendAppEvent("결제정보화면_결제수단_카드")
                clCredit?.setBackgroundResource(R.drawable.rectangle_transparent_maincolor_stroke)
                tvCredit?.setTextColor(ContextCompat.getColor(this, R.color.mainColor))
                clDeposit?.setBackgroundColor(Color.TRANSPARENT)
                tvDeposit?.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
                payType = "CREDIT"
            }
            R.id.clPaymentDeposit -> {
                sendAppEvent("결제정보화면_결제수단_계좌입금")
                clCredit?.setBackgroundColor(Color.TRANSPARENT)
                tvCredit?.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
                clDeposit?.setBackgroundResource(R.drawable.rectangle_transparent_maincolor_stroke)
                tvDeposit?.setTextColor(ContextCompat.getColor(this, R.color.mainColor))
                payType = "DEPOSIT"
            }
            R.id.clPaymentBankForAgreement -> {
                sendAppEvent("결제정보화면_조건_결제_동의")
                clAgreement?.setBackgroundResource(R.drawable.rectangle_transparent_maincolor_stroke)
                ivAgreement?.setImageResource(R.drawable.checkbox_on)
                tvAgreement?.setTextColor(ContextCompat.getColor(this, R.color.mainColor))
                isAgree = true
            }
            R.id.tvPaymentApply -> {
                sendAppEvent("결제정보화면_결제하기")
                var intent = Intent(this, PaymentWebViewActivity::class.java)
                startActivity(intent)
                //requestExperience()
            }
        }
    }

    var flBack: FrameLayout? = null
    var ticItem: TitledInfoContainer? = null
    var ticUser: TitledInfoContainer? = null
    var ticCost: TitledInfoContainer? = null
    var tvTotalCost: TextView? = null
    var clCredit: ConstraintLayout? = null
    var tvCredit: TextView? = null
    var clDeposit: ConstraintLayout? = null
    var tvDeposit: TextView? = null
    var tetfAccountHolder: TitledEditTextFlat? = null
    var tetfAccountNumber: TitledEditTextFlat? = null
    var clAgreement: ConstraintLayout? = null
    var ivAgreement: ImageView? = null
    var tvAgreement: TextView? = null
    var tvStoreName: TextView? = null
    var tvStoreOfficer: TextView? = null
    var tvStoreNumber: TextView? = null
    var tvStoreCall: TextView? = null
    var tvApply: TextView? = null

    var itemInfo: FeedDetailData? = null
    var userInfo: UserInfoData? = null
    var payType = ""
    var isAgree = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        itemInfo = intent.getSerializableExtra(getString(R.string.intent_key_name_feeddata)) as FeedDetailData

        flBack = findViewById(R.id.flPaymentBack)
        ticItem = findViewById(R.id.ticPaymentItem)
        ticUser = findViewById(R.id.ticPaymentCustomer)
        ticCost = findViewById(R.id.ticPaymentCost)
        tvTotalCost = findViewById(R.id.tvPaymentTotalCost)
        clCredit = findViewById(R.id.clPaymentCredit)
        tvCredit = findViewById(R.id.tvPaymentCredit)
        clDeposit = findViewById(R.id.clPaymentDeposit)
        tvDeposit = findViewById(R.id.tvPaymentDeposit)
        tetfAccountHolder = findViewById(R.id.tetfPaymentHolder)
        tetfAccountNumber = findViewById(R.id.tetfPaymentAccountNumber)
        clAgreement = findViewById(R.id.clPaymentBankForAgreement)
        ivAgreement = findViewById(R.id.ivPaymentAgreement)
        tvAgreement = findViewById(R.id.tvPaymentAgreement)
        tvStoreName = findViewById(R.id.tvPaymentStoreName)
        tvStoreOfficer = findViewById(R.id.tvPaymentStoreOfficer)
        tvStoreNumber = findViewById(R.id.tvPaymentStoreRegisterNumber)
        tvStoreCall = findViewById(R.id.tvPaymentStoreCall)
        tvApply = findViewById(R.id.tvPaymentApply)

        init()
    }

    private fun init() {
        userInfo = MyInfoStore.myInfo

        ticItem?.setTitle("주문상품정보")
        ticItem?.setContainerShort()
        var itemInfoList = ArrayList<String>()
        itemInfoList.add("상품명")
        itemInfoList.add("금액")
        var itemValueList = ArrayList<String>()
        itemValueList.add(itemInfo!!.title)
        itemValueList.add(NumberFormat.getCurrencyInstance().format(itemInfo!!.price) + "원")
        ticItem?.setData(itemInfoList, itemValueList)

        ticUser?.setTitle("주문자정보")
        ticUser?.showOptionButton()
        var userInfoList = ArrayList<String>()
        userInfoList.add("성명")
        userInfoList.add("메일")
        var userValueList = ArrayList<String>()
        userValueList.add(userInfo!!.nickname)
        userValueList.add(userInfo!!.email)
        ticUser?.setData(userInfoList, userValueList)

        ticCost?.setTitle("결제금액")
        var costInfoList = ArrayList<String>()
        costInfoList.add("상품금액")
        costInfoList.add("배송비")
        costInfoList.add("할인내역")

        var costValueList = ArrayList<String>()
        costValueList.add(NumberFormat.getCurrencyInstance().format(itemInfo!!.price) + "원")
        costValueList.add("-")
        costValueList.add("-")

        ticCost?.setData(costInfoList, costValueList)

        tvTotalCost?.text = NumberFormat.getCurrencyInstance().format(itemInfo!!.price)

        tetfAccountHolder?.setTitle("예금주")
        tetfAccountNumber?.setTitle("계좌번호")

        flBack?.setOnClickListener(this)
        clCredit?.setOnClickListener(this)
        clDeposit?.setOnClickListener(this)
        clAgreement?.setOnClickListener(this)
        tvApply?.setOnClickListener(this)

        tvStoreName?.text = "(주)브이드림"
        tvStoreOfficer?.text = "김민지"
        tvStoreNumber?.text = "186-86-00972"
        tvStoreCall?.text = "051-711-8572"
    }

    private fun requestExperience(){
        var requstData = ExperienceRequestData()
        if(itemInfo!!.price > 0){
            requstData.status = "WAIT"
        }
        var apiService = ApiManager.getInstance().apiService
        apiService.requestExperience(itemInfo!!.idx, requstData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        Toast.makeText(this, "체험학습이 신청되었습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.e("REQ_EXP", result.error)
                        Toast.makeText(this, "체험학습이 신청 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                    }
                    tvApply?.isClickable = true
                }, { err ->
                    Log.e("REQ_EXP", err.toString())
                    Toast.makeText(this, "체험학습이 신청 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                })
    }
}
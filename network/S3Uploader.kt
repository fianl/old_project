package vdream.vd.com.vdream.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.interfaces.UploadFinishCallback
import vdream.vd.com.vdream.view.dialog.FileTransferProgressDialog
import java.io.File
import java.security.MessageDigest
import java.util.*

/**
 * Created by SHINLIB on 2018-03-19.
 */
class S3Uploader {
    var context: Context? = null
    var uploadProgress: FileTransferProgressDialog? = null
    var uploadLsit = ArrayList<String>()
    var goalPath = ArrayList<String>()
    var originName = ArrayList<String>()
    var uploadFinishCallback: UploadFinishCallback? = null
    var folder = ""

    constructor(context: Context, uploadLsit: ArrayList<String>, uploadFinishCallback: UploadFinishCallback?){
        this.context = context
        this.uploadLsit = uploadLsit
        this.uploadFinishCallback = uploadFinishCallback
    }

    open fun upload(){
        startFileTransferDialog()
        var awsConfig = AWSConfiguration(context)
        var credentialsProvider = CognitoCachingCredentialsProvider(context,
                context?.getString(R.string.aws_s3_pool_id),
                Regions.fromName(context?.getString(R.string.aws_s3_region_name))
        )

        var s3client = AmazonS3Client(credentialsProvider)
        s3client.setRegion(Region.getRegion(Regions.fromName(context!!.getString(R.string.aws_s3_region_name))))

        var transferUtility = TransferUtility.builder()
                .context(context)
                .awsConfiguration(awsConfig)
                .s3Client(s3client)
                .build()

        var cnt = 0
        for(filePath in uploadLsit){
            var fileName = filePath.split("/").last()
            var date = Date()

            cnt++
            var uploadObserver = transferUtility.upload(
                    getDateFoler() + convertMd5(date.time.toString()) + "." + getFileExtension(fileName),
                    File(filePath))
            goalPath.add(uploadObserver.key)
            originName.add(fileName)
            uploadObserver.setTransferListener(object : TransferListener {
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    var rate = (bytesCurrent/bytesTotal * 100).toInt()
                    uploadProgress?.setCurrentRate(rate)
                }

                override fun onStateChanged(id: Int, state: TransferState?) {
                    if(state == TransferState.COMPLETED){
                        if(filePath.equals(uploadLsit.last())){
                            uploadProgress?.dismiss()
                            Toast.makeText(context, "파일업로드가 완료되었습니다", Toast.LENGTH_SHORT).show()

                            if(uploadFinishCallback != null){
                                uploadFinishCallback!!.uploadFinished(goalPath, originName)
                            }
                        }
                    }
                }

                override fun onError(id: Int, ex: java.lang.Exception?) {
                    Log.e("UPLOAD_ERR", ex.toString())
                }

            })
        }
    }

    private fun getFileExtension(filename: String): String {
        var extension = filename.split(".").last()

        if(extension == null || extension.equals(""))
            extension = "jpg"

        return extension
    }

    private fun convertMd5(origin: String): String {
        var sb = StringBuilder()
        try{
            var md = MessageDigest.getInstance("MD5")
            md.update(origin.toByteArray())
            var byteArray = md.digest().toTypedArray()
            for(byteData in byteArray){
                sb.append(String.format("%2x", byteData).replace(" ", "0"))
            }

            sb.setLength(sb.length - 1)
        }catch (e: Exception){
            Log.e("MD5", e.toString())
        }

        return sb.toString()
    }

    private fun startFileTransferDialog(){
        if(uploadProgress == null)
            uploadProgress = FileTransferProgressDialog(context!!)

        uploadProgress?.show()
    }

    private fun getDateFoler(): String {
        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH) + 1
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        return "$year/${String.format("%02d", month)}/${String.format("%02d", day)}/"
    }
}
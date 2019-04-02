package vdream.vd.com.vdream.network

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.internal.Constants
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.view.dialog.FileTransferProgressDialog
import java.io.File


/**
 * Created by SHINLIB on 2018-03-19.
 */
class S3Downloader {
    var context: Context? = null
    var downloadProgress: FileTransferProgressDialog? = null
    var path = ""
    var filename = ""
    var savePath = "/VDream_Download/"

    constructor(context: Context, path: String, filename: String){
        this.context = context
        this.path = path
        this.filename = filename
    }

    open fun download(){
        var awsConfig = AWSConfiguration(context)
        var credentialsProvider = CognitoCachingCredentialsProvider(context,
                context?.getString(R.string.aws_s3_pool_id),
                Regions.fromName(context?.getString(R.string.aws_s3_region_name))
        )

        var s3client = AmazonS3Client(credentialsProvider)
        s3client.setRegion(Region.getRegion(Regions.fromName("ap-northeast-2")))

        var transferUtility = TransferUtility.builder()
                .context(context)
                .awsConfiguration(awsConfig)
                .s3Client(s3client)
                .build()

        var goalPath = Environment.getExternalStorageDirectory().toString() + savePath

        var downloadObserver = transferUtility.download(
                context?.getString(R.string.aws_s3_bucket),
                path + filename,
                File(goalPath + filename))
        downloadObserver.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED === state) {
                    downloadProgress?.dismiss()
                    Toast.makeText(context, context?.getText(R.string.download_complete), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                var rate = (bytesCurrent/bytesTotal * 100).toInt()
                downloadProgress?.setCurrentRate(rate)
            }

            override fun onError(id: Int, ex: Exception) {
                Toast.makeText(context, context?.getText(R.string.error_occurred), Toast.LENGTH_SHORT).show()
                Log.e("DOWN_ERR", ex.toString())
            }
        })
    }

    private fun startFileTransferDialog(){
        if(downloadProgress == null)
            downloadProgress = FileTransferProgressDialog(context!!)

        downloadProgress?.show()
    }
}
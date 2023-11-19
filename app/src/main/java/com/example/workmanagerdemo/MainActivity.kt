package com.example.workmanagerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.work.*
import com.example.workmanagerdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val mBinding: ActivityMainBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mWorkManager: WorkManager by lazy(LazyThreadSafetyMode.NONE) {
        WorkManager.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initView()
        mWorkManager.getWorkInfosByTagLiveData(DownloadWorker.TAG)
            .observe(this) {
                if (it.isNotEmpty()) {
                    val workInfo = it[0]
                    when(workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            mBinding.download.setText("重新下载")
                            mBinding.tips.visibility = View.VISIBLE
                            mBinding.tips.text = workInfo.outputData.getString(DownloadWorker.OUTPUT_KEY)
                            mBinding.download.setOnClickListener {
                                enqueueDownloadWork()
                            }
                        }
                        WorkInfo.State.BLOCKED,
                        WorkInfo.State.ENQUEUED,
                        WorkInfo.State.RUNNING, -> {
                            mBinding.tips.text = "正在下载"
                            mBinding.download.text = "取消下载"
                            mBinding.download.setOnClickListener {
                                mWorkManager.cancelUniqueWork(DownloadWorker.TAG)
                            }
                        }
                        WorkInfo.State.CANCELLED -> {
                            mBinding.tips.visibility = View.GONE
                            mBinding.download.text = "开始下载"
                            mBinding.download.setOnClickListener {
                                enqueueDownloadWork()
                            }
                        }
                        WorkInfo.State.FAILED -> {
                            mBinding.tips.text = "下载失败"
                        }
                    }
                }
            }
    }

    private fun initView() {
        mBinding.download.setOnClickListener {
            enqueueDownloadWork()
        }
    }

    private fun enqueueDownloadWork() {
        val downloadContent = "复仇者联盟"
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val data = Data.Builder().putString(DownloadWorker.INPUT_KEY, downloadContent).build()
        mWorkManager.beginUniqueWork(
            DownloadWorker.TAG,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<DownloadWorker>()
                .addTag(DownloadWorker.TAG)
                .setInputData(data)
                .setConstraints(constraints)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)//
                .build()
        ).enqueue()
    }

}
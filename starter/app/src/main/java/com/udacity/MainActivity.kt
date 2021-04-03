package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var contentIntent: Intent

    private var radioValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        custom_button.setOnClickListener {
            val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
            notificationManager.cancelAll()
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {
                custom_button.isEnabled = true

                contentIntent = Intent(applicationContext, DetailActivity::class.java)
                contentIntent.putExtra(FILE_NAME, fileName)
                contentIntent.putExtra(STATUS, status)
                pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    NOTIFICATION_ID,
                    contentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                Toast.makeText(context, fileName, Toast.LENGTH_SHORT).show()
                notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
                notificationManager.sendNotification(fileName, applicationContext, pendingIntent)
            }
        }
    }

    private fun download() {
        if (radioValue.isBlank()) {
            Toast.makeText(this, getString(R.string.select_file), Toast.LENGTH_SHORT).show()
            return
        }
        custom_button.isEnabled = false
        val request =
            DownloadManager.Request(Uri.parse(radioValue))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }

    fun onUrlChoose(view: View) {
        if (view is RadioButton) {
            // Check which radio button was clicked
            when (view.getId()) {
                R.id.glide_radio -> {
                    fileName = getString(R.string.glide)
                    radioValue = getString(R.string.glide_url)
                    status = getString(R.string.status_success)
                }
                R.id.loadapp_radio -> {
                    radioValue = getString(R.string.loadapp_url)
                    fileName = getString(R.string.loadapp)
                    status = getString(R.string.status_failed)
                }
                R.id.retrofit_radio -> {
                    radioValue = getString(R.string.retrofit_url)
                    fileName = getString(R.string.retrofit)
                    status = getString(R.string.status_success)
                }
                else -> ""
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableVibration(true)
            notificationChannel.enableLights(true)
            notificationChannel.description = getString(R.string.notification_title)
            notificationChannel.apply {
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val NOTIFICATION_ID = 0
        const val FILE_NAME = "FILE_NAME"
        const val STATUS = "STATUS"

        private lateinit var fileName : String
        private lateinit var status : String
    }
}

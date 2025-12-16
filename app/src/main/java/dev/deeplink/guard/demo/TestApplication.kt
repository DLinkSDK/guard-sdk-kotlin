package dev.deeplink.guard.demo

import android.app.ActivityManager
import android.app.Application
import android.app.Notification
import android.content.Context
import android.os.Build
import android.os.Process
import androidx.core.app.NotificationCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dev.deeplink.guard.GuardManager
import dev.deeplink.guard.util.NotificationUtil

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //[require] Call this method to start the keep-alive service when the application is created.
        // If the check interval is passed in, the keep-alive service will check whether the process is alive according to the passed interval.
        // It is recommended not to set the interval too short to avoid excessive power consumption.
        GuardManager.launch(this, "ACCOUNT_ID", "DEV_TOKEN", 10 * 60 * 1000L)

        if (getProcessName(this) == this.packageName) {
            ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {

                override fun onStop(owner: LifecycleOwner) {
                    //[require] When the system is Android 8.0 or above, notifications need to be passed in to ensure that background services can run normally.
                    // You can call this method when your app switches to the background.
                    GuardManager.startBackgroundService(
                        this@TestApplication,
                        createNotification("title1", "content1")
                    )
                    //[optional] Pass in a permanent notification to increase the app process priority and ensure that the app is not killed as much as possible
                    // You can call this method when your app switches to the background.
                    GuardManager.startForegroundService(
                        this@TestApplication,
                        createNotification("title2", "content2"),
                        null
                    )
                }
            })
        }
    }

    private fun getProcessName(context: Context): String {
        return try {
            var processName = ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                processName = getProcessName()
            } else {
                val pid = Process.myPid()
                val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
                for (processInfo in manager.runningAppProcesses) {
                    if (processInfo.pid == pid) {
                        processName = processInfo.processName
                        break
                    }
                }
            }
            processName
        } catch (e: Exception) {
            ""
        }
    }

    private fun createNotification(title: String, content: String): Notification {
        NotificationUtil.createDaemonPushChannel(this)
        return NotificationCompat.Builder(this, NotificationUtil.DAEMON_PUSH_CHANNEL_ID)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }
}
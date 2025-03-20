# guard-sdk-kotlin

Step 1: Get the SDK

(1) Configure the Maven repository
```kotlin   
repositories {
   maven { url 'https://maven.deeplink.dev/repository/maven-releases/' }
}
```

Note: The Maven repository address needs to be configured in both 'buildscript' and 'allprojects' in the root directory's 'build.gradle'.

(2) If you are using Gradle for integration, add the following code to your project's build.gradle:
```kotlin
implementation 'dev.deeplink:guard:2.1.8'
```

Step 2: Initialize the SDK
```kotlin
class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //[require] Call this method to start the keep-alive service when the application is created.
        // If the check interval is passed in, the keep-alive service will check whether the process is alive according to the passed interval.
        // It is recommended not to set the interval too short to avoid excessive power consumption.
        GuardManager.launch(this, 10 * 60 * 1000L)

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
}
```
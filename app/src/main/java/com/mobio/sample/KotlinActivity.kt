package com.mobio.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobio.analytics.client.MobioSDK
import com.mobio.analytics.client.model.digienty.ScreenConfigObject

class KotlinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        val activityConfigObjectHashMap = HashMap<String, ScreenConfigObject>()
        activityConfigObjectHashMap["LoginActivity"] =
            ScreenConfigObject(
                "Login screen",
                "LoginActivity", intArrayOf(5, 10, 15), LoginActivity::class.java,
                true
            )
        activityConfigObjectHashMap["HomeActivity"] =
            ScreenConfigObject(
                "Home",
                "HomeActivity", intArrayOf(5, 10), HomeActivity::class.java,
                false
            )
        activityConfigObjectHashMap["SendMoneyInActivity"] =
            ScreenConfigObject(
                "Transfer",
                "SendMoneyInActivity", intArrayOf(10), SendMoneyInActivity::class.java,
                false
            )

        val fragmentConfigObjectHashMap = HashMap<String, ScreenConfigObject>()
        fragmentConfigObjectHashMap["FragmentA"] =
            ScreenConfigObject(
                "A",
                "FragmentA", intArrayOf(5), FragmentA::class.java,
                false
            )
        fragmentConfigObjectHashMap["FragmentB"] =
            ScreenConfigObject(
                "B",
                "FragmentB", intArrayOf(10), FragmentB::class.java,
                false
            )

        val builder = MobioSDK.Builder()
            .withSdkCode("m-android-test-1")
            .withSdkSource("MobioBank")
            .withEnvironment("test")
            .withDomainURL("")
            .withApiToken("")
            .withMerchantId("")
            .withApplication(application)
            .shouldTrackDeepLink(true)
            .shouldTrackScroll(false)
            .shouldTrackAppLifeCycle(true)
            .shouldTrackScreenLifeCycle(true)
            .withActivityMap(activityConfigObjectHashMap)
            .withFragmentMap(fragmentConfigObjectHashMap)
            .withIntervalSecond(10)
            .shouldRecordScreen(true)

        MobioSDK.setSingletonInstance(builder.build())
    }
}
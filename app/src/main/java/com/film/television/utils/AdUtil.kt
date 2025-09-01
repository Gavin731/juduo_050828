package com.film.television.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import com.bytedance.sdk.openadsdk.TTCustomController
import com.common.wheel.admanager.AdvertisementManager
import com.common.wheel.admanager.InfoAdCallBack
import com.common.wheel.admanager.InformationFlowAdCallback
import com.common.wheel.admanager.InitCallback
import com.common.wheel.admanager.OpenScreenAdCallBack
import com.common.wheel.admanager.RewardAdCallBack
import com.film.television.R
import com.film.television.mainScope
import kotlinx.coroutines.launch

object AdUtil {
    const val APP_ID = "5558135"
    private const val REWARD_AD_ID = "102935581"
    private const val SPLASH_AD_ID = "102936358"
    private const val FEEDS_AD_ID = "102934170"
    private const val INTERSTITIAL_AD_ID = "102935580"

    fun init(appContext: Context) {
        AdvertisementManager.getInstance()
            .init(
                appContext,
                APP_ID,
                appContext.getString(R.string.app_name),
                object : InitCallback {
                    override fun success() {
                        Log.d("lytest", "sdk init success")
                        ApplicationData.setInitStatusAsync(true)
                    }

                    override fun error() {
                        Log.d("lytest", "sdk init error")
                        ApplicationData.setInitStatusAsync(false)
                    }
                },
                object : TTCustomController() {}
            )
        mainScope.launch {
            val publicIp = DeviceUtil.getPublicIp()
            Log.d("lytest", "publicIp: $publicIp")
            DeviceUtil.getOaid(appContext) { oaid ->
                AdvertisementManager.getInstance().initConfig(oaid, publicIp)
            }
        }
    }

    fun showSplashAd(
        activity: Activity,
        splashContainer: FrameLayout,
        width: Int,
        height: Int,
        onClose: () -> Unit,
        onError: () -> Unit
    ) {
        AdvertisementManager.getInstance()
            .showOpenScreenAd(
                activity,
                SPLASH_AD_ID,
                splashContainer,
                width,
                height,
                object : OpenScreenAdCallBack {
                    override fun onAdClose() {
                        onClose()
                    }

                    override fun onSplashAdClick() {

                    }

                    override fun onSplashAdShow() {

                    }

                    override fun onSplashLoadFail() {
                        onError()
                    }

                    override fun onSplashRenderFail() {
                        onError()
                    }
                })
    }

    fun showInterstitialAd(activity: Activity, onClose: (() -> Unit)? = null) {
        AdvertisementManager.getInstance()
            .showInterstitialAd(activity, INTERSTITIAL_AD_ID, object : InfoAdCallBack {
                override fun onError() {
                    onClose?.invoke()
                }

                override fun onLoadSuccess() {

                }

                override fun onStartShow() {

                }

                override fun onAdShow() {

                }

                override fun onAdVideoBarClick() {

                }

                override fun onAdClose() {
                    onClose?.invoke()
                }

                override fun onVideoComplete() {

                }

                override fun onSkippedVideo() {

                }
            })
    }

    fun showFeedsAd(
        activity: Activity,
        container: FrameLayout,
        width: Int,
        height: Int,
        onAdLoad: (() -> Unit)? = null
    ) {
        AdvertisementManager.getInstance()
            .showInfoFlowAd(
                activity,
                FEEDS_AD_ID,
                container,
                width,
                height,
                object : InformationFlowAdCallback {
                    override fun onError() {

                    }

                    override fun onFeedAdLoad() {
                        onAdLoad?.invoke()
                    }

                    override fun onRenderSuccess() {

                    }

                    override fun onAdClick() {

                    }

                    override fun onRenderFail() {

                    }
                }
            )
    }

    fun showRewardVideoAd(
        activity: Activity,
        onClose: (() -> Unit)? = null
    ) {
        AdvertisementManager.getInstance()
            .showRewardAd(activity, REWARD_AD_ID, object : RewardAdCallBack {

                override fun onAdClose() {
                    onClose?.invoke()
                }

                override fun onVideoComplete() {

                }

                override fun onAdVideoBarClick() {

                }

                override fun onVideoError() {

                }

                override fun onRewardArrived() {

                }

                override fun onSkippedVideo() {

                }

                override fun onAdShow() {

                }

                override fun onError() {

                }
            })
    }

}
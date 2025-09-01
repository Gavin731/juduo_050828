package com.film.television.activity

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.film.television.databinding.ActivitySplashBinding
import com.film.television.mainHandler
import com.film.television.utils.AdUtil
import com.film.television.utils.ApplicationData
import com.film.television.utils.Constants
import com.film.television.utils.TokenUtil
import com.film.television.utils.UIUtil
import com.film.television.viewmodel.SplashViewModel
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun initObservation() {
        ApplicationData.isAdSdkInitNeeded.observe(this) { needed ->
            if (!needed) {
                ApplicationData.initStatus.observe(this, object : Observer<Boolean> {
                    override fun onChanged(initialized: Boolean) {
                        ApplicationData.initStatus.removeObserver(this)
                        lifecycleScope.launch {
                            TokenUtil.getToken()?.let { token ->
                                queryConfig(token)
                            }
                            if (initialized && Constants.GLOBAL_AD_ENABLED && Constants.SPLASH_AD_ENABLED) {
                                val (width, height) = UIUtil.getScreenWidthHeightWithoutInsets(this@SplashActivity)
                                AdUtil.showSplashAd(
                                    this@SplashActivity,
                                    binding.splashContainer,
                                    width,
                                    height,
                                    onClose = {
                                        goToMainActivity()
                                    },
                                    onError = {
                                        goToMainActivity()
                                    }
                                )
                            } else {
                                mainHandler.postDelayed({
                                    goToMainActivity()
                                }, 1500L)
                            }
                        }
                    }
                })
            } else {
                lifecycleScope.launch {
                    TokenUtil.getToken()?.let { token ->
                        queryConfig(token)
                    }
                    mainHandler.postDelayed({
                        goToMainActivity()
                    }, 1500L)
                }
            }
        }
    }

    private suspend fun queryConfig(token: String) {
        val resp = splashViewModel.queryConfig(token)
        for (item in resp.data) {
            when (item.configKey) {
                Constants.GLOBAL_AD_SWITCH -> Constants.GLOBAL_AD_ENABLED = item.configStatus
                Constants.SPLASH_AD_SWITCH -> Constants.SPLASH_AD_ENABLED = item.configStatus
                Constants.INTERSTITIAL_AD_SWITCH -> {
                    Constants.INTERSTITIAL_AD_ENABLED = item.configStatus
                }

                Constants.VIDEO_AD_SWITCH -> Constants.VIDEO_AD_ENABLED = item.configStatus
                Constants.FEEDS_AD_SWITCH -> Constants.FEEDS_AD_ENABLED = item.configStatus
            }
        }
        Log.d("lytest", "SplashActivity queryConfig resp: $resp")
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
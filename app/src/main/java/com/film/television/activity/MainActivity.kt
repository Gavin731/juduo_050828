package com.film.television.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.film.television.R
import com.film.television.databinding.ActivityMainBinding
import com.film.television.databinding.DialogGrantBinding
import com.film.television.databinding.DialogVipBinding
import com.film.television.databinding.DialogWarmTipsBinding
import com.film.television.fragment.CategoryFragment
import com.film.television.fragment.HomeFragment
import com.film.television.fragment.MineFragment
import com.film.television.isTeenModeEnabled
import com.film.television.showShortToast
import com.film.television.utils.AdUtil
import com.film.television.utils.ApplicationData
import com.film.television.utils.Constants
import com.film.television.utils.DataStoreUtil
import com.film.television.utils.Formatter
import com.film.television.utils.MyClickableSpan
import com.film.television.utils.RouteUtil
import com.film.television.utils.TokenUtil
import com.film.television.utils.UMUtil
import com.film.television.viewmodel.MainViewModel
import com.umeng.analytics.MobclickAgent
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val mainViewModel: MainViewModel by viewModels()
    private val tabList: MutableList<View> = mutableListOf()
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            lifecycleScope.launch {
                TokenUtil.getToken()?.let { token ->
                    postEnvInfo(token)
                }
                enterTeenMode()
            }
        }
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }
    private var lastBackPressTimeStamp = 0L

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun initView() {
        binding.viewpager.isUserInputEnabled = false
        binding.viewpager.offscreenPageLimit = 2
        val fragmentList = listOf(HomeFragment(), CategoryFragment(), MineFragment())
        binding.viewpager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }

            override fun getItemCount(): Int {
                return fragmentList.size
            }
        }
        tabList.addAll(listOf(binding.tabHome, binding.tabCategory, binding.tabMine))
        tabList.withIndex().forEach { indexedTab ->
            indexedTab.value.setOnClickListener {
                mainViewModel.setPosition(indexedTab.index)
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                val i = insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
                bottomMargin = i.bottom
            }
            insets
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.ACTION_FINISH_MAIN_ACTIVITY)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, intentFilter)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTimeStamp = System.currentTimeMillis()
                if (currentTimeStamp - lastBackPressTimeStamp < 1500L) {
                    MobclickAgent.onKillProcess(this@MainActivity)
                    finish()
                } else {
                    lastBackPressTimeStamp = currentTimeStamp
                    showShortToast("再按一次退出~")
                }
            }
        })
    }


    override fun initObservation() {
        mainViewModel.position.observe(this) { position ->
            selectPage(position)
        }
        mainViewModel.hotCategory.observe(this) {
            selectPage(1)
        }
        mainViewModel.showSupportDialog.observe(this, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                mainViewModel.showSupportDialog.removeObserver(this)
                showSupportDialog()
            }
        })
    }

    private fun selectPage(position: Int) {
        binding.viewpager.currentItem = position
        tabList.withIndex().forEach { indexedTab ->
            indexedTab.value.isSelected = indexedTab.index == position
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun postEnvInfo(token: String) {
        if (!DataStoreUtil.getEnvInfoPosted()) {
            DataStoreUtil.setEnvInfoPosted()
            mainViewModel.postEnvInfo(this, lifecycleScope, token) { resp ->
                Log.d("lytest", "MainActivity postEnvInfo resp: $resp")
            }
        }
    }

    private fun showPrivacyDialog1() {
        lifecycleScope.launch {
            if (!DataStoreUtil.getGranted()) {
                val grantDialog = AppCompatDialog(this@MainActivity, R.style.Theme_Dialog)
                grantDialog.show()
                grantDialog.window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                val grantDialogBinding = DialogGrantBinding.inflate(layoutInflater)
                grantDialogBinding.intro.movementMethod = LinkMovementMethod.getInstance()
                grantDialogBinding.intro.text = SpannableStringBuilder().apply {
                    append("衷心感谢您选用")
                    append(getString(R.string.app_name))
                    append("!我们非常尊重并保护您的个人信息和隐私，为了更好的保障您的权利，在您使用我们的产品前，请您务必谨慎阅读")
                    append("《用户协议》", object : MyClickableSpan() {
                        override fun onClick(widget: View) {
                            RouteUtil.goToWebActivity(
                                this@MainActivity,
                                null,
                                Constants.USER_AGREEMENT
                            )
                        }
                    }, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    append("和")
                    append("《隐私政策》", object : MyClickableSpan() {
                        override fun onClick(widget: View) {
                            RouteUtil.goToWebActivity(
                                this@MainActivity,
                                null,
                                Constants.PRIVACY_POLICY
                            )
                        }
                    }, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    append("内的所有条款。")
                }
                grantDialogBinding.action.setOnClickListener {
                    grantDialog.dismiss()
                    showTipsDialog()
                }
                grantDialogBinding.confirm.setOnClickListener {
                    grantDialog.dismiss()
                    onConfirm()
                }
                grantDialog.setContentView(grantDialogBinding.root)
            } else {
                requestMyPermissions()
            }
        }
    }

    private fun onConfirm() {
        lifecycleScope.launch {
            DataStoreUtil.setGranted()
            UMUtil.init(applicationContext)
            if (ApplicationData.isAdSdkInitNeeded.value == true) {
                ApplicationData.initStatus.observe(this@MainActivity, object : Observer<Boolean> {
                    override fun onChanged(initialized: Boolean) {
                        ApplicationData.initStatus.removeObserver(this)
                        if (initialized && Constants.GLOBAL_AD_ENABLED && Constants.INTERSTITIAL_AD_ENABLED) {
                            AdUtil.showInterstitialAd(this@MainActivity) {
                                requestMyPermissions()
                            }
                        } else {
                            requestMyPermissions()
                        }
                    }
                })
                AdUtil.init(applicationContext)
            } else {
                if (ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.INTERSTITIAL_AD_ENABLED) {
                    AdUtil.showInterstitialAd(this@MainActivity) {
                        requestMyPermissions()
                    }
                } else {
                    requestMyPermissions()
                }
            }
//            if (ApplicationData.initStatus.value == true) {
//                if (Constants.GLOBAL_AD_ENABLED && Constants.INTERSTITIAL_AD_ENABLED) {
//                    AdUtil.showInterstitialAd(this@MainActivity) {
//                        requestMyPermissions()
//                    }
//                } else {
//                    requestMyPermissions()
//                }
//            } else {
//                ApplicationData.initStatus.observe(this@MainActivity, object : Observer<Boolean> {
//                    override fun onChanged(value: Boolean) {
//                        ApplicationData.initStatus.removeObserver(this)
//                        if (value && Constants.GLOBAL_AD_ENABLED && Constants.INTERSTITIAL_AD_ENABLED) {
//                            AdUtil.showInterstitialAd(this@MainActivity) {
//                                requestMyPermissions()
//                            }
//                        } else {
//                            requestMyPermissions()
//                        }
//                    }
//                })
//                UMUtil.init(applicationContext)
//                AdUtil.init(applicationContext)
//            }
        }
    }

    private fun showTipsDialog() {
        val tipsDialog = AppCompatDialog(this, R.style.Theme_Dialog)
        tipsDialog.show()
        tipsDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        val tipsBinding = DialogWarmTipsBinding.inflate(layoutInflater)
        tipsBinding.msg.movementMethod = LinkMovementMethod.getInstance()
        tipsBinding.msg.text = SpannableStringBuilder().apply {
            append("您需要同意")
            append("《用户协议》", object : MyClickableSpan() {
                override fun onClick(widget: View) {
                    RouteUtil.goToWebActivity(
                        this@MainActivity,
                        null,
                        Constants.USER_AGREEMENT
                    )
                }
            }, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            append("和")
            append("《隐私政策》", object : MyClickableSpan() {
                override fun onClick(widget: View) {
                    RouteUtil.goToWebActivity(
                        this@MainActivity,
                        null,
                        Constants.PRIVACY_POLICY
                    )
                }
            }, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            append("才能使用我们提供的服务")
        }
        tipsBinding.lookAt.setOnClickListener {
            tipsDialog.dismiss()
            showPrivacyDialog2()
        }
        tipsBinding.confirm.setOnClickListener {
            tipsDialog.dismiss()
            onConfirm()
        }
        tipsDialog.setContentView(tipsBinding.root)
    }

    private fun showPrivacyDialog2() {
        val grantDialog = AppCompatDialog(this@MainActivity, R.style.Theme_Dialog)
        grantDialog.show()
        grantDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        val grantDialogBinding = DialogGrantBinding.inflate(layoutInflater)
        grantDialogBinding.intro.movementMethod = LinkMovementMethod.getInstance()
        grantDialogBinding.intro.text = SpannableStringBuilder().apply {
            append("衷心感谢您选用")
            append(getString(R.string.app_name))
            append("!我们非常尊重并保护您的个人信息和隐私，为了更好的保障您的权利，在您使用我们的产品前，请您务必谨慎阅读")
            append("《用户协议》", object : MyClickableSpan() {
                override fun onClick(widget: View) {
                    RouteUtil.goToWebActivity(
                        this@MainActivity,
                        null,
                        Constants.USER_AGREEMENT
                    )
                }
            }, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            append("和")
            append("《隐私政策》", object : MyClickableSpan() {
                override fun onClick(widget: View) {
                    RouteUtil.goToWebActivity(
                        this@MainActivity,
                        null,
                        Constants.PRIVACY_POLICY
                    )
                }
            }, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            append("内的所有条款。")
        }
        grantDialogBinding.action.text = "退出"
        grantDialogBinding.action.setOnClickListener {
            grantDialog.dismiss()
            finish()
        }
        grantDialogBinding.confirm.setOnClickListener {
            grantDialog.dismiss()
            onConfirm()
        }
        grantDialog.setContentView(grantDialogBinding.root)
    }

    private fun showSupportDialog() {
        Log.d("lytest", "Two")
        lifecycleScope.launch {
            if (!DataStoreUtil.getSupported() && ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.VIDEO_AD_ENABLED && !Constants.IS_IN_REVIEW) {
                val supportDialog = AppCompatDialog(this@MainActivity, R.style.Theme_Dialog)
                supportDialog.show()
                supportDialog.window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                val vipBinding: DialogVipBinding = DialogVipBinding.inflate(layoutInflater)
                fun showInterstitialAdAndPrivacyDialog() {
                    if (ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.INTERSTITIAL_AD_ENABLED) {
                        AdUtil.showInterstitialAd(this@MainActivity) {
                            showPrivacyDialog1()
                        }
                    } else {
                        showPrivacyDialog1()
                    }
                }
                vipBinding.close.setOnClickListener {
                    supportDialog.dismiss()
                    showInterstitialAdAndPrivacyDialog()
                }
                val listener = View.OnClickListener {
                    supportDialog.dismiss()
                    if (ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.VIDEO_AD_ENABLED) {
                        AdUtil.showRewardVideoAd(this@MainActivity) {
                            showInterstitialAdAndPrivacyDialog()
                        }
                    } else {
                        showInterstitialAdAndPrivacyDialog()
                    }
                    lifecycleScope.launch {
                        DataStoreUtil.setSupported()
                    }
                }
                vipBinding.join.setOnClickListener(listener)
                vipBinding.support.setOnClickListener(listener)
                supportDialog.setContentView(vipBinding.root)
            } else {
                showPrivacyDialog1()
            }
        }
    }

    private fun requestMyPermissions() {
        permissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
    }

    private suspend fun enterTeenMode() {
        if (isTeenModeEnabled()) {
            if (DataStoreUtil.getEverydayTeenModeUsableTime(Formatter.formatCalendar(Calendar.getInstance())) == 0L) {
                finish()
            } else {
                RouteUtil.goToTeenModeContentActivity(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

}
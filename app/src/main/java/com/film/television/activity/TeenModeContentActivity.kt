package com.film.television.activity

import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.film.television.R
import com.film.television.adapter.PagedVideoAdapter
import com.film.television.databinding.ActivityTeenModeContentBinding
import com.film.television.model.GeneralVideoInfoResp
import com.film.television.showShortToast
import com.film.television.utils.AdUtil
import com.film.television.utils.ApplicationData
import com.film.television.utils.Constants
import com.film.television.utils.DataStoreUtil
import com.film.television.utils.Formatter
import com.film.television.viewmodel.TeenModeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class TeenModeContentActivity : BaseActivity<ActivityTeenModeContentBinding>() {
    private val pagingAdapter: PagedVideoAdapter by lazy {
        PagedVideoAdapter(null, this).apply {
            lifecycleScope.launch {
                loadStateFlow.collectLatest { state ->
                    binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading
                }
            }
        }
    }
    private val teenModeViewModel: TeenModeViewModel by viewModels()
    private val passwordLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                lifecycleScope.launch {
                    DataStoreUtil.setPassword("")
                    finish()
                }
            }
        }
    private var countDownTimer: CountDownTimer? = null
    private var adSdkInitialized = false

    override fun getViewBinding(): ActivityTeenModeContentBinding {
        return ActivityTeenModeContentBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.timer.setOnClickListener {
            showShortToast("可用时间：${binding.timer.text}")
        }
        val initDateString = Formatter.formatCalendar(Calendar.getInstance())
        lifecycleScope.launch {
            val usableTime = DataStoreUtil.getEverydayTeenModeUsableTime(initDateString)
            countDownTimer = object : CountDownTimer(usableTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val tickDateString = Formatter.formatCalendar(Calendar.getInstance())
                    if (tickDateString == initDateString) {
                        binding.timer.text = Formatter.formatTime(millisUntilFinished)
                        lifecycleScope.launch {
                            DataStoreUtil.setEverydayTeenModeUsableTime(
                                initDateString,
                                millisUntilFinished
                            )
                        }
                    } else {
                        cancel()
                        binding.timer.text = Formatter.formatTime(Constants.TEEN_MODE_MAX_USE_TIME)
                        countDownTimer =
                            object : CountDownTimer(Constants.TEEN_MODE_MAX_USE_TIME, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    binding.timer.text = Formatter.formatTime(millisUntilFinished)
                                    lifecycleScope.launch {
                                        DataStoreUtil.setEverydayTeenModeUsableTime(
                                            tickDateString,
                                            millisUntilFinished
                                        )
                                    }
                                }

                                override fun onFinish() {
                                    // 倒计时结束
                                    lifecycleScope.launch {
                                        DataStoreUtil.setEverydayTeenModeUsableTime(
                                            tickDateString,
                                            0L
                                        )
                                        finish()
                                        LocalBroadcastManager.getInstance(this@TeenModeContentActivity)
                                            .sendBroadcast(Intent(Constants.ACTION_FINISH_MAIN_ACTIVITY))
                                    }
                                }
                            }.also {
                                it.start()
                            }
                    }
                }

                override fun onFinish() {
                    // 倒计时结束
                    lifecycleScope.launch {
                        DataStoreUtil.setEverydayTeenModeUsableTime(initDateString, 0L)
                        finish()
                        LocalBroadcastManager.getInstance(this@TeenModeContentActivity)
                            .sendBroadcast(Intent(Constants.ACTION_FINISH_MAIN_ACTIVITY))
                    }
                }
            }.also {
                it.start()
            }
        }
        binding.quit.setOnClickListener {
            val intent = Intent(this, PasswordActivity::class.java)
            intent.putExtra(Constants.KEY_MODE, Constants.MODE_VERIFY_PASSWORD)
            passwordLauncher.launch(intent)
        }
        binding.swipeRefresh.setColorSchemeResources(R.color.theme_color)
        binding.swipeRefresh.setOnRefreshListener {
            pagingAdapter.refresh()
        }
        binding.recyclerView.setHasFixedSize(true)
        adSdkInitialized = ApplicationData.initStatus.value == true
        val gridLayoutManager = GridLayoutManager(this, 3)
        if (adSdkInitialized && Constants.GLOBAL_AD_ENABLED && Constants.FEEDS_AD_ENABLED) {
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position % 7 == 6) {
                        3
                    } else {
                        1
                    }
                }
            }
        }
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.adapter = pagingAdapter
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                LocalBroadcastManager.getInstance(this@TeenModeContentActivity)
                    .sendBroadcast(Intent(Constants.ACTION_FINISH_MAIN_ACTIVITY))
            }
        })
        if (ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.INTERSTITIAL_AD_ENABLED) {
            AdUtil.showInterstitialAd(this)
        }
    }

    override fun initObservation() {
        ApplicationData.generalVideoInfoData.observe(
            this,
            object : Observer<GeneralVideoInfoResp.Data> {
                override fun onChanged(data: GeneralVideoInfoResp.Data) {
                    data.categories.entrySet().firstOrNull {
                        it.value.asString == "动漫"
                    }?.key?.let { cat ->
                        lifecycleScope.launch {
                            teenModeViewModel.getContentFlow(adSdkInitialized, cat)
                                .collectLatest { pagingData ->
                                    pagingAdapter.submitData(pagingData)
                                }
                        }
                    }
                    ApplicationData.generalVideoInfoData.removeObserver(this)
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
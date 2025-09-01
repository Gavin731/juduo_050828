package com.film.television.activity

import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.film.television.R
import com.film.television.adapter.SearchAdapter
import com.film.television.databinding.ActivitySearchBinding
import com.film.television.databinding.DialogClearSearchHistoryBinding
import com.film.television.showShortToast
import com.film.television.utils.AdUtil
import com.film.television.utils.ApplicationData
import com.film.television.utils.Constants
import com.film.television.utils.DataStoreUtil
import com.film.television.viewmodel.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private val searchViewModel: SearchViewModel by viewModels()
    private val searchAdapter: SearchAdapter by lazy {
        SearchAdapter(this).apply {
            lifecycleScope.launch {
                loadStateFlow.collectLatest { loadStates ->
                    binding.searchResult.isRefreshing = loadStates.refresh is LoadState.Loading
                }
            }
        }
    }
    private var searchVideoJob: Job? = null

    override fun getViewBinding(): ActivitySearchBinding {
        return ActivitySearchBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.back.setOnClickListener { finish() }
        binding.search.setOnClickListener {
            onSearch()
        }
        binding.clearHistory.setOnClickListener {
            clearHistory()
        }
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    onSearch()
                    true
                }

                else -> false
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin =
                    insets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars()).bottom
            }
            insets
        }
        binding.searchInput.addTextChangedListener { text ->
            if (text.isNullOrEmpty()) {
                binding.defaultLayout.visibility = View.VISIBLE
                binding.searchResult.visibility = View.GONE
            }
        }
        binding.searchResult.setColorSchemeResources(R.color.theme_color)
        binding.searchResult.setOnRefreshListener {
            searchAdapter.refresh()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = searchAdapter
        if (ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.INTERSTITIAL_AD_ENABLED) {
            AdUtil.showInterstitialAd(this)
        }
    }

    override fun initObservation() {
        lifecycleScope.launch {
            DataStoreUtil.getSearchHistoryFlow().collectLatest { historyList ->
                binding.searchHistory.removeAllViews()
                if (historyList.isEmpty()) {
                    binding.clearHistory.visibility = View.INVISIBLE
                } else {
                    binding.clearHistory.visibility = View.VISIBLE
                    for (history in historyList) {
                        val textView = TextView(this@SearchActivity)
                        val dp6 = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            6f,
                            resources.displayMetrics
                        ).toInt()
                        val dp12 = dp6 * 2
                        textView.setPadding(dp12, dp6, dp12, dp6)
                        textView.setBackgroundResource(R.drawable.bg_search_history)
                        textView.setTextColor(Color.BLACK)
                        textView.textSize = 14f
                        textView.text = history
                        textView.setOnClickListener {
                            binding.searchInput.setText(history)
                            binding.searchInput.setSelection(history.length)
                            onSearch()
                        }
                        binding.searchHistory.addView(textView)
                    }
                }
            }
        }
    }

    private fun onSearch() {
        val input = binding.searchInput.text.toString().trim()
        if (input.isEmpty()) {
            showShortToast("输入不能为空")
            return
        }
        binding.defaultLayout.visibility = View.GONE
        binding.searchResult.visibility = View.VISIBLE
        recordHistory(input)
        doSearch(input)
    }

    private fun recordHistory(input: String) {
        lifecycleScope.launch {
            DataStoreUtil.addSearchHistory(input)
        }
    }

    private fun doSearch(input: String) {
        searchVideoJob?.cancel()
        searchVideoJob = lifecycleScope.launch {
            searchViewModel.getSearchVideoFlow(input).collectLatest { pagingData ->
                searchAdapter.submitData(pagingData)
            }
        }
    }

    private fun clearHistory() {
        showClearHistoryDialog()
    }

    private fun showClearHistoryDialog() {
        val clearHistoryDialog = AppCompatDialog(this, R.style.Theme_Dialog)
        clearHistoryDialog.show()
        val binding = DialogClearSearchHistoryBinding.inflate(layoutInflater)
        clearHistoryDialog.setContentView(binding.root)
        binding.close.setOnClickListener {
            clearHistoryDialog.dismiss()
        }
        binding.cancel.setOnClickListener {
            clearHistoryDialog.dismiss()
        }
        binding.confirm.setOnClickListener {
            lifecycleScope.launch {
                DataStoreUtil.clearSearchHistory()
                clearHistoryDialog.dismiss()
            }
        }
    }
}
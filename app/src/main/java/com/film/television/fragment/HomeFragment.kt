package com.film.television.fragment

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.film.television.activity.SearchActivity
import com.film.television.databinding.FragmentHomeBinding
import com.film.television.utils.AdUtil
import com.film.television.utils.ApplicationData
import com.film.television.utils.Constants
import com.film.television.utils.TokenUtil
import com.film.television.viewmodel.HomeViewModel
import com.film.television.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var mediator: TabLayoutMediator? = null
    private var oneShotFlag = true

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        binding.viewpager.isUserInputEnabled = false
        binding.searchBg.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }
    }

    override fun initData() {
        viewLifecycleOwner.lifecycleScope.launch {
            TokenUtil.getToken()?.let { token ->
                val resp = homeViewModel.queryGeneralVideoInfo(token)
                if (resp.code == 200) {
                    ApplicationData.setGeneralVideoInfoData(resp.data)
                    val titleList = mutableListOf<String>("热门")
                    val fragmentList = mutableListOf<Fragment>(HotFragment())
                    for (category in resp.data.categories.entrySet()) {
                        titleList.add(category.value.asString)
                        fragmentList.add(HomeOtherFragment().apply {
                            arguments = bundleOf(Constants.KEY_CATEGORY to category.key)
                        })
                    }
                    binding.viewpager.adapter = object : FragmentStateAdapter(this@HomeFragment) {
                        override fun createFragment(position: Int): Fragment {
                            return fragmentList[position]
                        }

                        override fun getItemCount(): Int {
                            return fragmentList.size
                        }
                    }
                    binding.viewpager.offscreenPageLimit = fragmentList.size - 1
                    mediator = TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
                        tab.text = titleList[position]
                    }.also { it.attach() }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (oneShotFlag) {
            oneShotFlag = false
            if (ApplicationData.initStatus.value == true && Constants.GLOBAL_AD_ENABLED && Constants.INTERSTITIAL_AD_ENABLED) {
                AdUtil.showInterstitialAd(requireActivity()) {
                    Log.d("lytest", "One")
                    mainViewModel.setShowSupportDialog()
                }
            } else {
                mainViewModel.setShowSupportDialog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediator?.detach()
    }
}
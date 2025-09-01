package com.film.television.activity

import com.film.television.BuildConfig
import com.film.television.databinding.ActivityAboutUsBinding
import com.film.television.utils.Constants
import com.film.television.utils.RouteUtil

class AboutUsActivity : BaseActivity<ActivityAboutUsBinding>() {
    override fun getViewBinding() = ActivityAboutUsBinding.inflate(layoutInflater)

    override fun initView() {
        binding.toolbar.title.text = "关于我们"
        binding.toolbar.back.setOnClickListener { finish() }
        binding.version.text = "v${BuildConfig.VERSION_NAME}"
        binding.userAgreement.setOnClickListener {
            RouteUtil.goToWebActivity(this, "用户协议", Constants.USER_AGREEMENT)
        }
        binding.privacyPolicy.setOnClickListener {
            RouteUtil.goToWebActivity(this, "隐私政策", Constants.PRIVACY_POLICY)
        }
    }
}
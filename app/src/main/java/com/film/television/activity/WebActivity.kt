package com.film.television.activity

import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import com.film.television.databinding.ActivityWebBinding
import com.film.television.utils.Constants

class WebActivity : BaseActivity<ActivityWebBinding>() {

    override fun getViewBinding(): ActivityWebBinding {
        return ActivityWebBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.back.setOnClickListener {
            goBack()
        }
        binding.title.text = intent.getStringExtra(Constants.KEY_TITLE)
        binding.finish.setOnClickListener {
            finish()
        }
        binding.webView.webViewClient = MyWebViewClient()
        binding.webView.webChromeClient = WebChromeClient()
        with(binding.webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            javaScriptCanOpenWindowsAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        intent.getStringExtra(Constants.KEY_URL)?.let { url ->
            binding.webView.loadUrl(url)
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goBack()
            }
        })
    }

    private fun goBack() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            finish()
        }
    }

    private class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            return !(request.url.scheme == "http" || request.url.scheme == "https")
        }
    }
}
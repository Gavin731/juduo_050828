package com.film.television.activity

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.film.television.databinding.ActivityTeenModeBinding
import com.film.television.utils.RouteUtil

class TeenModeActivity : BaseActivity<ActivityTeenModeBinding>() {
    private val passwordLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                finish()
                RouteUtil.goToTeenModeContentActivity(this)
            }
        }

    override fun getViewBinding() = ActivityTeenModeBinding.inflate(layoutInflater)

    override fun initView() {
        binding.enableTeenMode.setOnClickListener {
            passwordLauncher.launch(Intent(this, PasswordActivity::class.java))
        }
    }
}
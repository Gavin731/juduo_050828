package com.film.television.activity

import android.view.KeyEvent
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.film.television.databinding.ActivityPasswordBinding
import com.film.television.utils.Constants
import com.film.television.utils.DataStoreUtil
import kotlinx.coroutines.launch

class PasswordActivity : BaseActivity<ActivityPasswordBinding>() {
    private var mode: String = Constants.MODE_SET_PASSWORD
    private var round: Round? = null
    private var roundOnePassword: String? = null

    override fun getViewBinding(): ActivityPasswordBinding {
        return ActivityPasswordBinding.inflate(layoutInflater)
    }

    override fun initView() {
        mode = intent.getStringExtra(Constants.KEY_MODE) ?: Constants.MODE_SET_PASSWORD
        binding.toolbar.back.setOnClickListener { finish() }
        binding.toolbar.title.text = "设置密码"
        val passList = listOf(binding.passOne, binding.passTwo, binding.passThree, binding.passFour)
        when (mode) {
            Constants.MODE_SET_PASSWORD -> {
                round = Round.ONE
                for (index in 0 until passList.size) {
                    val passView = passList[index]
                    passView.addTextChangedListener { text ->
                        if (text?.length == 1) {
                            if (index < passList.lastIndex) {
                                passList[index + 1].setText("")
                                passList[index + 1].requestFocus()
                            }
                            if (index == passList.lastIndex) {
                                if (passList.all { it.text?.length == 1 }) {
                                    when (round) {
                                        Round.ONE -> {
                                            roundOnePassword = passList.joinToString("") {
                                                it.text.toString()
                                            }
                                            binding.tip.text = "确认密码"
                                            passList.forEach {
                                                it.setText("")
                                            }
                                            passList[0].requestFocus()
                                            round = Round.TWO
                                        }

                                        Round.TWO -> {
                                            val roundTwoPassword = passList.joinToString("") {
                                                it.text.toString()
                                            }
                                            if (roundTwoPassword == roundOnePassword) {
                                                lifecycleScope.launch {
                                                    DataStoreUtil.setPassword(roundTwoPassword)
                                                    setResult(RESULT_OK)
                                                    finish()
                                                }
                                            } else {
                                                binding.tip.text = "输入密码"
                                                passList.forEach {
                                                    it.setText("")
                                                }
                                                passList[0].requestFocus()
                                                round = Round.ONE
                                            }
                                        }

                                        else -> {}
                                    }
                                } else {
                                    passList.first { it.text.isNullOrEmpty() }.requestFocus()
                                }
                            }
                        }
                    }
                    passView.setOnKeyListener { _, keyCode, _ ->
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            if (passView.text.length == 1) {
                                passView.setText("")
                            }
                            if (index > 0) {
                                passList[index - 1].requestFocus()
                            }
                            true
                        } else {
                            false
                        }
                    }
                }
            }

            Constants.MODE_VERIFY_PASSWORD -> {
                for (index in 0 until passList.size) {
                    val passView = passList[index]
                    passView.addTextChangedListener { text ->
                        if (text?.length == 1) {
                            if (index < passList.lastIndex) {
                                passList[index + 1].setText("")
                                passList[index + 1].requestFocus()
                            }
                            if (index == passList.lastIndex) {
                                if (passList.all { it.text?.length == 1 }) {
                                    val password = passList.joinToString("") {
                                        it.text.toString()
                                    }
                                    lifecycleScope.launch {
                                        val storedPassword = DataStoreUtil.getPassword()
                                        if (storedPassword == password) {
                                            setResult(RESULT_OK)
                                            finish()
                                        }
                                    }
                                } else {
                                    passList.first { it.text.isNullOrEmpty() }.requestFocus()
                                }
                            }
                        }
                    }
                    passView.setOnKeyListener { _, keyCode, _ ->
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            if (passView.text.length == 1) {
                                passView.setText("")
                            }
                            if (index > 0) {
                                passList[index - 1].requestFocus()
                            }
                            true
                        } else {
                            false
                        }
                    }
                }
            }
        }
        passList[0].requestFocus()
    }

    private enum class Round {
        ONE,
        TWO
    }
}
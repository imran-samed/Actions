package com.stpauls.dailyliturgy.base

import android.content.Context
import android.os.Bundle
import android.view.Window
import com.stpauls.dailyliturgy.R
import com.stpauls.dailyliturgy.databinding.LayoutLoadingDialogBinding

class MyProgressDialog(context: Context, val msg: String?) : BaseDialog(context) {
    private lateinit var binding: LayoutLoadingDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = LayoutLoadingDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)
        setDimBlur(window)
        if (!msg.isNullOrBlank()) {
            binding.tvMessage.text = msg
        }
    }
}

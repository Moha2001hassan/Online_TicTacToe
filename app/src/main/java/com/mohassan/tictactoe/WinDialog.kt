package com.mohassan.tictactoe

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mohassan.tictactoe.databinding.WinDialogLayoutBinding

class WinDialog(context: Context, private val message: String): Dialog(context) {

    private lateinit var binding: WinDialogLayoutBinding
    private var mainActivity = MainActivity()

    init {
        this.mainActivity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WinDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.startNewBtn.setOnClickListener {
            dismiss()
            context.startActivity(Intent(context,PlayerName::class.java))
            mainActivity.finish()
        }

    }

}
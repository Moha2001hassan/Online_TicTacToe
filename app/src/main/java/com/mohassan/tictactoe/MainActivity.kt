package com.mohassan.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mohassan.tictactoe.databinding.ActivityMainBinding
import com.mohassan.tictactoe.databinding.ActivityPlayerNameBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val getPlayerName = intent.getStringExtra("playerName")
        //binding.textView.text = getPlayerName.toString()



    }
}
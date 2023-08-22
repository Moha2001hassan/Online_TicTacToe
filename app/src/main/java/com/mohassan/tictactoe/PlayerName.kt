package com.mohassan.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mohassan.tictactoe.databinding.ActivityPlayerNameBinding

class PlayerName : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameBtn.setOnClickListener {
            val playerName = binding.playerNameEt.text.toString()

            if(playerName.isEmpty()){
                Toast.makeText(this,"Please enter player name!",Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("playerName",playerName)
                startActivity(intent)
                finish()
            }
        }

    }
}













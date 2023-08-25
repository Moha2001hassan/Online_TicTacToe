package com.mohassan.tictactoe

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mohassan.tictactoe.databinding.ActivityMainBinding
import com.mohassan.tictactoe.databinding.ActivityPlayerNameBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var combinationList = mutableListOf<List<Int>>()
    private var playerUniqueId = "0"
    private var opponentFound = false
    private var opponentUniqueId = "0"
    private var status = "matching"
    private var playerTurn = ""
    private var connectionId = ""
    private val doneBoxes = mutableListOf<String>()

    private lateinit var turnsEventListener : ValueEventListener
    private lateinit var wonEventListener : ValueEventListener

    // getting Firebase Database reference from URL
    private val databaseReference = FirebaseDatabase.getInstance()
        .getReferenceFromUrl("https://tic-tac-toe-9cdfe-default-rtdb.firebaseio.com/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val getPlayerName = intent.getStringExtra("playerName")

        combinationList.add(arrayListOf(0,1,2))
        combinationList.add(arrayListOf(3,4,5))
        combinationList.add(arrayListOf(6,7,8))

        combinationList.add(arrayListOf(0,3,6))
        combinationList.add(arrayListOf(1,4,7))
        combinationList.add(arrayListOf(2,3,8))
        combinationList.add(arrayListOf(2,4,6))
        combinationList.add(arrayListOf(0,4,8))

        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Waiting for opponent")
        progressDialog.show()

        playerUniqueId = System.currentTimeMillis().toString()

        binding.tvPlayerOne.text = getPlayerName

        //databaseReference.child("connections").addValueEventListener(ValueEventListener(){})
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // check if opponent found, if not look for the opponent
                if (opponentFound){

                    // check if there are others in the firebase database
                    if(snapshot.hasChildren()){

                        // checking all connections, if other users are also waiting for a user to play
                        for (connection in snapshot.children){

                            //getting connection unique id
                            val conId = connection.key.toString()

                            /*
                            2 player are required to play the game.
                            if getPlayerCount is 1, it means other player is waiting for an opponent
                            else if getPlayerCount is 2, it means this connection complete with 2 players
                             */
                            val getPlayerCount = connection.childrenCount.toString().toInt()
                            if (status == "waiting"){

                                if (getPlayerCount == 2){
                                    playerTurn = playerUniqueId
                                    applyPlayerTurn(playerTurn)

                                    var playerFound = false

                                    for(players in connection.children){

                                        val getPlayerUniqueId = players.key.toString()
                                        if(getPlayerUniqueId == playerUniqueId){
                                            playerFound = true
                                        }
                                        else if(playerFound){
                                            var getOpponentPlayerName = players.child("player_name").value.toString()
                                            opponentUniqueId = players.key.toString()

                                            connectionId = conId
                                            opponentFound = true

                                            databaseReference.child("turns").child(connectionId)
                                                .addValueEventListener(turnsEventListener)
                                            databaseReference.child("won").child(connectionId)
                                                .addValueEventListener(wonEventListener)

                                            if (progressDialog.isShowing){
                                                progressDialog.dismiss()
                                            }

                                            databaseReference.child("connection").removeEventListener(this)
                                        }
                                    }
                                }
                                // in case user hasn't created connection
                                else{
                                    // only 1 player available
                                    if(getPlayerCount == 1){
                                        connection.child("player_name").ref.setValue(getPlayerName)
                                        for (players in connection.children){
                                            val getOpponentName = players.child("player_name").value.toString()
                                            opponentUniqueId = players.key.toString()
                                            playerTurn = opponentUniqueId
                                            applyPlayerTurn(playerTurn)

                                            binding.tvPlayerTwo.text = getOpponentName

                                            connectionId = conId
                                            opponentFound = true

                                            databaseReference.child("turns").child(connectionId)
                                                .addValueEventListener(turnsEventListener)
                                            databaseReference.child("won").child(connectionId)
                                                .addValueEventListener(wonEventListener)

                                            if (progressDialog.isShowing){
                                                progressDialog.dismiss()
                                            }

                                            databaseReference.child("connection").removeEventListener(this)
                                            break
                                        }
                                    }
                                }
                            }
                        }
                        // if no opponent found and user won't wait
                        if(!opponentFound && status == "waiting"){
                            //generate unique id for the connection
                            val connectionUniqueId = System.currentTimeMillis().toString()

                            snapshot.child(connectionUniqueId).child(playerUniqueId)
                                .child("player_name").ref.setValue(getPlayerName)
                            status = "waiting"
                        }
                    }
                    /*
                    if there is no connections available in the firebase database then create new connection.
                    its like create a room and waiting for other players to join
                    */
                    else{
                        //generate unique id for the connection
                        val connectionUniqueId = System.currentTimeMillis().toString()

                        snapshot.child(connectionUniqueId).child(playerUniqueId)
                            .child("player_name").ref.setValue(getPlayerName)
                        status = "waiting"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        turnsEventListener = (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dataSnapshot in snapshot.children){
                    if(dataSnapshot.childrenCount.toInt() == 2){
                        val getBoxPosition = dataSnapshot.child("box_position").value.toString().toInt()
                        val getPlayerId = dataSnapshot.child("player_id").value.toString()

                        if(doneBoxes.contains(getBoxPosition.toString())){

                            doneBoxes.add(getBoxPosition.toString())
                            if (getBoxPosition == 1){

                            }
                            else if(getBoxPosition == 2) {

                            }
                            else if(getBoxPosition == 3) {

                            }
                            else if(getBoxPosition == 4) {

                            }
                            else if(getBoxPosition == 5) {

                            }
                            else if(getBoxPosition == 6) {

                            }
                            else if(getBoxPosition == 7) {

                            }
                            else if(getBoxPosition == 8) {

                            }
                            else if(getBoxPosition == 9) {

                            }

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        wonEventListener = (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun applyPlayerTurn(playerUniqueId2: String) {

        if (playerUniqueId2 == playerUniqueId){
            binding.player1Layout.setBackgroundResource(R.drawable.round_back_dark_blue)
            binding.player2Layout.setBackgroundResource(R.drawable.round_back_dark_blue2)


        }
        else{
            binding.player2Layout.setBackgroundResource(R.drawable.round_back_dark_blue)
            binding.player1Layout.setBackgroundResource(R.drawable.round_back_dark_blue2)

        }
    }

    private fun selectBox(){

    }
}
















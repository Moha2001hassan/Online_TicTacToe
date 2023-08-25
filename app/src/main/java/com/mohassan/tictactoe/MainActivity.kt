package com.mohassan.tictactoe

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mohassan.tictactoe.databinding.ActivityMainBinding

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
    private var boxesSelected = arrayListOf("","","","","","","","","")

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

        databaseReference.child("connections").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // check if opponent not found, if not look for the opponent
                //  !opponentFound
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

                        if(!doneBoxes.contains(getBoxPosition.toString())){

                            doneBoxes.add(getBoxPosition.toString())
                            when (getBoxPosition) {
                                1 -> {
                                    selectBox(binding.button1,getBoxPosition,getPlayerId)
                                }
                                2 -> {
                                    selectBox(binding.button2,getBoxPosition,getPlayerId)
                                }
                                3 -> {
                                    selectBox(binding.button3,getBoxPosition,getPlayerId)
                                }
                                4 -> {
                                    selectBox(binding.button4,getBoxPosition,getPlayerId)
                                }
                                5 -> {
                                    selectBox(binding.button5,getBoxPosition,getPlayerId)
                                }
                                6 -> {
                                    selectBox(binding.button6,getBoxPosition,getPlayerId)
                                }
                                7 -> {
                                    selectBox(binding.button7,getBoxPosition,getPlayerId)
                                }
                                8 -> {
                                    selectBox(binding.button8,getBoxPosition,getPlayerId)
                                }
                                9 -> {
                                    selectBox(binding.button9,getBoxPosition,getPlayerId)
                                }
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
                if(snapshot.hasChild("player_id")){

                    val getWinPlayerId = snapshot.child("player_id").value.toString()
                    val winDialog: WinDialog

                    if (getWinPlayerId == playerUniqueId){
                        winDialog = WinDialog(this@MainActivity,"You won the game")
                    }
                    else{
                        winDialog = WinDialog(this@MainActivity,"Opponent won the game")
                    }
                    winDialog.setCancelable(false)
                    winDialog.show()

                    databaseReference.child("turns").child(connectionId)
                        .removeEventListener(turnsEventListener)
                    databaseReference.child("won").child(connectionId)
                        .removeEventListener(wonEventListener)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.button1.setOnClickListener {

            // if box not selected before & current user turn
            if(!doneBoxes.contains("1") && playerTurn == playerUniqueId){
                binding.button1.background = ContextCompat.getDrawable(this, R.drawable.ic_x)

                // sent selected box position & player id to firebase
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("box_position").setValue("1")
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("player_id").setValue(playerUniqueId)
                // change player turn
                playerTurn = opponentUniqueId
            }
        }
        binding.button2.setOnClickListener {

            // if box not selected before & current user turn
            if(!doneBoxes.contains("2") && playerTurn == playerUniqueId){
                binding.button1.background = ContextCompat.getDrawable(this, R.drawable.ic_x)

                // sent selected box position & player id to firebase
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("box_position").setValue("2")
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("player_id").setValue(playerUniqueId)
                // change player turn
                playerTurn = opponentUniqueId
            }
        }
        binding.button3.setOnClickListener {

            // if box not selected before & current user turn
            if(!doneBoxes.contains("3") && playerTurn == playerUniqueId){
                binding.button1.background = ContextCompat.getDrawable(this, R.drawable.ic_x)

                // sent selected box position & player id to firebase
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("box_position").setValue("3")
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("player_id").setValue(playerUniqueId)
                // change player turn
                playerTurn = opponentUniqueId
            }
        }
        binding.button4.setOnClickListener {

            // if box not selected before & current user turn
            if(!doneBoxes.contains("4") && playerTurn == playerUniqueId){
                binding.button1.background = ContextCompat.getDrawable(this, R.drawable.ic_x)

                // sent selected box position & player id to firebase
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("box_position").setValue("4")
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("player_id").setValue(playerUniqueId)
                // change player turn
                playerTurn = opponentUniqueId
            }
        }
        binding.button5.setOnClickListener {

            // if box not selected before & current user turn
            if(!doneBoxes.contains("5") && playerTurn == playerUniqueId){
                binding.button1.background = ContextCompat.getDrawable(this, R.drawable.ic_x)

                // sent selected box position & player id to firebase
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("box_position").setValue("5")
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("player_id").setValue(playerUniqueId)
                // change player turn
                playerTurn = opponentUniqueId
            }
        }
        binding.button6.setOnClickListener {

            // if box not selected before & current user turn
            if(!doneBoxes.contains("6") && playerTurn == playerUniqueId){
                binding.button1.background = ContextCompat.getDrawable(this, R.drawable.ic_x)

                // sent selected box position & player id to firebase
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("box_position").setValue("6")
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("player_id").setValue(playerUniqueId)
                // change player turn
                playerTurn = opponentUniqueId
            }
        }
        binding.button7.setOnClickListener {

            // if box not selected before & current user turn
            if(!doneBoxes.contains("7") && playerTurn == playerUniqueId){
                binding.button1.background = ContextCompat.getDrawable(this, R.drawable.ic_x)

                // sent selected box position & player id to firebase
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("box_position").setValue("7")
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("player_id").setValue(playerUniqueId)
                // change player turn
                playerTurn = opponentUniqueId
            }
        }
        binding.button8.setOnClickListener {

            // if box not selected before & current user turn
            if(!doneBoxes.contains("8") && playerTurn == playerUniqueId){
                binding.button1.background = ContextCompat.getDrawable(this, R.drawable.ic_x)

                // sent selected box position & player id to firebase
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("box_position").setValue("8")
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("player_id").setValue(playerUniqueId)
                // change player turn
                playerTurn = opponentUniqueId
            }
        }
        binding.button9.setOnClickListener {

            // if box not selected before & current user turn
            if(!doneBoxes.contains("9") && playerTurn == playerUniqueId){
                binding.button1.background = ContextCompat.getDrawable(this, R.drawable.ic_x)

                // sent selected box position & player id to firebase
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("box_position").setValue("9")
                databaseReference.child("turns").child((doneBoxes.size + 1).toString())
                    .child("player_id").setValue(playerUniqueId)
                // change player turn
                playerTurn = opponentUniqueId
            }
        }

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

    private fun selectBox(button: Button, selectedBoxPosition: Int, selectedByPlayer: String){

        boxesSelected[selectedBoxPosition - 1] = selectedByPlayer

        if (selectedByPlayer == playerUniqueId){
            button.background = ContextCompat.getDrawable(this, R.drawable.ic_x)
            playerTurn = opponentUniqueId
        }
        else{
            button.background = ContextCompat.getDrawable(this, R.drawable.ic_o)
            playerTurn = opponentUniqueId
        }
        applyPlayerTurn(playerTurn)

        if (checkPlayerWin(selectedByPlayer)){
            databaseReference.child("won").child("player_id").setValue(selectedByPlayer)
        }

        if (doneBoxes.size == 9){
            val winDialog = WinDialog(this,"It was Draw!")
            winDialog.setCancelable(false)
            winDialog.show()
        }
    }

    private fun checkPlayerWin(playerId: String): Boolean{
        var isPlayerWin = false

        for (i in 0 until combinationList.size){
            val combination = combinationList[i]

            if(boxesSelected[combination[0]]== playerId
                && boxesSelected[combination[1]]== playerId
                && boxesSelected[combination[2]]== playerId){

                isPlayerWin = true
            }
        }
        return isPlayerWin
    }
}
















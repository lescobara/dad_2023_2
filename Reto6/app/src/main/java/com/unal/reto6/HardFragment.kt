package com.unal.reto6

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.system.exitProcess

class HardFragment : Fragment() {

    lateinit var buttons: List<ImageButton>
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var resetButton: Button

    private lateinit var soundManager: SoundManager

    var player1Count = 0
    var player2Count = 0

    var playerTurn = true
    var gameOver = false

    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()

    val WINNING_COMBINATIONS = listOf(
        listOf(1, 2, 3),  // Top row
        listOf(4, 5, 6),  // Middle row
        listOf(7, 8, 9),  // Bottom row
        listOf(1, 4, 7),  // Left column
        listOf(2, 5, 8),  // Middle column
        listOf(3, 6, 9),  // Right column
        listOf(1, 5, 9),  // Diagonal from top-left to bottom-right
        listOf(3, 5, 7)   // Diagonal from top-right to bottom-left
    )

    companion object {
        private const val ARG_PLAYER1_COUNT = "player1Count"
        private const val ARG_PLAYER2_COUNT = "player2Count"
        private const val ARG_LIST_PLAYER1_MOVES = "player1Moves"
        private const val ARG_LIST_PLAYER2_MOVES = "player2Moves"

        fun newInstance(player1Count: Int, player2Count: Int,player1Moves: ArrayList<Int>, player2Moves: ArrayList<Int>): HardFragment {
            val fragment = HardFragment()
            val args = Bundle()
            args.putInt(ARG_PLAYER1_COUNT, player1Count)
            args.putInt(ARG_PLAYER2_COUNT, player2Count)
            args.putIntegerArrayList(ARG_LIST_PLAYER1_MOVES,player1Moves)
            args.putIntegerArrayList(ARG_LIST_PLAYER2_MOVES,player2Moves)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        soundManager = SoundManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hard, container, false)
        // Retrieve player scores from arguments
        player1Count = arguments?.getInt(ARG_PLAYER1_COUNT, 0) ?: 0
        player2Count = arguments?.getInt(ARG_PLAYER2_COUNT, 0) ?: 0
        player1 = arguments?.getIntegerArrayList(ARG_LIST_PLAYER1_MOVES) ?: ArrayList()
        player2 = arguments?.getIntegerArrayList(ARG_LIST_PLAYER2_MOVES) ?: ArrayList()


        // Initialize UI components
        buttons = listOf(
            view.findViewById(R.id.button1),
            view.findViewById(R.id.button2),
            view.findViewById(R.id.button3),
            view.findViewById(R.id.button4),
            view.findViewById(R.id.button5),
            view.findViewById(R.id.button6),
            view.findViewById(R.id.button7),
            view.findViewById(R.id.button8),
            view.findViewById(R.id.button9)
        )

        textView1 = view.findViewById(R.id.textView1)
        textView2 = view.findViewById(R.id.textView2)
        // Set player scores in TextViews
        textView1.text = "Jugador1: $player1Count"
        textView2.text = "Teléfono: $player2Count"
        resetButton = view.findViewById(R.id.button10)

        // Initially set up the click listeners
        setupImageButtonClickListeners()

        // Set click listener for reset button
        resetButton.setOnClickListener {
            resetGame()
        }

        if (player1.size!=0){
            restoreGamePlayer1(player1)
        }

        if (player2.size!=0){
            restoreGamePlayer2(player2)
        }

        return view
    }

    private fun setupImageButtonClickListeners() {
        buttons.forEachIndexed { index, imageButton ->
            imageButton.setOnClickListener {
                if (!gameOver && playerTurn && isImageButtonEmpty(imageButton)) {
                    playerMove(index + 1)
                    checkWinner()
                    if (!gameOver) {
                        Handler().postDelayed({ robotMove() }, 600)
                    }
                }
            }
        }
    }

    private fun isImageButtonEmpty(imageButton: ImageButton): Boolean {
        return imageButton.drawable == null
    }

    private fun playerMove(cellID: Int) {
        if (!gameOver && playerTurn) {
            val imageButton = buttons[cellID - 1]
            if (isImageButtonEmpty(imageButton)) {
                imageButton.setImageResource(R.drawable.checkbox_cross_orange_icon)
                player1.add(cellID)
                imageButton.isEnabled = false // Disable the button after the move
                playerTurn = false // Switch to Player 2's turn
                soundManager.playPlayerSound()
                // Check for a winner after the player's move
                val winner = checkWinner()
                if (winner != -1) {
                    showGameOverDialog(winner)
                    gameOver=true
                } else {
                    Handler().postDelayed({ robotMove() }, 600)
                    //robotMove()
                }
            }
        }
    }

    private fun robotMove() {
        if (!gameOver && !playerTurn) {
            val bestMove = findBestMove()
            if (bestMove != -1) {
                val imageButton = buttons[bestMove - 1]
                if (isImageButtonEmpty(imageButton)) {
                    imageButton.setImageResource(R.drawable.starfilledminor_svgrepo_com)
                    soundManager.playRobotSound()
                    player2.add(bestMove)
                    imageButton.isEnabled = false // Disable the button after the move
                    // Check for a winner after the AI's move
                    val winner = checkWinner()
                    if (winner != -1 ) {
                        showGameOverDialog(winner)
                        gameOver=true
                    } else {
                        playerTurn = true // Switch to Player 1's turn after AI move
                    }
                }
            }
        } else {
            Log.d("Debug", "AI's turn skipped because it's not allowed now.")
        }
    }


    private fun findBestMove(): Int {
        // Check for a winning move for the AI
        for (cell in 1..9) {
            if (!player1.contains(cell) && !player2.contains(cell)) {
                val originalState = player2.toMutableList()
                player2.add(cell)
                if (checkWinner() == 2) {
                    player2.clear()
                    player2.addAll(originalState)
                    return cell
                }
                player2.clear()
                player2.addAll(originalState)
            }
        }

        // Check for a winning move for the player and block it
        for (cell in 1..9) {
            if (!player1.contains(cell) && !player2.contains(cell)) {
                val originalState = player1.toMutableList()
                player1.add(cell)
                if (checkWinner() == 1) {
                    player1.clear()
                    player1.addAll(originalState)
                    return cell
                }
                player1.clear()
                player1.addAll(originalState)
            }
        }

        // Make a strategic move (center, corners, or sides)
        val strategicMoves = listOf(5, 1, 3, 7, 9, 2, 4, 6, 8)
        for (cell in strategicMoves) {
            if (!player1.contains(cell) && !player2.contains(cell)) {
                return cell
            }
        }

        // If no strategic move is available, return a random empty cell
        val emptyCells = (1..9).filter { !player1.contains(it) && !player2.contains(it) }
        return if (emptyCells.isNotEmpty()) emptyCells.random() else -1
    }



    private fun checkWinner(): Int {

        for (combination in WINNING_COMBINATIONS) {
            if (player1.containsAll(combination)) {
                return 1 // Player 1 wins
            }
            if (player2.containsAll(combination)) {
                return 2 // Player 2 wins
            }
        }

        if (player1.size + player2.size == 9) {
            return 0 // It's a draw
        }

        // No winner yet
        return -1
    }

    private fun updateScore(winner: Int) {
        if (winner == 1) {
            player1Count++
            textView1.text = "Jugador 1: $player1Count"
        } else if (winner == 2) {
            player2Count++
            textView2.text = "Teléfono: $player2Count"
        }
    }

    private fun showGameOverDialog(winner: Int) {
        val message = when (winner) {
            1 -> "Has ganado el Juego!"
            2 -> "Teléfono ha ganado el juego!"
            else -> "Es un empate!"
        }

        if (winner == 1)
            soundManager.playWinSound()
        else if (winner == 2)
            soundManager.playLooseSound()

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Fin del Juego")
        builder.setMessage(message)

        builder.setPositiveButton("Reiniciar") { _, _ ->
            resetGame()
        }

        builder.setNegativeButton("Salir") { _, _ ->
            // Handle quitting the game or other actions here
            // You can exit the app or navigate to a different screen
            exitProcess(1)
        }

        // Update the score when there is a real winner
        if (winner != 0) {
            updateScore(winner)
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun resetGame() {
        player1 = ArrayList<Int>()
        player2 = ArrayList<Int>()
        (activity as MainActivity).switchToFragmentC(player1Count,player2Count,player1,player2)
    }

    override fun onPause() {
        super.onPause()
        soundManager.releaseMediaPlayer()
    }

    fun restoreGamePlayer1(movePlayer1List:ArrayList<Int>){
        val imageButton : ImageButton
        for (i in movePlayer1List) {
            val imageButton = buttons[i - 1]
            imageButton.setImageResource(R.drawable.checkbox_cross_orange_icon)
            imageButton.isEnabled = false
        }
    }

    fun restoreGamePlayer2(movePlayer2List:ArrayList<Int>){
        val imageButton : ImageButton
        for (i in movePlayer2List) {
            val imageButton = buttons[i - 1]
            imageButton.setImageResource(R.drawable.starfilledminor_svgrepo_com)
            imageButton.isEnabled = false
        }
    }

}

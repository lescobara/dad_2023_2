package com.unal.reto4

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.system.exitProcess

class HardFragment : Fragment() {
    lateinit var buttons: List<Button>
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var resetButton: Button

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hard, container, false)

        // Initialize UI components
        buttons = listOf(
            view.findViewById(R.id.button),
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
        resetButton = view.findViewById(R.id.button10)

        // Set click listeners for buttons
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (!gameOver && playerTurn && button.text.isEmpty()) {
                    playerMove(index + 1)
                    checkWinner()
                    if (!gameOver) {
                        Handler().postDelayed({ robotMove() }, 600)
                    }
                }
            }
        }

        // Set click listener for reset button
        resetButton.setOnClickListener {
            resetGame()
        }

        return view
    }

    private fun playerMove(cellID: Int) {
        if (!gameOver && playerTurn) {
            val button = buttons[cellID - 1]
            if (button.text.isEmpty()) {
                button.text = "X"
                button.setTextColor(Color.parseColor("#d90429"))
                player1.add(cellID)
                button.isEnabled = false // Disable the button after the move
                playerTurn = false // Switch to Player 2's turn
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
                val button = buttons[bestMove - 1]
                if (button.text.isEmpty()) {
                    button.text = "O"
                    button.setTextColor(Color.parseColor("#2b2d42"))
                    player2.add(bestMove)
                    button.isEnabled = false // Disable the button after the move
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
            textView1.text = "Player 1: $player1Count"
        } else if (winner == 2) {
            player2Count++
            textView2.text = "Player 2: $player2Count"
        }
    }

    private fun showGameOverDialog(winner: Int) {
        val message = when (winner) {
            1 -> "Has ganado el Juego!"
            2 -> "Teléfono ha ganado el juego!"
            else -> "Es un empate!"
        }

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
        player1.clear()
        player2.clear()
        gameOver = false
        playerTurn = true
        buttons.forEach {
            it.text = ""
            it.isEnabled = true // Re-enable all buttons
        }
    }

}

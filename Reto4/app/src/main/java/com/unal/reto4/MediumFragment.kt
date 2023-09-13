package com.unal.reto4

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlin.system.exitProcess

class MediumFragment : Fragment() {
    lateinit var button : Button
    lateinit var button2 : Button
    lateinit var button3 : Button
    lateinit var button4 : Button
    lateinit var button5 : Button
    lateinit var button6 : Button
    lateinit var button7 : Button
    lateinit var button8 : Button
    lateinit var button9 : Button
    lateinit var botonRst : Button

    lateinit var textView : TextView
    lateinit var textView2 : TextView


    // Contador de jugadores
    var player1Count = 0
    var player2Count = 0

    var playerTurn = true

    //variables varias
    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()
    var emptyCells = ArrayList<Int>()
    var activeUser = 1

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_medium,container,false)
        button=view.findViewById<Button>(R.id.button)
        button2=view.findViewById<Button>(R.id.button2)
        button3=view.findViewById<Button>(R.id.button3)
        button4=view.findViewById<Button>(R.id.button4)
        button5=view.findViewById<Button>(R.id.button5)
        button6=view.findViewById<Button>(R.id.button6)
        button7=view.findViewById<Button>(R.id.button7)
        button8=view.findViewById<Button>(R.id.button8)
        button9=view.findViewById<Button>(R.id.button9)
        botonRst=view.findViewById<Button>(R.id.button10)
        textView=view.findViewById<Button>(R.id.textView1)
        textView2=view.findViewById<Button>(R.id.textView2)

        button.setOnClickListener {
            clickfun(it)
        }
        button2.setOnClickListener {
            clickfun(it)
        }
        button3.setOnClickListener {
            clickfun(it)
        }
        button4.setOnClickListener {
            clickfun(it)
        }
        button5.setOnClickListener {
            clickfun(it)
        }
        button6.setOnClickListener {
            clickfun(it)
        }
        button7.setOnClickListener {
            clickfun(it)
        }
        button8.setOnClickListener {
            clickfun(it)
        }
        button9.setOnClickListener {
            clickfun(it)
        }

        botonRst.setOnClickListener {
            reset()
        }
        return view
    }

    fun clickfun(view: View)
    {
        if(playerTurn) {
            val but = view as Button
            var cellID = 0
            when (but.id) {
                R.id.button -> cellID = 1
                R.id.button2 -> cellID = 2
                R.id.button3 -> cellID = 3
                R.id.button4 -> cellID = 4
                R.id.button5 -> cellID = 5
                R.id.button6 -> cellID = 6
                R.id.button7 -> cellID = 7
                R.id.button8 -> cellID = 8
                R.id.button9 -> cellID = 9
            }
            playerTurn = false;
            Handler().postDelayed(Runnable { playerTurn = true } , 600)
            playnow(but, cellID)
        }
    }

    // Función que actualiza el tablero luego de cada movimiento
    fun playnow(buttonSelected:Button , currCell:Int)
    {
        if(activeUser == 1)
        {
            buttonSelected.text = "X"
            buttonSelected.setTextColor(Color.parseColor("#d90429"))
            player1.add(currCell)
            emptyCells.add(currCell)
            buttonSelected.isEnabled = false
            val checkWinner = checkwinner()
            if(checkWinner == 1){
                Handler().postDelayed(Runnable { reset() } , 2000)
            }
            else
                Handler().postDelayed(Runnable { robot() } , 500)
        }
        else
        {
            buttonSelected.text = "O"
            buttonSelected.setTextColor(Color.parseColor("#2b2d42"))
            activeUser = 1
            player2.add(currCell)
            emptyCells.add(currCell)
            buttonSelected.isEnabled = false
            val checkWinner  = checkwinner()
            if(checkWinner == 1)
                Handler().postDelayed(Runnable { reset() } , 4000)
        }
    }

    //Fucnión que revisa si hay un ganador
    fun checkwinner(): Int {
        if ((player1.contains(1) && player1.contains(2) && player1.contains(3)) || (player1.contains(
                1
            ) && player1.contains(4) && player1.contains(7)) ||
            (player1.contains(3) && player1.contains(6) && player1.contains(9)) || (player1.contains(
                7
            ) && player1.contains(8) && player1.contains(9)) ||
            (player1.contains(4) && player1.contains(5) && player1.contains(6)) || (player1.contains(
                1
            ) && player1.contains(5) && player1.contains(9)) ||
            player1.contains(3) && player1.contains(5) && player1.contains(7) || (player1.contains(2) && player1.contains(
                5
            ) && player1.contains(8))
        ) {
            player1Count += 1
            buttonDisable()
            disableReset()
            val build = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            build.setTitle("Ganador!")
            build.setMessage("Has ganado el juego.."+"\n\n"+"Deseas continuar jugando?")
            build.setPositiveButton("Si") { dialog, which ->
                reset()
            }
            build.setNegativeButton("No") { dialog, which ->
                exitProcess(1)

            }
            Handler().postDelayed(Runnable { build.show() }, 2000)
            return 1


        } else if ((player2.contains(1) && player2.contains(2) && player2.contains(3)) || (player2.contains(
                1
            ) && player2.contains(4) && player2.contains(7)) ||
            (player2.contains(3) && player2.contains(6) && player2.contains(9)) || (player2.contains(
                7
            ) && player2.contains(8) && player2.contains(9)) ||
            (player2.contains(4) && player2.contains(5) && player2.contains(6)) || (player2.contains(
                1
            ) && player2.contains(5) && player2.contains(9)) ||
            player2.contains(3) && player2.contains(5) && player2.contains(7) || (player2.contains(2) && player2.contains(
                5
            ) && player2.contains(8))
        ) {
            player2Count += 1
            buttonDisable()
            disableReset()
            val build = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            build.setTitle("Perdiste!")
            build.setMessage("El teléfono ha ganado!"+"\n\n"+"Deseas continuar jugando?")
            build.setPositiveButton("Si") { dialog, which ->
                reset()
            }
            build.setNegativeButton("No") { dialog, which ->
                exitProcess(1)
            }
            Handler().postDelayed(Runnable { build.show() }, 2000)
            return 1
        } else if (emptyCells.contains(1) && emptyCells.contains(2) && emptyCells.contains(3) && emptyCells.contains(
                4
            ) && emptyCells.contains(5) && emptyCells.contains(6) && emptyCells.contains(7) &&
            emptyCells.contains(8) && emptyCells.contains(9)
        ) {

            val build = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            build.setTitle("Empate!")
            build.setMessage("Nadie ha ganado" + "\n\n" + "Deseas jugar de nuevo?")
            build.setPositiveButton("Si") { dialog, which ->
                reset()
            }
            build.setNegativeButton("No") { dialog, which ->
                exitProcess(1)
            }
            build.show()
            return 1

        }
        return 0
    }

    // this function resets the game.
    fun reset()
    {
        player1.clear()
        player2.clear()
        emptyCells.clear()
        activeUser = 1;
        for(i in 1..9)
        {
            var buttonselected : Button?
            buttonselected = when(i){
                1 -> button
                2 -> button2
                3 -> button3
                4 -> button4
                5 -> button5
                6 -> button6
                7 -> button7
                8 -> button8
                9 -> button9
                else -> {button}
            }
            buttonselected.isEnabled = true
            buttonselected.text = ""
            textView.text = "Jugador1 : $player1Count"
            textView2.text = "Teléfono : $player2Count"
        }
    }

    fun robot() {
        // Check for winning moves
        for (cell in 1..9) {
            if (!emptyCells.contains(cell)) {
                // Try placing "O" in this cell to check for a win
                if (isWinningMove(player2, cell)) {
                    makeMove(cell)
                    return
                }
            }
        }

        // Check for blocking opponent's winning moves
        for (cell in 1..9) {
            if (!emptyCells.contains(cell)) {
                // Try placing "O" in this cell to block opponent's win
                if (isWinningMove(player1, cell)) {
                    makeMove(cell)
                    return
                }
            }
        }

        // If no winning or blocking moves, make a random move
        var rnd: Int
        do {
            rnd = (1..9).random()
        } while (emptyCells.contains(rnd))

        makeMove(rnd)
    }

    // Helper function to check for a winning move
    fun isWinningMove(playerMoves: List<Int>, cell: Int): Boolean {
        val tempPlayerMoves = ArrayList(playerMoves)
        tempPlayerMoves.add(cell)
        for (winningCombination in WINNING_COMBINATIONS) {
            if (tempPlayerMoves.containsAll(winningCombination)) {
                return true
            }
        }
        return false
    }

    // Helper function to make a move
    fun makeMove(cell: Int) {
        val buttonSelected: Button? = when (cell) {
            1 -> button
            2 -> button2
            3 -> button3
            4 -> button4
            5 -> button5
            6 -> button6
            7 -> button7
            8 -> button8
            9 -> button9
            else -> null
        }

        buttonSelected?.let {
            emptyCells.add(cell)
            it.text = "O"
            it.setTextColor(Color.parseColor("#2b2d42"))
            player2.add(cell)
            it.isEnabled = false
            val checkWinner = checkwinner()
            if (checkWinner == 1) {
                Handler().postDelayed({ reset() }, 2000)
            }
        }
    }


    fun buttonDisable() {
        for (i in 1..9) {
            val buttonSelected = when (i) {
                1 -> button
                2 -> button2
                3 -> button3
                4 -> button4
                5 -> button5
                6 -> button6
                7 -> button7
                8 -> button8
                9 -> button9
                else -> {
                    button
                }

            }
            if (buttonSelected.isEnabled == true)
                buttonSelected.isEnabled = false
        }
    }

    fun disableReset() {
        botonRst.isEnabled = false
        Handler().postDelayed(Runnable { botonRst.isEnabled = true }, 2200)
    }


}
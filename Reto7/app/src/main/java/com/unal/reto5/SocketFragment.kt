package com.unal.reto5

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SocketFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SocketFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var requestApi: ApiManager

    lateinit var buttons: List<ImageButton>
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var textView3: TextView
    lateinit var newButton: Button
    lateinit var listButton: Button
    lateinit var resetButton: Button


    private var juegoSeleccionado: String? = null
    val mSocket = SocketManager.getSocket()

    var player1Count = 0
    var player2Count = 0

    var gameOver = false
    var conexionId : String? = null
    var player1id : String? = null
    var player2id : String? = null

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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SocketFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            SocketFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_socket, container, false)
        requestApi=ApiManager(requireContext())

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
        textView3 = view.findViewById(R.id.textView3)

        realizarSolicitudHttp()

        // Set player scores in TextViews
        textView1.text = "Jugador1: $player1Count"
        textView2.text = "Jugador2: $player2Count"
        newButton = view.findViewById(R.id.button11)
        listButton = view.findViewById(R.id.button12)
        resetButton = view.findViewById(R.id.button10)


        // Initially set up the click listeners
        setupImageButtonClickListeners()

        resetButton.setOnClickListener {
            resetGame()
        }

        newButton.setOnClickListener {
            crearJuegoNuevo()
        }

        listButton.setOnClickListener {
            realizarSolicitudHttp()
        }

        return view
    }

    private fun setupImageButtonClickListeners() {
        buttons.forEachIndexed { index, imageButton ->
            imageButton.setOnClickListener {
                if (isImageButtonEmpty(imageButton) && !gameOver) {
                    mSocket.emit("movimiento", juegoSeleccionado, conexionId, index)
                }
            }
        }

        mSocket.on("actualizar-tablero") { data ->
            if (data.isNotEmpty()) {
                val temp = data.joinToString("").replace("[", "").replace("]", "").replace("\"", "")
                val boardList = listOf(*temp.split(",").toTypedArray())
                activity?.runOnUiThread {
                    updateBoard(boardList)
                }
            }
        }
    }
    private fun updateBoard(boardList: List<String>) {
        for (i in boardList.indices) {
            val element = boardList[i]
            val imageButton = buttons[i]
            when (element) {
                player1id -> {
                    activity?.runOnUiThread {
                        imageButton.setImageResource(R.drawable.checkbox_cross_orange_icon)
                        imageButton.isEnabled = false
                    }
                    player1.add(i+1)
                }
                player2id -> {
                    activity?.runOnUiThread {
                        imageButton.setImageResource(R.drawable.starfilledminor_svgrepo_com)
                        imageButton.isEnabled = false
                    }
                    player2.add(i+1)
                }
                // Maneja otras jugadas, como casillas vacías, si es necesario
                else -> {
                    // Por ejemplo, puedes hacer algo aquí para casillas vacías
                }
            }
        }
        activity?.runOnUiThread {
            val salida=checkWinner()
            if (salida != -1){
                mostrarDialogoResultado(salida)
            }
        }
    }

    private fun isImageButtonEmpty(imageButton: ImageButton): Boolean {
        return imageButton.drawable == null
    }

    private fun checkWinner(): Int {
        val jugadas1=player1.distinct()
        val jugadas2=player2.distinct()
        for (combination in WINNING_COMBINATIONS) {
            if (jugadas1.containsAll(combination)) {
                return 1 // Player 1 wins
                gameOver=true
            }
            if (jugadas2.containsAll(combination)) {
                return 2 // Player 2 wins
                gameOver=true
            }
        }

        if (jugadas1.size + jugadas2.size == 9) {
            return 0 // It's a draw
        }

        // No winner yet
        return -1
    }

    private fun mostrarDialogoResultado(resultado : Int) {

        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setTitle("Final!")

        if (resultado == 1) {
            dialogBuilder.setTitle("Jugador 1 ha ganado")
        }
        else if (resultado == 2){
            dialogBuilder.setTitle("Jugador 2 ha ganado")
        }
        else if (resultado == 0) {
            dialogBuilder.setTitle("Empate")
        }

        dialogBuilder.setPositiveButton("Aceptar") { _, _ ->
            // Realiza alguna acción al presionar el botón Aceptar
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }


    private fun realizarSolicitudHttp(){
        val url="http://10.0.2.2:3000/juegos"
        ApiManager(requireContext())
        requestApi.obtenerJuegosDisponibles(url,
            onSuccess = { response ->
                //textView3.text = "juego : $response"
                val gson = Gson()
                val juegosDisponiblesList: List<String> = gson.fromJson(response, object : TypeToken<List<String>>() {}.type)
                mostrarDialogoDeJuegos(juegosDisponiblesList)
            },
            onError ={ error ->
                Log.e("ERROR DE SOLICITUD","Error:$error")
            })
    }

    private fun mostrarDialogoDeJuegos(juegosDisponiblesList: List<String>) {
        // Crea un di�logo con una lista de juegos disponibles
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Selecciona un juego")

        // Convierte la lista de juegos disponibles en un arreglo de CharSequence
        val juegosArray = juegosDisponiblesList.toTypedArray()

        builder.setItems(juegosArray) { _, item ->
            // Maneja la selecci�n del juego aqu�
            juegoSeleccionado = juegosArray[item]

            // Realiza la acci�n deseada con el juego seleccionado, por ejemplo, inicia el juego
            // Aseg�rate de que juegoSeleccionado sea accesible en el �mbito correcto (puede ser una variable de clase en el fragmento
            mSocket.emit("unirse_juego",juegoSeleccionado)
            mSocket.on("juego-unido"){data->
                if (data.isNotEmpty()) {
                    textView3.text="juego: $juegoSeleccionado"
                    activity?.runOnUiThread {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Jugador Conectado")
                        builder.setMessage("¡Se ha unido al juego!")
                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        val dialog = builder.create()
                        dialog.show()
                    }
                }
            }
            mSocket.on("jugador-id"){data->
                if (data.isNotEmpty()){
                    player1id = data.joinToString ("")
                    textView3.text="juego: $juegoSeleccionado"
                    Log.d("JUGADORRRR 1", player1id.toString())
                }
            }
            mSocket.on("otro-jugador-id"){data->
                if (data.isNotEmpty()){
                    player2id = data.joinToString ("")
                    Log.d("JUGADORRRR 2", player2id.toString())
                }
            }

            mSocket.on("conexion-id"){data->
                if (data.isNotEmpty()){
                    conexionId = data.joinToString ("")
                    Log.d("CONEXION IDDDDD", conexionId.toString())
                }
            }
        }
        builder.create().show()
    }

    private fun crearJuegoNuevo(){
        mSocket.emit("nuevo-juego")
        mSocket.on("juego-nuevo"){data->
            if (data.isNotEmpty()){
                val temp=data.joinToString ("")
                activity?.runOnUiThread {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Juego nuevo")
                    builder.setMessage("¡Se creado el juego "+temp+" !")
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
    }

    private fun resetGame() {
        (activity as MainActivity).switchToFragmentD()
    }
}
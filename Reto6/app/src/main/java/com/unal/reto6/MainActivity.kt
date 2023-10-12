package com.unal.reto6

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.add
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity(){

    private var soundManager: SoundManager? = null
    private var nivel = ""
    private var puntaje1=0
    private var puntaje2=0
    var player1move = ArrayList<Int>()
    var player2move = ArrayList<Int>()

    //@SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nivel=savedInstanceState?.getString("nivel").toString()
        // Initialize SoundManager
        soundManager = SoundManager(this)

        setSupportActionBar(findViewById(R.id.toolbar))

        if (savedInstanceState != null && nivel =="easy") {
            switchToFragmentA(
                savedInstanceState.getInt("puntaje1"),
                savedInstanceState.getInt("puntaje2"),
                savedInstanceState.getIntegerArrayList("jugadas1") ?: ArrayList(),
                savedInstanceState.getIntegerArrayList("jugadas2") ?: ArrayList()

            )
        }else if (savedInstanceState != null && nivel =="medium") {
            switchToFragmentB(
                savedInstanceState.getInt("puntaje1"),
                savedInstanceState.getInt("puntaje2"),
                savedInstanceState.getIntegerArrayList("jugadas1") ?: ArrayList(),
                savedInstanceState.getIntegerArrayList("jugadas2") ?: ArrayList()
            )
        }else if (savedInstanceState != null && nivel =="hard"){
            switchToFragmentC(
                savedInstanceState.getInt("puntaje1"),
                savedInstanceState.getInt("puntaje2"),
                savedInstanceState.getIntegerArrayList("jugadas1") ?: ArrayList(),
                savedInstanceState.getIntegerArrayList("jugadas2") ?: ArrayList()
            )
        }else{
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<EasyFragment>(R.id.fragmentContainer)
                //add(R.id.fragmentContainer, EasyFragment.newInstance(isLandscape))
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (currentFragment is EasyFragment){
            nivel="easy"
            puntaje1 = currentFragment.player1Count
            puntaje2 = currentFragment.player2Count
            player1move = currentFragment.player1
            player2move = currentFragment.player2
        }else if(currentFragment is MediumFragment){
            nivel="medium"
            puntaje1 = currentFragment.player1Count
            puntaje2 = currentFragment.player2Count
            player1move = currentFragment.player1
            player2move = currentFragment.player2
        }else if (currentFragment is HardFragment){
            nivel="hard"
            puntaje1 = currentFragment.player1Count
            puntaje2 = currentFragment.player2Count
            player1move = currentFragment.player1
            player2move = currentFragment.player2
        }
        // Save important data to the Bundle
        outState.putInt("puntaje1", puntaje1)
        outState.putInt("puntaje2", puntaje2)
        outState.putString("nivel",nivel)
        outState.putIntegerArrayList("jugadas1",player1move)
        outState.putIntegerArrayList("jugadas2",player2move)
        // Add more data as needed
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_app, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.salir ->{
                val salir = Dialogos.newInstance(Dialogos.DIALOG_TYPE_ONE)
                salir.show(supportFragmentManager,"Salir")
                true
            }
            R.id.new_game->{
                val nuevo = Dialogos.newInstance(Dialogos.DIALOG_TYPE_DEFAULT)
                nuevo.show(supportFragmentManager,"Nuevo juego?")
                true
            }
            R.id.nivel->{
                val nivel = Dialogos.newInstance(Dialogos.DIALOG_TYPE_TWO)
                nivel.show(supportFragmentManager,"Nivel")
                true
            }
            /*R.id.volume -> {
                // Handle "Sonido" item
                soundManager?.toggleSound() // Toggle the sound state
                if (soundManager?.isSoundEnabled() == true) {
                    // Sound is currently enabled, so mute it
                    item.setIcon(R.drawable.volume_up_24) // Change the icon to the muted state
                } else {
                    // Sound is currently muted, so enable it
                    item.setIcon(R.drawable.volume_off_24) // Change the icon to the enabled state
                }
                true
            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Function to switch to Fragment Medium
    fun switchToFragmentB(player1Count: Int, player2Count: Int,player1Move: ArrayList<Int>,player2Move: ArrayList<Int>) {
        nivel = "medium"
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment = MediumFragment.newInstance(player1Count, player2Count,player1Move,player2Move)
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        //fragmentTransaction.replace(R.id.fragmentContainer, MediumFragment())
        fragmentTransaction.addToBackStack(null) // Add transaction to back stack
        fragmentTransaction.commit()
    }

    // Function to switch to Fragment Easy
    fun switchToFragmentA(player1Count: Int, player2Count: Int, player1Move: ArrayList<Int>,player2Move: ArrayList<Int>) {
        nivel = "easy"
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        // Pass the parameters to the HardFragment using newInstance
        val fragment = EasyFragment.newInstance(player1Count, player2Count, player1Move,player2Move)
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        //fragmentTransaction.replace(R.id.fragmentContainer, EasyFragment())
        fragmentTransaction.addToBackStack(null) // Add transaction to back stack
        fragmentTransaction.commit()
    }

    // Function to switch to Fragment Hard, this fragment requires players scores as an arguments
    fun switchToFragmentC(player1Count: Int, player2Count: Int,player1Move: ArrayList<Int>,player2Move: ArrayList<Int>) {
        nivel = "hard"
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        // Pass the parameters to the HardFragment using newInstance
        val fragment = HardFragment.newInstance(player1Count, player2Count,player1Move,player2Move)
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null) // Add transaction to back stack
        fragmentTransaction.commit()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        //val fragmentTransaction = supportFragmentManager.beginTransaction()
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        //val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (isLandscape) {
            if (nivel == "easy"){
                switchToFragmentA(puntaje1,puntaje2,player1move,player2move)
                /*fragmentTransaction.replace(R.id.fragmentContainer, EasyFragment())
                fragmentTransaction.addToBackStack(null) // Add transaction to back stack
                fragmentTransaction.commit()*/
            }else if (nivel == "medium"){
                switchToFragmentB(puntaje1,puntaje2,player1move,player2move)
                /*fragmentTransaction.replace(R.id.fragmentContainer, MediumFragment())
                fragmentTransaction.addToBackStack(null) // Add transaction to back stack
                fragmentTransaction.commit()*/
            }else if (nivel == "hard"){
                switchToFragmentC(puntaje1,puntaje2,player1move,player2move)
                /*fragmentTransaction.replace(R.id.fragmentContainer, HardFragment())
                fragmentTransaction.addToBackStack(null) // Add transaction to back stack
                fragmentTransaction.commit()*/
            }

        }else{
            if (nivel == "easy"){
                switchToFragmentA(puntaje1,puntaje2,player1move,player2move)
                /*fragmentTransaction.replace(R.id.fragmentContainer, EasyFragment())
                fragmentTransaction.addToBackStack(null) // Add transaction to back stack
                fragmentTransaction.commit()*/
            }else if (nivel == "medium"){
                switchToFragmentB(puntaje1,puntaje2,player1move,player2move)
                /*fragmentTransaction.replace(R.id.fragmentContainer, MediumFragment())
                fragmentTransaction.addToBackStack(null) // Add transaction to back stack
                fragmentTransaction.commit()*/
            }else if (nivel == "hard"){
                switchToFragmentC(puntaje1,puntaje2,player1move,player2move)
                /*fragmentTransaction.replace(R.id.fragmentContainer, HardFragment())
                fragmentTransaction.addToBackStack(null) // Add transaction to back stack
                fragmentTransaction.commit()*/
            }
        }
    }

}
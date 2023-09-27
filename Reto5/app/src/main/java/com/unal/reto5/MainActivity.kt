package com.unal.reto5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.add
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity(){

    private var soundManager: SoundManager? = null

    //@SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SoundManager
        soundManager = SoundManager(this)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<EasyFragment>(R.id.fragmentContainer)
        }
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
    fun switchToFragmentB() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, MediumFragment())
        fragmentTransaction.addToBackStack(null) // Add transaction to back stack
        fragmentTransaction.commit()
    }

    // Function to switch to Fragment Easy
    fun switchToFragmentA() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, EasyFragment())
        fragmentTransaction.addToBackStack(null) // Add transaction to back stack
        fragmentTransaction.commit()
    }

    // Function to switch to Fragment Hard, this fragment requires players scores as a arguments
    fun switchToFragmentC(player1Count: Int, player2Count: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        // Pass the parameters to the HardFragment using newInstance
        val fragment = HardFragment.newInstance(player1Count, player2Count)
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null) // Add transaction to back stack
        fragmentTransaction.commit()
    }

}
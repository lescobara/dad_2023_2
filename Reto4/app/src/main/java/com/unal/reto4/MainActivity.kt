package com.unal.reto4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.add
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity(){

    //@SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    // Function to switch to Fragment Hard
    fun switchToFragmentC() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, HardFragment())
        fragmentTransaction.addToBackStack(null) // Add transaction to back stack
        fragmentTransaction.commit()
    }

}
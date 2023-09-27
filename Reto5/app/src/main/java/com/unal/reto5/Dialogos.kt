package com.unal.reto5

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import kotlin.system.exitProcess


class Dialogos : DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogType = arguments?.getInt(ARG_DIALOG_TYPE) ?: DIALOG_TYPE_DEFAULT

        return when (dialogType) {
            DIALOG_TYPE_ONE -> createDialogOne()
            DIALOG_TYPE_TWO -> createDialogTwo()
            else -> createDefaultDialog()
        }
    }

    private fun createDialogOne(): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogo_salida_layout, null)

        builder.setView(dialogView)
            .setPositiveButton("Si") { _, _ ->
                // Handle positive button click
                exitProcess(1)
            }
            .setNegativeButton("No") { _, _ ->
                // Handle negative button click or dismiss
                dialog?.dismiss()
            }

        return builder.create()
    }

    private fun createDialogTwo(): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogo_nivel_layout, null)

        builder.setView(dialogView)

        dialogView.findViewById<Button>(R.id.option1_button).setOnClickListener {
            (activity as MainActivity).switchToFragmentA()
            dialog?.dismiss()
        }

        dialogView.findViewById<Button>(R.id.option2_button).setOnClickListener {
            (activity as MainActivity).switchToFragmentB()
            dialog?.dismiss()
        }

        dialogView.findViewById<Button>(R.id.option3_button).setOnClickListener {
            (activity as MainActivity).switchToFragmentC(0,0)
            dialog?.dismiss()
        }

        return builder.create()
    }

    private fun createDefaultDialog(): Dialog {
        // Create and return a default dialog
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Reiniciar juego")
        builder.setMessage("Desea reiniciar el juego?")
        builder.setPositiveButton("Si") { dialog, _ ->
            (activity as MainActivity).switchToFragmentA()
            dialog.dismiss()
        }
        builder.setNegativeButton("No"){ dialog, _ ->
            dialog.dismiss()
        }
        return builder.create()
    }

    companion object {
        const val ARG_DIALOG_TYPE = "dialog_type"
        const val DIALOG_TYPE_DEFAULT = 0
        const val DIALOG_TYPE_ONE = 1
        const val DIALOG_TYPE_TWO = 2

        // Factory method to create an instance of the fragment with a specified dialog type
        fun newInstance(dialogType: Int): Dialogos {
            val fragment = Dialogos()
            val args = Bundle()
            args.putInt(ARG_DIALOG_TYPE, dialogType)
            fragment.arguments = args
            return fragment
        }
    }
}
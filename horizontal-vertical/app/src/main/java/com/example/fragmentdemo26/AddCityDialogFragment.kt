package com.example.fragmentdemo26

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * Диалог ввода названия города для добавления в список.
 */
class AddCityDialogFragment : DialogFragment() {

    var onCityAdded: ((String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_city, null)
        val input = view.findViewById<EditText>(R.id.dialog_city_input)

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_city)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                input.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let { name ->
                    onCityAdded?.invoke(name)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }
}

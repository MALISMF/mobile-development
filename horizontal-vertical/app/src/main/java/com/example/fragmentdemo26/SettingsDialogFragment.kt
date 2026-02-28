package com.example.fragmentdemo26

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * Диалог настроек: язык, вид (краткий/детальный), город.
 * По нажатию OK сохраняет выбор в AppSettings и уведомляет Activity.
 */
class SettingsDialogFragment : DialogFragment() {

    interface Listener {
        fun onSettingsApplied()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        AppSettings.init(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_settings, null)
        val radioLanguage = view.findViewById<RadioGroup>(R.id.radio_language)
        val radioDesign = view.findViewById<RadioGroup>(R.id.radio_design)
        val radioCity = view.findViewById<RadioGroup>(R.id.radio_city)

        radioLanguage.check(
            when (AppSettings.languageIndex) {
                0 -> R.id.lang_ru
                else -> R.id.lang_en
            }
        )
        radioDesign.check(
            when (AppSettings.viewTypeIndex) {
                0 -> R.id.design_brief
                else -> R.id.design_detail
            }
        )
        radioCity.check(
            when (AppSettings.cityIndex) {
                0 -> R.id.city_moscow
                1 -> R.id.city_london
                else -> R.id.city_berlin
            }
        )

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_title)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                AppSettings.languageIndex = when (radioLanguage.checkedRadioButtonId) {
                    R.id.lang_en -> 1
                    else -> 0
                }
                AppSettings.viewTypeIndex = when (radioDesign.checkedRadioButtonId) {
                    R.id.design_detail -> 1
                    else -> 0
                }
                AppSettings.cityIndex = when (radioCity.checkedRadioButtonId) {
                    R.id.city_london -> 1
                    R.id.city_berlin -> 2
                    else -> 0
                }
                dismiss()
                (activity as? Listener)?.onSettingsApplied()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }
}

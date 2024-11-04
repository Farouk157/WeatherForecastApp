package com.example.weatherforecast.settingsfragment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.weatherforecast.R
import com.example.weatherforecast.SharedPreferencesManager
import java.util.Locale

class SettingsFragment : Fragment() {

    private val languages = arrayOf("English", "Arabic")
    private val units = arrayOf("Metric [Meter/s] [°C]", "US [Mile/Hour] [°K]")
    private val notification = arrayOf("ON", "OFF")

    private val ar_lan = arrayOf("الانجليزية","العربية")
    private val ar_unit = arrayOf("[متر/ثانية] [°س]", "[ميل/ساعة] [°ك]")
    private val ar_notification = arrayOf("تشغيل", "ايقاف")

    private lateinit var selectedLanguage: String

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        selectedLanguage = sharedPreferencesManager.getSelectedLanguage().let { language ->
            if (language == "Arabic") "ar" else "en"
        }

        if(selectedLanguage == "en"){
            val spinnerLanguage = view.findViewById<Spinner>(R.id.spinner_language)
            // Language spinner setup
            val languageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
            languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLanguage.adapter = languageAdapter
            spinnerLanguage.setSelection(languages.indexOf(sharedPreferencesManager.getSelectedLanguage()))
            // Language selection listener
            spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedLanguage = languages[position]
                    sharedPreferencesManager.setSelectedLanguage(selectedLanguage)
                    applyLanguage(selectedLanguage)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }else{
            val spinnerLanguage = view.findViewById<Spinner>(R.id.spinner_language)
            // Language spinner setup
            val languageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ar_lan)
            languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLanguage.adapter = languageAdapter
            spinnerLanguage.setSelection(languages.indexOf(sharedPreferencesManager.getSelectedLanguage()))
            // Language selection listener
            spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedLanguage = languages[position]
                    sharedPreferencesManager.setSelectedLanguage(selectedLanguage)
                    applyLanguage(selectedLanguage)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        if(selectedLanguage == "en"){
            val spinnerUnit = view.findViewById<Spinner>(R.id.spinner_unit)
            // Unit spinner setup
            val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUnit.adapter = unitAdapter
            // Set initial selections from shared preferences
            spinnerUnit.setSelection(units.indexOf(sharedPreferencesManager.getSelectedUnit()))
            // Unit selection listener
            spinnerUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedUnit = units[position]
                    sharedPreferencesManager.setSelectedUnit(selectedUnit)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }else{
            val spinnerUnit = view.findViewById<Spinner>(R.id.spinner_unit)
            // Unit spinner setup
            val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ar_unit)
            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUnit.adapter = unitAdapter
            // Set initial selections from shared preferences
            spinnerUnit.setSelection(units.indexOf(sharedPreferencesManager.getSelectedUnit()))
            // Unit selection listener
            spinnerUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedUnit = units[position]
                    sharedPreferencesManager.setSelectedUnit(selectedUnit)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }




        if(selectedLanguage=="en"){
            val spinnerNotif = view.findViewById<Spinner>(R.id.spinner_notification)
            // Notification spinner setup
            val notificationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, notification)
            notificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerNotif.adapter = notificationAdapter
            spinnerNotif.setSelection(notification.indexOf(sharedPreferencesManager.getNotificationState()))
            // Notification state selection listener
            spinnerNotif.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedNotificationState = notification[position]
                    sharedPreferencesManager.setNotificationState(selectedNotificationState)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }else{
            val spinnerNotif = view.findViewById<Spinner>(R.id.spinner_notification)
            // Notification spinner setup
            val notificationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ar_notification)
            notificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerNotif.adapter = notificationAdapter
            spinnerNotif.setSelection(notification.indexOf(sharedPreferencesManager.getNotificationState()))
            // Notification state selection listener
            spinnerNotif.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedNotificationState = notification[position]
                    sharedPreferencesManager.setNotificationState(selectedNotificationState)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        return view
    }

    private fun applyLanguage(language: String) {
        val locale = when (language) {
            "Arabic" -> Locale("ar")
            else -> Locale("en")
        }
        Locale.setDefault(locale)
        val config = requireActivity().resources.configuration
        config.setLocale(locale)
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)
    }


}

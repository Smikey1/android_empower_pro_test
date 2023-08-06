package com.hdd.empowerpro.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.hdd.empowerpro.R

class CustomSpinnerAdapter(context: Context, private val resource: Int, private val items: HashMap<String,String>) :
    ArrayAdapter<String>(context, resource, items.keys.toList()) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val tvCountryCodeDropdown: TextView = view.findViewById(R.id.tvCountryCodeDropdown)

        // Get the selected country code and corresponding country name from the HashMap
        val countryCode = getItem(position)
        val countryName = items[countryCode]

        // Display the full details (e.g., "+61 (Australia)") in the dropdown
        tvCountryCodeDropdown.text = "$countryCode ($countryName)"
        return view
    }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
            val tvCountryCodeDropdown: TextView = view.findViewById(R.id.tvCountryCodeDropdown)

            // Get the selected country code and corresponding country name from the HashMap
            val countryCode = getItem(position)
            val countryName = items[countryCode]

            // Display the full details (e.g., "+61 (Australia)") in the dropdown
            tvCountryCodeDropdown.text = "$countryCode ($countryName)"

            return view
    }


}

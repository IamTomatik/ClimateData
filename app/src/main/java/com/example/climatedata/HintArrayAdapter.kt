package com.example.climatedata.fragment_createLocation

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.climatedata.R

class HintArrayAdapter(context: Context, items: List<String>) :
    ArrayAdapter<String>(context, R.layout.spinner_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.setTextColor(
            if (position == 0) ContextCompat.getColor(context, R.color.gray2)
            else ContextCompat.getColor(context, R.color.black)
        )
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.setTextColor(
            if (position == 0) ContextCompat.getColor(context, R.color.gray2)
            else ContextCompat.getColor(context, R.color.black)
        )
        return view
    }

    override fun isEnabled(position: Int) = position != 0
}

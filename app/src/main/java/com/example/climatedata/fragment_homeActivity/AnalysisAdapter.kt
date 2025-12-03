package com.example.climatedata.fragment_homeActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.climatedata.data.models.Planting
import com.example.climatedata.R
import java.util.Date

class AnalysisAdapter(
    private var plants: MutableList<Planting>,
    private val getCropName: (Int) -> String,      // функция для получения имени культуры
    private val onClick: (Planting) -> Unit
) : RecyclerView.Adapter<AnalysisAdapter.PlantViewHolder>() {

    inner class PlantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val plantCard: LinearLayout = view.findViewById(R.id.plant)
        val plantName: TextView = view.findViewById(R.id.name_crop)
        val daysBefore: TextView = view.findViewById(R.id.days_before_collection)
        val daysPassed: TextView = view.findViewById(R.id.days_passed)
        val daysTotal: TextView = view.findViewById(R.id.days_total)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant_analysis, parent, false)
        return PlantViewHolder(view)



    }

    override fun getItemCount(): Int = plants.size

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plants[position]



        // Клик по карточке
        holder.plantCard.setOnClickListener {
            onClick(plant)
        }
    }

    fun updateData(newPlants: List<Planting>) {
        plants.clear()
        plants.addAll(newPlants)
        notifyDataSetChanged()
    }
}
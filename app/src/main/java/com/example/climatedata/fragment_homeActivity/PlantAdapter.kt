package com.example.climatedata.fragment_homeActivity

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.climatedata.R
import com.example.climatedata.data.models.Planting
import java.util.Date
import java.util.Locale

class PlantAdapter(
    private val context: Context,
    private var plantings: MutableList<Planting>,
    private val getCropName: (Int) -> String,
    private val getLocationName: (Int) -> String,
    private val onDelete: (Planting) -> Unit,
    private val onItemClick: (Planting) -> Unit
) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    private var filteredPlantings: MutableList<Planting> = plantings.toMutableList()

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView? = itemView.findViewById(R.id.card_plant)
        val cropName: TextView? = itemView.findViewById(R.id.text_plant_name)
        val locationName: TextView? = itemView.findViewById(R.id.name_location)
        val area: TextView? = itemView.findViewById(R.id.text_location_area)
        val temperature: TextView? = itemView.findViewById(R.id.temperatyre)
        val humidity: TextView? = itemView.findViewById(R.id.vlashnost)
        val carbon: TextView? = itemView.findViewById(R.id.carbon)

        val plantedDate: TextView? = itemView.findViewById(R.id.date_start)
        val dateEnd: TextView? = itemView.findViewById(R.id.date_end)
        val statePlantEnd: TextView? = itemView.findViewById(R.id.state_plant_end)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val layoutId = if (viewType == 1) R.layout.item_card_arhive else R.layout.plant_card
        val card = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return PlantViewHolder(card)
    }

    override fun getItemViewType(position: Int): Int {
        return if (filteredPlantings[position].status == "archive") 1 else 0
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val planting = filteredPlantings[position]
        Log.d("PlantAdapter", "Binding item at $position with status ${planting.status}")

        val sdf = java.text.SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        // Общие поля
        holder.cropName?.text = getCropName(planting.cropID)
        holder.locationName?.text = getLocationName(planting.locID)

        // Для активной карточки
        if (planting.status != "archive") {
            holder.area?.text = "${planting.area} га"

            val initialTemp = (18..30).random().toFloat()
            val initialHumidity = (40..80).random().toFloat()
            val initialSoil = (30..70).random().toFloat()

            holder.temperature?.text = String.format("%.1f°C", initialTemp)
            holder.humidity?.text = "${initialHumidity.toInt()}%"
            holder.carbon?.text = "${initialSoil.toInt()}%"



            // Обновление показателей каждые 6 секунд
            val handler = android.os.Handler()
            val runnable = object : Runnable {
                var temp = initialTemp
                var hum = initialHumidity
                var soil = initialSoil

                override fun run() {
                    temp += (-1..1).random()
                    hum += (-2..2).random()
                    soil += (-2..2).random()

                    temp = temp.coerceIn(18f, 30f)
                    hum = hum.coerceIn(40f, 80f)
                    soil = soil.coerceIn(30f, 70f)

                    holder.temperature?.text = String.format("%.1f°C", temp)
                    holder.humidity?.text = "${hum.toInt()}%"
                    holder.carbon?.text = "${soil.toInt()}%"

                    handler.postDelayed(this, 6000)
                }
            }
            handler.post(runnable)

            holder.cardView?.setOnClickListener {
                Log.d("PlantAdapter", "Active card clicked: planID=${planting.planID}, cropID=${planting.cropID}")
                onItemClick(planting)
            }
        } else {
            // Архивная посадка
            holder.plantedDate?.text = sdf.format(Date(planting.plantedDate))
            holder.dateEnd?.text = sdf.format(Date(planting.expectedHarvestDate)) // дата сбора
            holder.statePlantEnd?.text = if (System.currentTimeMillis() < planting.expectedHarvestDate) {
                "завершен досрочно"
            } else {
                "завершен успешно"
            }

            holder.cardView?.setOnClickListener {
                if (planting.status != "archive") {
                    onItemClick(planting)
                }
            }
        }
    }

    override fun getItemCount() = filteredPlantings.size



    fun updateData(newList: List<Planting>) {
        Log.d("PlantAdapter", "updateData called with ${newList.size} items")
        plantings.clear()
        plantings.addAll(newList)
        filteredPlantings = plantings.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        Log.d("PlantAdapter", "filter called with query='$query'")
        filteredPlantings.clear()
        if (query.isEmpty()) {
            filteredPlantings.addAll(plantings)
        } else {
            filteredPlantings.addAll(
                plantings.filter {
                    getCropName(it.cropID).contains(query, true) ||
                            getLocationName(it.locID).contains(query, true)
                }
            )
        }
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        val planting = filteredPlantings[position]
        plantings.remove(planting)
        filteredPlantings.removeAt(position)
        notifyItemRemoved(position)
        onDelete(planting)
    }
    fun getItemAt(position: Int) = filteredPlantings[position]

    fun markAsArchived(completedPlanting: Planting) {
        val index = filteredPlantings.indexOfFirst { it.planID == completedPlanting.planID }
        if (index >= 0) {
            filteredPlantings[index] = completedPlanting
            notifyItemChanged(index)
        } else {
            filteredPlantings.add(completedPlanting)
            notifyItemInserted(filteredPlantings.size - 1)
        }
    }


}

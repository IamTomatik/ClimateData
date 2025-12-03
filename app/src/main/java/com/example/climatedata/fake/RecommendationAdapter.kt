package com.example.climatedata.fake

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.climatedata.R
import com.example.climatedata.data.models.Recommendation

class RecommendationAdapter(
    private val recommendations: MutableList<Recommendation>,
    private val plantingName: String,
    private val onActionClick: (Recommendation, String) -> Unit
) : RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder>() {

    inner class RecommendationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.recommendationText)
        val actionButton: Button = itemView.findViewById(R.id.recommendationActionButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_specific_card_plant_recomendation, parent, false)
        return RecommendationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        val rec = recommendations[position]
        holder.messageText.text = rec.message

        // Определяем текст кнопки через map действий
        val actionText = getActionText(rec.message)
        holder.actionButton.text = actionText

        holder.actionButton.setOnClickListener {
            onActionClick(rec, actionText) // <--- передаем actionText
        }
    }

    private fun getActionText(message: String): String {
        val actionTextMap = listOf(
            "пересушена" to "Полить",
            "сух" to "Полить",
            "увлажнить" to "Полить",
            "переувлажн" to "Отключить полив",
            "проветр" to "Проветрить",
            "затен" to "Включить затенение",
            "освещ" to "Включить подсветку",
            "низкая" to "Увеличить температуру",
            "высокая" to "Уменьшить температуру",
            "охлажд" to "Включить охлаждение"
        )

        val text = message.lowercase()
        for ((key, value) in actionTextMap) {
            if (text.contains(key)) return value
        }
        return "Принять"
    }


    override fun getItemCount(): Int = recommendations.size

    fun updateData(newRecommendations: List<Recommendation>) {
        recommendations.clear()
        recommendations.addAll(newRecommendations)
        notifyDataSetChanged()
    }

    fun removeRecommendation(rec: Recommendation) {
        recommendations.remove(rec)
        notifyDataSetChanged()
    }
}
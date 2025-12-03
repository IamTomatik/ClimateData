package com.example.climatedata.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.climatedata.R
import com.example.climatedata.data.models.History

class HistoryAdapter(
    private val items: MutableList<History>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.actionText)
        val time: TextView = view.findViewById(R.id.actionTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_action, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]

        holder.text.text = item.message

        // показываем «2 часа назад»
        val diff = System.currentTimeMillis() - item.timestamp
        holder.time.text = formatTimeAgo(diff)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newData: List<History>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }

    private fun formatTimeAgo(diffMs: Long): String {
        val minutes = diffMs / 60000
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 1 -> "только что"
            minutes < 60 -> "$minutes мин назад"
            hours < 24 -> "$hours ч назад"
            else -> "$days дн назад"
        }
    }
}

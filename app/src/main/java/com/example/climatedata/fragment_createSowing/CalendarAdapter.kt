package com.example.climatedata.fragment_createSowing

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.climatedata.R
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private val days: List<Date>,
    private val onDateSelected: (Date) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private var selectedDate: Date = Date()

    // сегодняшняя дата с обнулением времени
    private val today: Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayNumber: TextView = itemView.findViewById(R.id.dayNumber)
        val dayName: TextView = itemView.findViewById(R.id.dayName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun getItemCount(): Int = days.size



    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val date = days[position]

        val dayNumStr = SimpleDateFormat("dd", Locale.getDefault()).format(date)
        val dayNameStr = SimpleDateFormat("EE", Locale("ru")).format(date)

        holder.dayNumber.text = dayNumStr
        holder.dayName.text = dayNameStr

        // проверка выделения
        if (isSameDay(date, selectedDate)) {
            holder.itemView.setBackgroundResource(R.drawable.bg_calendar_day_selector)
            holder.dayNumber.setTextColor(Color.BLACK)
            holder.dayName.setTextColor(Color.BLACK)
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            holder.dayNumber.setTextColor(Color.GRAY)
            holder.dayName.setTextColor(Color.GRAY)
        }

        // клик по дню
        holder.itemView.setOnClickListener {
            selectedDate = date
            notifyDataSetChanged() // перерисуем чтобы поменялось выделение
            onDateSelected(date)  // передаем дату во фрагмент
        }
    }

    // сравниваем только год, месяц и день
    fun isSameDay(d1: Date, d2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = d1 }
        val cal2 = Calendar.getInstance().apply { time = d2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}

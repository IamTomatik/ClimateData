package com.example.climatedata.fragment_createSowing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.climatedata.R
import com.example.climatedata.data.models.Location

class LocationAdapter(
    private val context: Context,
    private var locations: MutableList<Location>,
    private val onDelete: (Location) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.text_location_name)
        val type = view.findViewById<TextView>(R.id.text_location_type)
        val area = view.findViewById<TextView>(R.id.text_location_area)
        val card = view.findViewById<View>(R.id.card_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_card, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.name.text = location.name
        holder.type.text = location.type ?: "Не указано"
        holder.area.text = location.area?.toString() ?: "—"

        holder.card.setOnClickListener {

        }
    }

    override fun getItemCount() = locations.size

    fun getItem(position: Int) = locations[position]

    fun updateData(newList: List<Location>) {
        locations.clear()
        locations.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        val loc = locations[position]
        locations.removeAt(position)
        notifyItemRemoved(position)
        onDelete(loc)
    }

    fun attachSwipeHandler(recyclerView: RecyclerView) {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val paint = Paint()
                val icon = ContextCompat.getDrawable(context, R.drawable.ic_delete)
                val iconSize = 64
                val iconMargin = (itemView.height - iconSize) / 2
                val maxSwipe = itemView.width / 3f
                val limitedDx = if (dX < -maxSwipe) -maxSwipe else dX

                val background = RectF(
                    itemView.right + limitedDx,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                paint.color = Color.RED
                c.drawRect(background, paint)

                icon?.setBounds(
                    (itemView.right - iconMargin - iconSize),
                    itemView.top + iconMargin,
                    (itemView.right - iconMargin),
                    itemView.top + iconMargin + iconSize
                )
                icon?.draw(c)

                itemView.translationX = limitedDx * 0.9f
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val location = getItem(position)

                AlertDialog.Builder(context)
                    .setTitle("Удалить локацию?")
                    .setMessage("Вы уверены, что хотите удалить эту локацию?")
                    .setPositiveButton("Удалить") { _, _ -> removeAt(position) }
                    .setNegativeButton("Отмена") { dialog, _ ->
                        dialog.dismiss()
                        notifyItemChanged(position)
                    }
                    .show()
            }
        }

        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)
    }
}

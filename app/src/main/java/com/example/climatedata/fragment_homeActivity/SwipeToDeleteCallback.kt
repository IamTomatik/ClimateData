package com.example.climatedata.utils

import android.content.Context
import android.graphics.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.climatedata.R
import com.example.climatedata.fragment_homeActivity.PlantAdapter

class SwipeToDeleteCallback(
    private val context: Context,
    private val adapter: PlantAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.red_error)
        style = Paint.Style.FILL
    }

    private val cornerRadius = 4 * context.resources.displayMetrics.density // 4dp

    private val icon = ContextCompat.getDrawable(context, R.drawable.ic_delete)?.apply {
        setTint(Color.WHITE)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = context.resources.displayMetrics.density * 16 // 16sp
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("cygre_regular", Typeface.NORMAL)
    }

    private val text = "Удалить"

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
        val itemHeight = itemView.height
        val maxSwipe = itemView.width / 3f
        val translationX = dX.coerceIn(-maxSwipe, 0f)


        val backgroundLeft = itemView.right - maxSwipe
        val backgroundRight = itemView.right.toFloat()
        val rectF = RectF(backgroundLeft, itemView.top.toFloat(), backgroundRight, itemView.bottom.toFloat())
        c.drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint)

        // Вертикально центрируем иконку + текст
        val centerX = (backgroundLeft + backgroundRight) / 2f
        val centerY = itemView.top + itemHeight / 2f

        icon?.let {
            val iconWidth = it.intrinsicWidth
            val iconHeight = it.intrinsicHeight

            val totalHeight = iconHeight + 8 * context.resources.displayMetrics.density + textPaint.textSize
            val iconTop = (centerY - totalHeight / 2).toInt()
            val iconLeft = (centerX - iconWidth / 2).toInt()
            val iconRight = iconLeft + iconWidth
            val iconBottom = iconTop + iconHeight
            it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            it.draw(c)

            val textX = centerX
            val textY = iconBottom + 8 * context.resources.displayMetrics.density + textPaint.textSize / 2
            c.drawText(text, textX, textY, textPaint)
        }

        // Сдвигаем карточку
        itemView.translationX = translationX
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val planting = adapter.getItemAt(position)

        AlertDialog.Builder(context)
            .setTitle("Удалить посадку?")
            .setMessage("Вы уверены, что хотите удалить ${planting.cropID}?")
            .setPositiveButton("Удалить") { _, _ ->
                adapter.removeAt(position)
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .show()
    }
}

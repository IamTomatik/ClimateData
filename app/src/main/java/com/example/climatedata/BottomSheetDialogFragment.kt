package com.example.climatedata

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.climatedata.fragment_createLocation.CreateLocation
import com.example.climatedata.fragment_createSowing.CreateSowingActivity

class AddOptionsBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.bottom_sheet_add_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //
        val container = view.findViewById<View>(R.id.bottomSheetContainer)
        container.translationY = 600f
        container.alpha = 0f
        container.animate()
            .translationY(0f)
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(450)
            .setStartDelay(50)
            .start()

        val option1 = view.findViewById<LinearLayout>(R.id.option1)
        val option2 = view.findViewById<LinearLayout>(R.id.option2)
        val option3 = view.findViewById<LinearLayout>(R.id.option3)

        option1.setOnClickListener {

            dismissWithAnimation(container)

            val intent = Intent(requireContext(), CreateLocation::class.java)
            startActivity(intent)
        }

        option2.setOnClickListener {

            dismissWithAnimation(container)

            val intent = Intent(requireContext(), CreateSowingActivity::class.java)
            startActivity(intent)

        }

        option3.setOnClickListener {

            dismissWithAnimation(container)
        }
    }

    private fun dismissWithAnimation(container: View) {
        container.animate()
            .translationY(600f)
            .alpha(0f)
            .setDuration(250)
            .withEndAction { dismiss() }
            .start()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        parentFragmentManager.setFragmentResult("bottomSheetClosed", Bundle())
    }
}

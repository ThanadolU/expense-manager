package org.classapp.expensemanager

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color as color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor

class SummaryFragment : Fragment() {

    private lateinit var filterLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_summary, container, false)
        val view = inflater.inflate(R.layout.fragment_summary, container, false)
        filterLayout = view.findViewById<LinearLayout>(R.id.filterSummaryLayout)
        val filterOptions = listOf<String>("All", "Daily", "Weekly", "Monthly", "Yearly")
        for (option in filterOptions) {
            val button = Button(requireContext())
            button.text = option
            button.textSize = 10f

            val layoutParams = LinearLayout.LayoutParams(
                convertDpToPixel(50).toInt(),
//                LinearLayout.LayoutParams.WRAP_CONTENT
                convertDpToPixel(30).toInt()
            )
            layoutParams.setMargins(convertDpToPixel(18).toInt(), convertDpToPixel(5).toInt(), 0, 0)
            button.layoutParams = layoutParams

            val background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
//                setColor(color.TRANSPARENT)
//                cornerRadius = convertDpToPixel(2000)
            }
            button.background = background
            button.setBackgroundColor(color.TRANSPARENT)

            button.setOnClickListener {
                selectButton(button)
            }
            filterLayout.addView(button)

            if (option == "All") {
                button.setBackgroundColor(Color(255, 211, 81).toArgb())
            }
        }

        return view
    }

    private fun selectButton(selectedButton: Button) {
        val filterLayout = view?.findViewById<LinearLayout>(R.id.filterSummaryLayout)
        filterLayout?.let {
            for (i in 0 until it.childCount) {
                val button = it.getChildAt(i) as Button
                button.setBackgroundColor(color.TRANSPARENT) // Reset background color for all buttons
            }
//            selectedButton.setBackgroundColor(Color(255, 211, 81).toArgb()) // Highlight the selected button
            ObjectAnimator.ofArgb(
                selectedButton,
                "backgroundColor",
                color.TRANSPARENT,
                Color(255, 211, 81).toArgb()
            ).apply {
                duration = 300 // Set animation duration in milliseconds
                start() // Start the animation
            }
        }
    }

    private fun convertDpToPixel(dp: Int): Float {
        val scale = resources.displayMetrics.density
        return dp * scale + 0.5f
    }
}

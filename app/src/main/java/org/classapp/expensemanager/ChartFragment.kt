package org.classapp.expensemanager

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

class ChartFragment : Fragment() {

    private lateinit var filterLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        filterLayout = view.findViewById<LinearLayout>(R.id.filterChartLayout)
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
            }
            button.background = background
            button.setBackgroundColor(color.TRANSPARENT)

            button.setOnClickListener {
                selectedButton(button)
            }
            filterLayout.addView(button)

            if (option == "All") {
                button.setBackgroundColor(Color(255, 211, 81).toArgb())
            }
        }
        return view
    }

    private fun selectedButton(selectedButton: Button) {
        val filterLayout = view?.findViewById<LinearLayout>(R.id.filterChartLayout)
        filterLayout?.let {
            for (i in 0 until it.childCount) {
                val button = it.getChildAt(i) as Button
                button.setBackgroundColor(color.TRANSPARENT)
            }

            ObjectAnimator.ofArgb(
                selectedButton,
                "backgroundColor",
                color.TRANSPARENT,
                Color(255, 211, 81).toArgb()
            ).apply {
                duration = 300
                start()
            }
        }
    }

    private fun convertDpToPixel(dp: Int): Float {
        val scale = resources.displayMetrics.density
        return dp * scale + 0.5f
    }
}
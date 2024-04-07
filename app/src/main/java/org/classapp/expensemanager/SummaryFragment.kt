package org.classapp.expensemanager

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

class SummaryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_summary, container, false)
        val view = inflater.inflate(R.layout.fragment_summary, container, false)
        val filterLayout = view.findViewById<LinearLayout>(R.id.filterLayout)
        val filterOptions = listOf<String>("All", "Daily", "Weekly", "Monthly", "Yearly")
        for (option in filterOptions) {
            val button = Button(requireContext())
            val layoutParams = LinearLayout.LayoutParams(
                convertDpToPixel(50).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(convertDpToPixel(18).toInt(), 0, 0, 0)
            val background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
//                setColor(color.TRANSPARENT)
            }
            button.text = option
            button.textSize = 10f
            button.layoutParams = layoutParams
            button.background = background
            button.setBackgroundColor(color.TRANSPARENT)
            button.setOnClickListener {
                selectButton(button)
            }
            filterLayout.addView(button)
        }

        return view
    }
    private fun selectButton(selectedButton: Button) {
        val filterLayout = view?.findViewById<LinearLayout>(R.id.filterLayout)
        filterLayout?.let {
            for (i in 0 until it.childCount) {
                val button = it.getChildAt(i) as Button
                button.setBackgroundColor(color.TRANSPARENT) // Reset background color for all buttons
            }
            selectedButton.setBackgroundColor(Color(255, 211, 81).toArgb()) // Highlight the selected button
        }
    }

    private fun convertDpToPixel(dp: Int): Float {
        val scale = resources.displayMetrics.density
        return dp * scale + 0.5f
    }
}

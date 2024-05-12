package org.classapp.expensemanager

import android.animation.ObjectAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.classapp.expensemanager.ui.theme.ExpenseManagerTheme
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.lang.reflect.Modifier
import android.graphics.Color as color

//data class ExpensesSummary(
////    val details: String,
//    val expenses: Double
//)

class ChartFragment : Fragment() {

//    private lateinit var filterLayout: LinearLayout
    private lateinit var detailsShow: ScrollView
    private lateinit var detailsExpensesShow: ScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
//        filterLayout = view.findViewById(R.id.filterChartLayout)
        detailsShow = view.findViewById(R.id.detailsShow)
        detailsExpensesShow = view.findViewById(R.id.detailsExpensesShow)
//        val filterOptions = listOf<String>("All", "Daily", "Weekly", "Monthly", "Yearly")
//        for (option in filterOptions) {
//            val button = Button(requireContext())
//            button.text = option
//            button.textSize = 10f
//
//            val layoutParams = LinearLayout.LayoutParams(
//                convertDpToPixel(50).toInt(),
////                LinearLayout.LayoutParams.WRAP_CONTENT
//                convertDpToPixel(30).toInt()
//            )
//            layoutParams.setMargins(convertDpToPixel(18).toInt(), convertDpToPixel(5).toInt(), 0, 0)
//            button.layoutParams = layoutParams
//
//            val background = GradientDrawable().apply {
//                shape = GradientDrawable.RECTANGLE
//            }
//            button.background = background
////            button.setBackgroundColor(color.TRANSPARENT)
//
//            button.setOnClickListener {
//                selectedButton(button)
//            }
//            filterLayout.addView(button)
//
//            if (option == "All") {
//                button.setBackgroundColor(Color(255, 211, 81).toArgb())
//            }
//        }

        var pieChart: PieChart
        pieChart = view.findViewById(R.id.piechart);

        val expensesList = ArrayList<Map<String, Any?>>()

        val onFirebaseQueryFailed = { e:Exception ->
            Toast.makeText(view.context, e.message,
                Toast.LENGTH_LONG).show()
        }

        val onFirebaseQuerySuccess = { result: List<Map<String, Any?>> ->
            expensesList.clear()
            expensesList.addAll(result)
            populatePieChart(expensesList, pieChart)
            populateDetails(expensesList)
            populateDetailsExpenses(expensesList)
        }

        getExpensesFromDatabase(onFirebaseQuerySuccess, onFirebaseQueryFailed)
        
//        detailsShow.setContent {
//            ChartScreen(expensesList = expensesList)
//        }
//        populateDetails(expensesList)

        return view
    }

//    private fun selectedButton(selectedButton: Button) {
//        val filterLayout = view?.findViewById<LinearLayout>(R.id.filterChartLayout)
//        filterLayout?.let {
//            for (i in 0 until it.childCount) {
//                val button = it.getChildAt(i) as Button
//                button.setBackgroundColor(color.TRANSPARENT)
//            }
//
//            ObjectAnimator.ofArgb(
//                selectedButton,
//                "backgroundColor",
//                color.TRANSPARENT,
//                Color(255, 211, 81).toArgb()
//            ).apply {
//                duration = 300
//                start()
//            }
//        }
//    }
//
//    private fun convertDpToPixel(dp: Int): Float {
//        val scale = resources.displayMetrics.density
//        return dp * scale + 0.5f
//    }

    private fun populatePieChart(expensesDataMap: ArrayList<Map<String, Any?>>, pieChart: PieChart) {
        pieChart.clearChart()

        expensesDataMap.forEachIndexed { index, item ->
            val details = item["details"] as? String
            val totalExpenses = item["totalExpenses"] as? Double ?: 0.0

            pieChart.addPieSlice(
                PieModel(
                    details,
                    totalExpenses.toFloat(),
                    getColor(index)
                )
            )
        }

        pieChart.startAnimation()
    }

    private fun getColor(index: Int): Int {
        val colors = arrayOf("#FFA726", "#66BB6A", "#EF5350", "#29B6F6", "#AB47BC",
            "#FF5733", "#FFBD33", "#B4FF33", "#33FFC1", "#3343FF",
            "#D833FF", "#FF33D1", "#FF3333", "#33FF57", "#33B4FF",
            "#D1FF33", "#FF5733", "#FFBD33", "#B4FF33", "#33FFC1",
            "#3343FF", "#D833FF", "#FF33D1", "#FF3333", "#33FF57",
            "#33B4FF", "#D1FF33", "#FF5733", "#FFBD33", "#B4FF33",
            "#33FFC1", "#3343FF", "#D833FF", "#FF33D1", "#FF3333",
            "#33FF57", "#33B4FF", "#D1FF33", "#FF5733", "#FFBD33",
            "#B4FF33", "#33FFC1", "#3343FF", "#D833FF", "#FF33D1",
            "#FF3333", "#33FF57", "#33B4FF", "#D1FF33", "#FF5733")
        return color.parseColor(colors[index % colors.size])
    }

    private fun populateDetails(expensesList: List<Map<String, Any?>>) {
        val colors = arrayOf("#FFA726", "#66BB6A", "#EF5350", "#29B6F6", "#AB47BC",
            "#FF5733", "#FFBD33", "#B4FF33", "#33FFC1", "#3343FF",
            "#D833FF", "#FF33D1", "#FF3333", "#33FF57", "#33B4FF",
            "#D1FF33", "#FF5733", "#FFBD33", "#B4FF33", "#33FFC1",
            "#3343FF", "#D833FF", "#FF33D1", "#FF3333", "#33FF57",
            "#33B4FF", "#D1FF33", "#FF5733", "#FFBD33", "#B4FF33",
            "#33FFC1", "#3343FF", "#D833FF", "#FF33D1", "#FF3333",
            "#33FF57", "#33B4FF", "#D1FF33", "#FF5733", "#FFBD33",
            "#B4FF33", "#33FFC1", "#3343FF", "#D833FF", "#FF33D1",
            "#FF3333", "#33FF57", "#33B4FF", "#D1FF33", "#FF5733")
        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.VERTICAL

        expensesList.forEachIndexed { index, item ->
            val details = item["details"] as? String ?: ""
            Log.i("Chart $details Expenses:", "${item["totalExpenses"]}" )
            val color = colors[index % colors.size] // Get color based on index

            val detailView = createDetailItemView(details, color)
            linearLayout.addView(detailView)
        }

        detailsShow.addView(linearLayout)
    }

    private fun createDetailItemView(details: String, color: String): View {
        val itemView = LayoutInflater.from(view?.context).inflate(R.layout.detail_item_layout, null)

        val colorIndicator = itemView.findViewById<View>(R.id.colorIndicator)
        colorIndicator.setBackgroundColor(android.graphics.Color.parseColor(color))

        val detailTextView = itemView.findViewById<TextView>(R.id.detailTextView)
        detailTextView.text = details

        return itemView
    }
//    detailsExpensesShow
    private fun populateDetailsExpenses(expensesList: List<Map<String, Any?>>) {
        val colors = arrayOf("#FFA726", "#66BB6A", "#EF5350", "#29B6F6", "#AB47BC",
            "#FF5733", "#FFBD33", "#B4FF33", "#33FFC1", "#3343FF",
            "#D833FF", "#FF33D1", "#FF3333", "#33FF57", "#33B4FF",
            "#D1FF33", "#FF5733", "#FFBD33", "#B4FF33", "#33FFC1",
            "#3343FF", "#D833FF", "#FF33D1", "#FF3333", "#33FF57",
            "#33B4FF", "#D1FF33", "#FF5733", "#FFBD33", "#B4FF33",
            "#33FFC1", "#3343FF", "#D833FF", "#FF33D1", "#FF3333",
            "#33FF57", "#33B4FF", "#D1FF33", "#FF5733", "#FFBD33",
            "#B4FF33", "#33FFC1", "#3343FF", "#D833FF", "#FF33D1",
            "#FF3333", "#33FF57", "#33B4FF", "#D1FF33", "#FF5733")
        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.VERTICAL

        expensesList.forEachIndexed { index, item ->
            val details = item["details"] as? String ?: ""
            val totalExpenses = item["totalExpenses"].toString()
            Log.i("Chart $details Expenses:", "${item["totalExpenses"]}" )
            val color = colors[index % colors.size] // Get color based on index

            val detailView = createDetailsExpensesView(details, totalExpenses, color)
            linearLayout.addView(detailView)
        }

        detailsExpensesShow.addView(linearLayout)
    }

    private fun createDetailsExpensesView(details: String, expenses: String, color: String): View {
        val itemView = LayoutInflater.from(view?.context).inflate(R.layout.detail_expense_layout, null)

        val colorIndicator = itemView.findViewById<View>(R.id.colorIndicator)
        colorIndicator.setBackgroundColor(android.graphics.Color.parseColor(color))

        val detailsTextView = itemView.findViewById<TextView>(R.id.detailsTextView)
        detailsTextView.text = details

        val expensesTextView = itemView.findViewById<TextView>(R.id.expensesTextView)
        expensesTextView.text = expenses

        return itemView
    }
}

private fun getExpensesFromDatabase(onSuccess: (List<Map<String, Any?>>)->Unit,
                                    onFailure: (Exception)->Unit)
{
    val db = Firebase.firestore
    val transactionsCollection = db.collection("transactions")

    transactionsCollection.addSnapshotListener { snapshot, exception ->
        if (exception != null) {
            onFailure(exception)
            return@addSnapshotListener
        }

        if (snapshot != null && !snapshot.isEmpty) {
            val expensesDataMap = mutableMapOf<String?, Double>()

            for (document in snapshot.documents) {
                val transaction:Transaction? = document.toObject(Transaction::class.java)
                if (transaction != null) {
                    val details = transaction.details
                    val income = transaction.income ?: 0.0
                    val expenses = transaction.expenses ?: 0.0
//                    Log.i("$details Income:", "$income")
//                    Log.i("$details Expense:", "$expenses")

                    // Check if details is "Initial" to determine if it's the initial transaction
                    if (income == 0.0) {
                        // Add initial value only if it's the first transaction for this company
                        val currentSummary = expensesDataMap[details]
                        if (currentSummary == null) {
                            expensesDataMap[details] = expenses
                        } else {
                            val currentExpenses = expensesDataMap[details]
                            if (currentExpenses != null) {
                                expensesDataMap[details] = currentExpenses + expenses
                            }
                        }
                    }
                }
            }

            val result = mutableListOf<Map<String, Any?>>()
            expensesDataMap.forEach { (details, expenses) ->
//                Log.i("$details Expense:", "$expenses")
                result.add(mapOf("details" to details, "totalExpenses" to expenses))
            }

            onSuccess(result)
        } else {
            onFailure(Exception("Snapshot is null or empty"))
        }
    }
}
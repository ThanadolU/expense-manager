package org.classapp.expensemanager

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.firestore
import org.classapp.expensemanager.ui.theme.ExpenseManagerTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@IgnoreExtraProperties
data class Transaction(
    val date: Timestamp? = null,
    val details: String? = "",
//    val type: String? = "",
    val company: String? = "",
    val income: Double? = 0.00,
    val expenses: Double? = 0.00
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "date" to date,
            "details" to details,
//            "type" to type,
            "company" to company,
            "income" to income,
            "expenses" to expenses
        )
    }
}

data class TransactionSummary(
    val initial: Double,
    val sumOfIncome: Double,
    val sumOfExpenses: Double
)

//data class BankIcon(
//    val name: String,
//    val iconResourceId: Int
//)

class HomeFragment : Fragment() {

//    private lateinit var homeScroll: ScrollView
    private lateinit var homeScrollComposeView: ComposeView
    private lateinit var homeAddBankBtn: ImageButton
    private lateinit var addBankCardView: CardView
    private lateinit var arrowIconBtn: ImageButton
    private lateinit var addImageBtn: ImageView
    private lateinit var editTextName: EditText
    private lateinit var editTextNumber: EditText
    private lateinit var editTextDate: EditText
    private lateinit var addBankBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var selectBankIconCardView: CardView
//    private lateinit var imageArrowBtnSelectIcon: ImageButton
    private lateinit var editBankCardView: CardView
    private lateinit var editBank: TextView
    private lateinit var imageArrowBtnEdit: ImageButton
//    private lateinit var editBankBtn: Button
//    private lateinit var deleteBankBtn: Button
    private lateinit var openAddIncomeExpensesBtn: Button
    private lateinit var closeAddIncomeExpensesBtn: Button
    private lateinit var addIncomeExpensesCardView: CardView
    private lateinit var imageArrowBtnAddIncomeExpenses: ImageButton
    private lateinit var addIncomeExpensesBtn: Button
    private lateinit var cancelIncomeExpensesBtn: Button
    private lateinit var tableLayout: TableLayout
    private lateinit var dateEditText: EditText
    private lateinit var detailsEditText: EditText
//    private lateinit var detailsSpinner: Spinner
    private lateinit var companyEditText: EditText
    private lateinit var incomeEditText: EditText
    private lateinit var expensesEditText: EditText
    private lateinit var deleteBankCardView: CardView
    private lateinit var wantToDeleteBank: Button
    private lateinit var doNotWantToDeleteBank: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
//        homeScroll = view.findViewById(R.id.homeScroll)
        homeScrollComposeView = view.findViewById(R.id.homeScrollComposeView)
        homeAddBankBtn = view.findViewById(R.id.imageAddBankBtn)
        addBankCardView = view.findViewById(R.id.addBankCardView)
        arrowIconBtn = view.findViewById(R.id.imageArrowBtn)
//        addImageBtn = view.findViewById(R.id.addImageView)
        editTextName = view.findViewById(R.id.editTextName)
        editTextNumber = view.findViewById(R.id.editTextNumber)
        editTextDate = view.findViewById(R.id.editTextDate)
        addBankBtn = view.findViewById(R.id.addBankBtn)
        cancelBtn = view.findViewById(R.id.cancelBtn)
//        selectBankIconCardView = view.findViewById(R.id.selectBankIconCardView)
//        imageArrowBtnSelectIcon = view.findViewById(R.id.imageArrowBtnSelectIcon)
        editBankCardView = view.findViewById(R.id.editBankCardView)
        editBank = view.findViewById(R.id.editBank)
        imageArrowBtnEdit = view.findViewById(R.id.imageArrowBtnEdit)
        openAddIncomeExpensesBtn = view.findViewById(R.id.openAddIncomeExpensesBtn)
        closeAddIncomeExpensesBtn = view.findViewById(R.id.closeAddIncomeExpensesBtn)
        addIncomeExpensesCardView = view.findViewById(R.id.addIncomeExpensesCardView)
        imageArrowBtnAddIncomeExpenses = view.findViewById(R.id.imageArrowBtnAddIncomeExpenses)
        addIncomeExpensesBtn = view.findViewById(R.id.addIncomeExpensesBtn)
        cancelIncomeExpensesBtn = view.findViewById(R.id.cancelIncomeExpensesBtn)
        tableLayout = view.findViewById(R.id.tableLayout)
        dateEditText = view.findViewById(R.id.dateEditText)
        detailsEditText = view.findViewById(R.id.detailsEditText)
//        detailsSpinner = view.findViewById(R.id.detailsSpinner)
        companyEditText = view.findViewById(R.id.companyEditText)
        incomeEditText = view.findViewById(R.id.incomeEditText)
        expensesEditText = view.findViewById(R.id.expensesEditText)
        deleteBankCardView = view.findViewById(R.id.deleteBankCardView)
        wantToDeleteBank = view.findViewById(R.id.wantToDeleteBank)
        doNotWantToDeleteBank = view.findViewById(R.id.doNotWantToDeleteBank)

        homeScrollComposeView.setContent {
//            val screenContext = LocalContext.current
            HomePage(
                onEditTransactionClicked = { bankName ->
                editBank.text = bankName
                // Handle edit transaction clicked
                editBankCardView.visibility = View.VISIBLE // Show edit transactions page
                // Call getTransactionsForSpecificBankFromDatabase with the bank name or other necessary data
//                tableLayout.removeAllViews()
                if (tableLayout.childCount > 1) {
                    // Start removing views from the second view (index 1)
                    for (i in tableLayout.childCount - 1 downTo 1) {
                        tableLayout.removeViewAt(i)
                    }
                }
                getTransactionsForSpecificBankFromDatabase(view, tableLayout, bankName)

//                getDataFromFirebase(bankName)
//                wantToDeleteBank.setOnClickListener {
//                    deleteTransactionsForSpecificBankFromDatabase(bankName)
//                    deleteBankCardView.visibility = View.GONE
//                }
                },
                onDeleteTransactionClicked = {bankName ->
                    deleteBankCardView.visibility = View.VISIBLE
                    wantToDeleteBank.setOnClickListener {
                        deleteTransactionsForSpecificBankFromDatabase(bankName)
                        deleteBankCardView.visibility = View.GONE
                    }
                }
            )
        }

        homeAddBankBtn.setOnClickListener{ addBankCardView.visibility = View.VISIBLE }

        arrowIconBtn.setOnClickListener{
            editTextName.text.clear()
            editTextNumber.text.clear()
            editTextDate.text.clear()
            addBankCardView.visibility = View.GONE
        }

//        val bankIcons = listOf(
//            BankIcon("KBank", R.drawable.kbank),
//            BankIcon("BBL", R.drawable.bbl),
//            BankIcon("CIMB", R.drawable.cimb),
//            BankIcon("KTB", R.drawable.ktb),
//            BankIcon("LH Bank", R.drawable.lhbank),
//            BankIcon("SCB", R.drawable.scb),
//            BankIcon("TMB", R.drawable.ttb),
//            BankIcon("UOB", R.drawable.uob),
//            BankIcon("GSB", R.drawable.gsb),
//            BankIcon("KKP", R.drawable.kkp),
//            BankIcon("Citibank N.A.", R.drawable.citibankna),
//            BankIcon("BAAC", R.drawable.baac),
//            BankIcon("MHCB", R.drawable.mihuzo),
//            BankIcon("ibank", R.drawable.ibank),
//            BankIcon("Tisco", R.drawable.tisco),
//            BankIcon("Thai Credit", R.drawable.thaicredit),
//            BankIcon("SMBC", R.drawable.smbc),
//            BankIcon("HSBC", R.drawable.hsbc),
//            BankIcon("BNPP", R.drawable.bnpp)
//        )

//
//        val pickMedia =
//            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
//                if (uri != null) {
//                    Log.d("PhotoPicker", "Selected URI: $uri")
//                } else {
//                    Log.d("PhotoPicker", "No media selected")
//                }
//            }

//        addImageBtn.setOnClickListener {
////            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
////            val iconPickerDialog = IconPickerDialog.newInstance(bankIcons)
////            iconPickerDialog.show(supportFragmentManager, "IconPickerDialog")
//            selectBankIconCardView.visibility = View.VISIBLE
//        }

//        imageArrowBtnSelectIcon.setOnClickListener{
//            selectBankIconCardView.visibility = View.GONE
//        }

        addBankBtn.setOnClickListener {
            try {
                val name = editTextName.text.toString()
                val initial = editTextNumber.text.toString().toDoubleOrNull()
                val date = parseDate(editTextDate.text.toString())
                saveToFirebase(name, initial, date)
            } catch (e: Exception) {
                Log.e(TAG, "Error adding bank", e)
            }
        }

        cancelBtn.setOnClickListener {
            editTextName.text.clear()
            editTextNumber.text.clear()
            editTextDate.text.clear()
            addBankCardView.visibility = View.GONE
        }

        imageArrowBtnEdit.setOnClickListener {
            editBankCardView.visibility = View.GONE
        }

        openAddIncomeExpensesBtn.setOnClickListener {
            addIncomeExpensesCardView.visibility = View.VISIBLE
        }

        closeAddIncomeExpensesBtn.setOnClickListener {
            editBankCardView.visibility = View.GONE
        }

        addIncomeExpensesCardView

        imageArrowBtnAddIncomeExpenses.setOnClickListener {
            addIncomeExpensesCardView.visibility = View.GONE
        }

//        val details = arrayOf(
//            "Insurance",
//            "Automobile and Related",
//            "Café",
//            "Credit Card",
//            "Donation",
//            "Education",
//            "Electricity/Water Bill",
//            "Entertainment/Leisure",
//            "Gift",
//            "Hospital",
//            "Hotel/Resort",
//            "Internet/Telephone",
//            "Savings and Investment",
//            "Party",
//            "Shopping",
//            "Travel Expense",
//            "Tourism/Travel",
//            "Withdrawal",
//            "Salary"
//        )
//
//        val arrayAdapter = ArrayAdapter(view.context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, details)
//
//        detailsSpinner.adapter = arrayAdapter
//
//        detailsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                Toast.makeText(view?.context, "Item is ${details[position]}", Toast.LENGTH_LONG).show()
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                Toast.makeText(view?.context, "Nothing is selected", Toast.LENGTH_LONG).show()
//            }
//        }

        addIncomeExpensesBtn.setOnClickListener {
//            if (tableLayout.childCount > 1) {
//                // Start removing views from the second view (index 1)
//                for (i in tableLayout.childCount - 1 downTo 1) {
//                    tableLayout.removeViewAt(i)
//                }
//            }
            try {
                val date = parseDate(dateEditText.text.toString())
                val details = detailsEditText.text.toString()
//                val details = detailsSpinner.selectedItem.toString()
                val company = companyEditText.text.toString()
                val income = incomeEditText.text.toString().toDoubleOrNull()
                val expenses = expensesEditText.text.toString().toDoubleOrNull()
                addTransactionToFirebase(date, details, company, income, expenses)
            } catch (e: Exception) {
                Log.e(TAG, "Error adding transaction", e)
            }
        }

        cancelIncomeExpensesBtn.setOnClickListener {
            dateEditText.text.clear()
//            detailsSpinner.setSelection(0)
            companyEditText.text.clear()
            incomeEditText.text.clear()
            expensesEditText.text.clear()
            addIncomeExpensesCardView.visibility = View.GONE
        }

        doNotWantToDeleteBank.setOnClickListener {
            deleteBankCardView.visibility = View.GONE
        }

        return view
    }

    private fun parseDate(dateString: String): Date {
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return format.parse(dateString) ?: Date()
    }

    private fun saveToFirebase(name: String, initial: Double?, date: Date) {
//        val database = FirebaseFirestore.getInstance()
        val database = Firebase.firestore
//        val database = FirebaseDatabase.getInstance()
//        val myRef = database.getReference("transactions")

//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val value = dataSnapshot.getValue(String::class.java)
//                Log.d(TAG, "Value is: $value")
//
//                // Update UI or perform other actions based on the retrieved data
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException())
//            }
//        })

        val data = hashMapOf(
            "date" to Timestamp(date),
            "details" to "Initial",
//            "type" to "Initial",
            "company" to name,
            "income" to initial,
            "expenses" to 0.00
        )
        database.collection("transactions")
            .add(data)
            .addOnSuccessListener {
                editTextName.text.clear()
                editTextNumber.text.clear()
                editTextDate.text.clear()
                addBankCardView.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving transaction to Firebase: ${e.message}")
            }
    }

    private fun addTransactionToFirebase(date: Date, details: String?, company: String?, income: Double?, expenses: Double?) {
        val database = Firebase.firestore
        val data = hashMapOf(
            "date" to Timestamp(date),
            "details" to details,
//            "type" to type,
            "company" to company,
            "income" to income,
            "expenses" to expenses
        )
        database.collection("transactions")
            .add(data)
            .addOnSuccessListener {
                dateEditText.text.clear()
                detailsEditText.text.clear()
//                detailsSpinner.setSelection(0)
                companyEditText.text.clear()
                incomeEditText.text.clear()
                expensesEditText.text.clear()
                addIncomeExpensesCardView.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving transaction to Firebase: ${e.message}")
                // Handle error
            }
        if (tableLayout.childCount > 1) {
            // Start removing views from the second view (index 1)
            for (i in tableLayout.childCount - 1 downTo 1) {
                tableLayout.removeViewAt(i)
            }
        }
    }
}

@Composable
fun HomePage(onEditTransactionClicked: (String) -> Unit, onDeleteTransactionClicked: (String) -> Unit) {
    val screenContext = LocalContext.current
//    val transactionList = remember { mutableStateListOf<Transaction?>() }
    val transactionList = remember { mutableStateListOf<Map<String, Any?>>() }

    val onFirebaseQueryFailed = { e:Exception ->
        Toast.makeText(screenContext, e.message,
            Toast.LENGTH_LONG).show()
    }

//    val onFirebaseQuerySuccess = { result:QuerySnapshot ->
//        transactionList.clear()
//        if (!result.isEmpty) {
//            val resultDocuments = result.documents
//            for (document in resultDocuments) {
//                val transaction:Transaction? = document.toObject(Transaction::class.java)
////                Toast.makeText(screenContext, "${transaction?.details}", Toast.LENGTH_LONG).show()
//                transactionList.add(transaction)
//            }
//        }
//    }
    val onFirebaseQuerySuccess = { result: List<Map<String, Any?>> ->
        transactionList.clear()
        transactionList.addAll(result)
    }

    getTransactionsFromDatabase(onFirebaseQuerySuccess, onFirebaseQueryFailed)

    ExpenseManagerTheme {
        Surface (
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            color = colorResource(id = R.color.gray_1)
        ) {
//            Column ( modifier = Modifier
//                .verticalScroll(rememberScrollState())
//                .fillMaxSize(),
//                verticalArrangement = Arrangement.Top,
//                horizontalAlignment = Alignment.CenterHorizontally) {
//                TransactionList(transactions = transactionList)
//            } // end column scope
            TransactionList(
                transactions = transactionList, // Pass the list of transactions to TransactionList
                onEditClicked = onEditTransactionClicked, // Pass the lambda for editing
                onDeleteClicked = onDeleteTransactionClicked // Pass the lambda for deleting
            )
//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//                contentPadding = PaddingValues(8.dp)
//            ) {
//                items(transactionList) { transaction ->
//                    TransactionItem(transaction = transaction)
//                }
//            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Map<String, Any?>, onEditClicked: (String) -> Unit, onDeleteClicked: (String) -> Unit) {
//    val screenContext = LocalContext.current
    ElevatedCard (elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.white)
        )
    ) {

        Column (modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
//            Toast.makeText(screenContext, "${transaction?.company} ${transaction?.income}", Toast.LENGTH_LONG).show()
//            Row {
//
//            }
            Text(text = transaction["company"].toString(),
                style = TextStyle(color = colorResource(id = R.color.black),
                    fontSize = 15.sp, fontWeight = FontWeight.Bold))
            Row {
                Text(text = "Initial: ",
                    style = TextStyle(color = colorResource(id = R.color.black),
                        fontSize = 15.sp))
                Text(text = transaction["initial"].toString(),
                    style = TextStyle(color = colorResource(id = R.color.black),
                        fontSize = 15.sp))
                Text(text = " | ",
                    style = TextStyle(color = colorResource(id = R.color.black),
                        fontSize = 15.sp))
                Text(text = "Current: ",
                    style = TextStyle(color = colorResource(id = R.color.black),
                        fontSize = 15.sp))
                Text(text = transaction["current"].toString(),
                    style = TextStyle(color = colorResource(id = R.color.black),
                        fontSize = 15.sp))
            }
        }
        Column {
            Button(
                onClick = { onEditClicked(transaction["company"].toString()) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.teal_200)
                ),
                modifier = Modifier
                    .fillMaxWidth() // Adjusts button width to match parent width
                    .height(48.dp)   // Adjusts button height
                    .padding(8.dp)   // Adjusts padding around the button
            ) {
                Text("Add", color = colorResource(id = R.color.black))
            }
            Button(
                onClick = { onDeleteClicked(transaction["company"].toString()) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.red)
                ),
                modifier = Modifier
                    .fillMaxWidth() // Adjusts button width to match parent width
                    .height(48.dp)   // Adjusts button height
                    .padding(8.dp)   // Adjusts padding around the button
            ) {
                Text("Delete", color = colorResource(id = R.color.black))
            }
        }
    }
}

@Composable
fun TransactionList(transactions:List<Map<String, Any?>>, onEditClicked: (String) -> Unit, onDeleteClicked: (String) -> Unit) {
    val screenContext = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
//            .height(5.dp)
        contentPadding = PaddingValues(8.dp)
    ) {
        item {
            for (transaction in transactions) {
//                Toast.makeText(screenContext, "${transaction?.income}", Toast.LENGTH_LONG).show()
                TransactionItem(
                    transaction = transaction,
                    onEditClicked = onEditClicked,
                    onDeleteClicked = onDeleteClicked
                )
            }
        }
//        items(transactions) { transaction ->
//            TransactionItem(transaction = transaction)
//        }
    }
}

private fun getTransactionsFromDatabase(onSuccess: (List<Map<String, Any?>>)->Boolean,
                                        onFailure: (Exception)->Unit)
{
//    val db = Firebase.firestore
//    db.collection("transactions").get()
//        .addOnSuccessListener { result -> onSuccess(result) }
//        .addOnFailureListener { result -> onFailure(result) }
    val db = Firebase.firestore
    val transactionsCollection = db.collection("transactions")

    transactionsCollection.addSnapshotListener { snapshot, exception ->
        if (exception != null) {
            onFailure(exception)
            return@addSnapshotListener
        }

        if (snapshot != null && !snapshot.isEmpty) {
            val companyDataMap = mutableMapOf<String?, TransactionSummary>()

            for (document in snapshot.documents) {
                val transaction:Transaction? = document.toObject(Transaction::class.java)
                if (transaction != null) {
                    val companyName = transaction.company
                    val income = transaction.income ?: 0.0
                    val expenses = transaction.expenses ?: 0.0
                    Log.i("$companyName Income:", "$income")
                    Log.i("$companyName Expense:", "$expenses")

                    // Check if details is "Initial" to determine if it's the initial transaction
                    if (transaction.details == "Initial") {
                        // Add initial value only if it's the first transaction for this company
                        val currentSummary = companyDataMap[companyName]
                        if (currentSummary == null) {
                            val initial = income
                            val summary = TransactionSummary(initial, income, expenses)
                            companyDataMap[companyName] = summary
                        }
                    } else {
                        // For transactions other than "Initial", update the sum of income and sum of expenses
                        val currentSummary = companyDataMap[companyName]
                        if (currentSummary != null) {
                            val sumOfIncome = currentSummary.sumOfIncome + income
                            val sumOfExpenses = currentSummary.sumOfExpenses + expenses
                            Log.i("Sum of $companyName Expense:", "$sumOfExpenses")
                            val summary = currentSummary.copy(
                                sumOfIncome = sumOfIncome,
                                sumOfExpenses = sumOfExpenses
                            )
                            companyDataMap[companyName] = summary
                        }
                    }
                }
            }

            val result = mutableListOf<Map<String, Any?>>()
            companyDataMap.forEach { (companyName, incomeExpensesList) ->
                val initial = incomeExpensesList.initial
                Log.i("Initial Income:", "$initial")
                val current = incomeExpensesList.sumOfIncome - incomeExpensesList.sumOfExpenses
                Log.i("Current:", "$current")
                result.add(mapOf("company" to companyName, "initial" to initial, "current" to current))
            }


            onSuccess(result)
        } else {
            onFailure(Exception("Snapshot is null or empty"))
        }
    }
}

private fun deleteTransactionsForSpecificBankFromDatabase(company: String)
{
    val db = Firebase.firestore
    val transactionsCollection = db.collection("transactions").whereEqualTo("company", company).get()

    transactionsCollection
        .addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                document.reference.delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Transaction document successfully deleted!")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error deleting transaction document: ${e.message}")
                    }
            }
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error getting transactions for deletion: ${e.message}")
        }
}

private fun getTransactionsForSpecificBankFromDatabase(view: View, tableLayout: TableLayout, company: String)
{
    val db = Firebase.firestore
    val transactionsCollection = db.collection("transactions").whereEqualTo("company", company)

    transactionsCollection.addSnapshotListener { snapshot, exception ->
        if (exception != null) {
//            onFailure(exception)
            Log.e(TAG, "Error getting transactions for $company: ${exception.message}")
            return@addSnapshotListener
        }
//        onSuccess(snapshot)
//        val result = mutableListOf<Map<String, Any?>>()

        snapshot?.documents?.forEach { document ->
            val transaction = document.toObject(Transaction::class.java)
            if (transaction != null) {
                val date = transaction.date?.toDate()
                    ?.let { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(it).toString() }
                val details = transaction.details.toString()
                val company = transaction.company.toString()
                val income = transaction.income.toString()
                val expenses = transaction.expenses.toString()

                val tableRow = LayoutInflater.from(view.context).inflate(R.layout.table_row, null) as TableRow
                tableRow.findViewById<TextView>(R.id.dateTextView).text = date
                tableRow.findViewById<TextView>(R.id.detailsTextView).text = details
                tableRow.findViewById<TextView>(R.id.companyTextView).text = company
                tableRow.findViewById<TextView>(R.id.incomeTextView).text = income
                tableRow.findViewById<TextView>(R.id.expensesTextView).text = expenses
//                result.add(transaction)

                tableLayout.addView(tableRow)
            } else {
                Log.i(TAG, Exception("Snapshot is null or empty").toString())
            }
        }
    }
}

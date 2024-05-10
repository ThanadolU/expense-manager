package org.classapp.expensemanager

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
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
import com.google.firebase.firestore.firestore
import org.classapp.expensemanager.ui.theme.ExpenseManagerTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@IgnoreExtraProperties
data class Transaction(
    val date: Timestamp? = null,
    val details: String? = "",
    val type: String? = "",
    val company: String? = "",
    val income: Double? = 0.00,
    val expenses: Double? = 0.00
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "date" to date,
            "details" to details,
            "type" to type,
            "company" to company,
            "income" to income,
            "expenses" to expenses
        )
    }
}

data class BankIcon(
    val name: String,
    val iconResourceId: Int
)

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
    private lateinit var imageArrowBtnSelectIcon: ImageButton

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
        addImageBtn = view.findViewById(R.id.addImageView)
        editTextName = view.findViewById(R.id.editTextName)
        editTextNumber = view.findViewById(R.id.editTextNumber)
        editTextDate = view.findViewById(R.id.editTextDate)
        addBankBtn = view.findViewById(R.id.addBankBtn)
        cancelBtn = view.findViewById(R.id.cancelBtn)
        selectBankIconCardView = view.findViewById(R.id.selectBankIconCardView)
        imageArrowBtnSelectIcon = view.findViewById(R.id.imageArrowBtnSelectIcon)

        homeScrollComposeView.setContent {
            HomeScreen()
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

        addImageBtn.setOnClickListener {
//            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//            val iconPickerDialog = IconPickerDialog.newInstance(bankIcons)
//            iconPickerDialog.show(supportFragmentManager, "IconPickerDialog")
            selectBankIconCardView.visibility = View.VISIBLE
        }

        imageArrowBtnSelectIcon.setOnClickListener{
            selectBankIconCardView.visibility = View.GONE
        }

        addBankBtn.setOnClickListener {
//            val name = editTextName.text.toString()
//            val initial = editTextNumber.text.toString().toDoubleOrNull()
//            val date = parseDate(editTextDate.text.toString())
//            saveToFirebase(name, initial, date)
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
            "details" to "Initial money of this $name",
            "type" to "Initial",
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
                // Handle error
            }
    }
}

@Composable
fun HomeScreen() {
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
            TransactionList(transactions = transactionList)
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
fun TransactionItem(transaction: Map<String, Any?>) {
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
            Row {

            }
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
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.orange)
                ),
                modifier = Modifier
                    .fillMaxWidth() // Adjusts button width to match parent width
                    .height(48.dp)   // Adjusts button height
                    .padding(8.dp)   // Adjusts padding around the button
            ) {
                Text("Edit", color = colorResource(id = R.color.black))
            }
            Button(
                onClick = { /*TODO*/ },
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
fun TransactionList(transactions:List<Map<String, Any?>>) {
    val screenContext = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
//            .height(5.dp)
        contentPadding = PaddingValues(8.dp)
    ) {
        item {
            for (transaction in transactions) {
//                Toast.makeText(screenContext, "${transaction?.income}", Toast.LENGTH_LONG).show()
                TransactionItem(transaction = transaction)
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
            val companyDataMap = mutableMapOf<String?, Pair<Double, Double>>()
//            val companyDataMap = mutableMapOf<String?, List<Double>>()


            for (document in snapshot.documents) {
                val transaction:Transaction? = document.toObject(Transaction::class.java)
//                val moneyList = mutableListOf<Double>()
                if (transaction != null) {
                    val companyName = transaction.company
                    val income = transaction.income ?: 0.0
                    val expenses = transaction.expenses ?: 0.0
//                    moneyList.add(0, income)
//                    moneyList.add(1, expenses)
                    companyDataMap.putIfAbsent(companyName, Pair(income, expenses))
//                    companyDataMap.putIfAbsent(companyName, moneyList)

                    val currentData = companyDataMap[companyName]
                    if (currentData != null) {
                        val currentIncome = currentData.first
                        val currentExpenses = currentData.second
//                        val currentIncome = currentData[0]
//                        val currentExpenses = currentData[1]
//                        moneyList.add(1, currentIncome + income)
//                        moneyList.add(2, currentExpenses + expenses)
//                        companyDataMap[companyName] = moneyList
                        companyDataMap[companyName] = Pair(currentIncome + income, currentExpenses + expenses)
                    }
                }
            }

            val result = mutableListOf<Map<String, Any?>>()
            companyDataMap.forEach { (companyName, incomeExpensesList) ->
                val initialIncome = incomeExpensesList.first
                val currentIncome = incomeExpensesList.first - incomeExpensesList.second
                result.add(mapOf("company" to companyName, "initial" to initialIncome, "current" to currentIncome))
            }


            onSuccess(result)
        } else {
            onFailure(Exception("Snapshot is null or empty"))
        }
    }
}

package com.example.personalfinanceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ExpenseTrackerApp(navController)
        }
    }
}

@Composable
fun ExpenseTrackerApp(navController: NavHostController) {
    NavHost(navController, startDestination = "expense_list") {
        composable("expense_list") {
            val viewModel: ExpenseViewModel = hiltViewModel()
            ExpenseScreen(viewModel, navController)
        }
        composable("add_expense") {
            val viewModel: ExpenseViewModel = hiltViewModel()
            AddExpenseScreen(viewModel, navController)
        }
    }
}

@Composable
fun ExpenseScreen(viewModel: ExpenseViewModel, navController: NavHostController) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_expense") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ExpenseList(expenses, onDelete = { viewModel.deleteExpense(it) })
            ExpensePieChart(expenses)
        }
    }
}

@Composable
fun AddExpenseScreen(viewModel: ExpenseViewModel, navController: NavHostController) {
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
        TextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })
        TextField(value = date, onValueChange = { date = it }, label = { Text("Date") })

        Button(onClick = {
            if (category.isNotEmpty() && amount.isNotEmpty() && date.isNotEmpty()) {
                viewModel.addExpense(Expense(category = category, amount = amount.toFloat(), date = date))
                navController.popBackStack()
            }
        }) {
            Text("Save Expense")
        }
    }
}

@Composable
fun ExpenseList(expenses: List<Expense>, onDelete: (Expense) -> Unit) {
    LazyColumn {
        items(expenses) { expense ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Category: ${expense.category}", fontWeight = FontWeight.Bold)
                    Text(text = "Amount: ${expense.amount}")
                    Text(text = "Date: ${expense.date}")
                    Button(onClick = { onDelete(expense) }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun ExpensePieChart(expenses: List<Expense>) {
    val categoryTotals = expenses.groupBy { it.category }
        .mapValues { (_, expenses) -> expenses.sumOf { it.amount.toDouble() } }

    val total = categoryTotals.values.sum()
    val angles = categoryTotals.mapValues { (_, amount) -> (amount / total * 360).toFloat() }

    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Cyan)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Expense Breakdown", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Canvas(
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            ) {
                var startAngle = 0f
                angles.entries.forEachIndexed { index, entry ->
                    drawArc(
                        color = colors[index % colors.size],
                        startAngle = startAngle,
                        sweepAngle = entry.value,
                        useCenter = true
                    )
                    startAngle += entry.value
                }
            }
        }
    }
}


fun getColorForCategory(category: String): Color {
    return when (category) {
        "Food" -> Color.Red
        "Transport" -> Color.Blue
        "Entertainment" -> Color.Green
        "Bills" -> Color.Yellow
        "Miscellaneous" -> Color.Gray
        else -> Color.Magenta
    }
}

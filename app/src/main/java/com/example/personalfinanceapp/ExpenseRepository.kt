package com.example.personalfinanceapp

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class ExpenseRepository (private val expenseDao: ExpenseDao) {
    val expenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
}

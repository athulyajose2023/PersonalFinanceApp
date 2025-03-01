package com.example.personalfinanceapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(private val repository: ExpenseRepository) : ViewModel() {
    val expenses = repository.expenses.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun addExpense(expense: Expense) {
        viewModelScope.launch { repository.addExpense(expense) }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }
}

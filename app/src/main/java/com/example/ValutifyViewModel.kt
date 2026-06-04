package com.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.util.Locale

// Data models for the business logic
data class Transaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val isIncome: Boolean,
    val date: String,
    val category: String = "General"
)

data class DonutSegment(
    val name: String,
    val percentage: Float,
    val amount: Double,
    val colorHex: Long
)

// UI State representing the current dashboard data
data class ValutifyUiState(
    // Bottom tab index: 0 = Home, 1 = Stats, 2 = Transactions
    val activeTab: Int = 0,
    val transactions: List<Transaction> = emptyList(),
    // Added Search and filter
    val searchQuery: String = "",
    val filterCategory: String = "All"
) {
    val totalBalance: Double
        get() = transactions.sumOf { if (it.isIncome) it.amount else -it.amount }

    val monthlyIncome: Double
        get() = transactions.filter { it.isIncome }.sumOf { it.amount }

    val monthlyExpense: Double
        get() = transactions.filter { !it.isIncome }.sumOf { it.amount }

    val filteredTransactions: List<Transaction>
        get() {
            var filtered = transactions
            if (filterCategory != "All") {
                filtered = filtered.filter { it.category.equals(filterCategory, ignoreCase = true) || (it.isIncome && filterCategory == "Income") || (!it.isIncome && filterCategory == "Expense") }
            }
            if (searchQuery.isNotBlank()) {
                filtered = filtered.filter { it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }
            }
            return filtered
        }

    val donutSegments: List<DonutSegment>
        get() {
            val expenseTx = transactions.filter { !it.isIncome }
            val totalExp = expenseTx.sumOf { it.amount }
            if (totalExp == 0.0) return emptyList()

            val byCategory = expenseTx.groupBy { it.category }
                .mapValues { it.value.sumOf { t -> t.amount } }
                .toList()
                .sortedByDescending { it.second }

            val colors = listOf(0xFF09291E, 0xFFC7F0A1, 0xFF3B82F6, 0xFFF59E0B, 0xFFF43F5E)
            return byCategory.mapIndexed { index, (name, amount) ->
                val percentage = ((amount / totalExp) * 100).toFloat()
                DonutSegment(
                    name = name,
                    percentage = percentage,
                    amount = amount,
                    colorHex = colors[index % colors.size]
                )
            }
        }
}

class ValutifyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ValutifyUiState())
    val uiState: StateFlow<ValutifyUiState> = _uiState.asStateFlow()

    fun setActiveTab(tabIndex: Int) {
        _uiState.update { it.copy(activeTab = tabIndex) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateFilterCategory(category: String) {
        _uiState.update { it.copy(filterCategory = category) }
    }

    fun addTransaction(title: String, amount: Double, isIncome: Boolean, category: String) {
        val uniqueId = "dynamic_t_${System.currentTimeMillis()}"
        val formattedDate = "Today ${String.format(Locale.getDefault(), "%02d:%02d", (10..22).random(), (10..59).random())}"
        
        val newTx = Transaction(
            id = uniqueId,
            title = title,
            subtitle = formattedDate,
            amount = amount,
            isIncome = isIncome,
            date = "Today",
            category = category
        )

        _uiState.update { currentState ->
            currentState.copy(
                transactions = listOf(newTx) + currentState.transactions
            )
        }
    }

    companion object {
        fun formatCurrency(value: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale.US)
            formatter.maximumFractionDigits = 0
            return formatter.format(value)
        }

        fun formatCurrencyWithCents(value: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale.US)
            formatter.maximumFractionDigits = 2
            return formatter.format(value)
        }
    }
}

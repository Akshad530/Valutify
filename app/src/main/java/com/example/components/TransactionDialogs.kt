package com.example.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.AccentPeach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onAddTransaction: (title: String, amount: Double, isIncome: Boolean, category: String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var categoryInput by remember { mutableStateOf("General") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add Transaction",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryIndigo
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Title / Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryIndigo,
                        unfocusedBorderColor = Color(0xFFDEE5E1)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryIndigo,
                        unfocusedBorderColor = Color(0xFFDEE5E1)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Transaction Type", fontWeight = FontWeight.Medium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFEFF5F2), RoundedCornerShape(10.dp))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isIncome) PrimaryIndigo else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { isIncome = true }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "Income",
                                color = if (isIncome) Color.White else Color(0xFF6A7F71),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (!isIncome) PrimaryIndigo else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { isIncome = false }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "Expense",
                                color = if (!isIncome) Color.White else Color(0xFF6A7F71),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = categoryInput,
                    onValueChange = { categoryInput = it },
                    label = { Text("Category (e.g. Entertainment, Transfer)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryIndigo,
                        unfocusedBorderColor = Color(0xFFDEE5E1)
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                val validationError = remember(nameInput, amountInput) {
                    when {
                        nameInput.isBlank() -> "Please enter a valid title"
                        amountInput.toDoubleOrNull() == null || amountInput.toDouble() <= 0.0 -> "Please enter a valid amount"
                        else -> null
                    }
                }

                if (validationError != null && (nameInput.isNotEmpty() || amountInput.isNotEmpty())) {
                    Text(
                        text = validationError,
                        color = AccentPeach,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF6A7F71))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountInput.toDoubleOrNull() ?: 0.0
                            if (nameInput.isNotBlank() && amt > 0) {
                                onAddTransaction(nameInput, amt, isIncome, categoryInput)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        shape = RoundedCornerShape(12.dp),
                        enabled = validationError == null
                    ) {
                        Text("Add Record", color = Color.White)
                    }
                }
            }
        }
    }
}

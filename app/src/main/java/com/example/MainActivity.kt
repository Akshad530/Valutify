package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.components.AddTransactionDialog
import com.example.components.DonutChart
import com.example.ui.theme.*
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ValutifyApp()
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ValutifyApp(viewModel: ValutifyViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("valutify_scaffold"),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column {
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.height(72.dp)
                ) {
                    val tabs = listOf(
                        TabItem("Home", Icons.Default.Home, 0, "home_tab"),
                        TabItem("Stats", Icons.Default.Star, 1, "analytics_tab"),
                        TabItem("Wallet", Icons.Default.List, 2, "wallet_tab")
                    )
                    
                    tabs.forEach { tab ->
                        val isSelected = uiState.activeTab == tab.index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setActiveTab(tab.index) },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    tint = if (isSelected) PrimaryIndigo else IconSlate.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) PrimaryIndigo else IconSlate.copy(alpha = 0.7f)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFFD8EFDD),
                                selectedIconColor = PrimaryIndigo,
                                selectedTextColor = PrimaryIndigo
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryIndigo,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .testTag("add_record_fab")
            ) {
                Icon(Icons.Default.Add, "Add transaction", modifier = Modifier.size(28.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { pad ->
        Column(modifier = Modifier.padding(pad).fillMaxSize()) {
            TopDashboardHeader()
            
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AnimatedContent(
                    targetState = uiState.activeTab,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width / 2 } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width / 2 } + fadeOut())
                        } else {
                            (slideInHorizontally { width -> -width / 2 } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width / 2 } + fadeOut())
                        }
                    },
                    label = "TabTransition"
                ) { tab ->
                    when (tab) {
                        0 -> HomeDashboardScreen(uiState)
                        1 -> StatsScreen(uiState)
                        2 -> TransactionsScreen(uiState, viewModel)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            onDismiss = { showAddDialog = false },
            onAddTransaction = { title, amt, isInc, cat ->
                viewModel.addTransaction(title, amt, isInc, cat)
            }
        )
    }
}

data class TabItem(val label: String, val icon: ImageVector, val index: Int, val testTag: String)

@Composable
fun TopDashboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "venzer.", fontSize = 16.sp, fontWeight = FontWeight.Black, color = PrimaryIndigo, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Welcome Back!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
            Text(text = "Here's your latest account overview", fontSize = 11.sp, color = SecondaryText)
        }
        
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF818CF8), Color(0xFFA855F7)))),
            contentAlignment = Alignment.Center
        ) {
            Text("AL", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
    }
}

@Composable
fun HomeDashboardScreen(uiState: ValutifyUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth().height(180.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryIndigo),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth().background(AccentGreen).padding(horizontal = 24.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Jon Snow", color = PrimaryIndigo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("PayPal", color = PrimaryIndigo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    Box(modifier = Modifier.weight(1.5f).fillMaxWidth().padding(24.dp)) {
                        Column(modifier = Modifier.align(Alignment.Center)) {
                            Text(ValutifyViewModel.formatCurrency(uiState.totalBalance), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
                            Text("Total Balance", color = Color(0xFFA1B0A6), fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Default.KeyboardArrowUp, null, tint = AccentGreen, modifier = Modifier.size(24.dp).background(Color(0xFFEEF8F1), CircleShape).padding(4.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(ValutifyViewModel.formatCurrency(uiState.monthlyIncome), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                        Text("Income", fontSize = 12.sp, color = SecondaryText)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Default.KeyboardArrowDown, null, tint = AccentPeach, modifier = Modifier.size(24.dp).background(Color(0xFFFFF1F2), CircleShape).padding(4.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(ValutifyViewModel.formatCurrency(uiState.monthlyExpense), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                        Text("Expenses", fontSize = 12.sp, color = SecondaryText)
                    }
                }
            }
        }

        item {
            Text("Recent Transactions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
        }

        items(uiState.transactions.take(3)) { tx -> TransactionItem(tx) }
    }
}

@Composable
fun StatsScreen(uiState: ValutifyUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Your Balance Overview", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                Text("Track spending, earnings, and insights", fontSize = 13.sp, color = SecondaryText)
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    DonutChart(segments = uiState.donutSegments, totalAmount = uiState.monthlyExpense, modifier = Modifier.padding(vertical = 12.dp))
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    uiState.donutSegments.forEach { segment ->
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).background(Color(segment.colorHex), CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(segment.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SecondaryText)
                                }
                                Text("${segment.percentage.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Black, color = PrimaryText)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            // Create a custom bar line since LinearProgressIndicator without animated progress works best here
                            Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(Color(0xFFEFF5F2))) {
                                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(segment.percentage / 100f).clip(CircleShape).background(Color(segment.colorHex)))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(uiState: ValutifyUiState, viewModel: ValutifyViewModel) {
    val filters = listOf("All", "Income", "Expense")
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Transactions History", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
        }

        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search transactions...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp), tint = IconSlate) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryIndigo,
                    unfocusedBorderColor = BorderLight,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { filter ->
                    val isSel = uiState.filterCategory == filter
                    FilterChip(
                        selected = isSel,
                        onClick = { viewModel.updateFilterCategory(filter) },
                        label = { Text(filter, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryIndigo,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        if (uiState.filteredTransactions.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No transactions found", color = SecondaryText)
                }
            }
        } else {
            items(uiState.filteredTransactions) { tx -> TransactionItem(tx) }
        }
    }
}

@Composable
fun TransactionItem(tx: Transaction) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(if (tx.isIncome) Color(0xFFE8F3EE) else Color(0xFFFFF1F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (tx.isIncome) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = if (tx.isIncome) AccentGreen else AccentPeach,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = tx.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Text(text = "${tx.category} • ${tx.subtitle}", fontSize = 11.sp, color = SecondaryText)
                }
            }
            Text(
                text = "${if (tx.isIncome) "+" else "-"}${ValutifyViewModel.formatCurrencyWithCents(tx.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (tx.isIncome) AccentGreen else PrimaryText
            )
        }
    }
}

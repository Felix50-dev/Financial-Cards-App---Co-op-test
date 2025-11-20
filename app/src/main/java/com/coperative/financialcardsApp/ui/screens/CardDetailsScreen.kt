package com.coperative.financialcardsApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.coperative.financialcardsApp.domain.model.Card
import com.coperative.financialcardsApp.domain.model.Transaction
import com.coperative.financialcardsApp.ui.screens.viewmodel.CardViewModel

@Composable
fun CardDetailsScreen(
    card: Card,
    navController: NavHostController,
    viewModel: CardViewModel
) {
    // Track blocked state locally (sync with repo)
    var isBlocked by remember { mutableStateOf(card.isBlocked) }

    // Collect transactions from the ViewModel
    val transactionsState = viewModel.transactions.collectAsState().value

    // Fetch transactions when screen is displayed
    LaunchedEffect(card.id) {
        viewModel.fetchTransactions(card.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("${card.holderName} - ${card.number}", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Display the card itself
        CardItem(card = card, onClick = {})

        Spacer(modifier = Modifier.height(16.dp))

        Text("Balance: ${card.getDisplayBalance()}", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { /* New Card logic */ }) { Text("New Card") }
            Button(onClick = { /* Deposit logic */ }) { Text("Deposit") }
            Button(onClick = { /* Withdraw logic */ }) { Text("Withdraw") }
            Button(onClick = {
                isBlocked = !isBlocked
                viewModel.toggleCardBlock(card.id, isBlocked)
            }) {
                Text(if (isBlocked) "Unblock Card" else "Block Card")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Recent Transactions", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            when (transactionsState) {
                is com.coperative.financialcardsApp.common.Resource.Loading -> {
                    item {
                        Text("Loading transactions...")
                    }
                }
                is com.coperative.financialcardsApp.common.Resource.Error -> {
                    item {
                        Text("Error: ${transactionsState.message}")
                    }
                }
                is com.coperative.financialcardsApp.common.Resource.Success -> {
                    items(transactionsState.data) { transaction ->
                        TransactionRow(transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionRow(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.description)
            Text(transaction.date, fontSize = 12.sp)
        }
        Text("${transaction.amount} ${transaction.currency}", fontSize = 14.sp)
    }
}

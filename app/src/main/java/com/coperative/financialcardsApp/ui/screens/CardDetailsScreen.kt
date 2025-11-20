// File: CardDetailsScreen.kt
package com.coperative.financialcardsApp.ui.screens // Assuming a package name

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage // Requires coil dependency: implementation("io.coil-kt:coil-compose:2.4.0")

@Composable
fun CardDetailsScreen(card: Card, navController: NavHostController) {
    var isBlocked by remember { mutableStateOf(false) } // Local storage simulation

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("${card.type} Details", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        CardItem(card, onClick = {}) // Display the card again
        Spacer(modifier = Modifier.height(16.dp))
        if (card.balance != null) {
            Text("Balance: ${card.balance}", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { /* New Card logic */ }) { Text("New Card") }
            Button(onClick = { /* Deposit logic */ }) { Text("Deposit") }
            Button(onClick = { /* Withdraw logic */ }) { Text("Withdraw") }
            Button(onClick = { isBlocked = !isBlocked }) {
                Text(if (isBlocked) "Unblock Card" else "Block Card")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Recent Transactions", fontSize = 20.sp)
        sampleTransactions.take(10).forEach { transaction ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon if available
                if (transaction.iconUrl != null) {
                    AsyncImage(
                        model = transaction.iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                    Text(transaction.description)
                    Text(transaction.date, fontSize = 12.sp)
                }
                Text(transaction.amount, textAlign = TextAlign.End)
            }
        }
    }
}
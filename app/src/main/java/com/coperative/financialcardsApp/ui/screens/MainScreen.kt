// File: MyCardsScreen.kt
package com.example.cardsapp // Assuming a package name

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.coperative.financialcardsApp.domain.model.Card

@Composable
fun MyCardsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Cards", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleCards) { card ->
                CardItem(card) {
                    navController.navigate("card_details/${card.type}")
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.navigate("profile") }) {
            Text("View Profile")
        }
    }
}

@Composable
fun CardItem(card: Card, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(card.color)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(card.type, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(card.status, color = Color.White, fontSize = 14.sp)
            Text(card.maskedNumber, color = Color.White, fontSize = 18.sp)
            Text(card.holderName, color = Color.White, fontSize = 16.sp)
            if (card.balance != null) {
                Text(card.balance, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            } else if (card.limit != null) {
                Text("Limit: ${card.limit}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Text("VISA", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
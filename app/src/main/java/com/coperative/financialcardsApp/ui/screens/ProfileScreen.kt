//package com.coperative.financialcardsApp.ui.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import com.coperative.financialcardsApp.domain.model.Card
//import com.coperative.financialcardsApp.ui.screens.viewmodel.CardViewModel
//
//@Composable
//fun ProfileScreen(
//    navController: NavHostController,
//    viewModel: CardViewModel
//) {
//    val cardsState = viewModel.cards.collectAsState().value
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("My Cards", fontSize = 24.sp)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        when (cardsState) {
//            is com.coperative.financialcardsApp.common.Resource.Loading -> {
//                Text("Loading cards...")
//            }
//            is com.coperative.financialcardsApp.common.Resource.Error -> {
//                Text("Error: ${cardsState.message}")
//            }
//            is com.coperative.financialcardsApp.common.Resource.Success -> {
//                LazyRow(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    items(cardsState.data) { card ->
//                        CardItem(card) {
//                            navController.navigate("card_details/${card.id}")
//                        }
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Button(onClick = { navController.navigate("profile") }) {
//            Text("View Profile")
//        }
//    }
//}
//
//@Composable
//fun CardItem(card: Card, onClick: () -> Unit) {
//    // You can assign colors based on card type if you want
//    val cardColor = when (card) {
//        is Card.Prepaid -> Color(0xFF4CAF50)
//        is Card.Credit -> Color(0xFF2196F3)
//        is Card.Debit -> Color(0xFFFF9800)
//        is Card.MultiCurrency -> Color(0xFF9C27B0)
//    }
//
//    Box(
//        modifier = Modifier
//            .width(300.dp)
//            .height(180.dp)
//            .clip(RoundedCornerShape(16.dp))
//            .background(cardColor)
//            .clickable { onClick() }
//            .padding(16.dp)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(card.number, color = Color.White, fontSize = 16.sp)
//            Text(card.holderName, color = Color.White, fontSize = 14.sp)
//            Text(card.getDisplayBalance(), color = Color.White, fontSize = 16.sp)
//            Row(
//                horizontalArrangement = Arrangement.End,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("VISA", color = Color.White, fontSize = 16.sp)
//            }
//        }
//    }
//}

package com.coperative.financialcardsApp.ui.screens

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.coperative.financialcardsApp.common.Resource
import com.coperative.financialcardsApp.domain.model.Card
import com.coperative.financialcardsApp.ui.screens.viewmodel.CardViewModel

private const val TAG = "MyCardsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCardsScreen(
    navController: NavHostController,
    viewModel: CardViewModel
) {
    val cardsState = viewModel.cards.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hello Wanjiru",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { navController.navigate("profile") },
                        tint = Color.Black
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            when (cardsState) {
                is Resource.Loading -> CircularProgressIndicator()
                is Resource.Error -> Text("Error: ${cardsState.message}", color = Color.Red)
                is Resource.Success -> {
                    if (cardsState.data.isEmpty()) {
                        Text("No cards available")
                    } else {
                        Log.d(TAG, "MyCardsScreen: cards are: ${cardsState.data}")

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(cardsState.data) { card ->
                                CardItem(
                                    card = card,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                ) {
                                    navController.navigate("card_details/${card.id}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardItem(card: Card, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val cardColor = when (card) {
        is Card.Prepaid -> Color(0xFF3E92CC)
        is Card.Credit -> Color(0xFFEC6C5C)
        is Card.Debit -> Color(0xFF4CAF50)
        is Card.MultiCurrency -> Color(0xFF9C27B0)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                card.maskedNumber(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                card.holderName,
                color = Color.White,
                fontSize = 14.sp
            )
            Text(
                card.getDisplayBalance(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "VISA",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

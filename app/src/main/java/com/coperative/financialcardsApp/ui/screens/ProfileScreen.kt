import com.coperative.financialcardsApp.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.coperative.financialcardsApp.common.Resource
import com.coperative.financialcardsApp.ui.screens.viewmodel.UserViewModel

@Composable
fun UserProfileScreen(userViewModel: UserViewModel) {
    val userState = userViewModel.user.collectAsState().value

    when (userState) {
        is Resource.Loading -> {
            Text("Loading...", modifier = Modifier.padding(16.dp))
        }

        is Resource.Error -> {
            Text("Error: ${userState.message}", modifier = Modifier.padding(16.dp))
        }

        is Resource.Success -> {
            val user = userState.data

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Profile image
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    ) {
                        AsyncImage(
                            model = user.avatarUrl,
                            contentDescription = "${user.name} avatar",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(60.dp),
                            placeholder = painterResource(id = R.drawable.ic_person),
                            error = painterResource(id = R.drawable.ic_person),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Profile Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF004C3F) // deep green like design
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ----- White Card Container -----
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {

                    Column(modifier = Modifier.padding(20.dp)) {

                        ProfileItem(label = "Name", value = user.name)
                        ProfileItem(label = "Email Address", value = user.email)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Address",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ProfileItem(label = "Street", value = user.address)
                        ProfileItem(label = "City", value = user.city!!)
                        ProfileItem(label = "Country", value = user.country!!)
                        ProfileItem(label = "Postal Code", value = user.postalCode!!)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF006344), // green value text like mockup
            fontWeight = FontWeight.Bold
        )
    }
}

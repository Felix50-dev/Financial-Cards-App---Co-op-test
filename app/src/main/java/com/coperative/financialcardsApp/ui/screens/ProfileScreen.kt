import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coperative.financialcardsApp.common.Resource
import com.coperative.financialcardsApp.ui.screens.viewmodel.UserViewModel

@Composable
fun UserProfileScreen(userViewModel: UserViewModel) {
    val userState = userViewModel.user.collectAsState().value

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when (userState) {
            is Resource.Loading -> Text("Loading user...")
            is Resource.Error -> Text("Error: ${userState.message}")
            is Resource.Success -> {
                val user = userState.data
                Text("Name: ${user.name}")
                Text("Email: ${user.email}")
                Text("Phone: ${user.phone}")
                Text("Address: ${user.address}")
            }
        }
    }
}

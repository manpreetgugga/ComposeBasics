package com.mobile.composebasic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobile.composebasic.ui.theme.ComposeBasicTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeBasicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UsersList()
                }
            }
        }
    }
}

data class User(var data: ArrayList<UserDetail> = arrayListOf())

data class UserDetail(
    var id: Int = -1,
    var email: String = "",
    @SerializedName("first_name") var firstName: String = "",
    @SerializedName("lastName") var lastName: String = "",
    var avatar: String = ""
)

interface ApiService {
    @GET("api/users")
    suspend fun getItems(): User
}

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://reqres.in")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService: ApiService = retrofit.create(ApiService::class.java)

@Composable
fun UsersList() {
    var items by remember { mutableStateOf(arrayListOf<UserDetail>()) }

    LaunchedEffect(true) {
        items = withContext(Dispatchers.IO) {
            apiService.getItems().data
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (items.isEmpty()) {
            Text(text = "Loading...")
        } else {
            LazyColumn {
                items(items) { item ->
                    UserItem(item)
                }
            }
        }
    }
}

@Composable
fun UserItem(userDetail: UserDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        ) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = userDetail.avatar)
                    .apply(block = fun ImageRequest.Builder.() {
                        crossfade(true)
                    }).build()
            )

            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painter,
                contentScale = ContentScale.Crop,
                contentDescription = "Profile Picture"
            )

            if (painter.state is AsyncImagePainter.State.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${userDetail.firstName} ${userDetail.lastName}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = userDetail.email)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeBasicTheme {
        UsersList()
    }
}
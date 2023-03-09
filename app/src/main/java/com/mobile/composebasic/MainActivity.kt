package com.mobile.composebasic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobile.composebasic.ui.theme.ComposeBasicTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.Color
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
                    val tabTitles = listOf("Tab 1", "Tab 2", "Tab 3")
                    var selectedTabIndex by remember { mutableStateOf(0) }

                    Column {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            backgroundColor = Color.Cyan,
                            contentColor = Color.Magenta
                        ) {
                            tabTitles.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = { Text(title) }
                                )
                            }
                        }
                        when (selectedTabIndex) {
                            0 -> {
                                UsersList()
                            }
                            1 -> {
                            }
                            2 -> {
                            }
                        }
                    }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UsersList() {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    var items by remember { mutableStateOf(arrayListOf<UserDetail>()) }
    var isSheetExpanded by remember { mutableStateOf(0) }
    var userSelected by remember { mutableStateOf(UserDetail()) }

    LaunchedEffect(isSheetExpanded) {
        items = withContext(Dispatchers.IO) {
            apiService.getItems().data
        }
        if (isSheetExpanded > 0) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(16.dp),
        sheetContent = { // Content of the bottom sheet
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .background(Color.LightGray),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SheetContent(userSelected)
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), contentAlignment = Alignment.Center
        ) {
            if (items.isEmpty()) {
                Text(text = "Loading...")
            } else {
                LazyColumn {
                    items(items) { item ->
                        UserItem(item, Modifier.clickable {
                            userSelected = item
                             if(!bottomSheetScaffoldState.bottomSheetState.isExpanded){
                                 isSheetExpanded++
                             }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(userDetail: UserDetail, clickable: Modifier) {
    Column(
        modifier = clickable
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

@Composable
fun SheetContent(userDetail: UserDetail) {
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
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent eget nibh sit amet libero laoreet eleifend. Nam pharetra malesuada faucibus. Donec quis massa vel ipsum tincidunt interdum. Sed non faucibus eros. Phasellus eget sapien ut lacus efficitur vestibulum vel id quam. Nam vitae eleifend quam. Vestibulum feugiat felis in libero facilisis, ut efficitur massa dapibus. Donec pellentesque, velit ut interdum imperdiet, mi dolor consectetur orci, a posuere eros ante sit amet lorem. Nam vel velit euismod, interdum nibh quis, fermentum mi. Sed laoreet, libero vel malesuada fringilla",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


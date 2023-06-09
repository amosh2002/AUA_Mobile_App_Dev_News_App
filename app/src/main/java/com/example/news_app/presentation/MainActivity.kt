package com.example.news_app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.news_app.R
import com.example.news_app.domain.NewsItemModel
import com.example.news_app.data.Status


// Activity
class MainActivity : ComponentActivity() {
    private val dataLoaderViewModel: DataLoaderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataLoaderViewModel.loadNews()

        setContent {
            MyApp {
                Column {
                    TopNavBar()
                    NewsList(dataLoaderViewModel = dataLoaderViewModel)
                }
            }
        }
    }
}

// Composable Functions
/**
 * Top navigation bar with search functionality and a configure button.
 */
@Composable
fun TopNavBar() {
    val searchText = remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "News",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h5
            )

            Row(
                modifier = Modifier.fillMaxWidth(0.7f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = searchText.value,
                    onValueChange = { newText -> searchText.value = newText },
                    label = { Text("Search") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(48.dp)
                )

                Button(
                    onClick = { /* TODO: Configure button action */ },
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                        .padding(start = 5.dp)
                ) {
                    Text(text = "F")
                }
            }
        }
    }
}


/**
 * NewsList displays a list of news items fetched by the DataLoaderViewModel.
 */
@Composable
fun NewsList(dataLoaderViewModel: DataLoaderViewModel) {
    val newsList by dataLoaderViewModel.newsList.observeAsState(emptyList())
    val status by dataLoaderViewModel.status.observeAsState(Status.LOADING)

    Box(modifier = Modifier.fillMaxSize()) {
        when (status) {
            Status.LOADING -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Status.OK -> {
                LazyColumn {
                    items(newsList) { newsItem ->
                        NewsCard(newsItem)
                    }
                }
            }
            Status.ERROR -> {
                Text(
                    text = "Something went wrong",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * NewsCard displays a single news item with an image, title, and source.
 */
@Composable
fun NewsCard(newsItem: NewsItemModel) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            AsyncImage(
                model = newsItem.imageUrl,
                contentDescription = newsItem.title,
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                modifier = Modifier
                    .height(80.dp)
                    .width(150.dp)
                    .padding(end = 10.dp)
            )
            Column {
                Text(text = newsItem.title)
                Text(
                    text = newsItem.source,
                    modifier = Modifier.padding(top = 3.dp),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}

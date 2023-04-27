package com.example.news_app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.news_app.R
import com.example.news_app.domain.NewsItemModel
import com.example.news_app.data.Status


// Activity
class MainActivity : ComponentActivity(), OnNewsItemSelectedListener {
    private val dataLoaderViewModel: DataLoaderViewModel by viewModels()
    private var showNewsDetails = mutableStateOf(false)
    private var searchClicked = mutableStateOf(false)
    private var selectedNewsItem = mutableStateOf<NewsItemModel?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataLoaderViewModel.loadNews()

        val listener = this
        setContent {
            MyApp {
                if (showNewsDetails.value && selectedNewsItem.value != null) {
                    BackHandler {
                        showNewsDetails.value = false
                    }
                    Column {
                        TopNavBar(dataLoaderViewModel, showNewsDetails, searchClicked)
                        NewsDetails(selectedNewsItem.value!!)
                    }
                } else {
                    Column {
                        TopNavBar(dataLoaderViewModel, showNewsDetails, searchClicked)
                        NewsList(dataLoaderViewModel = dataLoaderViewModel, listener = listener)
                    }
                }
            }
        }
    }

    override fun onNewsItemSelected(newsItem: NewsItemModel) {
        selectedNewsItem.value = newsItem
        showNewsDetails.value = true
    }
}

// Composable Functions
/**
 * Top navigation bar with search functionality and a configure button.
 */
@Composable
fun TopNavBar(
    dataLoaderViewModel: DataLoaderViewModel,
    showNewsDetails: MutableState<Boolean>,
    searchClicked: MutableState<Boolean>
) {
    val searchText = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current


    val onFilterSelectedListener = object : OnFilterSelectedListener {
        override fun onFilterSelected(category: String, searchText: String) {
            dataLoaderViewModel.loadNews(category = category, searchQuery = searchText)
        }
    }

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
            if (showNewsDetails.value) {
                IconButton(
                    onClick = { showNewsDetails.value = false }
                ) {
                    Text(text = "Back")
                }
            } else {
                Box(
                    modifier = Modifier.clickable {
                        focusManager.clearFocus()
                        searchClicked.value = false
                        searchText.value = ""
                        dataLoaderViewModel.loadNews()
                    }
                ) {
                    Text(
                        text = "News",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.h5
                    )
                }

                OutlinedTextField(
                    value = searchText.value,
                    onValueChange = { newText -> searchText.value = newText },
                    label = { Text("Search") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight()
                        .padding(start = 5.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                searchClicked.value = true
                            }
                        },
                    trailingIcon = {
                        if (searchText.value.isNotEmpty()) {
                            IconButton(onClick = {
                                searchText.value = ""
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear search field"
                                )
                            }
                        }
                    }
                )
                if (!searchClicked.value) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterButton(
                            onFilterSelectedListener = onFilterSelectedListener,
                            searchText
                        )

                    }
                } else {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            searchClicked.value = false
                            dataLoaderViewModel.loadNews(searchQuery = searchText.value)
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .width(48.dp)
                            .padding(start = 5.dp),
                        enabled = searchText.value != ""
                    ) {
                        Text(text = "S")
                    }
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            searchClicked.value = false
                            searchText.value = ""
                            dataLoaderViewModel.loadNews()
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .width(48.dp)
                            .padding(start = 5.dp)
                    ) {
                        Text(text = "X")
                    }

                }
            }
        }
    }
}

@Composable
fun FilterButton(
    onFilterSelectedListener: OnFilterSelectedListener,
    searchText: MutableState<String>
) {
    val showMenu = remember { mutableStateOf(false) }
    val selectedCategory = remember { mutableStateOf("") }

    Button(
        onClick = { showMenu.value = !showMenu.value },
        modifier = Modifier
            .height(48.dp)
            .width(48.dp)
            .padding(start = 5.dp)
    ) {
        Text(text = "F")
    }

    DropdownMenu(
        expanded = showMenu.value,
        onDismissRequest = {
            showMenu.value = false
            selectedCategory.value = ""
        }
    ) {
        DropdownMenuItem(
            onClick = {
                selectedCategory.value = "business"

            }) {
            Text(
                "Business",
                color = if (selectedCategory.value == "business") Color.Magenta else Color.Black
            )
        }
        DropdownMenuItem(
            onClick = {
                selectedCategory.value = "entertainment"
            }) {
            Text(
                "Entertainment",
                color = if (selectedCategory.value == "entertainment") Color.Magenta else Color.Black
            )
        }
        DropdownMenuItem(
            onClick = {
                selectedCategory.value = "general"
            }) {
            Text(
                "General",
                color = if (selectedCategory.value == "general") Color.Magenta else Color.Black
            )
        }
        DropdownMenuItem(
            onClick = {
                selectedCategory.value = "health"
            }) {
            Text(
                "Health",
                color = if (selectedCategory.value == "health") Color.Magenta else Color.Black
            )
        }
        DropdownMenuItem(
            enabled = selectedCategory.value != "",
            onClick = {
                onFilterSelectedListener.onFilterSelected(
                    category = selectedCategory.value,
                    searchText = searchText.value
                )
                selectedCategory.value = ""
                showMenu.value = false
            },
            modifier = Modifier.background(
                color =
                if (selectedCategory.value != "") Color.Magenta else Color.LightGray
            ),
        ) {
            Text(
                text = "Apply",
                color = if (selectedCategory.value != "") Color.Black else Color.Gray
            )
        }
    }
}


/**
 * NewsList displays a list of news items fetched by the DataLoaderViewModel.
 */
@Composable
fun NewsList(dataLoaderViewModel: DataLoaderViewModel, listener: OnNewsItemSelectedListener) {
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
                        NewsCard(newsItem, listener)
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
fun NewsCard(newsItem: NewsItemModel, listener: OnNewsItemSelectedListener) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable { listener.onNewsItemSelected(newsItem) }
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
fun NewsDetails(newsItem: NewsItemModel) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(text = newsItem.title, style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Source: ${newsItem.source}",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        AsyncImage(
            model = newsItem.imageUrl,
            contentDescription = newsItem.title,
            placeholder = painterResource(id = R.drawable.ic_launcher_background),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = newsItem.description,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Light
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Author: ${newsItem.author}",
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold
        )
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

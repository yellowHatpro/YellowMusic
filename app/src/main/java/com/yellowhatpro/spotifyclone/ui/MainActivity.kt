package com.yellowhatpro.spotifyclone.ui


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.RequestManager
import com.yellowhatpro.spotifyclone.theme.SpotifyCloneTheme
import com.yellowhatpro.spotifyclone.ui.viewmodel.MainViewModel
import com.yellowhatpro.spotifyclone.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var glide:RequestManager
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotifyCloneTheme {
              Surface(
                  modifier = Modifier.fillMaxSize(),
                  color = MaterialTheme.colorScheme.background
              ) {
                  ShowAlbums()

              }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun ShowAlbums() {
    val viewModel = hiltViewModel<MainViewModel>()
    val songList = viewModel.mediaItems.collectAsState().value
    when (songList.status){
            Status.LOADING -> {
                Text(text = "ff")
            }
        else   -> Text(text = "Ddddd")
    }
    Scaffold {
        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {

            songList.data?.let { it1 ->
                items(it1.size) {
                    Card(
                        modifier = Modifier
                            .height(200.dp)
                            .width(180.dp)
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Column {
                            Text(text = songList.data[it].title)
                            Text(text = songList.data[it].artist)
                            //Text(text = albumList[it].albumId.toString())
                        }

                    }
                }
            }
        }
    }
}



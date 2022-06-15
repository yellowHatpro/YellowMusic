package com.yellowhatpro.yellowmusic.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.RequestManager
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.yellowhatpro.yellowmusic.data.entities.Song
import com.yellowhatpro.yellowmusic.theme.SpotifyCloneTheme
import com.yellowhatpro.yellowmusic.ui.MainActivity.Companion.crntPlayingSong
import com.yellowhatpro.yellowmusic.ui.viewmodel.MainViewModel
import com.yellowhatpro.yellowmusic.utils.Extensions.toSong
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var glide: RequestManager

    companion object {
        var crntPlayingSong: Song? = null
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotifyCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = hiltViewModel<MainViewModel>()
                    ShowAlbums(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@ExperimentalMaterial3Api
@Composable
fun ShowAlbums(viewModel: MainViewModel) {
    val songList = viewModel.mediaItems.collectAsState().value
    val currentPlayingSong = viewModel.currentlyPlayingSong.collectAsState().value
    crntPlayingSong = currentPlayingSong.toSong
    val pageState = rememberPagerState()
    val scope = rememberCoroutineScope()


    Scaffold(bottomBar = {
        songList.data?.let {

            HorizontalPager(count = it.size, state = pageState) { page ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = it[page].title)
                }
            }
        LaunchedEffect(pageState) {
            snapshotFlow { pageState.currentPage }
                .distinctUntilChanged()
                .collect { page->

                if (page>songList.data.indexOf(crntPlayingSong)) {
                    viewModel.skipToNextSong()
                    viewModel.playOrToggleSong(songList.data[page])
                }
                    else if (page<songList.data.indexOf(crntPlayingSong)) {
                    viewModel.skipToPrevious()
                    viewModel.playOrToggleSong(songList.data[page])
                }

                }
        }
        }
    }) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            modifier = Modifier.padding(innerPadding)
        ) {

            songList.data?.let { it1 ->
                items(it1.size) {
                    Card(
                        modifier = Modifier
                            .height(200.dp)
                            .width(180.dp)
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.medium,
                        onClick = {
                            viewModel.playOrToggleSong(songList.data[it])
                            crntPlayingSong = songList.data[it]
                            scope.launch {
                                if (currentPlayingSong == null) pageState.animateScrollToPage(0)
                                else pageState.animateScrollToPage(
                                    songList.data.indexOf(
                                        crntPlayingSong
                                    )
                                )
                            }
                        }
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



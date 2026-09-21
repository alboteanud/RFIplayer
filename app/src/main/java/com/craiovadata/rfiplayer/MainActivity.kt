package com.craiovadata.rfiplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.craiovadata.rfiplayer.ui.components.NowPlayingBar
import com.craiovadata.rfiplayer.ui.components.StationButton
import com.craiovadata.rfiplayer.ui.theme.RFIplayerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            viewModel.autoPlayDefaultStationIfIdle()
        }

        setContent {
            val context = LocalContext.current
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
            ) { /* permission result handled by system */ }

            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS,
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            val playerState by viewModel.uiState.collectAsState()

            RFIplayerTheme {
                MainScreen(
                    stations = viewModel.stations,
                    playerState = playerState,
                    onPlay = { station -> viewModel.play(station) },
                    onPlayPause = { viewModel.togglePlayPause() },
                    onStop = { viewModel.stop() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    stations: List<RadioStation>,
    playerState: PlayerUiState,
    onPlay: (RadioStation) -> Unit,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                stations.forEach { station ->
                    val isSelected = playerState.currentMediaUri?.toString() == station.url
                    StationButton(
                        text = stringResource(station.nameResId),
                        isSelected = isSelected,
                        isPlaying = isSelected && playerState.isPlaying,
                        isLoading = isSelected && playerState.isLoading,
                        onClick = { onPlay(station) },
                    )
                }

                // Error Message Display
                playerState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }

            // "Now Playing" Bar Overlay
            AnimatedVisibility(
                visible = playerState.currentMediaUri != null,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                NowPlayingBar(
                    playerState = playerState,
                    onPlayPauseClick = onPlayPause,
                    onStopClick = onStop,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val previewStations = listOf(
        RadioStation(R.string.rfi, "1"),
        RadioStation(R.string.inter, "2"),
        RadioStation(R.string.itzy_bitzy, "3"),
    )
    RFIplayerTheme {
        MainScreen(
            stations = previewStations,
            playerState = PlayerUiState(
                isPlaying = true,
                isLoading = false,
                currentMediaUri = null,
                title = "RFI Romania",
                subtitle = "Live Stream",
            ),
            onPlay = {},
            onPlayPause = {},
            onStop = {},
        )
    }
}

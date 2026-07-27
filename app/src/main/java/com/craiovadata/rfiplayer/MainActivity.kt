package com.craiovadata.rfiplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.craiovadata.rfiplayer.ui.components.NowPlayingBar
import com.craiovadata.rfiplayer.ui.components.StationButton
import com.craiovadata.rfiplayer.ui.theme.RFIplayerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PlayerViewModel by viewModels()

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), /* requestCode= */ 0)
        }

        setContent {
            val playerState = rememberPlayerState(viewModel.controllerFuture)
            RFIplayerTheme {
                MainScreen(
                    stations = viewModel.stations,
                    playerState = playerState,
                    onPlay = { station -> viewModel.play(station) },
                    onStop = { viewModel.stop() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun MainScreen(
    stations: List<RadioStation>,
    playerState: PlayerState,
    onPlay: (RadioStation) -> Unit,
    onStop: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            if (playerState.currentMediaUri != null) {
                NowPlayingBar(
                    playerState = playerState,
                    onStop = onStop
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(1f))

                stations.forEach { station ->
                    val isSelected = playerState.currentMediaUri?.toString() == station.url
                    StationButton(
                        text = stringResource(station.nameResId),
                        isSelected = isSelected,
                        onClick = { onPlay(station) }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Error Message Area
                playerState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 16.dp)
                    )
                } ?: Spacer(modifier = Modifier.height(40.dp))

                Spacer(modifier = Modifier.weight(0.5f))
            }
        }
    }
}

@UnstableApi
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val previewStations = listOf(
        RadioStation(R.string.rfi, "1"),
        RadioStation(R.string.inter, "2"),
        RadioStation(R.string.itzy_bitzy, "3")
    )
    RFIplayerTheme {
        MainScreen(
            previewStations,
            PlayerState(null, true, false, null, "RFI", "Live News"),
            {},
            {}
        )
    }
}

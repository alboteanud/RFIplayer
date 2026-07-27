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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.material3.buttons.PlayPauseButton
import com.craiovadata.rfiplayer.ui.theme.RFIplayerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PlayerViewModel by viewModels()

    private val stations = listOf(
        RadioStation(R.string.rfi, "http://asculta.rfi.ro:9128/live.mp3"),
        RadioStation(R.string.inter, "http://icecast.radiofrance.fr/franceinter-hifi.aac"),
        RadioStation(R.string.itzy_bitzy, "http://live.itsybitsy.ro:8000/itsybitsy")
    )

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
                    stations = stations,
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
@Composable
fun NowPlayingBar(
    playerState: PlayerState,
    onStop: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playerState.title ?: "Radio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (playerState.subtitle != null) {
                    Text(
                        text = playerState.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = "Live Stream",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (playerState.player != null) {
                    PlayPauseButton(
                        player = playerState.player,
                        modifier = Modifier.size(56.dp) // Increased size
                    )
                }
                
                Spacer(Modifier.width(12.dp))

                IconButton(
                    onClick = onStop,
                    modifier = Modifier.size(56.dp) // Larger stop button
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_stop),
                        contentDescription = "Stop",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp) // Larger icon
                    )
                }
            }
        }
    }
}

@Composable
fun StationButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .width(160.dp) // Slightly wider
            .height(75.dp),
        shape = MaterialTheme.shapes.medium,
        colors = if (isSelected) {
            ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            ButtonDefaults.elevatedButtonColors()
        }
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
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

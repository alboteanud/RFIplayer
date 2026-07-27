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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.craiovadata.rfiplayer.ui.theme.RFIplayerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PlayerViewModel by viewModels()

    private val stations = listOf(
        RadioStation(R.string.rfi, "http://asculta.rfi.ro:9128/live.mp3"),
        RadioStation(R.string.inter, "http://icecast.radiofrance.fr/franceinter-hifi.aac"),
        RadioStation(R.string.itzy_bitzy, "http://live.itsybitsy.ro:8000/itsybitsy")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), /* requestCode= */ 0)
        }

        setContent {
            RFIplayerTheme {
                MainScreen(
                    stations = stations,
                    onPlay = { url -> viewModel.play(url) },
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
    onPlay: (String) -> Unit,
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            stations.forEach { station ->
                StationButton(
                    text = stringResource(station.nameResId),
                    onClick = { onPlay(station.url) }
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            FilledTonalIconButton(
                onClick = onStop,
                modifier = Modifier.size(75.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_stop),
                    contentDescription = stringResource(R.string.stop),
                    modifier = Modifier.fillMaxSize(0.6f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StationButton(text: String,  onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .width(140.dp)
            .height(75.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.elevatedButtonColors()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val previewStations = listOf(
        RadioStation(R.string.rfi, "1"),
        RadioStation(R.string.inter, "2"),
        RadioStation(R.string.itzy_bitzy, "3")
    )
    RFIplayerTheme {
        MainScreen(previewStations,  {}, {})
    }
}

package com.craiovadata.rfiplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.craiovadata.rfiplayer.AudioService.Companion.startActionPlay
import com.craiovadata.rfiplayer.AudioService.Companion.startActionStop
import com.craiovadata.rfiplayer.ui.theme.RFIplayerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), /* requestCode= */ 0)
        }

        setContent {
            RFIplayerTheme {
                MainScreen(
                    onPlayRfi = { startActionPlay(this, "http://asculta.rfi.ro:9128/live.mp3") },
                    onPlayItsyBitsy = { startActionPlay(this, "http://live.itsybitsy.ro:8000/itsybitsy") },
                    onPlayFranceInter = { startActionPlay(this, "http://icecast.radiofrance.fr/franceinter-hifi.aac") },
                    onStop = { startActionStop(this) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onPlayRfi: () -> Unit,
    onPlayItsyBitsy: () -> Unit,
    onPlayFranceInter: () -> Unit,
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
            
            StationButton(
                text = stringResource(R.string.rfi),
                onClick = onPlayRfi
            )

            Spacer(modifier = Modifier.weight(1f))

            StationButton(
                text = stringResource(R.string.inter),
                onClick = onPlayFranceInter
            )

            Spacer(modifier = Modifier.weight(1f))

            StationButton(
                text = stringResource(R.string.itzy_bitzy),
                onClick = onPlayItsyBitsy
            )

            Spacer(modifier = Modifier.weight(1f))

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
fun StationButton(text: String, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .width(140.dp)
            .height(75.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    RFIplayerTheme {
        MainScreen({}, {}, {}, {})
    }
}

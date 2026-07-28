package com.craiovadata.rfiplayer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.material3.buttons.PlayPauseButton
import com.craiovadata.rfiplayer.PlayerState
import com.craiovadata.rfiplayer.R

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
                        text = playerState.subtitle!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (playerState.player != null) {
                    PlayPauseButton(
                        player = playerState.player,
                        modifier = Modifier.size(56.dp)
                    )
                }
                
                Spacer(Modifier.width(12.dp))

                IconButton(
                    onClick = onStop,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_stop),
                        contentDescription = "Stop",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
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
            .width(160.dp)
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

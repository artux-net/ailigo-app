package org.ailingo.app.features.topics.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.ailingo.app.core.utils.windowinfo.util.DeviceType
import org.ailingo.app.features.topics.data.Topic

@Composable
fun ContentTopics(
    topic: Topic,
    deviceType: DeviceType,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f), Color.Transparent)
    )
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card {
                AsyncImage(
                    model = topic.imageUrl,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight().drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(gradient, blendMode = BlendMode.Multiply)
                            }
                        }
                )
            }
            Text(
                text = topic.name.uppercase(),
                style = if (deviceType == DeviceType.Desktop) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(16.dp)
            )
        }
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomEnd
        ) {
            val containerColor = if (topic.price >= 0) Color(0xFFA8CD89) else Color(0xFF8B4513)
            if (deviceType == DeviceType.Desktop) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(2f))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = containerColor
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                8.dp,
                                Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Icon(imageVector = Icons.Outlined.Paid, contentDescription = null)
                            Text(
                                topic.price.toString(),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = containerColor
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(4.dp).padding(end = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.Paid, contentDescription = null)
                        Text(
                            topic.price.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

        }
    }
}

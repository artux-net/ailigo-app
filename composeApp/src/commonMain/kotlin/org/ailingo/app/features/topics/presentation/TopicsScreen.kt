package org.ailingo.app.features.topics.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ailingo.app.core.utils.presentation.ErrorScreen
import org.ailingo.app.core.utils.presentation.LoadingScreen
import org.ailingo.app.core.utils.windowinfo.info.WindowInfo
import org.ailingo.app.core.utils.windowinfo.util.DeviceType
import org.ailingo.app.features.topics.data.Topic

@Composable
fun TopicsScreen(
    windowInfo: WindowInfo,
    topicsViewModel: TopicViewModel
) {
    val topicsUiState = topicsViewModel.topicState.collectAsStateWithLifecycle()

    val deviceType = if (windowInfo.screenWidthInfo is WindowInfo.WindowType.DesktopWindowInfo) {
        DeviceType.Desktop
    } else {
        DeviceType.Mobile
    }

    when (val state = topicsUiState.value) {
        TopicUiState.Empty -> {}
        is TopicUiState.Error -> {
            ErrorScreen(errorMessage = state.message)
        }

        TopicUiState.Loading -> {
            LoadingScreen()
        }

        is TopicUiState.Success -> {
            TopicsContent(state.topics, deviceType)
        }
    }
}

@Composable
fun TopicsContent(
    topics: List<Topic>,
    deviceType: DeviceType
) {
    if (deviceType == DeviceType.Desktop) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(350.dp),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(end = 16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(topics) { photo ->
                ContentTopics(photo, deviceType = deviceType)
            }
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(150.dp),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(topics) { photo ->
                ContentTopics(photo, deviceType = deviceType)
            }
        }
    }
}
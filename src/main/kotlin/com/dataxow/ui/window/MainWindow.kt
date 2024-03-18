package com.dataxow.ui.window

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.Indicator
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.dataxow.app.AppData
import com.dataxow.enum.MainTab
import com.dataxow.ui.components.loadingDialog
import com.dataxow.ui.content.*

@Composable
fun mainWindow(
    applicationScope: ApplicationScope,
    projectPath: String,
    setProjectPath: (String) -> Unit,
    isLoading: Boolean,
    setForceUpdateWindowState: () -> Unit,
    imagePath: String?,
    setImagePath: (String?) -> Unit,
    videoPath: String?,
    setVideoPath: (String?) -> Unit,
    playerWindowOpen: Boolean,
    setPlayerWindowOpen: (Boolean) -> Unit,
    ips: List<String>,
    serverHost: String,
    setServerHost: (String) -> Unit,
    serverPort: String,
    setServerPort: (String) -> Unit,
    qrCodeBitmap: ImageBitmap?,
    setQrCodeBitmap: (ImageBitmap?) -> Unit,
    serverStatus: Boolean,
    setServerStatus: (Boolean) -> Unit,
    startServer: (String, Int) -> Unit,
    stopServer: () -> Unit,
    reload: () -> Unit,
) {
    val windowState by remember { mutableStateOf(WindowState()) }
    windowState.size = DpSize(1024.dp, 768.dp)

    val imageListListState = rememberLazyListState()
    val imageListGridState = rememberLazyGridState()
    var imageListIsGrid by remember { mutableStateOf(false) }
    var imageListShowListImage by remember { mutableStateOf(AppData.config.showListImage) }

    val videoListListState = rememberLazyListState()

    val textListSearchResultsListState = rememberLazyListState()
    val textListPreviewListState = rememberLazyListState()
    val textListContentSelectedListState = rememberLazyListState()
    val textListContentSelectedPreviewListState = rememberLazyListState()

    Window(
        onCloseRequest = applicationScope::exitApplication,
        state = windowState,
        title = "DataXow",
    ) {
        Scaffold {
            Column {
                var selectedTab by remember { mutableStateOf(MainTab.Home) }

                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    indicator = { tabPositions ->
                        Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            color = Color(0xFFC5CAE9),
                            height = 3.dp
                        )
                    }
                ) {
                    MainTab.entries.forEach { tab ->
                        Tab(
                            text = {
                                if (selectedTab == tab) {
                                    Text("• ${tab.title}")
                                } else {
                                    Text(tab.title)
                                }
                            },
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                        )
                    }
                }

                when (selectedTab) {
                    MainTab.Home -> homeContent(
                        applicationScope = applicationScope,
                        projectPath = projectPath,
                        setProjectPath = {
                            setProjectPath(it)
                        },
                        setForceUpdateWindowState = {
                            setForceUpdateWindowState()
                        },
                        reload = {
                            reload()
                        }
                    )

                    MainTab.Images -> imageListContent(
                        applicationScope = applicationScope,
                        projectPath = projectPath,
                        imagePath = imagePath,
                        setImagePath = {
                            setImagePath(it)
                        },
                        setPlayerWindowOpen = {
                            setPlayerWindowOpen(it)
                        },
                        isGrid = imageListIsGrid,
                        setIsGrid = {
                            imageListIsGrid = it
                        },
                        listState = imageListListState,
                        gridState = imageListGridState,
                        showListImage = imageListShowListImage,
                        setShowListImage = {
                            AppData.config.showListImage = it
                            imageListShowListImage = AppData.config.showListImage
                        },
                        reload = {
                            reload()
                        }
                    )

                    MainTab.Videos -> videoListContent(
                        applicationScope = applicationScope,
                        projectPath = projectPath,
                        videoPath = videoPath,
                        setVideoPath = {
                            setVideoPath(it)
                        },
                        setPlayerWindowOpen = {
                            setPlayerWindowOpen(it)
                        },
                        listState = videoListListState,
                        reload = {
                            reload()
                        }
                    )

                    MainTab.Text -> textContent(
                        applicationScope = applicationScope,
                        projectPath = projectPath,
                        setPlayerWindowOpen = {
                            setPlayerWindowOpen(it)
                        },
                        searchResultsListState = textListSearchResultsListState,
                        previewListState = textListPreviewListState,
                        contentSelectedListState = textListContentSelectedListState,
                        contentSelectedPreviewListState = textListContentSelectedPreviewListState,
                    )

                    MainTab.Remote -> remoteContent(
                        applicationScope = applicationScope,
                        projectPath = projectPath,
                        ips = ips,
                        serverHost = serverHost,
                        setServerHost = {
                            setServerHost(it)
                        },
                        serverPort = serverPort,
                        setServerPort = {
                            setServerPort(it)
                        },
                        qrCodeBitmap = qrCodeBitmap,
                        setQrCodeBitmap = {
                            setQrCodeBitmap(it)
                        },
                        serverStatus = serverStatus,
                        setServerStatus = {
                            setServerStatus(it)
                        },
                        startServer = { host, port ->
                            startServer(host, port)
                        },
                        stopServer = {
                            stopServer()
                        }
                    )
                }
            }
            loadingDialog(isLoading)
        }
    }
}


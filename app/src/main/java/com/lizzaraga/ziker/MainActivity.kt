package com.lizzaraga.ziker

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.lizzaraga.ziker.domains.models.FileDropdownAction
import com.lizzaraga.ziker.domains.models.SongModel
import com.lizzaraga.ziker.logics.vms.HomeViewModel
import com.lizzaraga.ziker.screens.SongListScreen
import com.lizzaraga.ziker.ui.composables.FileDropdownMenu
import com.lizzaraga.ziker.ui.theme.ZikerTheme
import com.lizzaraga.ziker.util.toMb
import java.security.Permission
import java.security.Permissions
import java.util.jar.Manifest

class MainActivity : ComponentActivity() {
    private lateinit var homeViewModel: HomeViewModel
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
        homeViewModel = ViewModelProvider
            .AndroidViewModelFactory(application).create(HomeViewModel::class.java)

        val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if (isGranted){
                homeViewModel.onLoadSong()
            }
            else{
                Log.d(this::class.java.toString(), "Not Granted")
            }
        }
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                Log.d(this::class.java.toString(), "Granted")
                homeViewModel.onLoadSong()
            }
            /*shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                Log.d(this::class.java.toString(), "Require Educational UI")
            }*/
            else -> {
                requestPermLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        setContent {
            ZikerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    MainActivityScreen(homeViewModel = homeViewModel)
                }
            }
        }
    }
}

@Composable
fun MainActivityScreen(homeViewModel: HomeViewModel){
    val haptic = LocalHapticFeedback.current
    val scaffoldState = rememberScaffoldState()
    BackHandler(enabled = homeViewModel.inSelectedMode) {
        homeViewModel.onResetSelection()
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            when(homeViewModel.inSelectedMode){
                true -> {
                    SelectionModeTopAppBar(homeViewModel, haptic)
                }
                else -> {
                    DefaultTopAppBar(homeViewModel, haptic)
                }
            }
        }
    ){
        SongListScreen(
            songs = homeViewModel.songList,
            inSelectedMode = homeViewModel.inSelectedMode,
            isListDisplay = homeViewModel.isListView,
            selectedIds = homeViewModel.selectedSongIds,
            onSelected = homeViewModel::onSelectedSong,
            onUnSelected = homeViewModel::onUnSelectedSong,
            onSelectedFileAction = homeViewModel::onSelectedFileAction
        )
    }
}

@Composable
private fun DefaultTopAppBar(
    homeViewModel: HomeViewModel,
    haptic: HapticFeedback
) {
    var isDropdownVisible by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        title = {
            Text(text = "Audios")
        },
        backgroundColor = MaterialTheme.colors.primaryVariant,
        elevation = 5.dp,
        actions = {
            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }) {
                Icon(Icons.Rounded.Search, contentDescription = "Search a song")
            }
            if (homeViewModel.isListView) {
                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    homeViewModel.onGridView()
                }) {
                    Icon(Icons.Rounded.GridView, contentDescription = "Switch to grid view")
                }
            } else {
                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    homeViewModel.onListView()
                }) {
                    Icon(Icons.Rounded.ViewList, contentDescription = "Switch to list view")
                }
            }
            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                isDropdownVisible = true
            }) {
                Icon(Icons.Rounded.MoreVert, contentDescription = "More Actions")
            }


            FileDropdownMenu(
                items = listOf(FileDropdownAction.SelectAll, FileDropdownAction.OrderBy),
                offset = DpOffset(0.dp,(-40).dp),
                isVisible = isDropdownVisible,
                onDismiss = { isDropdownVisible = false},
                onItemSelected = {
                    homeViewModel.onSelectedFileAction(it)
                    isDropdownVisible = false
                },
                modifier = Modifier.defaultMinSize(200.dp)
            )

        }
    )
}

@Composable
private fun SelectionModeTopAppBar(
    homeViewModel: HomeViewModel,
    haptic: HapticFeedback
) {
    var isDropdownVisible by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                homeViewModel.onResetSelection()
            }) {

                Icon(Icons.Default.Close, contentDescription = "Close Selection Mode")
            }
        },
        title = {
            val totalSize = homeViewModel.selectedSongs.sumOf { it.size }.toMb()
            Column() {
                Text(text = "Audios", fontSize = 16.sp)
                Text(text = "$totalSize MB", fontSize = 13.sp, fontWeight = FontWeight.Normal)
            }
        },
        backgroundColor = MaterialTheme.colors.primaryVariant,
        elevation = 5.dp,
        actions = {
            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }) {
                Icon(Icons.Rounded.Delete, contentDescription = "Delete a song")
            }
            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }) {
                Icon(Icons.Rounded.Share, contentDescription = "Share song(s)")
            }
            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                isDropdownVisible = true

            }) {
                Icon(Icons.Rounded.MoreVert, contentDescription = "More Actions")
            }

            FileDropdownMenu(
                isVisible = isDropdownVisible,
                items = when (homeViewModel.selectedSongIds.size) {
                    1 -> listOf(FileDropdownAction.SelectAll,
                        FileDropdownAction.OpenWith, FileDropdownAction.Favorites,
                        FileDropdownAction.Rename, FileDropdownAction.FileInfo)
                    homeViewModel.songList.size -> listOf(FileDropdownAction.DeselectAll,
                        FileDropdownAction.Favorites)
                    else -> listOf(FileDropdownAction.SelectAll,
                        FileDropdownAction.Favorites)
                },
                offset = DpOffset(0.dp,(-40).dp),

                onDismiss = { isDropdownVisible = false},
                onItemSelected = {
                    homeViewModel.onSelectedFileAction(it)
                    isDropdownVisible = false
                },
                modifier = Modifier.defaultMinSize(200.dp)
            )

        }
    )
}



@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ZikerTheme {
        Greeting("Android")
    }
}
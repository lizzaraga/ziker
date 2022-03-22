package com.lizzaraga.ziker.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lizzaraga.ziker.R
import com.lizzaraga.ziker.domains.models.FileDropdownAction
import com.lizzaraga.ziker.domains.models.SongModel
import com.lizzaraga.ziker.ui.composables.FileDropdownMenu
import com.lizzaraga.ziker.util.toMb
import com.lizzaraga.ziker.util.toMinutes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongListScreen(songs: List<SongModel>,
                   onSelected: (SongModel) -> Unit = {},
                   onUnSelected: (SongModel) -> Unit = {},
                   inSelectedMode: Boolean = false,
                   selectedIds: List<Long> = listOf(),
                   isListDisplay: Boolean = true,
                   onSelectedFileAction: (FileDropdownAction, SongModel) -> Unit
){

    Column() {
        if (isListDisplay){
            LazyColumn{

                items(items = songs, key = { it.id }){

                    SongRow(song = it,
                        onSelected = onSelected,
                        onUnSelected = onUnSelected,
                        isSelected = selectedIds.contains(it.id),
                        inSelectedMode = inSelectedMode,
                        onSelectedFileAction = onSelectedFileAction
                    )
                }
            }
        }
        else{
            LazyVerticalGrid(
                cells = GridCells.Fixed(3) ){
                items(items = songs){
                    SongGrid(song = it)
                }
            }
        }
    }
}

@Composable
fun SongGrid(song: SongModel){
    val bitmapImage = remember{
        song.imageCover
    }
    if(bitmapImage != null){
        SongBoxWithCover(bitmapImage = bitmapImage, modifier = Modifier
            .padding(end = 0.dp)
        )
    }
    else{
        SongBoxWithoutCover(modifier = Modifier
            .padding(end = 0.dp)
            .size(150.dp)
            .background(
                color = when (isSystemInDarkTheme()) {
                    true -> Color(0xFF050505)
                    else -> Color(0xFFFAFAFA)
                },
                shape = MaterialTheme.shapes.medium
            )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongRow(song: SongModel,
            onSelected: (SongModel) -> Unit = {},
            onUnSelected: (SongModel) -> Unit = {},
            inSelectedMode: Boolean = false,
            isSelected: Boolean,
            onSelectedFileAction: (FileDropdownAction, SongModel) -> Unit
){
    val haptic = LocalHapticFeedback.current
    val onTapInSelectionModeAction = {
        when(isSelected){
            true -> {
                onUnSelected(song)
            }
            else -> onSelected(song)
        }
    }
    val backgroundColor = when(isSelected){
        true -> MaterialTheme.colors.primaryVariant.copy(alpha = 0.05f)
        else -> Color.Unspecified
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    var isDropdownVisible by remember {
        mutableStateOf(false)
    }
    Row(modifier = Modifier
        .background(color = backgroundColor)
        .combinedClickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                if (inSelectedMode) {
                    onTapInSelectionModeAction()
                }
            },
            onLongClick = {
                if (!isSelected) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSelected(song)
                }
            }
        )
        .padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val bitmapImage = remember{
            song.imageCover
        }
        SongBoxCover(bitmapImage = bitmapImage)
        Column(modifier = Modifier
            .weight(1f)
            .padding(end = 2.dp)) {
            Text(song.fileName, maxLines = 1, overflow = TextOverflow.Ellipsis,
                color = LocalContentColor.current.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium, fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            val size = remember(song.id) {
                song.size.toMb()
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()){
                Text("$size MB", maxLines = 1,  fontSize = 13.sp, color = LocalContentColor.current.copy(alpha = 0.7f))
                /*Row(verticalAlignment = Alignment.CenterVertically){

                    Text(song.duration.toMinutes(), maxLines = 1,  fontSize = 10.sp,
                        color = LocalContentColor.current.copy(alpha = 0.6f),  fontWeight = FontWeight.Medium)

                }*/
            }
        }
        when(inSelectedMode){
            true -> {
                val selectionIcon = when(isSelected){
                    true -> Icons.Default.RadioButtonChecked
                    else -> Icons.Default.RadioButtonUnchecked
                }
                val selectionIconColor = when(isSelected){
                    true -> MaterialTheme.colors.primaryVariant
                    else -> LocalContentColor.current.copy(alpha = 0.8f)
                }
                Icon(
                    rememberVectorPainter(image = selectionIcon), contentDescription = "Selection Icon",
                    tint = selectionIconColor,
                    modifier = Modifier.padding(end = 12.dp)
                )

                /*val onTap= {

                }
                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onTap()
                }) {
                    Icon(
                        rememberVectorPainter(image = selectionIcon), contentDescription = "Selection Icon",
                        tint = LocalContentColor.current.copy(alpha = 0.8f))
                }*/
            }
            else -> {
                Box(modifier = Modifier){
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        isDropdownVisible = true
                    }) {
                        Icon(
                            rememberVectorPainter(image = Icons.Default.MoreVert), contentDescription = "More Actions",
                            tint = LocalContentColor.current.copy(alpha = 0.8f))
                    }
                    FileDropdownMenu(
                        items = listOf(FileDropdownAction.Select,
                            FileDropdownAction.Share,
                            FileDropdownAction.OpenWith,
                            FileDropdownAction.Rename,
                            FileDropdownAction.Favorites,
                            FileDropdownAction.MoveToTrash,
                            FileDropdownAction.FileInfo,
                        ),
                        offset = DpOffset(0.dp,(-40).dp),
                        isVisible = isDropdownVisible,
                        onDismiss = { isDropdownVisible = false},
                        onItemSelected = {
                            onSelectedFileAction(it, song)
                            isDropdownVisible = false
                        },
                        modifier = Modifier.width(200.dp)
                    )
                }
            }
        }


    }

}

@Composable
private fun SongBoxCover(bitmapImage: Bitmap?) {
    if (bitmapImage != null) {
        SongBoxWithCover(bitmapImage = bitmapImage, modifier = Modifier.padding(end = 16.dp))
    } else {
        SongBoxWithoutCover(
            modifier = Modifier
                .padding(end = 16.dp)
                .background(
                    color = when (isSystemInDarkTheme()) {
                        true -> Color(0xFF050505)
                        else -> Color(0xFFFAFAFA)
                    },
                    shape = MaterialTheme.shapes.medium
                )
        )
    }
}

@Composable
private fun SongBoxWithCover(bitmapImage: Bitmap, modifier: Modifier = Modifier,  iconOnSurfaceModifier: Modifier = Modifier,
                             boxContent: @Composable() (BoxScope.() -> Unit) = {}) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clipToBounds()
            .size(48.dp)
            .background(color = Color(0xFF000000), shape = MaterialTheme.shapes.medium)
    ) {
        Image(
            bitmap = bitmapImage.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)

        )
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .clipToBounds()
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.25f),
                        0.7f to Color.Black.copy(alpha = 0.2f),
                        1.0f to Color.Black.copy(alpha = 0.1f),
                        startY = 0.0f,
                        endY = 48.0f
                    )
                )
        ) {
            Image(
                painterResource(id = R.drawable.ic_baseline_music_note_24),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = iconOnSurfaceModifier
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .clip(MaterialTheme.shapes.medium),
                colorFilter = ColorFilter.tint(color = Color.White)
            )
        }

        boxContent()
    }
}

@Composable
private fun SongBoxWithoutCover(modifier: Modifier = Modifier,
                                imageFilter: ColorFilter = ColorFilter.tint(color = Color.DarkGray),
                                boxContent: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clipToBounds()
            .size(48.dp)

    ) {
        Image(
            painterResource(id = R.drawable.ic_baseline_music_note_24),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center),
            colorFilter = imageFilter
        )
        boxContent()
    }
}


@Composable
fun ImageBox(modifier: Modifier){
    Box {

    }
}
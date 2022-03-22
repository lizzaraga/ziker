package com.lizzaraga.ziker.ui.composables

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.DpOffset
import com.lizzaraga.ziker.domains.models.FileDropdownAction

@Composable
fun FileDropdownMenu(
    modifier: Modifier = Modifier,
    items: List<FileDropdownAction>,
    offset: DpOffset,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onItemSelected: (FileDropdownAction) -> Unit
){
    val haptic = LocalHapticFeedback.current
    DropdownMenu(expanded = isVisible, onDismissRequest = onDismiss, offset = offset, modifier = modifier) {
        items.forEach{
            DropdownMenuItem(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onItemSelected(it)
            }) {

                Text(it.title)
            }
        }
    }
}
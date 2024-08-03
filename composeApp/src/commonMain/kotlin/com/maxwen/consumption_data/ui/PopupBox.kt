package com.maxwen.consumption_data.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun PopupBox(
    showPopup: Boolean,
    onClickOutside: () -> Unit,
    offset: IntOffset = IntOffset(0, 0),
    content: @Composable() () -> Unit
) {
    val expandedState = remember { MutableTransitionState(false) }
    expandedState.targetState = showPopup
    if (expandedState.currentState || expandedState.targetState || !expandedState.isIdle) {
        Popup(
            offset = offset,
            alignment = Alignment.Center,
            // to dismiss on click outside
            onDismissRequest = { onClickOutside() }
        ) {
            AnimatedVisibility(
                visibleState = expandedState, enter = scaleIn(),
                exit = scaleOut(),
            ) {
                Column(
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    content()
                }
            }
        }
    }
}
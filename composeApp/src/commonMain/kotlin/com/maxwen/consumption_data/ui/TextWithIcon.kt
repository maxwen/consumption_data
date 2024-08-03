package com.maxwen.consumption_data.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun TextWithIcon(text: String, icon: DrawableResource, textStyle: TextStyle = MaterialTheme.typography.bodyMedium) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(5.dp))
        Icon(imageVector = vectorResource(icon), contentDescription = null)
        Spacer(modifier = Modifier.width(5.dp))
        Text(text, style = textStyle)
    }
}
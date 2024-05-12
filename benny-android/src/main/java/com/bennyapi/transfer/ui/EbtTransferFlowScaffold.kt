package com.bennyapi.transfer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.textButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import com.bennyapi.transfer.ui.theme.Black
import com.bennyapi.transfer.ui.theme.GreyDark
import com.bennyapi.transfer.ui.theme.GreyLight
import com.bennyapi.transfer.ui.theme.Typography
import com.bennyapi.transfer.ui.theme.White

@Composable
internal fun EbtTransferFlowScaffold(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    primaryButtonText: String,
    onPrimaryButtonPress: () -> Unit,
    isPrimaryButtonEnabled: Boolean,
    isSecondaryButtonEnabled: Boolean = false,
    secondaryButtonText: String? = null,
    onSecondaryButtonPress: () -> Unit = {},
    onExitPress: () -> Unit,
    entryComponent: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(White)
            .padding(horizontal = 16.dp, vertical = 24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton(
                onClick = { onExitPress() },
                modifier = Modifier
                    .size(36.dp)
                    .background(color = GreyLight, shape = CircleShape)
                    .align(Alignment.End),
            ) {
                Icon(imageVector = Icons.Filled.Close, tint = Black, contentDescription = "Exit")
            }
            Text(
                text = title,
                style = Typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = description,
                style = Typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp),
            )
            entryComponent()
            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                if (secondaryButtonText != null) {
                    TextButton(
                        modifier = Modifier
                            .weight(1F)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(8.dp),
                        colors = textButtonColors(
                            containerColor = Transparent,
                            contentColor = Black,
                            disabledContainerColor = Black,
                            disabledContentColor = GreyDark,
                        ),
                        onClick = { onSecondaryButtonPress() },
                        enabled = isSecondaryButtonEnabled,
                    ) {
                        Text(text = secondaryButtonText, style = Typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Button(
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Black,
                        contentColor = White,
                        disabledContainerColor = GreyDark,
                        disabledContentColor = GreyLight,
                    ),
                    onClick = { onPrimaryButtonPress() },
                    enabled = isPrimaryButtonEnabled,
                ) {
                    Text(text = primaryButtonText, style = Typography.bodyLarge)
                }
            }
        }
    }
}

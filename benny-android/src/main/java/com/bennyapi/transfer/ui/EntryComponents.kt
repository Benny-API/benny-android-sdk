package com.bennyapi.transfer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardType.Companion.Number
import androidx.compose.ui.text.input.KeyboardType.Companion.NumberPassword
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import com.bennyapi.transfer.common.CARD_MAX_LENGTH
import com.bennyapi.transfer.common.PIN_LENGTH
import com.bennyapi.transfer.ui.theme.Black
import com.bennyapi.transfer.ui.theme.GreyLight
import com.bennyapi.transfer.ui.theme.Red
import com.bennyapi.transfer.ui.theme.Typography

@Composable
internal fun PinEntry(
    onPinChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: String? = null,
) {
    val pinValue = remember { mutableStateOf(TextFieldValue()) }
    Entry(
        value = pinValue.value,
        onValueChange = {
            if (it.text.length <= PIN_LENGTH) {
                val digitsOnly = it.text.filter { char -> char.isDigit() }
                pinValue.value = it.copy(text = digitsOnly)
                onPinChange(digitsOnly)
            }
        },
        isLoading = isLoading,
        error = error,
        keyboardType = NumberPassword,
        visualTransformation = PasswordVisualTransformation(),
        modifier = modifier
            .width(180.dp)
            .defaultMinSize(minHeight = 56.dp),
    )
}

@Composable
internal fun CardNumberEntry(
    onCardNumberChange: (value: String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: String? = null,
) {
    val cardNumberValue = remember { mutableStateOf(TextFieldValue()) }
    Entry(
        value = cardNumberValue.value,
        onValueChange = {
            if (it.text.length <= CARD_MAX_LENGTH) {
                val digitsOnly = it.text.filter { char -> char.isDigit() }
                cardNumberValue.value = it.copy(text = digitsOnly)
                onCardNumberChange(digitsOnly)
            }
        },
        isLoading = isLoading,
        error = error,
        keyboardType = Number,
        visualTransformation = EbtCardVisualTransformation(),
        modifier = modifier
            .width(272.dp)
            .defaultMinSize(minHeight = 56.dp),
    )
}

@Composable
private fun Entry(
    value: TextFieldValue,
    onValueChange: (value: TextFieldValue) -> Unit,
    isLoading: Boolean,
    error: String?,
    visualTransformation: VisualTransformation,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
        LoadingIndicator(modifier)
        return
    }

    OutlinedTextField(
        modifier = modifier,
        textStyle = Typography.bodyLarge.merge(textAlign = Center),
        shape = RoundedCornerShape(8.dp),
        value = value,
        isError = error != null,
        supportingText = {
            error?.let {
                Text(
                    text = it,
                    style = Typography.labelLarge.merge(color = Red, textAlign = Center),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        },
        onValueChange = { onValueChange(it) },
        colors = colors(
            cursorColor = Black,
            focusedContainerColor = GreyLight,
            focusedIndicatorColor = Transparent,
            unfocusedContainerColor = GreyLight,
            unfocusedIndicatorColor = Transparent,
            errorContainerColor = GreyLight,
            errorIndicatorColor = Red,
            errorTextColor = Red,
        ),
        visualTransformation = visualTransformation,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    )
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.background(GreyLight, RoundedCornerShape(8.dp)),
    ) {
        CircularProgressIndicator(
            color = Black,
            strokeWidth = 2.5.dp,
            modifier = Modifier.size(24.dp),
        )
    }
}

private class EbtCardVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = text.chunked(4).joinToString(" ")
        val ebtCardOffsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when (offset) {
                    in (1..4) -> offset
                    in (5..8) -> offset + 1
                    in (9..12) -> offset + 2
                    in (13..16) -> offset + 3
                    in (17..19) -> offset + 4
                    else -> offset
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when (offset) {
                    in (1..4) -> offset
                    in (5..9) -> offset - 1
                    in (11..14) -> offset - 2
                    in (15..19) -> offset - 3
                    in (20..23) -> offset - 4
                    else -> offset
                }
            }
        }

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = ebtCardOffsetMapping,
        )
    }
}

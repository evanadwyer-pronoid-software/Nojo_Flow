@file:OptIn(ExperimentalMaterial3Api::class)

package com.pronoidsoftware.nojoflow.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pronoidsoftware.nojoflow.R

@Composable
fun EnterTitleDialog(
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialText: String = "",
) {
    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onDismiss()
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.LightGray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val titleTextField = rememberTextFieldState(initialText)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.enter_title), color = Color.Black)
            BasicTextField(
                titleTextField,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Words
                ),
                onKeyboardAction = {
                    onSubmit(titleTextField.text.toString().trim())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .focusRequester(focusRequester)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.DarkGray),
                onClick = {
                    onSubmit(titleTextField.text.toString().trim())
                }
            ) {
                Text(stringResource(R.string.use_title), color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
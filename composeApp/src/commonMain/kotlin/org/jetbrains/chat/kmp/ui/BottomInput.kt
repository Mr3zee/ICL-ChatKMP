package org.jetbrains.chat.kmp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.chat.kmp.AppModel

@Composable
fun BottomInput(model: AppModel) {
    val authenticated by model.authenticated.collectAsState()
    var input by remember { mutableStateOf("") }
    val disabled by model.inputDisabled.collectAsState()
    val sysEnabled by model.systemMessageInputEnabled.collectAsState()
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun send() {
        error = null
        if (!authenticated) {
            val id = input.toLongOrNull()
            if (id == null) {
                error = "Id should be a number"
            } else {
                scope.launch {
                    if (!model.login(id)) {
                        error = "Unvalid id"
                    }
                }
                input = ""
            }
        } else {
            val text = input
            if (text.isEmpty()) {
                return
            }

            scope.launch {
                model.sendMessage(text)
            }
            input = ""
        }
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .onKeyEvent {
                if (it.key == Key.Enter && it.isShiftPressed) {
                    send()
                    true
                } else false
            },
        singleLine = false,
        value = input,
        onValueChange = { input = it },
        readOnly = disabled,
        trailingIcon = {
            IconButton(
                enabled = input.isNotEmpty(),
                content = {
                    // automirrored does not cimpile on all platforms
                    Icon(Icons.Filled.Send, null)
                },
                onClick = { send() },
            )
        },
        placeholder = {
            when {
                disabled -> {
                    Text("Input is locked")
                }

                sysEnabled -> {
                    Text("Enter new system message...")
                }

                else -> {
                    Text("Start typing...")
                }
            }
        },
        supportingText = {
            val err = error
            if (err != null) {
                Text(err)
            }
        },
        isError = error != null,
    )
}

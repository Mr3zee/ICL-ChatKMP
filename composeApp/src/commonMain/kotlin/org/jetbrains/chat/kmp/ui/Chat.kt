package org.jetbrains.chat.kmp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.chat.kmp.AppModel
import org.jetbrains.chat.kmp.ChatKMPMessage
import org.jetbrains.chat.kmp.Sender

@Composable
fun Chat(model: AppModel) {
    val authentacated by model.authenticated.collectAsState()
    val scope = rememberCoroutineScope()

    if (!authentacated) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val disabled by model.inputDisabled.collectAsState()
            Text("Please, enter your login id or register")
            Button(
                enabled = !disabled,
                onClick = {
                    scope.launch {
                        model.register()
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text("Register")
            }
        }
    } else {
        ChatList(model)
    }
}

@Composable
fun ChatList(model: AppModel) {
    val messages by model.chatMessages.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = listState,
    ) {
        items(messages) { message ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp),
            ) {
                Message(message)
            }
        }
    }
}

@Composable
fun ColumnScope.Message(message: ChatKMPMessage) {
    val isUser = message.sender == Sender.User
    Box(
        modifier = Modifier
            .align(if (isUser) Alignment.End else Alignment.Start)
            .background(if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.medium)
            .padding(8.dp)
            .fillMaxWidth(0.8f),
    ) {
        Text(
            text = message.text,
            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.background(Color.Transparent),
        )
    }
}

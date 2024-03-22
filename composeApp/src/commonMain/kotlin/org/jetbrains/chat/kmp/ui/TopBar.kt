package org.jetbrains.chat.kmp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.jetbrains.chat.kmp.AppModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(model: AppModel) {
    val authenticated by model.authenticated.collectAsState()

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),

        title = {
            if (authenticated) {
                val id by model.chatName.collectAsState()

                Text("ChatKMP #$id")
            } else {
                Text("Login")
            }
        },

//        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior { false },

        actions = {
            if (authenticated) {
                IconButton(
                    onClick = {
                        model.systemMessageInputEnabled.value = !model.systemMessageInputEnabled.value
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }

                IconButton(
                    onClick = {
                        model.logout()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

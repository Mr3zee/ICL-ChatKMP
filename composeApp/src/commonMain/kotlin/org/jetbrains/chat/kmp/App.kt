package org.jetbrains.chat.kmp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.chat.kmp.ui.AppTheme
import org.jetbrains.chat.kmp.ui.BottomInput
import org.jetbrains.chat.kmp.ui.Chat
import org.jetbrains.chat.kmp.ui.TopBar

@Composable
fun App(database: DatabaseQueries) {
    AppTheme {
        Navigator(AppScreen(database))
    }
}

class AppScreen(private val database: DatabaseQueries) : Screen {
    @Composable
    override fun Content() {
        val model = rememberScreenModel { AppModel(database) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.ime)
        ) {
            Scaffold(
                topBar = {
                    TopBar(model)
                },
                bottomBar = {
                    BottomInput(model)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    Chat(model)
                }
            }
        }
    }
}

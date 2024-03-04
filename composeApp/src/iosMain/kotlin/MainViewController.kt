import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import org.jetbrains.chat.kmp.App

fun MainViewController() = ComposeUIViewController(
    configure = {
        onFocusBehavior = OnFocusBehavior.DoNothing
    }
) {
    App()
}

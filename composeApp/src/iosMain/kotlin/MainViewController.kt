import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import org.jetbrains.chat.kmp.App
import org.jetbrains.chat.kmp.DriverFactory
import org.jetbrains.chat.kmp.createDatabase

fun MainViewController() = ComposeUIViewController(
    configure = {
        onFocusBehavior = OnFocusBehavior.DoNothing
    }
) {
    App(createDatabase(DriverFactory()))
}

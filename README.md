# ChatKMP

## NOTE: Wasm and desktop are disabled in this version
To try wasm and desktop checkout to commit prior to sqldelight addition
Sqldelight does not support wasm, and we didn't add implementation for desktop an the lecture

![mobile](/.github/images/mobile_screenshot.png)
![desktop+web](/.github/images/desktop_web_screenshot.png)

This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop, Server.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* `/server` is for the Ktor server application.

* `/shared` is for the code that will be shared between all targets in the project.
  The most important subfolder is `commonMain`. If preferred, you can add code to the platform-specific folders here too.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

**Note:** Compose/Web is Experimental and may be changed at any time. Use it only for evaluation purposes.
We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [GitHub](https://github.com/JetBrains/compose-multiplatform/issues).

You can open the web application by running the `:composeApp:wasmJsBrowserDevelopmentRun` Gradle task.

## Links:
- [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Kotlin Multiplatform wizard](https://kmp.jetbrains.com/)
- [Fleet](https://www.jetbrains.com/fleet/)
- [Jetpack Compose docs](https://developer.android.com/jetpack/compose/)
- [Layout basics in Compose](https://developer.android.com/jetpack/compose/layouts/basics)
- Animations in Compose (docs and guides)
  - [Animation modifiers](https://developer.android.com/jetpack/compose/animation/composables-modifiers#animation-modifiers)
  - [Keyframes](https://proandroiddev.com/animate-with-jetpack-compose-animate-as-state-and-animation-specs-ffc708bb45f8)
  - [Animatable](https://stackoverflow.com/questions/74903014/how-to-start-and-stop-animation-in-jetpack-compose)
- [Bottom Navigation guide](https://proandroiddev.com/implement-bottom-bar-navigation-in-jetpack-compose-b530b1cd9ee2)
- [No navigation controller for now and alternatives](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation-routing.html)
- [Voyager - navigation and app model](https://voyager.adriel.cafe/)
- [Google icons](https://fonts.google.com/icons)
- Resources about screen width in Compose
  - [Stackoverflow](https://stackoverflow.com/questions/68919900/screen-width-and-height-in-jetpack-compose)
  - [Github discussion](https://github.com/JetBrains/compose-multiplatform/discussions/3225)
  - [About density](https://medium.com/@android-world/jetpack-compose-localdensity-pixel-dp-d679370ccf05)
- SQLDelight
  - [Docs](https://cashapp.github.io/sqldelight/2.0.0/multiplatform_sqlite/)
  - [Linker issue for iOS](https://github.com/cashapp/sqldelight/issues/1442) - for me worked Xcode settings
- Local security checks
  - [Disable for Android Cleartext](https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted)
  - [Localhost for Android](https://stackoverflow.com/questions/5806220/how-to-connect-to-my-http-localhost-web-server-from-android-emulator)
  - [Disable for iOS](https://stackoverflow.com/questions/6077888/how-do-i-access-the-host-machine-itself-from-the-iphone-simulator)
- [DI (Dependency injection) framework - Koin](https://insert-koin.io/)
- [Database library for server - Exposed](https://github.com/JetBrains/Exposed)
- [Web framework, HTTP clients and servers - Ktor](https://ktor.io/)
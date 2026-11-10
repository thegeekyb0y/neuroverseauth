# NeuroVerseAuth

An Android authentication app built with Kotlin and Jetpack Compose for NeuroVerse
Software Solutions. Users can register, sign in, reset a forgotten password, view their
profile and sign out. Sessions survive app restarts, and every authenticated screen is
unreachable while signed out.

Authentication is provided by **Firebase Authentication** (email and password). The app
never stores, copies or logs a token, an email address or a password.

---

## Screens

| # | Screen | Purpose |
|---|--------|---------|
| 1 | Welcome | Entry point. Brand mark, create account, log in |
| 2 | Register | Name, email, password, inline validation |
| 3 | Log in | Email, password, show/hide, forgot password |
| 4 | Forgot password | Request a reset link, neutral confirmation |
| 5 | Home | Greeting, signed-in card, profile entry, sign out |
| 6 | Profile | Initials avatar, name, email, sign out |

## Tech stack

| Area | Choice |
|---|---|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM (View / ViewModel / Model + repository) |
| Async | Kotlin Coroutines, StateFlow |
| Navigation | Navigation Compose |
| Auth | Firebase Authentication (email/password) |
| Build | Gradle 9.6, AGP 9.5.1, Kotlin DSL, version catalog |
| SDK | minSdk 24, targetSdk 37, compileSdk 37 |

One activity, no XML layouts, no fragments.

## Project structure

```
app/src/main/java/com/neuroverse/auth/
├── MainActivity.kt              single activity, edge to edge
├── data/                        AuthRepository, FirebaseAuthRepository, AuthUser, AuthError
├── domain/                      Validators, UserDisplay  (pure Kotlin, no Android imports)
├── viewmodel/                   Session, Register, Login, ForgotPassword, Profile view models
├── navigation/                  routes, root session gate, auth and main graphs
└── ui/
    ├── components/              text fields, buttons, banner, snackbar, top bar, dialogs
    ├── screens/                 the six screens
    └── theme/                   colour, type, shapes, spacing, motion tokens
```

Dependencies point one way: `ui → viewmodel → data/domain`. Nothing in `data/` or
`domain/` imports Compose, and no screen imports Firebase.

## Prerequisites

- Android Studio (recent stable) with JDK 17 or newer
- Android SDK 37 installed
- A device or emulator running Android 7.0 (API 24) or newer
- A Firebase project you control (see next section)

## Running locally from a fresh clone

This repository intentionally **does not** contain `google-services.json` — it is
project-specific configuration and stays out of version control. You must supply your own.

1. **Clone the repository**

   ```bash
   git clone https://github.com/thegeekyb0y/neuroverseauth.git
   cd neuroverseauth
   ```

2. **Create a Firebase project** at <https://console.firebase.google.com>.

3. **Add an Android app** to that project with the package name:

   ```
   com.neuroverse.auth
   ```

   The package name must match exactly, or the Google Services plugin fails at build time.

4. **Enable Email/Password sign-in**: Authentication → Sign-in method → Email/Password → Enable.

5. **Download `google-services.json`** from Project settings → Your apps, and place it at:

   ```
   app/google-services.json
   ```

6. *(Recommended)* In Authentication → Settings, turn on **Email enumeration protection**.
   The app works either way, but the Forgot Password screen is designed around it — see
   [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

7. **Build and run**

   ```bash
   ./gradlew installDebug
   ```

   or open the project in Android Studio and press Run.

If the build fails with *"File google-services.json is missing"*, step 5 was skipped.

## Build

```bash
./gradlew assembleDebug          # debug APK
./gradlew assembleRelease        # unsigned release build
```

The debug APK is written to:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Tests

```bash
./gradlew testDebugUnitTest
```

47 JVM unit tests, no device required, no instrumentation tests:

| Suite | Tests | Covers |
|---|---|---|
| `ValidatorsTest` | 7 | Name, email, password rules |
| `AuthErrorTest` | 7 | Exception to user-safe error mapping |
| `LoginViewModelTest` | 8 | Validation, generic credential error, network retry, loading |
| `RegisterViewModelTest` | 11 | Validation, duplicate email, form focus, welcome flag |
| `ForgotPasswordViewModelTest` | 8 | Neutral result for known and unknown emails |
| `SessionViewModelTest` | 6 | Loading → signed in / signed out, logout entry rule |

See [docs/TESTING.md](docs/TESTING.md) for the manual scenario results.

## Version pins

**Firebase BoM is pinned to 34.19.0.** BoM 35.x ships `firebase-auth` 25.0.0, whose Kotlin
metadata version is 2.4.0; this project compiles with Kotlin 2.2.10, which rejects it with
`Module was compiled with an incompatible version of Kotlin`. BoM 34.19.0 ships
`firebase-auth` 24.2.0 and builds cleanly. Raising the BoM requires raising Kotlin first.

`kotlinx-coroutines-test` is a test-only dependency and is not part of the APK.

## Screenshots

Captured on a physical device at 1080×2408. Images live in
[`docs/screenshots/`](docs/screenshots).

| File | Screen |
|---|---|
| [`01-welcome.jpg`](docs/screenshots/01-welcome.jpg) | Welcome |
| [`02-register.jpg`](docs/screenshots/02-register.jpg) | Register with validation errors |
| [`03-login.jpg`](docs/screenshots/03-login.jpg) | Log in |
| [`04-forgot-password.jpg`](docs/screenshots/04-forgot-password.jpg) | Forgot password form |
| [`05-forgot-password-sent.jpg`](docs/screenshots/05-forgot-password-sent.jpg) | Neutral confirmation card |
| [`06-home.jpg`](docs/screenshots/06-home.jpg) | Home |
| [`07-profile.jpg`](docs/screenshots/07-profile.jpg) | Profile |
| [`08-logout-dialog.jpg`](docs/screenshots/08-logout-dialog.jpg) | Log out confirmation |
| [`09-home-light.jpg`](docs/screenshots/09-home-light.jpg) | Home, light theme |

The app supports a dark theme; only the light theme is captured here.

## Security summary

- Passwords never stored or logged; Firebase hashes them server side.
- No secrets in the repository. `google-services.json` is ignored by Git.
- Tokens are owned by the Firebase SDK, which encrypts them with an Android
  Keystore-backed key. The app never copies a token anywhere.
- `android:allowBackup="false"` so auth state is not swept into a cloud backup.
- Login and password-reset failures are deliberately generic.
- Sign-out clears the Firebase session and every in-memory auth flag.

Details in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Third-party licences

- **Poppins** font — SIL Open Font License 1.1. Full text in
  [`licenses/Poppins-OFL.txt`](licenses/Poppins-OFL.txt).
- **Material Symbols** icons (`ic_arrow_back`, `ic_visibility`, `ic_visibility_off`,
  `ic_logout`, `ic_error`) — Apache License 2.0, from
  [google/material-design-icons](https://github.com/google/material-design-icons).
  Full text in [`licenses/Material-Icons-Apache-2.0.txt`](licenses/Material-Icons-Apache-2.0.txt).
- AndroidX, Compose, Navigation, Kotlin Coroutines and Firebase are used under their
  respective Apache 2.0 / Android SDK terms.

## Further documentation

- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) — layers, session gate, auth flows, security mapping
- [docs/TESTING.md](docs/TESTING.md) — manual scenarios and unit test results

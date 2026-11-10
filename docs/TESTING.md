# Testing

Two things are recorded here: the automated unit tests (run by the developer, results
filled in) and the manual acceptance scenarios from the specification (executed on a
physical device).

---

## 1. Environment

| Field | Value |
|---|---|
| Device | realme RMX3612 (physical device) |
| Android version | Android 14 |
| Build tested | `app-debug.apk`, versionCode 1, versionName 1.0 |
| Firebase project | `neuroverseauth` |
| Tester | Aditya Tiwari |
| Date | 2026-11-10 |

## 2. Automated tests

Run from the project root:

```bash
./gradlew testDebugUnitTest
```

JVM only — no emulator, no device, no Firebase project required.

**Latest run: 2026-11-10 — 47 tests, 0 failures, 0 skipped.**

| Suite | Tests | Result | What it proves |
|---|---|---|---|
| `ValidatorsTest` | 7 | PASS | Name, email and password rules, including the 8-character minimum |
| `AuthErrorTest` | 7 | PASS | Every supported Firebase code maps to a user-safe category; wrong password and unknown account collapse into one |
| `LoginViewModelTest` | 8 | PASS | Blank-field rejection, generic credential banner, network retry, loading flag, success keeps the spinner for the gate |
| `RegisterViewModelTest` | 11 | PASS | Three-field validation, focus target, duplicate email inline, weak password, network retry, welcome flag, trimming |
| `ForgotPasswordViewModelTest` | 8 | PASS | Neutral result for known **and** unknown emails, network retry, rate-limit banner |
| `SessionViewModelTest` | 6 | PASS | `Loading → SignedIn/SignedOut`, logout entry rule, sign-out delegation |

Reports: `app/build/reports/tests/testDebugUnitTest/index.html`

---

## 3. Manual acceptance scenarios

Each scenario records what was done, what should happen, what actually happened, and a
pass/fail verdict. The `Actual` and `Result` fields were completed by the tester on the
device.

### Scenario 1 — Register a new user successfully

**Steps**
1. Launch the app and tap **Create account**.
2. Enter a name, an unused email address and a password of at least 8 characters.
3. Tap **Create account**.

**Expected**
- The button shows a spinner and cannot be tapped twice.
- The app moves to Home without returning to Welcome.
- Home greets the user by the name entered, and a snackbar reads "Account created.
  Welcome to NeuroVerse."
- The account exists in the Firebase console under Authentication → Users.

**Actual:** Entered a name, a new email address and a valid password. The button showed a
spinner and could not be tapped again. The app went straight to Home, greeted by the name
entered, and the "Account created. Welcome to NeuroVerse." snackbar appeared once. The new
account is listed in the Firebase console.

**Result:** PASS

---

### Scenario 2 — Prevent duplicate email registration

**Steps**
1. Sign out, then open **Create account**.
2. Enter any name, an email address that is already registered, and a valid password.
3. Tap **Create account**.

**Expected**
- An inline error appears directly under the email field: "An account with this email
  already exists. Try logging in."
- Focus moves to the email field.
- The typed name and password are still present.
- The user stays on the Register screen.

**Actual:** The inline message appeared under the email field, focus moved there, and the
typed name and password were still in the form. The Register screen stayed open.

**Result:** PASS

---

### Scenario 3 — Log in with valid credentials

**Steps**
1. Sign out if signed in, then open **Log in**.
2. Enter a registered email address and its correct password.
3. Tap **Log in**.

**Expected**
- The button shows a spinner.
- The app moves to Home.
- Home shows the account's greeting and email address.

**Actual:** The button showed a spinner and the app moved to Home, showing the account
greeting and the correct email address.

**Result:** PASS

---

### Scenario 4 — Reject invalid credentials

**Steps**
1. Open **Log in**.
2. Enter a registered email address with a wrong password. Submit.
3. Repeat with an email address that does not exist. Submit.

**Expected**
- In both cases exactly one message appears: "Email or password is incorrect."
- The message is identical in both runs, so it does not reveal whether the account exists.
- No inline field error is shown, because neither field is individually at fault.

**Actual:** A wrong password and an unknown email both produced the single banner "Email
or password is incorrect.", with no inline error under either field. The two runs were
indistinguishable.

**Result:** PASS

---

### Scenario 5 — Handle invalid email and password inputs

**Steps**
1. Open **Create account**, leave all fields empty and submit.
2. Fill the name, enter `ada@` as the email and `1234567` as the password. Submit.
3. Switch to **Log in** and submit an empty form.

**Expected**
- Register: three inline errors ("Enter your name." / "Enter a valid email address." /
  "Use at least 8 characters.") and focus on the name field.
- Register: after fixing the name, the email and password errors remain under their fields.
- Log in: "Enter a valid email address." and "Enter your password.".
- No network request is made for an invalid form.

**Actual:** The empty Register form produced all three inline errors and focus moved to
the name field. A malformed email and a 7-character password produced their errors under
the respective fields. The empty Log in form produced both of its errors.

**Result:** PASS

---

### Scenario 6 — Complete the password-reset flow

**Steps**
1. Open **Log in** → **Forgot password?**.
2. Enter the email address of an existing account that you can access. Submit.
3. Check that inbox, including the spam folder.
4. Back to login, repeat with an address that does not exist.

**Expected**
- Both runs show the same card: "If an account exists for that email, we've sent a reset
  link." No difference in wording, colour or layout.
- A reset email arrives for the existing account only, from
  `noreply@<project-id>.firebaseapp.com`, within a few minutes (often in spam on first send).
- The reset link opens a Firebase-hosted page in the browser.
- **Back to login** returns to the Login screen.

**Actual:** Both addresses produced the identical confirmation card. The reset email
arrived for the existing account from `noreply@neuroverseauth.firebaseapp.com` and its
link opened the password-reset page in the browser; no email arrived for the unknown
address. **Back to login** returned to the Login screen.

**Result:** PASS

---

### Scenario 7 — Maintain the session after restarting the application

**Steps**
1. Sign in and confirm Home is shown.
2. Remove the app from the recents list, or force-stop it.
3. Launch the app again from the app drawer.

**Expected**
- Splash, then Home directly.
- No Welcome screen and no Login screen appear.

**Actual:** After force-stopping and relaunching from the app drawer, the splash was
followed by Home directly. Neither Welcome nor Login appeared.

**Result:** PASS

---

### Scenario 8 — Prevent unauthenticated users from accessing protected screens

**Steps**
1. While signed in, open Profile, then sign out.
2. On the Login screen press the system Back button.
3. Relaunch the app.

**Expected**
- Back closes the app; Home and Profile do not appear.
- Relaunching shows the auth flow, not Home.
- Home and Profile have no route in the signed-out graph, so this is structural, not just
  a hidden button.

**Actual:** Pressing Back on the Login screen closed the app; Home and Profile were not
shown. Relaunching the app returned to the authentication flow.

**Result:** PASS

---

### Scenario 9 — Log out successfully and clear the local session

**Steps**
1. Sign in and go to Home.
2. Tap the logout icon in the top bar, then **Cancel**.
3. Tap it again and confirm **Log out**.
4. Relaunch the app.

**Expected**
- Cancel closes the dialog and keeps the user signed in.
- Confirming returns to the **Login** screen (not Welcome, because a session existed in this
  run of the app).
- Relaunching shows the auth flow; the account is not restored.
- A later registration test does not show a leftover welcome snackbar.

**Actual:** Cancel closed the dialog and kept the session. Confirming returned to the Login
screen, and relaunching showed the auth flow with the account no longer signed in.

**Result:** PASS

---

### Scenario 10 — Handle network errors and authentication failures

**Steps**
1. Enable Airplane mode.
2. On **Log in**, submit valid credentials.
3. On **Forgot password**, submit a valid address.
4. Disable Airplane mode and tap **Retry** on each snackbar.

**Expected**
- Both screens show a snackbar: "No connection. Check your network and try again." with a
  **Retry** action.
- Typed values are preserved.
- The reset screen does **not** show its success card while offline.
- Retry after reconnecting completes normally.
- Rate-limited reset requests show "Too many attempts. Wait a moment and try again." and do
  not claim success.

**Actual:** With Airplane mode on, both screens showed the "No connection. Check your
network and try again." snackbar with a Retry action, and the typed values were preserved.
The reset screen did not show its success card while offline. After reconnecting, Retry
completed both actions.

**Result:** PASS

---

## 4. Summary

| # | Scenario | Result |
|---|---|---|
| 1 | Register a new user successfully | PASS |
| 2 | Prevent duplicate email registration | PASS |
| 3 | Log in with valid credentials | PASS |
| 4 | Reject invalid credentials | PASS |
| 5 | Handle invalid email and password inputs | PASS |
| 6 | Complete the password-reset flow | PASS |
| 7 | Maintain the session after restarting the application | PASS |
| 8 | Prevent unauthenticated users from accessing protected screens | PASS |
| 9 | Log out successfully and clear the local session | PASS |
| 10 | Handle network errors and authentication failures | PASS |

All ten acceptance scenarios passed on the device recorded in section 1.

## 5. Notes

- Scenario 6 requires access to the mailbox of a registered account; the email could not be
  verified without it.
- Scenario 8 cannot be attempted through a URL, because the app declares no deep links.
  It is verified by pressing Back after logout and by the composition structure described in
  [ARCHITECTURE.md](ARCHITECTURE.md).

# Keyper


## Running the fully working demo app:
- Clone the repo and build the app with Android Studio, or
- Download the prebuilt APK file from [here](app/release/app-release.apk) and install it on your Android device

The app expects an encrypted [keepass](https://keepass.info/) file at the root of the phone's internal storage with the name  `keyper.kdbx`.

Enter the password in the app, grant the necessary permissions and turn on the accessibility service.

Keyper sends an alert if sensitive data is input into a field where it shouldn't.

Keyper can also detect 2FA codes from SMS and treats these as sensitive as well.

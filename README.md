#KEYPER

Have you ever opened a dangerous website and entered your password without noticing that it was not the real page you were looking for?
Our project's focus was on increasing awereness about the ever growing problem of phishing (sites looking like the ones we use daily or people impersonating a trusted one asking for sensitive information). If this goes unnoticed then you might be giving out your precious data to fraudsters.

Our application helps you detect these scenarios and prevent you from giving your passwords and other informations to third parties with malicious intentions.
We manage to do this using a local password bank and checking your context (application, website url). After any update occurs in the active textbox (writing or deleting characters, copy-pasting), the app checks the content against the password bank. In case it detects that you typed in a password or some other saved data (for example bank account number) on a site that it's not saved to or on a fraudster site it notifies you. This way you know that you are not on the real website you were meant to be on or that you shouldn't give out your data through unsecure channels like messages or emails.

To reach this goal we used the following technologies:
 - Android Studio
 - Java
 - Accessability service
 - KeePass standard

Our future plans include making this service available on desktop machines through browser extensions, making a public website containing a list of recently popular phising types, blacklisting sites that have been flagged as potentially malicious multiple times and integration with other types of password banks.


## Running the fully working demo app:
- Clone the repo and build the app with Android Studio, or
- Download the prebuilt APK file from [here](app/release/app-release.apk) and install it on your Android device

The app expects an encrypted [keepass](https://keepass.info/) file at the root of the phone's internal storage with the name  `keyper.kdbx`.

Enter the password in the app, grant the necessary permissions and turn on the accessibility service.

Keyper sends an alert if sensitive data is input into a field where it shouldn't.

Keyper can also detect 2FA codes from SMS and treats these as sensitive as well.

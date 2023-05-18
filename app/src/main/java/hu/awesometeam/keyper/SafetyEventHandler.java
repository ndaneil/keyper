package hu.awesometeam.keyper;

import static android.os.Looper.getMainLooper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Matcher;

public class SafetyEventHandler {

    PasswordVault vault;

    private static class InternalState {
        String packageName;
        String browserUrl;
        String enteredText;
        boolean shown = false;
        String matchString;
        String OTP;
        long OTPTTL;
        @Override
        public String toString() {
            return "InternalState{" +
                    "packageName='" + packageName + '\'' +
                    ", browserUrl='" + browserUrl + '\'' +
                    ", enteredText='" + enteredText + '\'' +
                    ", shown=" + shown +
                    ", matchString='" + matchString + '\'' +
                    '}';
        }
    }
    InternalState state = new InternalState();

    private static SafetyEventHandler handler;

    public SafetyEventHandler(Context context) {
        this.vault = PasswordVault.getInstance(context);
    }

    public static SafetyEventHandler getInstance(Context context){
        if(handler == null) handler = new SafetyEventHandler(context);
        return handler;
    }


    public void textUpdatedEvent(String text, Context context) {
        if(!Objects.equals(text, state.enteredText)) {
            state.enteredText = text;
            this.checkAlertCondition(context);
        }
        Log.d("SEH", "TU " + state.toString());

    }

    public void urlUpdatedEvent(String url, Context context) {
        if(!Objects.equals(url, state.browserUrl)) {
            state.browserUrl = url;
            state.shown = false;
        }
        Log.d("SEH", "UU " + state.toString());
    }

    public void packageEvent(String packageString, Context context) {
        if(!Objects.equals(packageString, state.packageName)) {
            state.packageName = packageString;
            state.browserUrl = null;
            state.enteredText = null;
            state.shown = false;
        }
        Log.d("SEH", "PU " + state.toString());
    }

    public void notificationEvent(String text, Context context) {
        if(text == null ||(text.length()==0)) return;
        if(text.toLowerCase().contains("code")){
            if (text.matches(".*d{3}.*")){
                Matcher m = java.util.regex.Pattern.compile("(\\d{3}[\\d\\-]*)").matcher(text);
                if (m.find()) {
                    state.OTP = m.group(1);
                    state.OTPTTL = System.currentTimeMillis() + 120000;
                    Toast.makeText(context, "Security code detected: " + state.OTP, Toast.LENGTH_SHORT).show();
                }
            }
            ///
        }


    }

    public void checkAlertCondition(Context context) {
        Log.d("SEH", "CAC1 " + state.toString());
        if(state.enteredText.length() >= 3) {
            String[] result = vault.checkIfMatch(state.enteredText);
            if (result != null) {
                if (state.shown && Objects.equals(state.matchString, result[0])) {
                    return;
                }
                if (state.browserUrl != null && result.length > 1 && result[1] != null) {
                    URL url = null;
                    try {
                        url = new URL(state.browserUrl);
                        String host = url.getHost();
                        /**
                         * Hostname matching rules
                         */
                        if (host.equals(result[1])) {
                            Toast.makeText(context, "URL match. skipping...", Toast.LENGTH_SHORT).show();
                            return;
                        }


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        /**
                         * Hostname matching rules
                         */
                        if (state.browserUrl.length() > 4 && result[1].equals(state.browserUrl.toLowerCase())) {
                            Toast.makeText(context, "URL match. skipping...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }


                state.shown = true;
                state.matchString = result[0];
                Handler mainHandler = new Handler(getMainLooper());

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        alert("Warning! You are about to send sensitive data", "The text you entered relates to " + state.matchString + ". Are you sure you want to enter it here?", context);
                        //Toast.makeText(context, "MATCH:" + result[0] + " package:" + state.packageName, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if(state.OTP != null && state.OTPTTL > System.currentTimeMillis()){
                    if(state.enteredText.contains(state.OTP) && state.enteredText.length()>3){
                        Toast.makeText(context, "OTP MATCH", Toast.LENGTH_SHORT).show();
                        //state.OTP = null;
                        //state.OTPTTL = 0;
                        Handler mainHandler = new Handler(getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                alert("Warning! You are about to send sensitive data", "The text you entered may be a contain a security code. Are you sure you want to send it?", context);
                            }
                        });
                    }

                } else {
                    state.shown = false;
                    state.matchString = null;
                }
            }
        } else {
            state.shown = false;
            state.matchString = null;
        }
        Log.d("SEH", "CAC2 " + state.toString());
    }

    public void alert(String title, String text, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog alertDialog = builder.create();


        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        alertDialog.show();

    }





}

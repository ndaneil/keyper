package hu.awesometeam.keyper;

import static android.os.Looper.getMainLooper;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class SafetyEventHandler {

    PasswordVault vault;

    private static class InternalState {
        String packageName;
        String browserUrl;
        String enteredText;
        boolean shown = false;
        String matchString;

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

    public void checkAlertCondition(Context context) {
        Log.d("SEH", "CAC1 " + state.toString());
        if(state.enteredText.length() >= 3) {
            String[] result = vault.checkIfMatch(state.enteredText);
            if (result != null) {
                if (state.shown && Objects.equals(state.matchString, result[0])) {
                    return;
                }

                if (state.browserUrl != null && result.length > 1) {
                    URL url = null;
                    try {
                        url = new URL(state.browserUrl);
                        String host = url.getHost();
                        /**
                         * Hostname matching rules
                         */
                        if (host.equals(state.browserUrl.toLowerCase())) {
                            Toast.makeText(context, "URL match. skipping...", Toast.LENGTH_SHORT).show();
                            return;
                        }


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }


                state.shown = true;
                state.matchString = result[0];
                Handler mainHandler = new Handler(getMainLooper());

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "MATCH:" + result + " package:" + state.packageName, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                state.shown = false;
                state.matchString = null;
            }
        } else {
            state.shown = false;
            state.matchString = null;
        }
        Log.d("SEH", "CAC2 " + state.toString());
    }




}

package hu.awesometeam.keyper;

import android.content.Context;

public class SafetyEventHandler {

    PasswordVault vault;

    private static SafetyEventHandler handler;

    public SafetyEventHandler(Context context) {
        this.vault = PasswordVault.getInstance(context);

    }

    public static void getInstance(Context context){
        if(handler == null) handler = new SafetyEventHandler(context);
    }


    public void textUpdatedEvent(String text, Context context) {

    }

    public void urlUpdatedEvent(String url, Context context) {

    }

    public void packageEvent(String packageString, Context context) {

    }

}

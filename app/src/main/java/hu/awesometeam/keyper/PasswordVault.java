package hu.awesometeam.keyper;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import de.slackspace.openkeepass.KeePassDatabase;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.Group;
import de.slackspace.openkeepass.domain.KeePassFile;

public class PasswordVault {

    private static KeePassFile db;

    private static PasswordVault instance;

    public PasswordVault(Context ctx) throws FileNotFoundException{
        SharedPreferences sharedPref =  PreferenceManager.getDefaultSharedPreferences(ctx);
        String uriString = sharedPref.getString("keepass_uri","");
        String pass = sharedPref.getString("pref_keepass_password","");
        //Uri uri = Uri.parse(uriString);
        if(uriString.equals("")) throw new FileNotFoundException("Empty path");
        if(pass.equals("")) throw new FileNotFoundException("Empty password");

        String path = Environment.getExternalStorageDirectory() + File.separator + "keyper.kdbx";//Uri.decode(uriString).split("primary:")[1];
        File f = new File(path);

        try {
            load(f, pass);
        }catch (de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException e){
            Toast.makeText(ctx, "Database unreadable", Toast.LENGTH_SHORT).show();
        }
    }

    public static PasswordVault getInstance(Context context){
        if (instance == null){
            try {
                instance = new PasswordVault(context);
            } catch (FileNotFoundException e) {
                Toast.makeText(context, "Keepass file missing", Toast.LENGTH_SHORT).show();
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
                Toast.makeText(context, "Keepass file format error", Toast.LENGTH_SHORT).show();
            }
        }
        return instance;
    }

    private void load(File f, String password) {
        db = KeePassDatabase.getInstance(f).openDatabase(password);
    }
    public String[] checkIfMatch(String input) {
        // Retrieve all entries
        List<Entry> entries = db.getEntries();
        for (Entry entry : entries) {
            if ((entry.getPassword() != null && entry.getPassword().length() >= 3 && input.contains(entry.getPassword()) || (entry.getCustomProperties() != null && entry.getCustomProperties().stream().anyMatch(p -> p != null && p.getPropertyValue().getValue().length() >= 3 && input.contains(p.getPropertyValue().getValue()))))) {
                if (entry.getUrl() != null && entry.getUrl().length() > 0) {
                    String[] parts = new String[2];
                    parts[0] = entry.getTitle();
                    parts[1] = entry.getUrl();
                } else {
                    String[] parts = new String[1];
                    parts[0] = entry.getTitle();
                    return parts;
                }

            }
        }

        return null;
    }
}

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

        String path = Environment.getExternalStorageDirectory() + File.separator + Uri.decode(uriString).split("primary:")[1];
        File f = new File(path);

        load(f, ctx, pass);
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

    private void load(File f, Context context, String password) throws FileNotFoundException {
//        ParcelFileDescriptor pdf = context.getContentResolver().openFileDescriptor(uri, "r");
//
//        assert pdf != null;
//        assert pdf.getStatSize() <= Integer.MAX_VALUE;
//        byte[] data = new byte[(int) pdf.getStatSize()];
//
//        FileDescriptor fd = pdf.getFileDescriptor();
//        FileInputStream fileStream = new FileInputStream(fd);
        db = KeePassDatabase.getInstance(f).openDatabase(password);
    }
    public String checkIfMatch(String needed) {
        // Retrieve all entries
        List<Entry> entries = db.getEntries();
        for (Entry entry : entries) {
            if ((entry.getPassword() != null && entry.getPassword().equals(needed)) || (entry.getCustomProperties() != null && entry.getCustomProperties().stream().anyMatch(p -> p != null && p.getPropertyValue().getValue().equals(needed)))){
                return entry.getTitle();
            }
            //Log.i("WOW","Title: " + entry.getTitle() + " Password: " + entry.getPassword());
        }

        // Retrieve all top groups
        List<Group> groups = db.getTopGroups();
//        for (Group group : groups) {
//            Log.i("WOW", group.getName());
//        }
        return null;
    }
}

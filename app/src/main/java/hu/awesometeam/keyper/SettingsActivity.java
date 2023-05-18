package hu.awesometeam.keyper;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;


public class SettingsActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.tvCheckNotif).setOnClickListener(view ->
                startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)));

    }





    public static class SettingsFragment extends PreferenceFragmentCompat {

        Preference status;
        private static final int PICK_FILE = 20;
        PasswordVault vault;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference pref = findPreference("pref_keepass_open");
            vault = PasswordVault.getInstance(getActivity());
            status = findPreference("pref_keepass_status");
            if (vault == null) {
                status.setTitle("DB not loaded");
                Toast.makeText(getActivity(), "Vault is null", Toast.LENGTH_SHORT).show();
            } else {
                status.setTitle("DB load succeeded");
            }

            assert pref != null;
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    getActivity().startActivityForResult(intent, PICK_FILE);
                    return true;
                }
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode,
                                     Intent resultData) {
            if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK) {
                // The result data contains a URI for the document or directory that
                // the user selected.
                Uri uri = null;
                if (resultData != null) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    uri = resultData.getData();
                    Log.i("SET", uri.toString());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("keepass_uri", uri.toString());
                    editor.commit(); //Commit as we want to make sure it is executed synchronously
                    vault = PasswordVault.getInstance(getActivity());
                    if (vault == null) {
                        status.setTitle("DB not loaded");
                        Toast.makeText(getActivity(), "Vault is null", Toast.LENGTH_SHORT).show();
                    } else {
                        status.setTitle("DB load succeeded");
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, resultData);
        }
    }
}
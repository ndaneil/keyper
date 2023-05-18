package hu.awesometeam.keyper;

import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;
import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;


public class SettingsActivity extends AppCompatActivity {
    private static final int OVERLAY_PERM_CODE = 101;
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                if (isGranted.values().stream().allMatch(e -> e)) {
                    Toast.makeText(getApplicationContext(), "File access granted", Toast.LENGTH_SHORT).show();
                    Log.i("permissions:", "granted");
                } else {
                    Log.i("permission:", "denied");
                }
            });


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
        findViewById(R.id.tvCheckSave).setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    Uri uri = Uri.parse("package:" + getApplicationContext().getPackageName());
                    Intent intent = new Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, uri);
                    Log.i("permission", "action_mng_all_files");
                    startActivity(intent);
                } catch (Exception ex) {
                    Intent intent = new Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            } else {
                String[] PERMISSIONS;
                PERMISSIONS = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                Log.i("permission", " trying the old way");
                requestPermissionLauncher.launch(PERMISSIONS);
            }
        });

        findViewById(R.id.tvCheckOverlay).setOnClickListener(view -> {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivityForResult(intent, OVERLAY_PERM_CODE);
            } else {
                Toast.makeText(getApplicationContext(), "Overlay permission already granted", Toast.LENGTH_SHORT).show();
            }
        });

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
            } else if (requestCode == OVERLAY_PERM_CODE) {
                if (!Settings.canDrawOverlays(getActivity().getApplicationContext())) {
                    Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
            super.onActivityResult(requestCode, resultCode, resultData);
        }
    }
}
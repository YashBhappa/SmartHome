package com.finalyear.smarthome.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.finalyear.smarthome.R;
import com.finalyear.smarthome.fragments.HomeFragment;
import com.finalyear.smarthome.fragments.ProfileFragment;
import com.finalyear.smarthome.fragments.SettingsFragment;
import com.finalyear.smarthome.helper.LocaleHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity:";
    private final int REQUEST_SPEECH=1;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    Toolbar toolbar;
    ImageButton mMicButton;
    int count = 0;

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //make fully Android Transparent Status bar
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        //if (InitApplication.getInstance().isNightModeEnabled()) {
        //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //} else {
        //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //}

        Executor newExecutor = Executors.newSingleThreadExecutor();

        final BiometricPrompt myBiometricPrompt = new BiometricPrompt(MainActivity.this , newExecutor, new BiometricPrompt.AuthenticationCallback() {
            @Override

            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    finish();
                    finishAffinity();
                    finishAndRemoveTask();
                    System.exit(0);
                } else {
                    Log.d(TAG, "An unrecoverable error occurred");
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.d(TAG, "Fingerprint recognised successfully");
            }

            @Override
            public void onAuthenticationFailed() {

                if(count>=3){
                    Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                count++;}
                super.onAuthenticationFailed();
                Log.d(TAG, "onAuthenticationFailed: "+ count);
                Log.d(TAG, "Fingerprint not recognised");
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Smart Home")
                .setSubtitle("Please Authenticate YOURSELF!")
                .setNegativeButtonText("Cancel")
                .build();

        final boolean isAuthOn = sharedPreferences.getBoolean("isAuthOn", false);

        if (isAuthOn){
            myBiometricPrompt.authenticate(promptInfo);
        }


        setContentView(R.layout.activity_main);

        mMicButton = findViewById(R.id.micButton);
        PackageManager pm = getPackageManager();
        @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if(activities.isEmpty()){
            mMicButton.setEnabled(false);
            Toast.makeText(this,"Speech Recognition Not Supported", Toast.LENGTH_LONG).show();
        }

        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        toolbar = findViewById(R.id.myToolbar);
        BottomNavigationView navigationView = findViewById(R.id.bottomNav_view);
        navigationView.setSelectedItemId(R.id.navigation_home);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        HomeFragment fragment_home = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment_home, "");
        fragmentTransaction.commit();

        checkUserStatus();

    }

    public void speakNow(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, REQUEST_SPEECH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_SPEECH && resultCode == RESULT_OK && data!=null){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(!result.isEmpty()) {
                for (String item : result) {
                    Toast.makeText(this, "Voice: "+item, Toast.LENGTH_LONG).show();
                }
            }else{
                    Toast.makeText(this,"Nothing Spoken",Toast.LENGTH_LONG).show();
                }
            }
    }
    private void checkUserStatus() {
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String mUserId = user.getUid();
            SharedPreferences sp = getSharedPreferences("USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_UserID", mUserId);
            editor.apply();
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    // handle item click
                    switch (menuItem.getItemId()) {
                        case R.id.navigation_home:
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                           // mMicButton.setVisibility(View.VISIBLE);
                            ft1.commit();
                            return true;


                        case R.id.navigation_profile:
                            ProfileFragment fragment2 = new ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, fragment2, "");
                            //mMicButton.setVisibility(View.INVISIBLE);
                            ft2.commit();
                            return true;


                        case R.id.navigation_settings:
                            SettingsFragment fragment4 = new SettingsFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content, fragment4, "");
                           // mMicButton.setVisibility(View.INVISIBLE);
                            ft4.commit();
                            return true;
                    }
                    return false;
                }
            };

    int doubleBackToExitPressed = 1;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressed == 2) {
            finishAffinity();
            System.exit(0);
        }
        else {
            doubleBackToExitPressed++;
            Toast.makeText(this, "Please press Back again to exit", Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressed=1;
            }
        }, 2000);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

}

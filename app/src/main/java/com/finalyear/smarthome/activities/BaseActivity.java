package com.finalyear.smarthome.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.finalyear.smarthome.helper.LocaleHelper;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    private Locale mCurrentLocale;

    @Override
    protected void onStart(){
        super.onStart();

        mCurrentLocale = getResources().getConfiguration().locale;
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Locale locale = getLocale(this);

        if(!locale.equals(mCurrentLocale)){
            mCurrentLocale = locale;
            recreate();
        }
    }

    public static Locale getLocale(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = sharedPreferences.getString("language","en");
        switch (lang){
            case "English":
                lang = "en";
                break;
            case "हिंदी":
                lang = "hi";
                break;
        }
        return new Locale(lang);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
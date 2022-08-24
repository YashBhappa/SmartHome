package com.finalyear.smarthome.misc;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

    public class App extends Application {

    public static Object prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        setLocale();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLocale();
    }

    private void setLocale() {

        final Resources resources = getResources();
        final Configuration configuration = resources.getConfiguration();
        final Locale locale = getLocale(this);
        if (!configuration.locale.equals(locale)) {
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, null);
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

}
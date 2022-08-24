package com.finalyear.smarthome.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.finalyear.smarthome.activities.LoginActivity;
import com.finalyear.smarthome.activities.MainActivity;
import com.finalyear.smarthome.R;
import com.finalyear.smarthome.misc.EditProfile;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    FirebaseUser user;
    DatabaseReference myRef;
    String uid;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    TextView mChangeProfile, mChangePassword, mLogOut, mChangeLanguage, mContactUs, mFAQ;
    private GoogleSignInClient mGoogleSignInClient;
    String lang;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchCompat, authCompat;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        final boolean isAuthOn = sharedPreferences.getBoolean("isAuthOn", false);

        switchCompat = view.findViewById(R.id.darkModeSwitch);
        authCompat = view.findViewById(R.id.authentication);

        switchCompat.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        authCompat.setChecked(isAuthOn);

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchCompat.setText(R.string.enable_light_mode);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            switchCompat.setText(R.string.enable_dark_mode);
        }

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            switchCompat.setChecked(true);

        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDarkModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("isDarkModeOn", false);
                    editor.apply();
                    switchCompat.setText("Enable Dark Mode");
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkModeOn", true);
                    editor.apply();
                    switchCompat.setText("Disable Dark Mode");
                }
                restartApp();
            }
        });

        authCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAuthOn){
                    editor.putBoolean("isAuthOn", false);
                    editor.apply();
                }else{
                    editor.putBoolean("isAuthOn", true);
                    editor.apply();
                }
                restartApp();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserInfo");

        mChangeProfile = view.findViewById(R.id.changeProfile);
        mChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
                getActivity().finish();
                //showEditProfileDialog();
            }
        });

        mChangePassword = view.findViewById(R.id.changePassword);
        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        mLogOut = view.findViewById(R.id.logOut);
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogOutDialog();
            }
        });

        mChangeLanguage = view.findViewById(R.id.language);
        mChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageChangeDialog();
            }
        });

        mContactUs = view.findViewById(R.id.contact_us);
        mContactUs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showContactDialog();
            }
        });

        mFAQ = view.findViewById(R.id.faq);
        mFAQ.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showFaqDialog();
            }
        });
        return view;
    }

    private void showContactDialog(){
        String email1 = "E-mail : ybhappa@gmail.com";
        String email2 = "E-mail : siddheshparkhe123@gmail.com";
        String email3 = "E-mail : pshivale06@gmail.com";
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Contact Us"+"\n\n");
        alert.setMessage(email1 + "\n\n" + email2 + "\n\n" + email3);

       // alert.setItems(options, new DialogInterface.OnClickListener() {
       //     @Override
       //     public void onClick(DialogInterface dialog, int which) {
       //     }
       // });
        alert.create().show();
    }

    private void showFaqDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("FAQ");
        builder.setMessage(R.string.demo_content);
        builder.create().show();
    }
    private void showLogOutDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(R.string.settingsFragment_1)
                .setPositiveButton(R.string.settingsFragment_2, new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {

                        logOut(); // Last step. Logout function

                    }
                }).setNegativeButton(R.string.settingsFragment_3, null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }
    public void logOut() {
       firebaseAuth.getInstance().signOut();
       Toast.makeText(getActivity(), R.string.settingsFragment_4, Toast.LENGTH_SHORT).show();
       Intent intent = new Intent(getActivity(), LoginActivity.class);
       startActivity(intent);
       getActivity().finish();
    }
    private void showEditProfileDialog() {
        // option to show in dialog
        String options[] ={getString(R.string.settingsFragment_5), getString(R.string.settingsFragment_6)};
        // alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle(R.string.settingsFragment_7);
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    //calling method and pass key "name" as parameter to update its value in database
                    showNamePhoneUpdateDialog("name");
                }
                else if (which == 1){
                    showNamePhoneUpdateDialog("phone");

                }
            }
        });
        // create and show dialog
        builder.create().show();
    }
    private void showNamePhoneUpdateDialog(final String key) {
        /*parameter "key" will contain value:
         * either "name" which is key in users database which is used to update users mname
         * or     "phone" which is key in users database which is used to update users phone*/

        //custom dialog
        AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+ key); //e.g. update name or update phone
        // set layout of dialog
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        // ADD EDIT TEXT
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter "+ key); //hint e.g. edit name or edit phone
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //add button in dialog to update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from edit text
                final String value = editText.getText().toString().trim();
                //validate if user has entered something or not
                if (!TextUtils.isEmpty(value)){
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    myRef.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "Updated...", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                    // if user edit his name, also change it from his posts
                    if (key.equals("name")){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                        Query query= ref.orderByChild("uid").equalTo(uid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    String child = ds.getKey();
                                    dataSnapshot.getRef().child(child).child("uName").setValue(value);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        // update name in current users comments on posts
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for ( DataSnapshot ds: dataSnapshot.getChildren()){
                                    String child = ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("Comments")){
                                        String child1 = ""+dataSnapshot.child(child).getKey();
                                        Query child2 =FirebaseDatabase.getInstance().getReference("Posts").child(child1).child("Comments")
                                                .orderByChild("uid").equalTo(uid);

                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child = ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("uName").setValue(value);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Please enter "+key, Toast.LENGTH_SHORT).show();

                }
            }
        });
        //add button in dialog to cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //create and show dialog
        builder.create().show();
    }
    private void showChangePasswordDialog() {
        //password change dialog with custom layout having current pass, new pass and update button

        //inflate layout for dialog
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.update_password, null);
        final EditText passwordEt = view.findViewById(R.id.passwordEt);
        final EditText newPasswordEt = view.findViewById(R.id.newPasswordEt);
        final Button updatePasswordBtn = view.findViewById(R.id.updatePasswordBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        updatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate data
                String oldPassword = passwordEt.getText().toString().trim();
                String newPassword = newPasswordEt.getText().toString().trim();
                if (TextUtils.isEmpty(oldPassword)){
                    Toast.makeText(getActivity(), "Enter your current password...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPassword.length()<6){
                    Toast.makeText(getActivity(), "Password length must atleast 6 characters...", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                updatePassword(oldPassword,newPassword);
            }
        });
    }
    private void updatePassword(String oldPassword, final String newPassword) {
        //get current user
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        //before changing password re-authenticate the user
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), oldPassword);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // successfully authenticated begin update
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        HashMap<String, Object> result = new HashMap<>();
                                        result.put("password", newPassword);
                                        myRef.child(user.getUid()).updateChildren(result);
                                        Toast.makeText(getActivity(), "Password Updated!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //authentication failed, show reason
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLanguageChangeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String[] itemsLang = {"English", "हिंदी", "ગુજરાતી", "मराठी", "বাংলা", "français", "русский", "日本語"};
        final String[] items = {"en", "hi", "gu", "mr", "bn", "fr", "ru", "ja"};

        builder.setTitle(R.string.string_language_change_dialog_title)
                .setSingleChoiceItems(itemsLang, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        lang = items[item];
                    }
                }).setPositiveButton(R.string.string_dialog_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //When user submits, restart the activity in
                //the new language
                setLocale(lang);

            }
        }).setNegativeButton(R.string.string_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Do nothing
            }
        });
        builder.create().show();
    }

    public void setLocale(String lang) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("Language", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", "en");
        editor.apply();

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
        SharedPreferences getPrefs = requireActivity().getApplicationContext().getSharedPreferences("com.example.app", MODE_PRIVATE);
        String lan = getPrefs.getString("language", "");
    }
    private void restartApp() {
            Intent i = requireActivity().getPackageManager().getLaunchIntentForPackage(requireActivity().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            requireActivity().finish();
        }
}

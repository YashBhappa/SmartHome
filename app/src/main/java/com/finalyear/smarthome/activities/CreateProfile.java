package com.finalyear.smarthome.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.finalyear.smarthome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class CreateProfile extends AppCompatActivity {
    EditText mName,mUsername ,mEmail ,mPassword ;
    Button createButton ;
    ProgressBar progressBar ;
    String phoneNo;
    FirebaseAuth fAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user;
    Button skipButton;

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
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        mName = findViewById(R.id.crpr_name);
        mUsername = findViewById(R.id.crpr_username);
        mEmail = findViewById(R.id.crpr_email);
        mPassword = findViewById(R.id.crpr_password);
        skipButton = findViewById(R.id.skip_btn);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserInfo");

        createButton = findViewById(R.id.create_profile);
        progressBar = findViewById(R.id.create_progress_bar);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullName = mName.getText().toString();
                final String registerUserName = mUsername.getText().toString();
                final String email = mEmail.getText().toString().trim();
                final String phone = phoneNo;
                String password = mPassword.getText().toString().trim();

                // below line is for checking weather the
                // edittext fields are empty or not.
                if (TextUtils.isEmpty(fullName) && TextUtils.isEmpty(email) && TextUtils.isEmpty(phone) && TextUtils.isEmpty(password) && TextUtils.isEmpty(registerUserName)) {
                    // if the text fields are empty
                    // then show the below message.
                    Toast.makeText(CreateProfile.this, "Please add some data.", Toast.LENGTH_SHORT).show();
                } else {
                    // else call the method to add
                    // data to our database.
                    createProfile(email, password);
                }
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateProfile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void createProfile(String email, String password) {
        String fullName = mName.getText().toString();
        String phone = getIntent().getStringExtra("Phone number");
        String registerUserName = mUsername.getText().toString().trim();

        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // send verification link
                    FirebaseUser fUser = fAuth.getCurrentUser();
                    assert fUser != null;
                    fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CreateProfile.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                        }
                    });

                    user = fAuth.getCurrentUser();
                    assert user != null;
                    String uid = user.getUid();
                    HashMap<String, Object> user = new HashMap<>();
                    user.put("name", fullName);
                    user.put("email", email);
                    user.put("phoneNo", phone);
                    user.put("username", registerUserName);
                    user.put("password", password);
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference reference = firebaseDatabase.getReference("UserInfo");
                    reference.child(uid).setValue(user);
                    Toast.makeText(CreateProfile.this, "Profile created...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateProfile.this, MainActivity.class));
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(CreateProfile.this, "Creation failed...", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateProfile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

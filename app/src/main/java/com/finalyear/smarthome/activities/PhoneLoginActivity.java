package com.finalyear.smarthome.activities;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.finalyear.smarthome.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;
//import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private static final String TAG = "PhoneLoginActivity";
    String phoneNumber;
    String otpCode;
    EditText phone;
    PinView optEnter;
    Button mGenerateBtn, mVerifyBtn;
    CountryCodePicker countryCodePicker;
    Boolean verificationOnProgress = false;
    ProgressBar mProgress, mProgress1;
    TextView mResend;
    FirebaseFirestore fStore;
    ImageView mLoginImage,mOtpImage;
    TextView mLoginTitle, mOtpTitle;
    TextView mLoginDesc, mOtpDesc;

    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private String mVerificationId;
    private FirebaseAuth fAuth;
    private ProgressDialog pd;

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
        setContentView(R.layout.activity_login_phone);

        phone = findViewById(R.id.phone);
        optEnter = findViewById(R.id.codeEnter);

        countryCodePicker = findViewById(R.id.ccp);
        mGenerateBtn = findViewById(R.id.generate_btn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mProgress = findViewById(R.id.login_progress_bar);
        mProgress1 = findViewById(R.id.login_progress_bar3);
        mResend = findViewById(R.id.resendOtpBtn);
        mLoginImage = findViewById(R.id.login_image);
        mOtpImage = findViewById(R.id.otp_image);
        mLoginTitle = findViewById(R.id.login_title);
        mOtpTitle = findViewById(R.id.otp_title);
        mLoginDesc = findViewById(R.id.login_desc);
        mOtpDesc = findViewById(R.id.otp_desc);
        mVerifyBtn = findViewById(R.id.verify_Otp);

        fAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);
        pd.setTitle("Please wait.....");
        pd.setCanceledOnTouchOutside(false);

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                mVerifyBtn.setOnClickListener(v -> {
                    otpCode = Objects.requireNonNull(optEnter.getText()).toString().trim();
                    if(TextUtils.isEmpty(otpCode)){
                        Toast.makeText(PhoneLoginActivity.this,"Please enter verification code...",Toast.LENGTH_LONG).show();
                    }
                    else{
                        verifyPhoneNumberWithCode(mVerificationId, otpCode);
                    }
                });
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                mProgress.setVisibility(View.INVISIBLE);
                mGenerateBtn.setEnabled(true);
                Toast.makeText(PhoneLoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(TAG, "onCodeSent: " + verificationId);
                mVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Verification Code sent.....", Toast.LENGTH_SHORT).show();
                phone.setVisibility(View.INVISIBLE);
                countryCodePicker.setVisibility(View.INVISIBLE);
                mProgress.setVisibility(View.GONE);
                mLoginImage.setVisibility(View.INVISIBLE);mLoginTitle.setVisibility(View.INVISIBLE);mLoginDesc.setVisibility(View.INVISIBLE);
                mOtpImage.setVisibility(View.VISIBLE);mOtpTitle.setVisibility(View.VISIBLE);mOtpDesc.setVisibility(View.VISIBLE);
                optEnter.setVisibility(View.VISIBLE);
                mGenerateBtn.setVisibility(View.INVISIBLE);
                mVerifyBtn.setVisibility(View.VISIBLE);mProgress1.setVisibility(View.VISIBLE);
            }
        };

        mGenerateBtn.setOnClickListener(v -> {
            phoneNumber = phone.getText().toString().trim();
            if(!phone.getText().toString().isEmpty() && phone.getText().toString().length() == 10) {
                if(!verificationOnProgress){
                    mGenerateBtn.setEnabled(false);
                    mProgress.setVisibility(View.VISIBLE);
                    String phoneNum = "+" + countryCodePicker.getSelectedCountryCode() + " " + phone.getText().toString();
                    Log.d("phone", "Phone No.: " + phoneNum);
                    startPhoneNumberVerification(phoneNum);
                }else {
                    mGenerateBtn.setEnabled(false);
                    optEnter.setVisibility(View.GONE);
                    mProgress.setVisibility(View.VISIBLE);
                    otpCode = Objects.requireNonNull(optEnter.getText()).toString();
                    if(otpCode.isEmpty()){
                        optEnter.setError("Required");
                    }
                }
            }else {
                phone.setError("Valid Phone Required");
            }
        });


        mResend.setOnClickListener(v -> {
            phoneNumber = phone.getText().toString().trim();
            if(TextUtils.isEmpty(phoneNumber)){
                Toast.makeText(PhoneLoginActivity.this,"Please enter some number...", Toast.LENGTH_LONG).show();
            }
            else{
                resendVerificationCode(phoneNumber, forceResendingToken);
            }
       });


        mVerifyBtn.setOnClickListener(v -> {
            otpCode = Objects.requireNonNull(optEnter.getText()).toString().trim();
            if(TextUtils.isEmpty(otpCode)){
                Toast.makeText(PhoneLoginActivity.this,"Please enter verification code...",Toast.LENGTH_LONG).show();
            }
            else{
                verifyPhoneNumberWithCode(mVerificationId, otpCode);
            }
        });
    }

    private void verifyPhoneNumberWithCode(String mVerificationId, String otpCode) {
        pd.setMessage("Verifying Code...");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCode);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Logging in...");

        fAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    pd.dismiss();
                    String phone = Objects.requireNonNull(fAuth.getCurrentUser()).getPhoneNumber();
                    Toast.makeText(PhoneLoginActivity.this,"Logged in "+phone, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PhoneLoginActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(PhoneLoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    mProgress1.setVisibility(View.GONE);
                });
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resending Code....");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        pd.setMessage("Verifying Phone Number");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
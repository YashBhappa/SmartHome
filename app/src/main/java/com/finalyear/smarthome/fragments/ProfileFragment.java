package com.finalyear.smarthome.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.finalyear.smarthome.activities.LoginActivity;
import com.finalyear.smarthome.R;
import com.finalyear.smarthome.misc.EditProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


public class ProfileFragment extends Fragment {
    public static final String TAG = "Profile Fragment";
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView ProfileName,Username, Name, Email, Phone, Password;
    String uid;
    CircleImageView mUserImage;

    private StorageReference storageReference;
    Uri image_uri;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    // ARRAYS OF PERMISSION TO BE REQUESTED
    String[] cameraPermissions;
    String[] storagePermissions;
    Button mChangeProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //prevents screenshots i.e. doesn't allow user to take screenshot
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        myRef = database.getReference("UserInfo");

        cameraPermissions =new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions =new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        StorageReference profileRef = storageReference.child("UserInfo").child(user.getUid()).child("Profile_Image");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mUserImage);
            }
        });

        ProfileName = view.findViewById(R.id.profileName);
        Username = view.findViewById(R.id.frag_username);
        Name = view.findViewById(R.id.frag_name);
        Email = view.findViewById(R.id.frag_email);
        Phone = view.findViewById(R.id.frag_phone);
        Password = view.findViewById(R.id.frag_password);
        mUserImage = view.findViewById(R.id.ProfileImage);
        mChangeProfile = view.findViewById(R.id.changeProfile);

        if(user.getEmail() != null){
        Query query = myRef.orderByChild("email").equalTo(user.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String user_Username = (String) dataSnapshot.child("username").getValue();
                        String user_Name = (String) dataSnapshot.child("name").getValue();
                        String user_Email = (String) dataSnapshot.child("email").getValue();
                        String user_Phone = (String) dataSnapshot.child("phoneNo").getValue();
                        String user_Password = (String) dataSnapshot.child("password").getValue();
                        ProfileName.setText(user_Name);
                        Username.setText(user_Username);
                        Name.setText(user_Name);
                        Email.setText(user_Email);
                        Phone.setText(user_Phone);
                        Password.setText(user_Password);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else{
            Query query = myRef.orderByChild("phoneNo").equalTo(user.getPhoneNumber());
            int count = 0;
            if(count<4) {
                Toast.makeText(getActivity(), R.string.toast_phone, Toast.LENGTH_SHORT).show();
                count += 1;
            }
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String user_Username = (String) dataSnapshot.child("username").getValue();
                        String user_Name = (String) dataSnapshot.child("name").getValue();
                        String user_Email = (String) dataSnapshot.child("email").getValue();
                        String user_Phone = (String) dataSnapshot.child("phoneNo").getValue();
                        String user_Password = (String) dataSnapshot.child("password").getValue();
                        ProfileName.setText(user_Name);
                        Username.setText(user_Username);
                        Name.setText(user_Name);
                        Email.setText(user_Email);
                        Phone.setText(user_Phone);
                        Password.setText(user_Password);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }


        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfileImage();
            }
        });

        mChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                Intent i = new Intent(v.getContext(), EditProfile.class);
                i.putExtra("userName",Username.getText().toString());
                i.putExtra("name",ProfileName.getText().toString());
                i.putExtra("email",Email.getText().toString());
                i.putExtra("phoneNo",Phone.getText().toString());
                startActivity(i);
            }
        });

        checkUserStatus();
        return view;
    }

    private void uploadProfileImage() {
        String[] options ={"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(" Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                else if (which == 1){
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length >0){
                    boolean  cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean  writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){ pickFromCamera(); }
                    else { Toast.makeText(getActivity(), "Please enable camera & storage permission", Toast.LENGTH_SHORT).show(); }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length >0){
                    boolean  writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    private boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission(){
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");

        image_uri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data != null ? data.getData() : null;
                //mUserImage.setImageURI(image_uri);
                uploadProfilePhoto(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                // IMAGE IS PICKED FROM CAMERA, GET URI OF IMAGE
                if (data != null) {
                    image_uri = data.getData();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePhoto(Uri uri) {
       StorageReference fileRef = storageReference.child("UserInfo").child(user.getUid()).child("Profile_Image");
       fileRef.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               Log.d(TAG, "onSuccess: Pic uploaded");
               Picasso.get().load(image_uri).into(mUserImage);
               Toast.makeText(getActivity(),"Image Uploaded",Toast.LENGTH_SHORT).show();
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull @NotNull Exception e) {
               Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
           }
       });
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            uid = user.getUid();
        }
        else{
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

}

package com.group_project.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileInfoActivity extends AppCompatActivity {

    CircleImageView add_profile_img;
    EditText add_user_name;
    MaterialButton btn_add_profile;
    ProgressDialog progressDialog;
    String currentLoginUserId, name, phoneNumber, about, imageUri;
    Uri updateImageUri;
    User_Model usersModel;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please, wait for Profile Uploading...");
        progressDialog.setCancelable(false);

        add_profile_img = findViewById(R.id.add_profile_img);
        add_user_name = findViewById(R.id.add_user_name);
        btn_add_profile = findViewById(R.id.btn_add_profile);

        currentLoginUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        phoneNumber = user.getPhoneNumber();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Details").child(phoneNumber);
        StorageReference storageReference = firebaseStorage.getReference().child("Users Profile Image").child(Objects.requireNonNull(phoneNumber));

        add_profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);

                /*
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                */
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("phone")) {
                    name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    about = Objects.requireNonNull(snapshot.child("about").getValue()).toString();
                    phoneNumber = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();
                    imageUri = Objects.requireNonNull(snapshot.child("profile_image").getValue()).toString();

                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    name = user.getDisplayName();
                    about = "";
                    phoneNumber = user.getPhoneNumber();
                    imageUri = "https://firebasestorage.googleapis.com/v0/b/quick-learning-androidfire.appspot.com/o/profile_image.png?alt=media&token=4593c56f-8927-46d6-aeb9-2807aae56bfc";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Database error...", Toast.LENGTH_SHORT).show();

            }
        });

        btn_add_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profileName = add_user_name.getText().toString().trim();
                if (TextUtils.isEmpty(profileName)) {
                    add_user_name.setError("Please enter your name.");
                } else {
                    progressDialog.show();
                    if (updateImageUri == null) {
                        String finalUpdate_ImageUri = "https://firebasestorage.googleapis.com/v0/b/quick-learning-androidfire.appspot.com/o/profile_image.png?alt=media&token=4593c56f-8927-46d6-aeb9-2807aae56bfc";
                        usersModel = new User_Model(auth.getUid(), profileName, phoneNumber, about, finalUpdate_ImageUri);
                        databaseReference.setValue(usersModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Profile Uploaded.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ProfileInfoActivity.this, MainActivity.class));
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Profile not uploading!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        if (updateImageUri != null) {
                            storageReference.putFile(updateImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String finalUpdate_ImageUri = uri.toString();
                                            usersModel = new User_Model(auth.getUid(), profileName, phoneNumber, about, finalUpdate_ImageUri);
                                            databaseReference.setValue(usersModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Profile Uploaded.", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(ProfileInfoActivity.this, MainActivity.class));
                                                        finish();
                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "Profile not uploading!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        } else {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalUpdate_ImageUri = uri.toString();
                                    usersModel = new User_Model(auth.getUid(), profileName, phoneNumber, about, finalUpdate_ImageUri);
                                    databaseReference.setValue(usersModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Profile Uploaded.", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ProfileInfoActivity.this, MainActivity.class));
                                                finish();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Profile not uploading!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200) {
            if (data != null) {
                updateImageUri = data.getData();
                add_profile_img.setImageURI(updateImageUri);
            }
        }
        /*
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            updateImageUri = data.getData();
            add_profile_img.setImageURI(updateImageUri);
        }
        */
    }

}
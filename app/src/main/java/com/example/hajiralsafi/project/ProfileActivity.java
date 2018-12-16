package com.example.hajiralsafi.project;


import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar profileToolbar;
    ConstraintLayout profileRelative;
    ImageView profileImage;
    CircleImageView profile_image;
    EditText usernameEditText;
    Button profileButton;
    String currentUserId ;
    private StorageReference storageReference , storage;
    private DatabaseReference databaseReference;



    private FirebaseAuth firebaseAuth;
    Uri resultUri = null;



    public void updateProfile(View view) {

        if (usernameEditText.getText().toString().matches("")) {

            Toast.makeText(ProfileActivity.this, "Please provide a username", Toast.LENGTH_SHORT).show();

        } else {

            storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(final Uri uri) {

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users");

                    HashMap<String, Object> hashMap = new HashMap<>();

                    hashMap.put("name", usernameEditText.getText().toString());
                    hashMap.put("image",uri.toString());

                    databaseReference.child(currentUserId).updateChildren(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();

                                        //go to browser
                                            Intent intent = new Intent(getApplicationContext(), BrowsingActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.putExtra("name" ,  usernameEditText.getText().toString()) ;
                                            intent.putExtra("image" , uri.toString()) ;
                                            startActivity(intent);
                                            finish();


                                    }else {

                                        Toast.makeText(ProfileActivity.this, "Couldn't update profile " + task.getException(), Toast.LENGTH_LONG).show();
                                    }

                                }
                            }) ;


                }
            }) ;

        }
    }

    public void retrieveUserInfo() {

        profileButton.setEnabled(false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))) {

                    String retrieveName = dataSnapshot.child("name").getValue().toString();
                    String retrieveImage = dataSnapshot.child("image").getValue().toString();

                    usernameEditText.setText(retrieveName);

                       resultUri = Uri.parse(retrieveImage) ;
                                           //retrieve profile image
                        Glide.with(getApplicationContext()).load(retrieveImage).into(profile_image) ;



                        } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {

                    String retrieveName = dataSnapshot.child("name").getValue().toString();

                    usernameEditText.setText(retrieveName);



                    } else {

                    Toast.makeText(ProfileActivity.this, "Please Provide profile information", Toast.LENGTH_SHORT).show();

                    }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                }
                });
                        profileButton.setEnabled(true);



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileToolbar = findViewById(R.id.profile_page_toolbar);

        setSupportActionBar(profileToolbar);
        getSupportActionBar().setTitle(R.string.profile_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileRelative = findViewById(R.id.profileRelative);
        profileImage = findViewById(R.id.profileImage);
        profile_image = findViewById(R.id.profile_image);
        usernameEditText = findViewById(R.id.usernameEditText);
        profileButton = findViewById(R.id.profileButton);

        profileRelative.setOnClickListener(this);
        profileImage.setOnClickListener(this);
        profile_image.setOnClickListener(this);
        profileButton.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference() ;
        storage = storageReference.child("Profile_Images").child(currentUserId + ".jpg");
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        retrieveUserInfo();



    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.profileRelative || v.getId() == R.id.profileImage) {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        if (v.getId() == R.id.profile_image) {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (v.getId() == R.id.profileButton) {

            updateProfile(v);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                 resultUri = result.getUri();

                 profile_image.setImageURI(resultUri);

                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar) ;
                progressBar.setVisibility(View.VISIBLE);
                storage = storageReference.child("Profile_Images").child(currentUserId + ".jpg");
                storage.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){

                            progressBar.setVisibility(View.GONE);

                        }else {

                            Toast.makeText(ProfileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(ProfileActivity.this, error.getMessage() , Toast.LENGTH_SHORT).show();
            }
        }
    }
}

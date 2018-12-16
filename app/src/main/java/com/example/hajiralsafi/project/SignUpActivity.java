package com.example.hajiralsafi.project;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    Toolbar toolbar ;
    ConstraintLayout signUpRelative ;
    ImageView signUpImage ;
    EditText emailEditText , passwordEditText ;
    Button signUpButton ;
    ProgressDialog progressDialog ;
    private FirebaseAuth firebaseAuth ;
    private DatabaseReference databaseReference ;
    String current_user_id ;

    public void signUp (View view) {

        if (emailEditText.getText().toString().matches("")) {

            emailEditText.setError("Email is required");
            emailEditText.requestFocus() ;
        }
        if (passwordEditText.getText().toString().matches("") || passwordEditText.length() < 6) {

            passwordEditText.setError("password should not be less than 6 characters");
            passwordEditText.requestFocus() ;
        }

        else {

            progressDialog.setMessage("Signing up ...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(emailEditText.getText().toString() , passwordEditText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {


                                String current_user_id = firebaseAuth.getCurrentUser().getUid() ;
                                String device_token = FirebaseInstanceId.getInstance().getToken() ;

                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users") ;
                                databaseReference.child(current_user_id).child("device_token").setValue(device_token);



                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();

                                // go to profile activity
                                Intent intent = new Intent(getApplicationContext() ,ProfileActivity.class) ;
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
                                startActivity(intent);
                                finish();


                            }else {

                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Couldn't sign up " + task.getException() + ", Please try again", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        toolbar = findViewById(R.id.signUp_page_toolbar) ;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.signUp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        signUpRelative = findViewById(R.id.signUpRelative) ;
        signUpImage = findViewById(R.id.signUpImage) ;
        emailEditText = findViewById(R.id.emailEditText) ;
        passwordEditText = findViewById(R.id.passwordEditText) ;
        signUpButton = findViewById(R.id.signUpButton) ;

        signUpRelative.setOnClickListener(this);
        signUpImage.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(this) ;
        firebaseAuth = FirebaseAuth.getInstance() ;




    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.signUpRelative || v.getId() == R.id.signUpImage) {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE) ;
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken() , 0) ;
        }

        if (v.getId() == R.id.signUpButton) {

            signUp(v) ;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = firebaseAuth.getCurrentUser() ;

        if (user != null) {

            Intent intent = new Intent(getApplicationContext() , BrowsingActivity.class) ;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
            startActivity(intent);
            finish();
        }
    }
}

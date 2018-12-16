package com.example.hajiralsafi.project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar resetPasswordToolbar ;
    private FirebaseAuth firebaseAuth ;
    EditText resetText ;
    Button resetButton ;
    ImageView resetImageView ;


    public void reset (View view) {

        if (resetText.getText().toString().matches("")){

            resetText.setError("Enter a valid email !");
            resetText.requestFocus() ;
        }else {

            firebaseAuth.sendPasswordResetEmail(resetText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        
                            if (task.isSuccessful()) {

                                Toast.makeText(ResetPasswordActivity.this, "Check your Email", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext() , LoginActivity.class) ;
                                startActivity(intent);
                                finish();
                            }
                        }
                    }) ;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        resetPasswordToolbar = findViewById(R.id.reset_Password_page_toolbar) ;
        setSupportActionBar(resetPasswordToolbar);
        getSupportActionBar().setTitle(R.string.reset);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resetText = (EditText) findViewById(R.id.resetText) ;
        resetButton = (Button) findViewById(R.id.resetButton) ;
        resetImageView = (ImageView) findViewById(R.id.resetImageView) ;
        firebaseAuth = FirebaseAuth.getInstance() ;

        resetImageView.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.resetImageView) {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE) ;
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken() , 0) ;
        }
    }
}

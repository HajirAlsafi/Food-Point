package com.example.hajiralsafi.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {


    public void  goToSignUp (View view) {


        Intent intent = new Intent(getApplicationContext() , SignUpActivity.class) ;
        startActivity(intent);
    }

    public void goToLogin (View view) {

        Intent intent = new Intent(getApplicationContext() , LoginActivity.class) ;
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
}

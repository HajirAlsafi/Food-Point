package com.example.hajiralsafi.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(){

            @Override
            public void run() {


                try {
                    sleep(3000);

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

                finally {

                    Intent intent = new Intent(getApplicationContext() , StartActivity.class);
                    startActivity(intent);
                }
            }
        };

        thread.start();
    }



   @Override
    protected void onPause() {
        super.onPause();

            finish();
        }
    }



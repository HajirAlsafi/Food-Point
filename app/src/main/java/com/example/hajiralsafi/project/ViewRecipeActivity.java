package com.example.hajiralsafi.project;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ViewRecipeActivity extends AppCompatActivity {


    Toolbar viewRecipeToolbar;
    TextView viewRicepeName, viewPrepTime, viewCookTime, viewIngredients, viewInstructions, ricepeUser;
    ImageView viewRecipeImage;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    String userId;
    Food food;
    Intent refIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);


        viewRecipeToolbar = findViewById(R.id.view_recipe_page_toolbar);

        setSupportActionBar(viewRecipeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewRecipeImage = (ImageView) findViewById(R.id.viewRecipeImage);
        viewRicepeName = (TextView) findViewById(R.id.viewRicepeName);
        viewPrepTime = (TextView) findViewById(R.id.viewPrepTime);
        viewCookTime = (TextView) findViewById(R.id.viewCookTime);
        viewIngredients = (TextView) findViewById(R.id.viewIngredients);
        viewInstructions = (TextView) findViewById(R.id.viewInstructions);
        ricepeUser = (TextView) findViewById(R.id.ricepeUser);

        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();



        refIntent = getIntent() ;
        String recipe_name = refIntent.getStringExtra("recipe_name") ;

        viewRicepeName.setText(recipe_name);
        getSupportActionBar().setTitle(recipe_name);

        String recipe_image =  refIntent.getStringExtra("recipe_image") ;
        Uri imageUri = Uri.parse(recipe_image);
        Glide.with(ViewRecipeActivity.this).load(imageUri).into(viewRecipeImage);

        String prep_time =  refIntent.getStringExtra("prep_time") ;
        viewPrepTime.setText(prep_time);


        String cook_time =  refIntent.getStringExtra("cook_time") ;
        viewCookTime.setText(cook_time);

        String ingredients =  refIntent.getStringExtra("ingredients") ;
        viewIngredients.setText(ingredients);

        String instructions =  refIntent.getStringExtra("instructions") ;
        viewInstructions.setText(instructions);


        refIntent = getIntent() ;
        String user_id = refIntent.getStringExtra("user_id") ;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(user_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString() ;

                ricepeUser.setText(name);


                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
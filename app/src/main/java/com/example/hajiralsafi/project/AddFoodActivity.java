package com.example.hajiralsafi.project;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import static io.opencensus.tags.TagKey.MAX_LENGTH;

public class AddFoodActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar addFoodToolbar;
    ConstraintLayout addRecipeRelative ;
    ImageView addRecipeImage , recipeImage ;
    EditText ingredients , instructions , ricepeName , prepTime , cookTime ;
    TextView tapToAddPhoto ;
    private FirebaseAuth firebaseAuth ;
    private DatabaseReference databaseReference;
    private StorageReference storageReference , referenceStorage ;
    String currentUserId , currentDate , currentTime ;
    private static final int goToGallery = 1;
    Uri recipePhoto ;
    LinearLayout addFoodLinear ;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.add_recipe_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);




        if (item.getItemId() == R.id.shareMenu) {

            if (ingredients.getText().toString().matches("") || instructions.getText().toString().matches("")
                    || ricepeName.getText().toString().matches("") || cookTime.getText().toString().matches("")
                    || prepTime.getText().toString().matches("")) {

                Toast.makeText(this, "provide all recipe information !", Toast.LENGTH_SHORT).show();

            } else {


                referenceStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                databaseReference = FirebaseDatabase.getInstance().getReference();

                HashMap<String, Object> hashMap = new HashMap<>();


                hashMap.put("recipe_name", ricepeName.getText().toString()) ;
                hashMap.put("prep_time" , prepTime.getText().toString()) ;
                hashMap.put("cook_time" , cookTime.getText().toString()) ;
                hashMap.put("ingredients", ingredients.getText().toString());
                hashMap.put("instructions" , instructions.getText().toString()) ;
                hashMap.put("recipe_image",uri.toString());
                hashMap.put("user_id", currentUserId);

                databaseReference.child("Recipes").push().setValue(hashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(AddFoodActivity.this, "Recipe shared successfully ", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), BrowsingActivity.class);
                                    startActivity(intent);
                                    finish();


                                } else {

                                    Toast.makeText(AddFoodActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });


            }

        }
        if (item.getItemId() == R.id.cancelMenu) {


            Intent intent = new Intent(getApplicationContext() ,BrowsingActivity.class) ;
            startActivity(intent);
            finish();
        }

        return true ;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);


        addFoodToolbar = findViewById(R.id.add_food_page_toolbar);

        setSupportActionBar(addFoodToolbar);
        getSupportActionBar().setTitle(R.string.new_recipe);

        addRecipeImage = (ImageView) findViewById(R.id.addRecipeImage);
        addRecipeRelative = (ConstraintLayout) findViewById(R.id.addRecipeRelative);
        recipeImage = (ImageView) findViewById(R.id.recipeImage) ;
        ingredients = (EditText) findViewById(R.id.ingredients) ;
        instructions = (EditText) findViewById(R.id.instructions) ;
        tapToAddPhoto = (TextView) findViewById(R.id.tapToAddPhoto) ;
        ricepeName = (EditText) findViewById(R.id.ricepeName) ;
        cookTime = (EditText) findViewById(R.id.cookTime) ;
        prepTime = (EditText) findViewById(R.id.prepTime) ;
        addFoodLinear = (LinearLayout) findViewById(R.id.addFoodLinear);

        tapToAddPhoto.setOnClickListener(this);
        recipeImage.setOnClickListener(this);
        addRecipeImage.setOnClickListener(this);
        addRecipeRelative.setOnClickListener(this);
        addFoodLinear.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance() ;
        currentUserId = firebaseAuth.getCurrentUser().getUid() ;
        storageReference = FirebaseStorage.getInstance().getReference("Recipes");
        referenceStorage = storageReference.child(currentUserId +System.currentTimeMillis() + ".jpg") ;

        Calendar callForDate = Calendar.getInstance() ;
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy") ;
        currentDate = date.format(callForDate.getTime()) ;

        Calendar callForTime = Calendar.getInstance() ;
        SimpleDateFormat time =new SimpleDateFormat("HH:mm:ss") ;
        currentTime = time.format(callForTime.getTime()) ;



    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.addRecipeRelative || v.getId() == R.id.addRecipeImage || v.getId() == R.id.addFoodLinear) {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        if (v.getId() == R.id.recipeImage || v.getId()== R.id.tapToAddPhoto) {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, goToGallery);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == goToGallery && resultCode == RESULT_OK && data != null) {

            recipePhoto = data.getData() ;

            recipeImage.setImageURI(recipePhoto);

            referenceStorage.putFile(recipePhoto).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        Log.i("photoUploading", "successful");
                    }else {

                        Log.i("photoUploading" , "failed" + task.getException()) ;

                    }
                }
            });

        }
    }

}



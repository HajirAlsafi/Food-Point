package com.example.hajiralsafi.project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddNewFeedActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar addNewFeedToolbar;
    CircleImageView addFeedProfileIcon;
    EditText addFeedField;
    TextView usernameDisplay;
    private DatabaseReference databaseReference;
    ImageView newFeedImageView;
    ConstraintLayout newFeedRelative;
    String currentDate , currentTime , postDate ;



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

            if (addFeedField.getText().toString().matches("")) {

                Toast.makeText(this, "you can't share empty posts !", Toast.LENGTH_SHORT).show();

            } else {


                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = currentUser.getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");


                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("user_id", currentUserId);
                hashMap.put("post", addFeedField.getText().toString());
                hashMap.put("date",postDate);


               databaseReference.child(currentUserId+currentTime).updateChildren(hashMap)
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {

                               if (task.isSuccessful()) {

                                   Toast.makeText(AddNewFeedActivity.this, "Post Posted successfully ", Toast.LENGTH_SHORT).show();
                                   Intent intent = new Intent(getApplicationContext(), BrowsingActivity.class);
                                   startActivity(intent);
                                   finish();


                               } else {

                                   Toast.makeText(AddNewFeedActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                               }

                           }
                       });


            }
        }
        if (item.getItemId() == R.id.cancelMenu) {


            Intent intent = new Intent(getApplicationContext(), BrowsingActivity.class);
            startActivity(intent);
            finish();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_feed);

        addNewFeedToolbar = findViewById(R.id.addFeed_page_toolbar);

        setSupportActionBar(addNewFeedToolbar);
        getSupportActionBar().setTitle(R.string.new_post);



        newFeedImageView = (ImageView) findViewById(R.id.newFeedImageView);
        newFeedRelative = (ConstraintLayout) findViewById(R.id.newFeedRelative);
        addFeedProfileIcon = (CircleImageView) findViewById(R.id.addFeedProfileIcon);
        addFeedField = (EditText) findViewById(R.id.addFeedField);
        usernameDisplay = (TextView) findViewById(R.id.usernameDisplay);


        newFeedRelative.setOnClickListener(this);
        newFeedImageView.setOnClickListener(this);


        String current_user = FirebaseAuth.getInstance().getCurrentUser().getUid() ;
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(current_user) ;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString() ;
                String image = dataSnapshot.child("image").getValue().toString() ;
                usernameDisplay.setText(name);


                Uri resultUri = Uri.parse(image) ;
              Glide.with(getApplicationContext()).load(resultUri).into(addFeedProfileIcon) ;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;


        Calendar callForDate = Calendar.getInstance() ;
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy") ;
        currentDate = date.format(callForDate.getTime()) ;

        Calendar callForTime = Calendar.getInstance() ;
        SimpleDateFormat time =new SimpleDateFormat("HH:mm") ;
        currentTime = time.format(callForTime.getTime()) ;
        postDate = currentDate +" at " + currentTime ;





    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.newFeedRelative || v.getId() == R.id.newFeedImageView) {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
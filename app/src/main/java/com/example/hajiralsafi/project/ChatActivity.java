package com.example.hajiralsafi.project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {



     Toolbar chatToolbar;
     RecyclerView chatRecyclerView ;
     ImageView chatImageView , sendMessageImageView , sendPhotoImageView ;
     ConstraintLayout chatConstraint ;
     EditText typeMessageEditText ;
     private FirebaseAuth auth ;
     private DatabaseReference reference , userRef , notificationRef ;
     private StorageReference storageReference ;
     CircleImageView profileIcon ;
     TextView displayNameTextView ;
     Intent intent ;
     String currentUser , user_id , currentDate , currentTime ;
     MessageAdapter messageAdapter ;
     List<Chat> chatList = new ArrayList<>() ;
     static final int GO_TO_GALLERY = 1;



    public void sendMessage (View view) {

     final String message = typeMessageEditText.getText().toString() ;


        if (message.isEmpty()) {

            Toast.makeText(this, R.string.empty_message, Toast.LENGTH_SHORT).show();
        }else {


            userRef.child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   // String name = dataSnapshot.child("name").getValue().toString() ;


                    reference = FirebaseDatabase.getInstance().getReference().child("Chats");

                    String current_user_ref = "message/" + currentUser+ "/" + user_id;
                    String user_ref = "message/" + user_id + "/" + currentUser;

                    DatabaseReference user_message_push = reference.child("message")
                            .child(currentUser).child(user_id).push();

                    String push_id = user_message_push.getKey();

                    HashMap<String, Object> messageMap = new HashMap<>();

                    messageMap.put("message", message);
                    messageMap.put("type" , "text") ;
                    messageMap.put("time", currentTime);
                    messageMap.put("date" , currentDate) ;
                    messageMap.put("from", currentUser);
                    messageMap.put("uid" , user_id);
                   // messageMap.put("name" , name) ;

                    HashMap<String, Object> messageUserMap = new HashMap<>();

                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(user_ref + "/" + push_id, messageMap);

                    typeMessageEditText.setText("");
                    reference.updateChildren(messageUserMap);


                    //Notifications
                   /* HashMap<String , String> notificationMap = new HashMap<>() ;

                    notificationMap.put("from" , currentUser) ;
                    notificationMap.put("message" , message) ;
                    notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications") ;
                    notificationRef.child(currentUser).push().setValue(notificationMap)
                    ;*/






                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

    }

    public void sendPhoto(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GO_TO_GALLERY);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


       chatToolbar = findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);



         messageAdapter = new MessageAdapter(chatList, ChatActivity.this);
         chatRecyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView) ;


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) ;
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);

        chatRecyclerView.setAdapter(messageAdapter);



        typeMessageEditText = (EditText) findViewById(R.id.typeMessageEditText) ;
        sendMessageImageView = (ImageView) findViewById(R.id.sendMessageImageView) ;
        chatImageView = (ImageView) findViewById(R.id.chatImageView) ;
        profileIcon = (CircleImageView) findViewById(R.id.profileIcon) ;
        displayNameTextView = (TextView) findViewById(R.id.displayNameTextView) ;
        chatConstraint = (ConstraintLayout) findViewById(R.id.chatConstraint) ;
        sendPhotoImageView = (ImageView) findViewById(R.id.sendPhotoImageView) ;

        chatImageView.setOnClickListener(this);
        chatConstraint.setOnClickListener(this);


        auth = FirebaseAuth.getInstance() ;
        currentUser = auth.getCurrentUser().getUid() ;
        storageReference = FirebaseStorage.getInstance().getReference().child("Chats_Images");

        intent = getIntent() ;
        user_id = intent.getStringExtra("userId");

        userRef = FirebaseDatabase.getInstance().getReference("Users") ;

        userRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString() ;
                String image = dataSnapshot.child("image").getValue().toString() ;

                displayNameTextView.setText(name);

                RequestOptions request = new RequestOptions() ;
                request.placeholder(R.drawable.profile_image);

                Glide.with(getApplicationContext()).setDefaultRequestOptions(request)
                        .load(image).into(profileIcon);

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


        showMessage();

    }





    public void showMessage () {


        reference = FirebaseDatabase.getInstance().getReference("Chats")
        .child("message").child(currentUser).child(user_id);

                reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @android.support.annotation.Nullable String s) {

                Chat chat = dataSnapshot.getValue(Chat.class);

                chatList.add(chat);
                messageAdapter.notifyDataSetChanged();
               chatRecyclerView.scrollToPosition(chatList.size() - 1);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @android.support.annotation.Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @android.support.annotation.Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GO_TO_GALLERY && resultCode == RESULT_OK && data != null && data.getData()!= null) {

            Uri uri = data.getData() ;

            reference = FirebaseDatabase.getInstance().getReference().child("Chats");

            final String current_user_ref = "message/" + currentUser+ "/" + user_id;
            final String user_ref = "message/" + user_id + "/" + currentUser;

            DatabaseReference user_message_push = reference.child("message")
                    .child(currentUser).child(user_id).push();

           final String push_id = user_message_push.getKey();

            final StorageReference imageStorage = storageReference.child(push_id +".jpg") ;
            imageStorage.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        imageStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {


                                HashMap<String, Object> messageMap = new HashMap<>();

                                messageMap.put("message", uri.toString());
                                messageMap.put("type" , "image") ;
                                messageMap.put("time", currentTime);
                                messageMap.put("date" , currentDate) ;
                                messageMap.put("from", currentUser);
                                messageMap.put("uid" , user_id);


                                HashMap<String, Object> messageUserMap = new HashMap<>();

                                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                                messageUserMap.put(user_ref + "/" + push_id, messageMap);

                                typeMessageEditText.setText("");
                                reference.updateChildren(messageUserMap);



                            }
                        }) ;


                    }else {

                        Toast.makeText(ChatActivity.this, "Something went Wrong , try again", Toast.LENGTH_SHORT).show();


                    }
                }
            });



        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.chatImageView || v.getId()==R.id.chatConstraint) {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE) ;
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken() , 0) ;
        }
    }
}

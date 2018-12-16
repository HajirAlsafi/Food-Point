package com.example.hajiralsafi.project;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener {

    EditText commentsEditText ;
    ConstraintLayout commentsRelative ;
    ImageView commentsImageView , commentsButton ;
    RecyclerView commentsRecyclerView ;
    private DatabaseReference userReference , postReference ;
    String post_key , currentUserId , currentDate , currentTime , commentKey , comment ;


    public void saveComment () {

        if (commentsEditText.getText().toString().matches("")) {

            Toast.makeText(CommentsActivity.this, "You can't send an empty comment !", Toast.LENGTH_SHORT).show();
        }else {

             commentKey = currentUserId +System.currentTimeMillis()   ;
             comment = commentsEditText.getText().toString() ;

            userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {


                        String name = dataSnapshot.child("name").getValue().toString() ;
                        HashMap<String , Object> commentsMap = new HashMap<>() ;
                        commentsMap.put("uid" , currentUserId) ;
                        commentsMap.put("comment" , comment) ;
                        commentsMap.put("date" , currentDate) ;
                        commentsMap.put("time" , currentTime) ;
                        commentsMap.put("name" , name) ;


                        postReference.child(commentKey).updateChildren(commentsMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            commentsEditText.setText("");

                                            Toast.makeText(CommentsActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(postReference, Comments.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comments_cardview, parent , false) ;



                return new CommentsViewHolder(view) ;
            }

            @Override
            protected void onBindViewHolder(@NonNull final CommentsViewHolder holder, int position, @NonNull final Comments model) {

                holder.commentUserName.setText(model.getName());
                holder.commentTextView.setText(model.getComment());
                holder.commentDate.setText(model.getDate()+ " at " + model.getTime());

                userReference.child(model.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String image = dataSnapshot.child("image").getValue().toString() ;

                        Uri resultUri = Uri.parse(image) ;
                        Glide.with(CommentsActivity.this).load(resultUri).into(holder.commentProfileIcon) ;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }) ;

            }
        };

        commentsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        TextView commentUserName , commentDate , commentTextView ;
        CircleImageView commentProfileIcon ;

        public CommentsViewHolder(View itemView) {
            super(itemView);

            commentUserName = (TextView) itemView.findViewById(R.id.commentUserName) ;
            commentDate = (TextView) itemView.findViewById(R.id.commentDate) ;
            commentTextView = (TextView) itemView.findViewById(R.id.commentTextView) ;
            commentProfileIcon = (CircleImageView) itemView.findViewById(R.id.commentProfileIcon) ;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentsEditText = (EditText) findViewById(R.id.commentEditText) ;
        commentsRelative = (ConstraintLayout) findViewById(R.id.commentRelative);
        commentsButton = (ImageView) findViewById(R.id.commentButton) ;
        commentsImageView = (ImageView) findViewById(R.id.commentImageView) ;


        commentsRelative.setOnClickListener(this);
        commentsImageView.setOnClickListener(this);
        commentsButton.setOnClickListener(this);

        commentsRecyclerView = (RecyclerView) findViewById(R.id.commentsRecyclerView) ;
        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent() ;
        post_key = intent.getStringExtra("PostKey") ;

        userReference = FirebaseDatabase.getInstance().getReference().child("Users") ;
        postReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_key).child("Comments") ;
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid() ;


        Calendar callForDate = Calendar.getInstance() ;
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy") ;
        currentDate = date.format(callForDate.getTime()) ;

        Calendar callForTime = Calendar.getInstance() ;
        SimpleDateFormat time =new SimpleDateFormat("HH:mm") ;
        currentTime = time.format(callForTime.getTime()) ;










    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.commentImageView || v.getId() == R.id.commentRelative) {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        if (v.getId() == R.id.commentButton) {

            saveComment() ;
        }

    }
}

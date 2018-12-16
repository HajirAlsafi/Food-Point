package com.example.hajiralsafi.project;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView homeList;
    private DatabaseReference reference ,  usersReference , likesRef ;
    boolean likePressed = false ;
    private FirebaseAuth firebaseAuth ;
    String currentUser ;
    Notification notification ;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        homeList = view.findViewById(R.id.homeList);
        homeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        firebaseAuth = FirebaseAuth.getInstance() ;
        currentUser = firebaseAuth.getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes") ;




        return view;


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<HomeFeeds> options =
                new FirebaseRecyclerOptions.Builder<HomeFeeds>()
                        .setQuery(reference, HomeFeeds.class)
                        .build();


        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<HomeFeeds, PostViewHolder>(options) {


            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_list_item, parent , false) ;


                return new PostViewHolder(view) ;

            }

            @Override
            protected void onBindViewHolder(@NonNull final PostViewHolder  holder, final int position, @NonNull final HomeFeeds model) {

                holder.feedTextView.setText(model.getPost());
                holder.dateAndTime.setText(model.getDate());
                final String postKey = getRef(position).getKey();

                final String user_id = model.getUser_id() ;
                usersReference = FirebaseDatabase.getInstance().getReference("Users").child(user_id) ;
                usersReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        String name = dataSnapshot.child("name").getValue().toString() ;
                        String image = dataSnapshot.child("image").getValue().toString() ;

                        holder.userName.setText(name);
                        if( getActivity() != null ) {
                            Uri resultUri = Uri.parse(image);
                            Glide.with(getActivity())
                                    .load(resultUri).into(holder.profileIcon);
                        }

                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }) ;


                holder.setCommentsButtonStatus(postKey);
                holder.setLikeButtonStatus(postKey);

                holder.likeImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        likePressed = true;
                        final String postKey = getRef(position).getKey();
                        holder.setLikeButtonStatus(postKey);




                            likesRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (likePressed) {

                                    if (dataSnapshot.child(postKey).hasChild(currentUser)) {

                                        likesRef.child(postKey).child(currentUser).removeValue();
                                        likePressed = false;


                                    } else {

                                        likesRef.child(postKey).child(currentUser).setValue("Liked");
                                        likePressed = false;

                                    }
                                }
                            }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                    }
                });


                holder.chatTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //go to chat activity
                        Intent intent = new Intent(getContext() , ChatActivity.class) ;
                        intent.putExtra("userId" , user_id) ;
                        startActivity(intent);
                    }
                });

                holder.commentsTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        Intent intent = new Intent(getContext() , CommentsActivity.class) ;
                        intent.putExtra("PostKey" , postKey) ;
                        startActivity(intent);
                    }
                });



            }
        };

        homeList.setAdapter(adapter);
        adapter.startListening();
    }

        public static class PostViewHolder extends RecyclerView.ViewHolder {

            TextView  userName , feedTextView, dateAndTime , likesTextView , chatTextView  , commentsTextView;
            CircleImageView profileIcon ;
            ImageView likeImageView ;
            private DatabaseReference likesRef ,  CommentsReference ;
            int countLikes , countComments;
            String  currentUser ;


            public PostViewHolder(View itemView) {
                super(itemView);

                dateAndTime = (TextView)itemView.findViewById(R.id.dateAndTime) ;
                userName = (TextView) itemView.findViewById(R.id.userName) ;
                feedTextView = (TextView) itemView.findViewById(R.id.feedTextView) ;
                profileIcon = (CircleImageView) itemView.findViewById(R.id.profileIcon) ;
                likesTextView = (TextView) itemView.findViewById(R.id.likesTextView) ;
                chatTextView = (TextView) itemView.findViewById(R.id.chatTextView) ;
                likeImageView = (ImageView) itemView.findViewById(R.id.likeImageView) ;
                commentsTextView = (TextView) itemView.findViewById(R.id.commentsTextView) ;

                likesRef = FirebaseDatabase.getInstance().getReference().child("Likes") ;
                currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                CommentsReference = FirebaseDatabase.getInstance().getReference().child("Posts") ;


            }


            //ُEdit likes and comments counting

            public void setLikeButtonStatus (final String post_key) {


                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(post_key).hasChild(currentUser)) {

                            countLikes = (int) dataSnapshot.child(post_key).getChildrenCount() ;
                            likeImageView.setImageResource(R.drawable.liked_button);
                            likesTextView.setText(Integer.toString(countLikes) + "Likes");
                        }else {

                            countLikes = (int) dataSnapshot.child(post_key).getChildrenCount() ;
                            likeImageView.setImageResource(R.drawable.like_button);
                            likesTextView.setText(Integer.toString(countLikes) + "Likes");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            public void setCommentsButtonStatus(final String post_key) {


                CommentsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(post_key).hasChild("Comments")) {

                            countComments = (int) dataSnapshot.child(post_key).child("Comments").getChildrenCount() ;
                            commentsTextView.setText(Integer.toString(countComments) + "Comments");
                        }else {

                            countLikes = (int) dataSnapshot.child("Comments").getChildrenCount() ;
                            commentsTextView.setText(Integer.toString(countComments)+ "Comments");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }


            }


}

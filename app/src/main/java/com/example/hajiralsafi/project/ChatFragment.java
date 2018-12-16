package com.example.hajiralsafi.project;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


   RecyclerView chatsRecyclerView ;
   DatabaseReference databaseReference , chatsRef ;
   FirebaseAuth auth ;
   String current_user_id ;
    View view ;



    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_chat, container, false);


        chatsRecyclerView = (RecyclerView) view.findViewById(R.id.chatsRecycler);
        chatsRecyclerView.setHasFixedSize(true);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        auth = FirebaseAuth.getInstance() ;
        current_user_id = auth.getCurrentUser().getUid() ;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        chatsRef = FirebaseDatabase.getInstance().getReference().child("Chats").child("message").child(current_user_id) ;




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(chatsRef, Chat.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Chat, ChatsViewHolder>(options) {
            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.display_chats_layout, parent , false) ;



                return new ChatsViewHolder(view) ;
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull final Chat model) {

                final String user_id = getRef(position).getKey();


                Query lastMsgQuery = chatsRef.child(user_id).limitToLast(1) ;
                lastMsgQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        String lastMsg = dataSnapshot.child("message").getValue().toString() ;
                        String type = dataSnapshot.child("type").getValue().toString() ;
                        String time = dataSnapshot.child("date").getValue().toString() ;

                        holder.timeTextView.setText(time);

                        if (type.equals("text")) {

                            holder.lastMsgtTextView.setText(lastMsg);


                        }else {

                            holder.lastMsgtTextView.setText(R.string.photo);



                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                databaseReference.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("name").getValue().toString() ;
                        String image = dataSnapshot.child("image").getValue().toString() ;

                        holder.nameTextView.setText(name);
                        Glide.with(getActivity()).load(image).into(holder.usersImageView) ;


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // go to chat activity
                        Intent intent = new Intent(getContext() , ChatActivity.class) ;
                        intent.putExtra("userId" , user_id) ;
                        startActivity(intent);
                    }
                });


            }
        };

        chatsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        CircleImageView usersImageView ;
        TextView nameTextView , lastMsgtTextView , timeTextView ;

        public ChatsViewHolder(View itemView) {
            super(itemView);

            usersImageView = (CircleImageView) itemView.findViewById(R.id.usersImageView) ;
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView) ;
            lastMsgtTextView = (TextView) itemView.findViewById(R.id.lastMsgtTextView);
            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView) ;


        }
    }
}

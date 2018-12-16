package com.example.hajiralsafi.project;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public Context context ;
    public List<Chat> chat ;
    public static final int MSG_TYPE_RIGHT = 0 ;
    public static final int MSG_TYPE_LEFT = 1 ;
    private FirebaseUser firebaseUser ;




    public MessageAdapter (List<Chat> chat , Context context ) {

        this.chat = chat;
        this.context = context;



    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        if (viewType == MSG_TYPE_RIGHT) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_display_layout_two, parent, false);

            return new ViewHolder(view);
        }else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_display_layout, parent, false);

            return new ViewHolder(view);
        }



    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {

        final Chat mChat = chat.get(position) ;

        if (mChat.getType().equals("text")){

            holder.showTextView.setText(mChat.getMessage());
            holder.showTextView.setVisibility(View.VISIBLE);
            holder.sentPhoto.setVisibility(View.GONE);


        }else {

            Glide.with(context).load(mChat.getMessage()).into(holder.sentPhoto) ;
            holder.showTextView.setVisibility(View.GONE);
            holder.sentPhoto.setVisibility(View.VISIBLE);

        }

        holder.timeTextView.setText(mChat.getTime());

    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView showTextView , timeTextView ;
        ImageView sentPhoto ;


        public ViewHolder(View itemView) {
            super(itemView);


            showTextView = (TextView) itemView.findViewById(R.id.show_message) ;
            timeTextView = (TextView) itemView.findViewById(R.id.time);
            sentPhoto = (ImageView) itemView.findViewById(R.id.sentPhoto) ;

        }
    }


    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        if (chat.get(position).getFrom().equals(firebaseUser.getUid())) {

            return MSG_TYPE_RIGHT ;
        }else {

            return MSG_TYPE_LEFT ;
        }
    }
}




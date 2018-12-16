package com.example.hajiralsafi.project;


import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {


    private View view ;
    private RecyclerView searchingRecyclerView  ;
    private DatabaseReference reference ;
    private ImageView searchImageView ;
    private EditText searchEditText ;
    private FirebaseAuth firebaseAuth ;
    private String user_id ;

    public SearchFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);



        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid() ;
        reference = FirebaseDatabase.getInstance().getReference("Users") ;

        searchImageView = (ImageView) view.findViewById(R.id.searchImageView) ;
        searchEditText = (EditText) view.findViewById(R.id.searchEditText) ;
        searchingRecyclerView = (RecyclerView) view.findViewById(R.id.searchingRecyclerView) ;
        searchingRecyclerView.setHasFixedSize(true);
        searchingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

      /*  searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().isEmpty()) {

                    searching(s.toString());
                }else {

                    searching("");
                }

            }
        });*/

       searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String search = searchEditText.getText().toString() ;

                searching(search);
            }
        });

        return view ;
    }

    public void searching (String search) {


        Query query = reference.orderByChild("name").startAt(search).endAt(search + "\uf8ff") ;

        FirebaseRecyclerOptions<HomeFeeds> options =
                new FirebaseRecyclerOptions.Builder<HomeFeeds>()
                        .setQuery(query, HomeFeeds.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<HomeFeeds, SearchingViewHolder>(options) {
            @NonNull
            @Override
            public SearchingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_custom_bar, parent , false) ;


                return new SearchingViewHolder(view) ;
            }

            @Override
            protected void onBindViewHolder(@NonNull SearchingViewHolder holder, int position, @NonNull HomeFeeds model) {


                holder.displayNameTextView.setText(model.getName());
               holder.displayNameTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                Uri uri = Uri.parse(model.getImage()) ;
                Glide.with(getContext()).load(uri).into(holder.profile_image) ;

            }

        };

        searchingRecyclerView.setAdapter(adapter);

    }

    public static class SearchingViewHolder extends RecyclerView.ViewHolder {

       TextView displayNameTextView ;
       CircleImageView profile_image;


        public SearchingViewHolder(View itemView) {

            super(itemView);

          displayNameTextView = (TextView) itemView.findViewById(R.id.displayNameTextView) ;
          profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image) ;


        }
    }
}

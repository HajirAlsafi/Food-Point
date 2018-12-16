package com.example.hajiralsafi.project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import de.hdodenhof.circleimageview.CircleImageView;

public class BrowsingActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar browsingToolbar;
    ActionBar actionBar ;
    private DatabaseReference reference ;
    com.getbase.floatingactionbutton.FloatingActionButton action1 , action2 ;
    BottomNavigationView nav_bottom ;
    FrameLayout frame_Layout ;
    HomeFragment homeFragment ;
    FoodFragment foodFragment ;
    ChatFragment chatFragment ;
    SearchFragment searchFragment ;
    ImageView browsingImageView ;
    CircleImageView profileIcon ;
    TextView displayNameTextView ;
    String current_user ;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.aap_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case R.id.logoutMenu:

                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(getApplicationContext() , StartActivity.class) ;
                startActivity(logoutIntent);
                finish();


                return true;

            case R.id.profileSetting:


                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);

                return true;

            case R.id.searchMenu :

                //complete searching fragment
                setFragment(searchFragment);
                browsingImageView.setImageResource(R.drawable.searchimage);
                return true ;

            default:
                return false;
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing);

        browsingToolbar = findViewById(R.id.browsing_page_toolbar);

        setSupportActionBar(browsingToolbar);


        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        profileIcon = (CircleImageView) findViewById(R.id.profileIcon) ;
        displayNameTextView = (TextView) findViewById(R.id.displayNameTextView) ;
        current_user = FirebaseAuth.getInstance().getCurrentUser().getUid() ;



        action1 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action1) ;
        action2 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action2) ;

        action1.setOnClickListener(this);
        action2.setOnClickListener(this);


        nav_bottom = findViewById(R.id.nav_bottom) ;
        frame_Layout = findViewById(R.id.frame_layout) ;
        browsingImageView = findViewById(R.id.browsingImageView) ;


        homeFragment = new HomeFragment() ;
        foodFragment = new FoodFragment() ;
        chatFragment = new ChatFragment() ;
        searchFragment = new SearchFragment() ;

        setFragment(homeFragment);


        nav_bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.homeMenu :

                        setFragment (homeFragment) ;
                        browsingImageView.setImageResource(R.drawable.feed);
                        return true ;

                    case R.id.foodMenu :

                        setFragment(foodFragment) ;
                        browsingImageView.setImageResource(R.drawable.fragmet3);
                        return true ;

                    case R.id.chatMenu :

                        setFragment(chatFragment) ;
                        browsingImageView.setImageResource(R.drawable.fragment2);

                        return true ;


                    default:
                        return false ;


                }


            }
        });



    }


    public void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction() ;
        fragmentTransaction.replace(R.id.frame_layout , fragment) ;
        fragmentTransaction.addToBackStack(null) ;
        fragmentTransaction.commit() ;
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.action1) {

            //go To add naw post activity
            Log.i("button clicked", "true");
            Intent intent = new Intent(getApplicationContext() , AddNewFeedActivity.class) ;
            startActivity(intent);

        }
        if (v.getId() == R.id.action2) {

                //go To add new recipe activity
                Log.i("button clicked", "true") ;
            Intent intent = new Intent(getApplicationContext() , AddFoodActivity.class) ;
            startActivity(intent);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser() ;
        if(user == null) {

            Intent logoutIntent = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(logoutIntent);
            finish();


        }else {


            reference = FirebaseDatabase.getInstance().getReference().child("Users") ;

            reference.child(current_user).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.child("name").exists()) {

                        Intent intent = new Intent(getApplicationContext() ,ProfileActivity.class) ;
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
                        startActivity(intent);
                        finish();

                    }else {

                            String name = dataSnapshot.child("name").getValue().toString();
                            String image = dataSnapshot.child("image").getValue().toString();
                            displayNameTextView.setText(name);

                        Uri resultUri = Uri.parse(image);
                            Glide.with(getApplicationContext())
                                    .load(resultUri).into(profileIcon);
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

}

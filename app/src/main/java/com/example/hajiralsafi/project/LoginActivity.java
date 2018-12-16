package com.example.hajiralsafi.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int RC_SIGN_IN = 1 ;
    private static final String TAG = "LoginActivity" ;
    Toolbar loginToolbar ;
    ConstraintLayout loginRelative ;
    ImageView loginImage ;
    TextView forgetPasswordTextView ;
    EditText emailText , passwordText ;
    Button loginButton ;
    private FirebaseAuth mAuth ;
    private ProgressDialog progressDialog;
    private SignInButton googleSignInBtn ;
    private GoogleSignInClient mGoogleSignInClient ;
    private DatabaseReference databaseReference ;



    public void login (View view) {

        if (emailText.getText().toString().matches("")) {

            emailText.setError("Enter a valid email");
            emailText.requestFocus() ;
        }

        if (passwordText.getText().toString().matches("")) {

            passwordText.setError("Invalid password!");
            passwordText.requestFocus() ;
        }

        else {

            progressDialog.setMessage("Logging ...");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(emailText.getText().toString() , passwordText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {


                                String current_user_id = mAuth.getCurrentUser().getUid() ;
                                String device_token = FirebaseInstanceId.getInstance().getToken() ;


                                databaseReference.child(current_user_id).child("device_token").setValue(device_token);


                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                                //go To profile
                                Intent intent = new Intent(getApplicationContext() ,BrowsingActivity.class) ;
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
                                startActivity(intent);
                                finish();
                            }else {

                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Coudln't login "+task.getException()+", Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        loginToolbar = findViewById(R.id.Login_page_toolbar);
        setSupportActionBar(loginToolbar);
        getSupportActionBar().setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginRelative = findViewById(R.id.loginRelative);
        loginImage = findViewById(R.id.loginImage);
        emailText = findViewById(R.id.emailText);
        forgetPasswordTextView = (TextView) findViewById(R.id.forgetPasswordTextView);
        passwordText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);
        googleSignInBtn = findViewById(R.id.googleLoginBtn);

        loginRelative.setOnClickListener(this);
        loginImage.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        forgetPasswordTextView.setOnClickListener(this);
        googleSignInBtn.setOnClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users") ;


        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data) ;
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class) ;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
               Log.w(TAG , "Google sign in failed", e) ;
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this,"you are not able to log in to google",Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }


                    }
                });
    }

  private void updateUI(FirebaseUser user) {

       final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
       final String user_id = mAuth.getCurrentUser().getUid();
        if (acct != null) {

           databaseReference.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   if (dataSnapshot.child(user_id).exists()) {

                       String personName = acct.getDisplayName();

                       String device_token = FirebaseInstanceId.getInstance().getToken() ;

                       databaseReference.child(user_id).child("device_token").setValue(device_token);

                       Intent intent = new Intent(getApplicationContext(), BrowsingActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);
                       finish();


                       Toast.makeText(LoginActivity.this, "Hello " + personName, Toast.LENGTH_SHORT).show();
                   }else {

                       Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);
                       finish();
                   }
                   }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }

            });


            }

        }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.loginRelative || v.getId() == R.id.loginImage) {

            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE) ;
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken() , 0) ;
        }
        if(v.getId() == R.id.loginButton){

            login(v) ;
        }
        if (v.getId() == R.id.forgetPasswordTextView) {

            // forgetPassword
            Intent intent = new Intent(getApplicationContext() , ResetPasswordActivity.class);
            startActivity(intent);
        }
        if(v.getId() == R.id.googleLoginBtn) {

            signIn();

        }

    }



}

package css.cis3334.firebaseauthentication;

import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener{

    private TextView textViewStatus;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonGoogleLogin;
    private Button buttonCreateLogin;
    private Button buttonSignOut;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_SIGN_IN_FLAG = 9001;

    /*
     * onCreate method runs the program when the application starts
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonGoogleLogin = (Button) findViewById(R.id.buttonGoogleLogin);
        buttonCreateLogin = (Button) findViewById(R.id.buttonCreateLogin);
        buttonSignOut = (Button) findViewById(R.id.buttonSignOut);

        mAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(new View.OnClickListener() {      //set onclick listener for login button
            public void onClick(View v) {
                signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                textViewStatus.setText("Logging in");
            }
        });

        buttonCreateLogin.setOnClickListener(new View.OnClickListener() {   //set onclick listener for create button
            public void onClick(View v) {
                createAccount(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                textViewStatus.setText("Creating a new account");
            }
        });

        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {   //set onclick listener for goggle login button
            public void onClick(View v) {
                textViewStatus.setText("Signing in with Google Account");
                googleSignIn();    //calling sign in method
            }
        });

        buttonSignOut.setOnClickListener(new View.OnClickListener() {   //set onclick listener fpr sign out button
            public void onClick(View v) {
                textViewStatus.setText("Signing out");
                signOut();     //calling sign out method
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {     //Set mAuthListener to a new FirebaseAuth object
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();     //create a new FirebaseUser object
                if (user != null) {

                    textViewStatus.setText("User signed in");    // User is signed in
                } else {

                    textViewStatus.setText("User signed out");     // User is signed out
                }
                // ...
            }
        };


        //Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            textViewStatus.setText("Google Account Fail");
        }

        private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            textViewStatus.setText("Sign In successful");

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                textViewStatus.setText("Aunthentication failed");
                            }
                            // ...
                        }
                    });
        }



    /*
     * method called to start displaying activity to user
     */
    @Override
    public void onStart() {
        super.onStart();     //activity is visible to user
        mAuth.addAuthStateListener(mAuthListener);
    }

    /*
     * method called to stop displaying activity to user
     */
    @Override
    public void onStop() {
        super.onStop();   //activity invisible to user
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /*
     * method creates a user firebase account
     * @param email
     * @param password
     */

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        textViewStatus.setText("User account created");  //task occurs when account creation is successful

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            textViewStatus.setText("Aunthentication failed");  //error occurs if account creation fails

                        }

                        // ...
                    }
                });
    }

    /*
     * method is used to sign in firebase account
     * @param email
     * @param password
     */

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        textViewStatus.setText("User logging in successful");  //task occurs when user signs in successfully

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            textViewStatus.setText("Aunthentication failed");    //error occurs when user does not sign in correctly, with wrong credentials

                        }

                        // ...
                    }
                });
    }

    /*
     * method is used to sign out of firebase account
     */

    private void signOut () {
        mAuth.signOut();

    }

    /*
     * method used to sign in using google account credentials
     */

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_FLAG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_FLAG) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    }


package com.makgyber.vbuys.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.makgyber.vbuys.R;
import com.makgyber.vbuys.models.User;

import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private final static String COLLECTION = "user";
    private static final String TAG = "SignInActivity";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userDbRef = db.collection(COLLECTION);

    List<AuthUI.IdpConfig> providers;

    private final static int RC_SIGN_IN = 5;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    // do nothing
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    Log.d(TAG, "onCreate: " + user);
                    getUserDetails();
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                } else {
                    setupSignInUI();
                }
            }
        };
    }

    private void setupSignInUI() {
        // Choose authentication providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build()
                , new AuthUI.IdpConfig.EmailBuilder().build()
                , new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .setLogo(R.drawable.full_banner_icon)
                        .setTosAndPrivacyPolicyUrls("https://www.google.com", "https://www.yahoo.com")
//                        .setAlwaysShowSignInMethodScreen(true)
//                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "onActivityResult: " + user);
                getUserDetails();

                finish();
            } else {
                Toast.makeText(SignInActivity.this, ""+response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void getUserDetails() {
        Query userQuery = userDbRef.whereEqualTo("uid", user.getUid());
        userQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    if (document.isEmpty()) {
                        Log.d(TAG, "onComplete: no user profile yet. add one here.");
                        initializeUserProfile();
                    } else {
                        Log.d(TAG, "onComplete: user profile displayName. " + document.getDocuments().get(0).get("displayName"));
                        Log.d(TAG, "onComplete: user profile phoneNumber. " + document.getDocuments().get(0).get("phoneNumber"));
                        Log.d(TAG, "onComplete: user profile email. " + document.getDocuments().get(0).get("email"));
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userId", document.getDocuments().get(0).getId());
                        if ( null!=document.getDocuments().get(0).get("email") )
                            editor.putString("email", document.getDocuments().get(0).get("email").toString());
                        if ( null!=document.getDocuments().get(0).get("displayName") )
                            editor.putString("displayName", document.getDocuments().get(0).get("displayName").toString());
                        if ( null!=document.getDocuments().get(0).get("phoneNumber") )
                            editor.putString("phoneNumber",document.getDocuments().get(0).get("phoneNumber").toString());
                        if ( null!=document.getDocuments().get(0).get("address") )
                            editor.putString("address", document.getDocuments().get(0).get("address").toString());
                        if ( null!=document.getDocuments().get(0).get("facebook") )
                            editor.putString("facebook", document.getDocuments().get(0).get("facebook").toString());
                        if ( null!=document.getDocuments().get(0).get("twitter") )
                            editor.putString("twitter", document.getDocuments().get(0).get("twitter").toString());
                        if ( null!=document.getDocuments().get(0).get("photoUrl") )
                            editor.putString("photoUrl", document.getDocuments().get(0).get("photoUrl").toString());
                        editor.putString("deliveryRadius", "5");
                        editor.commit();
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    }
                }
            }
        });
    }

    private void initializeUserProfile() {
        String email = user.getEmail().isEmpty() ? "my email" : user.getEmail();
        String displayName = user.getDisplayName().isEmpty() ? "" : user.getDisplayName();
        String phoneNumber = user.getPhoneNumber().isEmpty() ? "my phone" : user.getPhoneNumber();
        String photoUrl = user.getPhotoUrl() == null ? "https://firebasestorage.googleapis.com/v0/b/villagebuys-13fa9.appspot.com/o/images%2Fusers%2FiOWv9iplGZC29sd1EJSb.jpg?alt=media&token=6487b466-5377-4589-b621-76809228188e" : user.getPhotoUrl().toString();
        String address = "my city";
        String facebook = "my facebook";
        String twitter = "my twitter";

        User newUser = new User(email, phoneNumber, displayName, photoUrl, address, user.getUid());

        DocumentReference userDocRef = userDbRef.document();
        String userProfileId = userDocRef.getId();
        userDocRef.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: we're good, proceed please." + userProfileId);

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId", userProfileId);
                editor.putString("email", email);
                editor.putString("displayName", displayName);
                editor.putString("phoneNumber",phoneNumber);
                editor.putString("address", address);
                editor.putString("facebook", facebook);
                editor.putString("twitter", twitter);
                editor.putString("photoUrl", photoUrl);
                editor.putString("deliveryRadius", "5");

                editor.commit();
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: we have a problem with your identification");
            }
        });
    }
}

package com.makgyber.vbuys.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.makgyber.vbuys.R;
import com.makgyber.vbuys.adapters.ProductAdapter;
import com.makgyber.vbuys.fragments.BuyerMainFragment;
import com.makgyber.vbuys.fragments.ChatFragment;
import com.makgyber.vbuys.fragments.FeedbackFragment;
import com.makgyber.vbuys.fragments.ProfileFragment;
import com.makgyber.vbuys.fragments.SellerDashboardFragment;
import com.makgyber.vbuys.fragments.TindahanListFragment;

public class SellerMainActivity extends AppCompatActivity {

    private static final String TAG = "SellerMainActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef = db.collection("product");
    private CollectionReference userRef = db.collection("user");
    private CollectionReference chatRef = db.collection("chat");
    private ProductAdapter adapter;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

//        BottomNavigationView navigationView = findViewById(R.id.bnv_seller_main);
//        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch(item.getItemId()) {
//                    case R.id.seller_dashboard:
//                        loadFragment(new SellerDashboardFragment());
//                        return true;
//                    case R.id.seller_settings:
//                        loadFragment(new TindahanListFragment());
//                        return true;
//                    case R.id.seller_feedback:
//                        loadFragment(new FeedbackFragment());
//                        return true;
//                }
//                return false;
//            }
//        });

        getSupportActionBar().setTitle("My Stores");

//        Query query = chatRef.whereEqualTo("sellerSeen", false);
//        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                if (queryDocumentSnapshots.size() > 0) {
//                    navigationView.getOrCreateBadge(R.id.action_messages).setVisible(true);
//                }
//            }
//        });

        loadFragment(new TindahanListFragment());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setIcon(R.drawable.white_vb_icon);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seller_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_store) {
            startActivity(new Intent(SellerMainActivity.this, StoreSetupActivity.class));
            return true;
        }

        if (id == R.id.action_home) {
            startActivity(new Intent(SellerMainActivity.this, MainActivity.class));
            return true;
        }

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();

            getApplicationContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE).edit().clear().commit();
            getApplicationContext().getSharedPreferences("TINDAHAN", MODE_PRIVATE).edit().clear().commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onNavigateUpFromChild(Activity child) {
        Toast.makeText(SellerMainActivity.this, "I come from :  " + child.getLocalClassName().toString(), Toast.LENGTH_SHORT).show();
        return super.onNavigateUpFromChild(child);
    }
}

package com.makgyber.vsells.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.makgyber.vsells.models.Product;
import com.makgyber.vsells.R;
import com.makgyber.vsells.adapters.SearchProductResultsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchableActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef = db.collection("product");
    private SearchProductResultsAdapter adapter;
    private final static String TAG = "SearchableActivity";
    private final static int AUTOCOMPLETE_REQUEST_CODE = 2;
    SharedPreferences sharedPreferences;
    SearchView sv;
    String query;
    TextView tvSearchPlace;
    Spinner spinnerRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        getSupportActionBar().setTitle("");

        if (!Places.isInitialized()) {
            String apiKey = getString(R.string.google_api_key);
            Places.initialize(SearchableActivity.this, apiKey);
        };

        tvSearchPlace = findViewById(R.id.tv_search_places);
        tvSearchPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields).setCountry("PH")
                        .build(SearchableActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sv = (SearchView) menu.findItem(R.id.search).getActionView();
        sv.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        sv.setIconifiedByDefault(false);
        sv.setLayoutParams(new ActionBar.LayoutParams(Gravity.LEFT));
        sv.setQuery(query, false);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doMySearch(query);
                sv.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doMySearch(newText);
                return false;
            }
        });


        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {

        Log.d(TAG, "doMySearch: " + query);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
        Double deliveryLat = Double.parseDouble(sharedPreferences.getString("deliveryLatitude", "14.6"));
        Double deliveryLng = Double.parseDouble(sharedPreferences.getString("deliveryLatitude", "120.99"));
        Double deliveryRadius = Double.parseDouble(sharedPreferences.getString("deliveryRadius", "5"));

        double lat = 0.009009009; //111km
        double lon = 0.01136363636; //88km

        double lowerLat = deliveryLat - (lat * deliveryRadius);
        double lowerLon = deliveryLng - (lon * deliveryRadius);

        double greaterLat = deliveryLat + (lat * deliveryRadius);
        double greaterLon = deliveryLng + (lon * deliveryRadius);

        GeoPoint lesserGeopoint = new GeoPoint(lowerLat, lowerLon);
        GeoPoint greaterGeopoint = new GeoPoint(greaterLat, greaterLon);

        Query searchQuery = productRef.whereEqualTo("publish", true)
                .whereArrayContains("tags", query.trim())
                .whereGreaterThan("position", lesserGeopoint)
                .whereLessThan("position", greaterGeopoint);

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(searchQuery, new SnapshotParser<Product>() {
                    @NonNull
                    @Override
                    public Product parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Product product = snapshot.toObject(Product.class);
                        product.setId( snapshot.getId() );
                        return product;
                    }
                })
                .build();

        adapter = new SearchProductResultsAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.rv_search_product_result);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchableActivity.this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleIntent(getIntent());
        sharedPreferences = getApplicationContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
        String deliveryLocation = sharedPreferences.getString("deliveryLocation", "");
        tvSearchPlace.setText(deliveryLocation);
        spinnerSetup();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                String address = place.getAddress();
                tvSearchPlace.setText(address);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("deliveryLocation", address);
                editor.putString("deliveryLatitude", Double.toString(place.getLatLng().latitude));
                editor.putString("deliveryLatitude", Double.toString(place.getLatLng().latitude));
                editor.commit();
                doMySearch(query);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(SearchableActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void spinnerSetup() {
        spinnerRadius = findViewById(R.id.spinner_radius);
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("1");
        categories.add("5");
        categories.add("10");
        categories.add("20");
        categories.add("30");
        categories.add("50");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SearchableActivity.this, R.layout.spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerRadius.setAdapter(dataAdapter);
        String deliveryRadius = sharedPreferences.getString("deliveryRadius", "5");
        spinnerRadius.setSelection(dataAdapter.getPosition(deliveryRadius));
        spinnerRadius.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString().replace("km", "");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("deliveryRadius", item);
                editor.commit();
                doMySearch(query);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}



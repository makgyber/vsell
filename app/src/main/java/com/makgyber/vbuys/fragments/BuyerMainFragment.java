package com.makgyber.vbuys.fragments;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.makgyber.vbuys.activities.MainActivity;
import com.makgyber.vbuys.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuyerMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuyerMainFragment extends Fragment {
    private TextView ivHealth, ivFood, ivServices, ivRealty, ivDevices, ivDelivery, tvWelcome;
    private String displayName;
    private final static int AUTOCOMPLETE_REQUEST_CODE = 2;
    private final static String TAG = "BuyerMainFragment";
    SharedPreferences sharedPreferences;
    private Spinner spinnerRadius;

    public BuyerMainFragment() {
        // Required empty public constructor
    }


    public static BuyerMainFragment newInstance(String displayName) {
        BuyerMainFragment fragment = new BuyerMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putString("displayName", displayName);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            displayName = getArguments().getString("displayName");
        }
        if (!Places.isInitialized()) {
            String apiKey = getString(R.string.google_api_key);
            Places.initialize(getContext(), apiKey);
        };


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buyer_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupIconButtons(view);
    }

    private void setupIconButtons(View view) {
        ivServices = view.findViewById(R.id.tv_services);
        ivFood = view.findViewById(R.id.tv_food);
        ivDevices = view.findViewById(R.id.tv_devices);
        ivDelivery = view.findViewById(R.id.tv_delivery);


        ivFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.searchManager.triggerSearch("food", getActivity().getComponentName(), null);
            }
        });

        ivDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.searchManager.triggerSearch("devices", getActivity().getComponentName(), null);
            }
        });

        ivDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.searchManager.triggerSearch("delivery", getActivity().getComponentName(), null);
            }
        });

        ivServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.searchManager.triggerSearch("services", getActivity().getComponentName(), null);
            }
        });

        tvWelcome = view.findViewById(R.id.text_view_welcome);

        tvWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the fields to specify which types of place data to return.
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields).setCountry("PH")
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        sharedPreferences = getContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
        String deliveryLocation = sharedPreferences.getString("deliveryLocation", "");

        if (deliveryLocation == "") {
            PlacesClient placesClient = Places.createClient(getContext());
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();
            placesClient.findCurrentPlace(request)
                    .addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                            if (task.isSuccessful()) {
                                FindCurrentPlaceResponse response = task.getResult();
                                Place place = response.getPlaceLikelihoods().get(0).getPlace();
                                tvWelcome.setText(place.getAddress());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("deliveryLocation", deliveryLocation);
                                editor.putString("deliveryLatitude", Double.toString(place.getLatLng().latitude));
                                editor.putString("deliveryLatitude", Double.toString(place.getLatLng().latitude));
                                editor.commit();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else {
            tvWelcome.setText(deliveryLocation);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                Toast.makeText(getContext(), "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                tvWelcome.setText(address);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("deliveryLocation", address);
                editor.putString("deliveryLatitude", Double.toString(place.getLatLng().latitude));
                editor.putString("deliveryLatitude", Double.toString(place.getLatLng().latitude));
                editor.commit();

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getContext(), "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}

package com.makgyber.vsells.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makgyber.vsells.R;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateProfileFragment extends Fragment {

    private final static String COLLECTION = "user";
    private static final String TAG = "UpdateProfileFragment";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userDbRef = db.collection(COLLECTION);
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    TextInputEditText displayName, phoneNumber, email, address, facebook, twitter;
    ImageView profileImage;
    FloatingActionButton saveProfileButton;
    String userProfileId;

    public UpdateProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateProfileFragment newInstance(String param1, String param2) {
        UpdateProfileFragment fragment = new UpdateProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateUpdateForm(view);
    }

    private void populateUpdateForm(View vw) {
        displayName = vw.findViewById(R.id.tiet_display_name);
        phoneNumber = vw.findViewById(R.id.tiet_phone_number);
        email = vw.findViewById(R.id.tiet_email);
        address = vw.findViewById(R.id.tiet_address);
        facebook = vw.findViewById(R.id.tiet_facebook);
        twitter = vw.findViewById(R.id.tiet_twitter);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
        userProfileId = sharedPreferences.getString("userId", "");
        displayName.setText(sharedPreferences.getString("displayName", "no name"));
        phoneNumber.setText(sharedPreferences.getString("phoneNumber", "no phone"));
        address.setText(sharedPreferences.getString("address", "no address"));
        email.setText(sharedPreferences.getString("email", "no email"));
        facebook.setText(sharedPreferences.getString("facebook", "no facebook"));
        twitter.setText(sharedPreferences.getString("twitter", "no twitter"));
        saveProfileButton = vw.findViewById(R.id.fab_save_profile);
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileToDB();
            }
        });
    }

    private void saveProfileToDB() {

        DocumentReference userRef = userDbRef.document(userProfileId);
        userRef.update(
                "displayName", displayName.getText().toString(),
                "email", email.getText().toString(),
                "phoneNumber", phoneNumber.getText().toString(),
                "address", address.getText().toString(),
                "facebook", facebook.getText().toString(),
                "twitter", twitter.getText().toString()
        )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                        saveToSharedPreferences();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_container, new ProfileFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Profile not updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveToSharedPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email.getText().toString());
        editor.putString("displayName", displayName.getText().toString());
        editor.putString("phoneNumber", phoneNumber.getText().toString());
        editor.putString("address", address.getText().toString());
        editor.putString("facebook", facebook.getText().toString());
        editor.putString("twitter", twitter.getText().toString());
        editor.commit();
    }


}

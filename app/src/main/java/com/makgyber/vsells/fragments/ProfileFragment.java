package com.makgyber.vsells.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makgyber.vsells.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private final static String COLLECTION = "user";
    private final static int PICK_IMAGE = 1;
    private static final String TAG = "ProfileFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    CollectionReference dbRef = db.collection(COLLECTION);
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    TextView displayName, email, phoneNumber, address, facebook, twitter;
    ImageView profileImage;
    FloatingActionButton updateButton, photoButton;
    String userProfileId;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateProfile(view);
    }

    private void populateProfile(View vw) {
        displayName = vw.findViewById(R.id.tv_display_name);
        phoneNumber = vw.findViewById(R.id.tv_phone_number);
        address = vw.findViewById(R.id.tv_address);
        facebook = vw.findViewById(R.id.tv_facebook);
        twitter = vw.findViewById(R.id.tv_twitter);
        email = vw.findViewById(R.id.tv_email);
        profileImage = vw.findViewById(R.id.iv_profile_photo2);
        updateButton = vw.findViewById(R.id.fab_update_profile);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
        userProfileId = sharedPreferences.getString("userId", "");
        displayName.setText(sharedPreferences.getString("displayName", "no name"));
        phoneNumber.setText(sharedPreferences.getString("phoneNumber", "no phone"));
        address.setText(sharedPreferences.getString("address", "no address"));
        facebook.setText(sharedPreferences.getString("facebook", "no facebook"));
        twitter.setText(sharedPreferences.getString("twitter", "no twitter"));
        email.setText(sharedPreferences.getString("email", "no email"));

        String photoUrl = sharedPreferences.getString("photoUrl", "");
        if (!photoUrl.isEmpty() && photoUrl.toString().length() > 0) {
            Picasso.get().load(photoUrl).centerCrop().resize(400,400).into(profileImage);
        }

        updateButton = vw.findViewById(R.id.fab_update_profile);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, new UpdateProfileFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        photoButton = vw.findViewById(R.id.fab_update_photo);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Log.d(TAG, "onActivityResult: starting here" );

        if (requestCode == PICK_IMAGE) {
            if (data == null) {
                //return error
                return;
            }
            Log.d(TAG, "onActivityResult: " + data.getDataString());
            Uri selectedImage = data.getData();

            Picasso.get().load(selectedImage).centerCrop().resize(400,400).into(profileImage);
            profileImage.setImageURI(selectedImage);
            uploadProfileImage();
        }
    }

    private void uploadProfileImage() {
        StorageReference productRef = storageRef.child("images/users/" + userProfileId + ".jpg");
        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.25), (int)(bitmap.getHeight()*0.25), true);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = productRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getContext(), "File Upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                updateProfileImageUri(downloadUrl);

            }
        });
    }

    private void updateProfileImageUri(Uri downloadUrl) {
        DocumentReference profileRef = dbRef.document(userProfileId);
        Log.d(TAG, "updateProfileImageUri: userProfileId " + userProfileId);
        profileRef.update(
                "photoUrl", downloadUrl.toString()
        )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("photoUrl", downloadUrl.toString());
                        editor.commit();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Profile image not updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

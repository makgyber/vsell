package com.makgyber.vsells.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.makgyber.vsells.R;
import com.makgyber.vsells.adapters.ViewPagerAdapter;
import com.makgyber.vsells.models.Chat;
import com.makgyber.vsells.models.Message;
import com.makgyber.vsells.models.Tindahan;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {

    private final static String COLLECTION = "tindahan";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference dbRef = db.collection(COLLECTION);
    CollectionReference chatRef = db.collection("chat");
    CollectionReference messageRef = db.collection("message");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    private String productId;
    private String productName;
    private String productDescription;
    private String productImageUri;
    private String productPrice;
    private String tindahanId, tindahanName;

    private static String TAG = "ProductDetailActivity";

    TextView tvProductName, tvDescription, tvPrice, tvTindahanName, tvDeliveryOptions, tvPaymentOptions, tvContactInfo;
    ImageView ivProduct;
    ViewPager vpImages;
    Button bMessageSeller;
    ArrayList<String> productImageList;
    TabLayout tabDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        setupPage();
    }

    private void setupPage() {
        if (getIntent().hasExtra("PRODUCT_ID")) {
            productId = getIntent().getExtras().get("PRODUCT_ID").toString();
        }

        if (getIntent().hasExtra("PRODUCT_NAME")) {
            productName = getIntent().getExtras().get("PRODUCT_NAME").toString();
            getSupportActionBar().setTitle(productName);
        }

        if (getIntent().hasExtra("PRODUCT_DESCRIPTION")) {
            productDescription = getIntent().getExtras().get("PRODUCT_DESCRIPTION").toString();
        }

//        if (getIntent().hasExtra("PRODUCT_IMAGE")) {
//            productImageUri = getIntent().getExtras().get("PRODUCT_IMAGE").toString();
//        }

        if (getIntent().hasExtra("PRODUCT_PRICE")) {
            productPrice = getIntent().getExtras().get("PRODUCT_PRICE").toString();
        }

        if (getIntent().hasExtra("PRODUCT_TINDAHAN_ID")) {
            tindahanId = getIntent().getExtras().get("PRODUCT_TINDAHAN_ID").toString();
        }

        if (getIntent().hasExtra("PRODUCT_TINDAHAN_NAME")) {
            tindahanName = getIntent().getExtras().get("PRODUCT_TINDAHAN_NAME").toString();
        }

        if (getIntent().hasExtra("PRODUCT_IMAGE_LIST")) {
            productImageList = getIntent().getStringArrayListExtra("PRODUCT_IMAGE_LIST");
        }

        Log.d(TAG, "onCreate: PRODUCT_ID " + productId);

        tvProductName = findViewById(R.id.product_detail_name);
        tvDescription = findViewById(R.id.product_detail_description);
        tvPrice = findViewById(R.id.product_detail_price);
//        ivProduct = findViewById(R.id.product_detail_image);
        vpImages = findViewById(R.id.product_detail_image);
        tabDots = findViewById(R.id.tabDots);
        tabDots.setupWithViewPager(vpImages);

        tvTindahanName = findViewById(R.id.product_detail_tindahan_name);
        tvDeliveryOptions = findViewById(R.id.delivery_options);
        tvPaymentOptions = findViewById(R.id.payment_options);
        tvContactInfo = findViewById(R.id.contact_info);

        tvProductName.setText(productName);
        tvDescription.setText(productDescription);
        tvPrice.setText("Php " + productPrice);


//        Picasso.get().load(productImageUri).centerCrop().resize(480, 480).into(ivProduct);

        ViewPagerAdapter imageAdapter = new ViewPagerAdapter(ProductDetailActivity.this, productImageList);
        vpImages.setAdapter(imageAdapter);

        bMessageSeller = findViewById(R.id.btn_message_seller);
        bMessageSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChatSession();
            }
        });

        populateTindahan();
        checkIfOwner();
    }

    private void checkIfOwner() {
        dbRef.document(tindahanId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
                        String userProfileId = sharedPreferences.getString("userId", "");
                        bMessageSeller.setEnabled(!document.get("owner").toString().equalsIgnoreCase(userProfileId));
                    }
                }
            }
        });
    }

    private void createChatSession() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("USER_PROFILE", MODE_PRIVATE);
        String userProfileId = sharedPreferences.getString("userId", "");
        String displayName = sharedPreferences.getString("displayName", "no name");
        String photoUrl = sharedPreferences.getString("photoUrl", "");

        String chatId = userProfileId + tindahanId;
        DocumentReference docRef = chatRef.document(chatId);
        Chat chat = new Chat(chatId, displayName + " + " + tindahanName, userProfileId, displayName, photoUrl, tindahanId, tindahanName, photoUrl, Timestamp.now());
        chatRef.document(chatId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        switchToChatPanel(chatId, photoUrl);
                    } else {
                        //no record found,
                        docRef.set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "New session created", Toast.LENGTH_SHORT).show();
                               switchToChatPanel(chatId, photoUrl);
                            }
                        });
                    }
                }
            }
        });
    }

    private void switchToChatPanel(String chatId, String profileImage) {
        Message newMesg = new Message(chatId, Timestamp.now(), "buyer", productName, profileImage);
        messageRef.document().set(newMesg).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra("chatId", chatId);
                intent.putExtra("topic", tindahanName);
                intent.putExtra("persona", "buyer");
                startActivity(intent);
            }
        });

    }

    private void populateTindahan() {
        Log.d(TAG, "populateTindahan: tindahanId " + tindahanId);
        final DocumentReference docRef = db.collection(COLLECTION).document(tindahanId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        tvTindahanName.setText("Sold by: " + document.get("tindahanName").toString());
                        tvContactInfo.setText("Contact Info:\n" + document.get("contactInfo").toString());

                        if (document.get("deliveryOptions") != null)
                            tvDeliveryOptions.setText("Delivery Options:\n" + document.get("deliveryOptions").toString());
                        if (document.get("paymentOptions") != null)
                            tvPaymentOptions.setText("Payment Options:\n" +document.get("paymentOptions").toString());
                    }
                }
            }
        });
    }
}

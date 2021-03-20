package com.makgyber.vbuys.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makgyber.vbuys.adapters.ImageRecyclerViewAdapter;
import com.makgyber.vbuys.models.Product;
import com.makgyber.vbuys.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class InventoryDetailActivity extends AppCompatActivity {
    private final static String TINDAHAN = "tindahan";
    private final static String PRODUCT = "product";
    private final static int PICK_IMAGE = 1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    CollectionReference tindahanDbRef = db.collection(TINDAHAN);
    CollectionReference productDbRef = db.collection(PRODUCT);

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    String productId = "";
    String TAG = "InventoryDetailActivity";
    TextInputEditText name, description, price, tags;
    ImageView productImage;
    Boolean imageUpdated = false;
    ProgressBar spinner;
    String tindahanName, tindahanId, tindahanPayment, tindahanLatitude, tindahanLongitude;
    ChipGroup categoryGroup;
    Chip foodChip, deliveryChip, devicesChip, servicesChip;
    Switch featureSwitch;
    String selectedCategory="";
    ViewPager vpImages;
    ArrayList<String> productImageList;
    ImageRecyclerViewAdapter irvaImageAdapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_detail);
        getSupportActionBar().setTitle("Inventory Details");

        if (getIntent().hasExtra("PRODUCT_ID")) {
            productId = getIntent().getExtras().get("PRODUCT_ID").toString();
            populateProductForm(productId);
        }

        if (getIntent().hasExtra("TINDAHAN_ID")) {
            tindahanId = getIntent().getExtras().get("TINDAHAN_ID").toString();
        }

        if (getIntent().hasExtra("TINDAHAN_NAME")) {
            tindahanName = getIntent().getExtras().get("TINDAHAN_NAME").toString();
        }

        if (getIntent().hasExtra("TINDAHAN_LATITUDE")) {
            tindahanLatitude = getIntent().getExtras().get("TINDAHAN_LATITUDE").toString();
        }

        if (getIntent().hasExtra("TINDAHAN_LONGITUDE")) {
            tindahanLongitude = getIntent().getExtras().get("TINDAHAN_LONGITUDE").toString();
        }

        name = (TextInputEditText) findViewById(R.id.name);
        description = (TextInputEditText) findViewById(R.id.description);
        price = (TextInputEditText) findViewById(R.id.price);
        tags = (TextInputEditText) findViewById(R.id.tags);
//        productImage = (ImageView) findViewById(R.id.product_image);
        categoryGroup = findViewById(R.id.cg_category);
        foodChip = findViewById(R.id.c_food);
        servicesChip = findViewById(R.id.c_services);
        deliveryChip = findViewById(R.id.c_delivery);
        devicesChip = findViewById(R.id.c_devices);
        featureSwitch = findViewById(R.id.s_feature_me);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
//        productImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGallery();
//            }
//        });

        categoryGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                Chip chip = chipGroup.findViewById(i);
                if (chip != null) {
                    String oldCategory = selectedCategory;
                    selectedCategory = chip.getText().toString().toLowerCase();
                    String[] currentTags = tags.getText().toString().split(",");
                    Collection<String> newTags = Arrays.stream(currentTags).map(String::trim).filter(e -> !e.equalsIgnoreCase(oldCategory)).collect(Collectors.toList());
                    newTags.add(selectedCategory);
                    tags.setText(newTags.toString().replace("[", "").replace("]", ""));
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (data == null) {
                //return error
                return;
            }
            imageUpdated = true;
            Log.d(TAG, "onActivityResult: " + data.getDataString());
            Uri selectedImage = data.getData();

            StorageReference productRef = storageRef.child("images/tindahan/"+ tindahanId + "/" + productId + "/" + UUID.randomUUID().toString() + ".jpg");
            UploadTask uploadTask = productRef.putFile(selectedImage);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(InventoryDetailActivity.this, "File Upload failed", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();
                    productImageList.add(downloadUrl.toString());
                    productDbRef.document(productId).update("imageList", productImageList);
                    irvaImageAdapter.notifyDataSetChanged();
                }
            });

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateProductForm(String productId) {
        DocumentReference docRef = db.collection(PRODUCT).document(productId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String category = document.get("category").toString();

                        foodChip.setChecked(category.equalsIgnoreCase("food"));
                        devicesChip.setChecked(category.equalsIgnoreCase("devices"));
                        servicesChip.setChecked(category.equalsIgnoreCase("services"));
                        deliveryChip.setChecked(category.equalsIgnoreCase("delivery"));

                        featureSwitch.setChecked(document.get("publish").toString().equals("true"));

                        name.setText(document.get("productName").toString());
                        description.setText(document.get("description").toString());
                        price.setText(document.get("price").toString());
                        String tagsStr = document.get("tags").toString();
                        tags.setText(tagsStr.replace("[", "").replace("]",""));
                        productImageList= (ArrayList<String>)document.get("imageList");

                        irvaImageAdapter = new ImageRecyclerViewAdapter(productImageList);

                        recyclerView = findViewById(R.id.rv_inventory_images);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(InventoryDetailActivity.this));
                        recyclerView.setAdapter(irvaImageAdapter);


                        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

                            @Override
                            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                                Toast.makeText(InventoryDetailActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            @Override
                            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                                Toast.makeText(InventoryDetailActivity.this, "Image deleted", Toast.LENGTH_SHORT).show();
                                final int position = viewHolder.getAdapterPosition();
                                productImageList.remove(position);
                                productDbRef.document(productId).update("imageList", productImageList);
                                irvaImageAdapter.notifyItemRemoved(position);
                            }
                        };

                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                        itemTouchHelper.attachToRecyclerView(recyclerView);
                    }
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_add_menu, menu);
        return true;
    }

    private void uploadProductImage() {
        StorageReference productRef = storageRef.child("images/tindahan/"+ tindahanId + "/" + productId + ".jpg");
        productImage.setDrawingCacheEnabled(true);
        productImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) productImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.25), (int)(bitmap.getHeight()*0.25), true);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        spinner.setVisibility(View.VISIBLE);
        UploadTask uploadTask = productRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(InventoryDetailActivity.this, "File Upload failed", Toast.LENGTH_SHORT).show();
                spinner.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                updateProductImageUri(downloadUrl);
                spinner.setVisibility(View.GONE);
            }
        });
    }

    private void updateProductImageUri(Uri downloadUrl) {
        spinner.setVisibility(View.VISIBLE);
        DocumentReference prodRef = productDbRef.document(productId);
        prodRef.update(
                "imageUri", downloadUrl.toString()
        )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(InventoryDetailActivity.this, "Product ImageUrl updated", Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InventoryDetailActivity.this, "Product not updated", Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.inventory_save) {
            saveProduct();
            return true;
        }

        if (id == R.id.inventory_gallery) {
            openGallery();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void saveProduct() {
        spinner.setVisibility(View.VISIBLE);
        List<String> tagsList = Arrays.asList(tags.getText().toString().split(","));
        tagsList.replaceAll(String::trim);

        Product product = new Product(
                name.getText().toString(),
                description.getText().toString(),
                tindahanName,
                tindahanId,
                Double.parseDouble(price.getText().toString()),
                featureSwitch.isChecked(),
                tagsList,
                "",
                selectedCategory,
                new GeoPoint(Double.parseDouble(tindahanLatitude), Double.parseDouble(tindahanLongitude)));

        Log.d(TAG, "saveProduct: " + product.getProductName());

        if (productId.equals("0")) {
            DocumentReference pdref = productDbRef.document();
            productId = pdref.getId();
            pdref.set(product)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            uploadProductImage();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            spinner.setVisibility(View.GONE);
                        }
                    });

        } else {
            DocumentReference prodRef = productDbRef.document(productId);
            prodRef.update(
                    "productName", product.getProductName(),
                    "description", product.getDescription(),
                    "price", product.getPrice(),
                    "tags", product.getTags(),
                    "tindahanName", product.getTindahanName(),
                    "category", product.getCategory(),
                    "publish", product.getPublish(),
                    "position", product.getPosition(),
                    "tindahanId", product.getTindahanId()
                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(InventoryDetailActivity.this, "Product updated", Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(InventoryDetailActivity.this, "Product not updated", Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);
                        }
                    });
//            if (imageUpdated)
//                uploadProductImage();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}

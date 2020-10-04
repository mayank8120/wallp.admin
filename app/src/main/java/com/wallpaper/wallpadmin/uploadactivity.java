package com.wallpaper.wallpadmin;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class uploadactivity extends AppCompatActivity {


    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    Spinner spinnerDropDownViewCategory;
    String[] spinnerValueHoldValue = {"3D Abstract", "Art & Drawing", "Animals", "Architecture", "Beach", "Cars", "Celebrations & lights", "Cityscapes", "Dark/BLack", "Fantasy", "Flowers", "Girls", "Lifestyle", "Macro", "Materialistic", "Minimalistic", "Nature", "People", "Photography", "Posters", "Space", "Sports", "Superheroes", "Technology", "Underwater", "World", "Neon lights", "Anime", "Cartoon", "Amoled", "Tattoo", "Colorify it", "Be happy !!!", "Weird Geometry", "Sketchy", "Lettering Art & Quotes", "Relaxing", "Patterns", "Love", "Food"};


    String[] cameraPermission;
    String[] storagePermission;

    Uri img1uri = null;
    ProgressDialog pd;

    FirebaseAuth firebaseAuth;
    ImageView img1;
    Button submit;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadactivity);


        submit = findViewById(R.id.submit_button);
        img1 = (ImageView) findViewById(R.id.wallimage);


        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        pd = new ProgressDialog(this);


        firebaseAuth = FirebaseAuth.getInstance();


        spinnerDropDownViewCategory = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(uploadactivity.this, android.R.layout.simple_list_item_1, spinnerValueHoldValue);
        spinnerDropDownViewCategory.setAdapter(adapter);

        spinnerDropDownViewCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(uploadactivity.this, spinnerDropDownViewCategory.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(uploadactivity.this, "imsge pick dialog", Toast.LENGTH_SHORT).show();

                showimagepickdialog();


            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String spinnercategory = spinnerDropDownViewCategory.getSelectedItem().toString().trim();


                if (TextUtils.isEmpty(spinnercategory)) {
                    Toast.makeText(uploadactivity.this, "select category", Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadData(spinnercategory);


            }
        });

    }


    private void showimagepickdialog() {

        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose image from");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                if (which == 0) {
                    if (!checkcamerapermissions()) {
                        requestcamerapermission();
                    } else {
                        pickfromcamera();
                    }

                }
                if (which == 1) {

                    if (!checkStoragepermissions()) {
                        requeststoragepermission();
                    } else {
                        pickfromcGallery();
                    }

                }
            }
        });
        builder.create().show();
    }

    private void uploadData(final String spinnercategory) {

        pd.setMessage("Airing your image ......");
        pd.show();

        final String timestamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Wallpapers/" + timestamp;


        if (/*!Uri.equals("noImage")*/img1.getDrawable() != null) {

            Bitmap bitmap = ((BitmapDrawable) img1.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!uriTask.isSuccessful()) ;

                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()) {
                                HashMap<Object, String> hashMap = new HashMap<>();


                                hashMap.put("wallpaperlink", downloadUri);
                                hashMap.put("category", spinnercategory);


                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wallpapers");
                                ref.child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                pd.dismiss();
                                                Intent intent = new Intent(uploadactivity.this, MainActivity.class);
                                                startActivity(intent);
                                                Toast.makeText(uploadactivity.this, "wallpaper submitted successfully ", Toast.LENGTH_SHORT).show();

                                                img1.setImageURI(null);
                                                img1uri = null;


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                pd.dismiss();
                                                Toast.makeText(uploadactivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            pd.dismiss();
                            Toast.makeText(uploadactivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        } else {

           /* HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("uid", uid);
            hashMap.put("uname", name);
            hashMap.put("uemail", email);
            hashMap.put("udp", dp);
            hashMap.put("pid", timestamp);
            hashMap.put("ptime", timestamp);
            hashMap.put("phone", phone);
            hashMap.put("address", address);
            hashMap.put("pincode", pincode);
            hashMap.put("pimage", "noImage");
            hashMap.put("city", spinnercity);
            hashMap.put("price", propprice);
            hashMap.put("locality", spinnerlocality);
            hashMap.put("date", date);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(add_property.this, "Property submitted without image", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(add_property.this, main_activity.class);
                            startActivity(intent);
                            username.setText("");
                            phonenumber.setText("");
                            addressss.setText("");
                            pincod.setText("");
                            img1.setImageURI(null);
                            img1uri = null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(add_property.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });*/
            Toast.makeText(uploadactivity.this, "Property not submit ", Toast.LENGTH_SHORT).show();
        }
    }


    private void pickfromcGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickfromcamera() {

        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp pic");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp description");
        img1uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, img1uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }


    private boolean checkStoragepermissions() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requeststoragepermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
        //requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkcamerapermissions() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestcamerapermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
        //requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case CAMERA_REQUEST_CODE: {

                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        pickfromcamera();
                    } else {
                        Toast.makeText(this, "please enable permissions", Toast.LENGTH_SHORT).show();
                    }

                } else {

                }
            }
            break;

            case STORAGE_REQUEST_CODE: {

                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {
                        pickfromcGallery();
                    } else {
                        Toast.makeText(this, "enable permissions", Toast.LENGTH_SHORT).show();
                    }

                } else {

                }

            }
            break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {

                img1uri = data.getData();
                img1.setImageURI(img1uri);


            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {

                img1.setImageURI(img1uri);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
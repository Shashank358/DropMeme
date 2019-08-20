package com.dropout.dropmeme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView userImage;
    private TextView changeImage;
    private EditText userName, userId, userEmail, userPhnNo, userAbout;
    private String path;
    private StorageReference mImageStorage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String mCurrentUser;

    public static final int PICK_PHOTO_FOR_AVATAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

//        path = getIntent().getStringExtra("path");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();


        toolbar = findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userImage = findViewById(R.id.edit_profile_user_image);
        changeImage = findViewById(R.id.edit_profile_change_image);
        userName = findViewById(R.id.edit_profile_user_name_edit);
        userId = findViewById(R.id.edit_profile_user_id_edit);
        userEmail = findViewById(R.id.edit_profile_email_edit);
        userPhnNo = findViewById(R.id.edit_profile_phone_number_edit);
        userAbout = findViewById(R.id.edit_profile_about_edit);

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(EditProfileActivity.this, GalleryActivity.class);
//                intent.putExtra("purpose", "profile");
//                startActivity(intent);

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_PHOTO_FOR_AVATAR);

            }
        });

        getUserInfo();

    }

    private void getUserInfo() {
        db.collection("Users").document(mCurrentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String user_name = task.getResult().get("user_name").toString();
                    String user_id = task.getResult().get("user_id").toString();
                    String user_email = task.getResult().get("user_email").toString();
                    String user_phone = task.getResult().get("user_phone").toString();
                    String user_about = task.getResult().get("user_about").toString();
                    String user_profile =  task.getResult().get("user_thumbImage").toString();

                    userName.setText(user_name);
                    userId.setText(user_id);
                    userEmail.setText(user_email);
                    userPhnNo.setText(user_phone);
                    userAbout.setText(user_about);
                    Picasso picasso = Picasso.get();
                    picasso.setIndicatorsEnabled(false);
                    picasso.load(user_profile).placeholder(R.drawable.avatar).into(userImage);


                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_profile_done:
                setUserInfo();
        }
        return true;
    }

    private void setUserInfo() {

        String user_name = userName.getText().toString();
        String user_id = userId.getText().toString();
        String user_email = userEmail.getText().toString();
        String user_phone = userPhnNo.getText().toString();
        String user_about = userAbout.getText().toString();

        if (!TextUtils.isEmpty(user_name) || !TextUtils.isEmpty(user_id) ||
                !TextUtils.isEmpty(user_email) || !TextUtils.isEmpty(user_phone) ||
                !TextUtils.isEmpty(user_about)){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("user_name", user_name);
            hashMap.put("user_id", user_id);
            hashMap.put("user_email", user_email);
            hashMap.put("user_phone", user_phone);
            hashMap.put("user_about", user_about);
            db.collection("Users").document(mCurrentUser).update(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                onBackPressed();
                                Toast.makeText(EditProfileActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();

                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(resultUri).placeholder(R.drawable.avatar).into(userImage);


                final File thumb_file_Path = new File(resultUri.getPath());
                Bitmap thumb_bitmap = null;

                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_file_Path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                final byte[] thumb_byte = baos.toByteArray();

                mImageStorage = FirebaseStorage.getInstance().getReference();

                final StorageReference filepath = mImageStorage.child("profile_images").child(mCurrentUser + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumb")
                        .child(mCurrentUser + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    final String download_url = uri.toString();

                                    UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                thumb_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String thumb_downloadUrl = uri.toString();

                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                        hashMap.put("user_image", download_url);
                                                        hashMap.put("user_thumbImage", thumb_downloadUrl);

                                                        db.collection("Users").document(mCurrentUser)
                                                                .update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(EditProfileActivity.this, "uploaded", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(EditProfileActivity.this, "error, try again", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });

            }
        }
    }

}

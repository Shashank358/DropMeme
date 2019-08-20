package com.dropout.dropmeme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dropout.dropmeme.Adapters.ViewPagerAdapter;
import com.dropout.dropmeme.Fragments.AddTextFragment;
import com.dropout.dropmeme.Fragments.BrushFragment;
import com.dropout.dropmeme.Fragments.EditImageFragment;
import com.dropout.dropmeme.Fragments.EmojiFragment;
import com.dropout.dropmeme.Fragments.FiltersListFragment;
import com.dropout.dropmeme.Interfaces.AddTextFragmentListener;
import com.dropout.dropmeme.Interfaces.BrushFragmentListener;
import com.dropout.dropmeme.Interfaces.EditImageFragmentListener;
import com.dropout.dropmeme.Interfaces.EmojiFragmentListener;
import com.dropout.dropmeme.Interfaces.FiltersListFragmentListener;
import com.dropout.dropmeme.Utils.BitmapUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.common.collect.Table;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.model.value.TimestampValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import id.zelory.compressor.Compressor;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class CreateMemeActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener, BrushFragmentListener, EmojiFragmentListener, AddTextFragmentListener {

    private Toolbar toolbar;

    private FirebaseFirestore db;
    private StorageReference mImageStorage;
    private FirebaseAuth mAuth;
    private String mCurrentUser;

    //new
    public static final String pictureName = "flash.jpg";
    public static final int PERMISSION_PICK_IMAGE = 1000;
    public static final int PERMISSION_INSERT_IMAGE = 1001;
    public static final int CAMERA_REQUEST = 1002;


    PhotoEditorView photoEditorView;
    PhotoEditor photoEditor;
    TabLayout tabLayout;
    ViewPager viewPager;
    ConstraintLayout coordinatorLayout;

    Bitmap originalBitmap, filteredBitmap, finalBitmap;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    CardView btn_filters_list, btn_edit, btn_brush, btn_emoji, btn_add_text, btn_add_image, btn_crop;
    ImageView undoIcon, redoIcon;

    int brigntnessFinal = 0;
    float saturationFinal = 1.0f;
    float constrantFinal = 1.0f;

    Uri image_selected_uri;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme);
        Intent intent = getIntent();

//        path = intent.getStringExtra("path");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.create_meme_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Meme");

        photoEditorView = (PhotoEditorView) findViewById(R.id.image_preview);
        photoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(), "emojione-android.ttf"))
                .build();

        coordinatorLayout = (ConstraintLayout) findViewById(R.id.coordinator);

        btn_edit = (CardView) findViewById(R.id.btn_edit);
        btn_filters_list = (CardView) findViewById(R.id.btn_filter_list);
        btn_brush = (CardView) findViewById(R.id.btn_brush);
        btn_emoji = (CardView) findViewById(R.id.btn_emoji);
        btn_add_text = (CardView) findViewById(R.id.btn_add_text);
        btn_add_image = (CardView) findViewById(R.id.btn_add_image);
        btn_crop = (CardView) findViewById(R.id.btn_crop);
        undoIcon = (ImageView) findViewById(R.id.undo_image_icon);
        redoIcon = (ImageView) findViewById(R.id.redo_image_icon);

        btn_filters_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (filtersListFragment != null){

                    filtersListFragment.show(getSupportFragmentManager(), filtersListFragment.getTag());
                }
                else {
                    FiltersListFragment filtersListFragment = FiltersListFragment.getInstance(null);
                    filtersListFragment.setListener(CreateMemeActivity.this);
                    filtersListFragment.show(getSupportFragmentManager(), filtersListFragment.getTag());
                }

            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditImageFragment editImageFragment = EditImageFragment.getInstance();
                editImageFragment.setListener(CreateMemeActivity.this);
                editImageFragment.show(getSupportFragmentManager(), editImageFragment.getTag());

            }
        });

        btn_brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //enable brush mode
                photoEditor.setBrushDrawingMode(true);

                BrushFragment brushFragment = BrushFragment.getInstance();
                brushFragment.setListener(CreateMemeActivity.this);
                brushFragment.show(getSupportFragmentManager(), brushFragment.getTag());

            }
        });

        btn_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmojiFragment emojiFragment = EmojiFragment.getInstance();
                emojiFragment.setListener(CreateMemeActivity.this);
                emojiFragment.show(getSupportFragmentManager(), emojiFragment.getTag());

            }
        });

        btn_add_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTextFragment addTextFragment = AddTextFragment.getInstance();
                addTextFragment.setListener(CreateMemeActivity.this);
                addTextFragment.show(getSupportFragmentManager(), addTextFragment.getTag());

            }
        });

        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageToPicture();
            }
        });

        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCrop(image_selected_uri);
            }
        });

        undoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEditor.undo();
            }
        });

        redoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEditor.redo();

            }
        });

        loadImage();

    }

    private void startCrop(Uri uri) {

        String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)) );

        uCrop.start(CreateMemeActivity.this);
    }

    private void addImageToPicture() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted())
                        {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, PERMISSION_INSERT_IMAGE);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(CreateMemeActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private void loadImage() {

        originalBitmap = BitmapUtils.getBitmapFromAssets(this, pictureName, 300, 300);
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(originalBitmap);

    }

    private void setupViewPager() {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, "FILTERS");
        adapter.addFragment(editImageFragment, "EDIT");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBrightnessChanged(int brightness) {

        brigntnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));

    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));

    }

    @Override
    public void onConstrantChanged(float constrant) {
        constrantFinal = constrant;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(constrant));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));

    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {

        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brigntnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(constrantFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));

        finalBitmap = myFilter.processFilter(bitmap);
    }


    @Override
    public void onFilterSelected(Filter filter) {

//        resetControl();
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    private void resetControl() {
        if (editImageFragment != null)
        {
            editImageFragment.resetControls();
            brigntnessFinal = 0;
            saturationFinal = 1.0f;
            constrantFinal = 1.0f;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_meme_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open){
            openImageFromGallery();
            return true;
        }
        else if (id == R.id.action_camera)
        {
            openCamera();
            return true;
        }
        else if (id == R.id.action_save){
            saveImageToGallery();
            return true;
        }
        else if (id == R.id.action_send){
            postMeme();
        }
        return super.onOptionsItemSelected(item);
    }

    private void postMeme() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("Memes")
                .child(mCurrentUser)
                .child(random() + ".jpg");

        final UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String download_uri = uri.toString();

                        final String post_id = db.collection("HomeMeme").document().getId();

                        db.collection("Users").document(mCurrentUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                String user_uid = documentSnapshot.get("uId").toString();

                                HashMap<String, Object> postMap = new HashMap<>();
                                postMap.put("caption", "New post application is in developing mode");
                                postMap.put("time_ago", FieldValue.serverTimestamp());
                                postMap.put("meme_image", download_uri);
                                postMap.put("meme_id", post_id);
                                postMap.put("user_uid", user_uid);

                                db.collection("HomeMeme").document(post_id).set(postMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    onBackPressed();
                                                    Toast.makeText(CreateMemeActivity.this, "successful", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            }
                        });
                    }
                });
            }
        });

    }

    private void openCamera() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted())
                        {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, "New Picture");
                            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camers");
                            image_selected_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    values);

                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_selected_uri);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);

                        }
                        else
                        {
                            Toast.makeText(CreateMemeActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void saveImageToGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted())
                        {
                            photoEditor.saveAsBitmap(new OnSaveBitmap() {
                                @Override
                                public void onBitmapReady(Bitmap saveBitmap) {

                                    photoEditorView.getSource().setImageBitmap(saveBitmap);

                                    final String path = BitmapUtils.insertImage(getContentResolver(),
                                            finalBitmap,
                                            System.currentTimeMillis()+"_profile.jpg",
                                            null);
                                    if (!TextUtils.isEmpty(path))
                                    {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                "Image saved to gallery",
                                                Snackbar.LENGTH_LONG)
                                                .setAction("OPEN", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        openImage(path);
                                                    }
                                                });
                                        snackbar.show();
                                    }else {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                "Unable to save",
                                                Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        }
                        else {
                            Toast.makeText(CreateMemeActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }

    private void openImageFromGallery() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted())
                        {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, PERMISSION_PICK_IMAGE);
                        }
                        else
                        {
                            Toast.makeText(CreateMemeActivity.this, "permission denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK )
        {
            if (requestCode ==PERMISSION_PICK_IMAGE){

                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

                image_selected_uri = data.getData();

                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();

                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

                photoEditorView.getSource().setImageBitmap(originalBitmap);
                bitmap.recycle();

                //render selected image thumbnail
                filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);
            }
            if (requestCode ==CAMERA_REQUEST){

                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();

                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

                photoEditorView.getSource().setImageBitmap(originalBitmap);
                bitmap.recycle();

                //render selected image thumbnail
                filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);
            }
            else if (requestCode == PERMISSION_INSERT_IMAGE)
            {
                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 300, 300);
                photoEditor.addImage(bitmap);

            }
            else if (requestCode == UCrop.REQUEST_CROP)
            {
                handleCropRequest(data);
            }
        }else if (resultCode == UCrop.RESULT_ERROR){
            handleCropError(data);
        }
    }

    private void handleCropError(Intent data) {
        final Throwable cropError = UCrop.getError(data);
        if (cropError != null)
        {
            Toast.makeText(this, ""+cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
        else 
        {
            Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCropRequest(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);
        if (resultUri != null)
        {
            photoEditorView.getSource().setImageURI(resultUri);

            Bitmap bitmap = ((BitmapDrawable)photoEditorView.getSource().getDrawable()).getBitmap();
            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredBitmap = originalBitmap;
            finalBitmap = originalBitmap;

        }
        else {
            Toast.makeText(this, "cannot retrieve crop image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBrushSizeChangedListener(float size) {
        photoEditor.setBrushSize(size);
    }

    @Override
    public void onBrushOpacityChangedListener(int opacity) {
        photoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushColorChangedListener(int color) {
        photoEditor.setBrushColor(color);
    }

    @Override
    public void onBrushStateChangedListener(boolean isEraser) {

        if (isEraser){
            photoEditor.brushEraser();
        }
        else {
            photoEditor.setBrushDrawingMode(true);
        }
    }

    @Override
    public void onEmojiSelected(String emoji) {
        photoEditor.addEmoji(emoji);
        finalBitmap = originalBitmap;

    }


    @Override
    public void onAddTextButtonSelected(Typeface typeface, String text, int color) {
        photoEditor.addText(typeface, text, color);
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}

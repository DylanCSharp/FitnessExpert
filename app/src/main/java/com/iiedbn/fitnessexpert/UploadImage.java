package com.iiedbn.fitnessexpert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class UploadImage extends AppCompatActivity {

    private String userID;
    private FirebaseAuth fAuth;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button BtnChooseUpload;
    private Button BtnUploadImage;
    private EditText FileName;
    private ImageView Image;
    private android.widget.ProgressBar ProgressBar;
    Uri imageUri;
    private Button BtnCameraUpload;

    private StorageReference fStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        BtnChooseUpload = findViewById(R.id.BtnChooseImage);
        BtnUploadImage = findViewById(R.id.BtnUploadImage);
        FileName = findViewById(R.id.ETFileNme);
        Image = findViewById(R.id.ImageView);

        userID = FirebaseAuth.getInstance().getUid();
        fStorage = FirebaseStorage.getInstance().getReference(userID);

        try {
            BtnChooseUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Directs the on click to this method
                    openFileChooser();
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            BtnUploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Directs the on click to this method
                    ImageUploader();
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            //Asks the user for permission to the camera, and if the permissions is false, it will ask again
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UploadImage.this, new String[] {
                        Manifest.permission.CAMERA }, 100);
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try
        {
            BtnCameraUpload = findViewById(R.id.BtnCamera);
            BtnCameraUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Starting a new intent and opens the camera so the user can take a photo
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 100);
                }
            });
        }catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void openFileChooser()
    {
        //Opens the gallery so that a user can choose an existing image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getExtension(Uri uri) {
        //Getting the extension of the uri that is chosen
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    public void ImageUploader()
    {
        //Making sure the user is entering an image when submitting
        if (TextUtils.isEmpty(FileName.getText()) && imageUri != null){

            try {
                //Sets a file name if one isnt already entered
                //Creates a folder in firebase storage for the specific user entering their images
                FileName.setText("ImgNoName" + System.currentTimeMillis());
                StorageReference ref = fStorage.child(FileName.getText() + "." + getExtension(imageUri));
                ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Telling the user if the upload was successful or failed
                        Toast.makeText(getApplicationContext(), "Image Uploaded Successful", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Image Uploaded Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), "ERROR: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            try{
                //Uploading the image with the selected file name they chose
                StorageReference ref = fStorage.child(FileName.getText() + "." + getExtension(imageUri));
                ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Image Uploaded Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Image Uploaded Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), "ERROR: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
            {
                //Loading the image view with the uri with the picasso implementation so that the user can preview what theyre uploading
                imageUri = data.getData();
                Picasso.with(this).load(imageUri).into(Image);
            }
            else if (requestCode == 100) {
                //Loading the image view with the uri with the picasso implementation so that the user can preview what theyre uploading
                Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                Image.setImageBitmap(capturedImage);
                imageUri = getImageUri(getApplicationContext(), capturedImage);
            } }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        //Converting the bitmap to a uri format so that it can be uploaded to firebase
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}

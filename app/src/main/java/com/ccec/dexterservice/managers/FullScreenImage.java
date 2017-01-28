package com.ccec.dexterservice.managers;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.Toast;

import com.ccec.dexterservice.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FullScreenImage extends Activity {
    private ImageView imageView;
    private PhotoViewAttacher pAttacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_image);

        imageView = (ImageView) findViewById(R.id.imageView);
        final String path = AppData.currentImagePath;

        Toast.makeText(FullScreenImage.this, "Loading..", Toast.LENGTH_LONG).show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");

        pAttacher = new PhotoViewAttacher(imageView);

        storageRef.child("attachments/" + path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri).noPlaceholder().into(imageView);
                pAttacher.update();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}
package com.ccec.dexterservice.managers;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ccec.dexterservice.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AttachmentsViewAdapter extends RecyclerView.Adapter<AttachmentsViewAdapter.ViewHolder> {
    private ArrayList<String> countries;
    private Context ctx;
    private String AudioSavePathInDevice;

    public AttachmentsViewAdapter(ArrayList<String> countries, Context ctx) {
        this.countries = countries;
        this.ctx = ctx;
    }

    @Override
    public AttachmentsViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_attachment_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AttachmentsViewAdapter.ViewHolder viewHolder, final int i) {
        if (countries.get(i).contains("3gp")) {
            viewHolder.play.setVisibility(View.VISIBLE);
            viewHolder.car.setVisibility(View.GONE);
        } else {
            setPic(countries.get(i), viewHolder.car);
            viewHolder.car.setVisibility(View.VISIBLE);
            viewHolder.play.setVisibility(View.GONE);
        }

        viewHolder.car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.currentImagePath = countries.get(i);
                Intent in = new Intent(ctx, FullScreenImage.class);
                ctx.startActivity(in);
            }
        });

        viewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.play.setAlpha(128);
                Toast.makeText(ctx, "Downloading...", Toast.LENGTH_SHORT).show();

                AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        countries.get(i);
                final File file = new File(AudioSavePathInDevice);
                try {
                    file.createNewFile();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    final StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");

                    storageRef.child("attachments/" + countries.get(i)).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            MediaPlayer mMediaPlayer = new MediaPlayer();
                            try {
                                mMediaPlayer.setDataSource(AudioSavePathInDevice);
                                mMediaPlayer.prepare();
                                mMediaPlayer.start();
                                Toast.makeText(ctx, "Playing...", Toast.LENGTH_LONG).show();

                                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        viewHolder.play.setAlpha(255);
                                    }
                                });
                            } catch (IOException e) {
                                Toast.makeText(ctx, "Something went wrong..", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(ctx, "Something went wrong..", Toast.LENGTH_SHORT).show();
                            viewHolder.play.setAlpha(255);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setPic(String path, final ImageView im) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");

        storageRef.child("attachments/" + path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Glide.with(ctx).load(uri).centerCrop().into(im);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView play, car;

        public ViewHolder(View view) {
            super(view);

            play = (ImageView) view.findViewById(R.id.play);
            car = (ImageView) view.findViewById(R.id.car);
        }
    }
}

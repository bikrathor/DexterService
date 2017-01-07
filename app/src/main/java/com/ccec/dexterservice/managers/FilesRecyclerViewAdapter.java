package com.ccec.dexterservice.managers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ccec.dexterservice.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class FilesRecyclerViewAdapter extends RecyclerView.Adapter<FilesRecyclerViewHolders> {
    private List<String> itemMap;
    protected Context context;
    private CircularImageView img;

    public FilesRecyclerViewAdapter(Context context, List<String> requestRow) {
        this.itemMap = requestRow;
        this.context = context;
    }

    @Override
    public FilesRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        FilesRecyclerViewHolders viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_files_row, parent, false);
        viewHolder = new FilesRecyclerViewHolders(layoutView, itemMap);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilesRecyclerViewHolders holder, final int position) {
//        holder.requestID.setText((String) requestMap.get("key"));
        holder.areaModel.setText(itemMap.get(position));

        img = holder.RVCircle;
        String temp = itemMap.get(position);
        setPic(temp, img);

        holder.requestID.setTypeface(FontsManager.getBoldTypeface(context));
        holder.areaModel.setTypeface(FontsManager.getRegularTypeface(context));

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Downloading..", Toast.LENGTH_SHORT).show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");
                StorageReference riversRef = storageRef.child("obdFiles/" + itemMap.get(position) + ".txt");

                File localFile = null;
                try {
                    localFile = new File(Environment.getExternalStorageDirectory(), "output-" + itemMap.get(position) + ".txt");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final File finalLocalFile = localFile;
                riversRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Uri uri = Uri.fromFile(finalLocalFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "text/plain");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        });
    }

    private void setPic(String path, final CircularImageView im) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");

        storageRef.child("profilePics/" + path + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri).noPlaceholder().into(im);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.itemMap.size();
    }
}

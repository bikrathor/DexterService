package com.ccec.dexterservice.managers;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccec.dexterservice.R;
import com.ccec.dexterservice.ServiceFragment;
import com.ccec.dexterservice.entities.RequestRow;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
    private List<RequestRow> requestRow;
    protected Context context;
    private ServiceFragment fragment;
    private CircularImageView img;

    public RecyclerViewAdapter(Context context, List<RequestRow> requestRow, ServiceFragment fragment) {
        this.requestRow = requestRow;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolders viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_service_row, parent, false);
        viewHolder = new RecyclerViewHolders(layoutView, requestRow);

        fragment.stopLoading();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        Map<String, Object> requestMap = requestRow.get(position).getRequestMap();
        Map<String, Object> itemMap = requestRow.get(position).getItemMap();

        holder.requestID.setText((String) requestMap.get("key"));
        holder.areaModel.setText((String) itemMap.get("model"));
        holder.openTime.setText((String) requestMap.get("openTime"));
        holder.scheduledTime.setText((String) requestMap.get("scheduleTime"));

        img = holder.RVCircle;
        String temp = (String) requestMap.get("item");
        setPic(temp, img);

        holder.requestID.setTypeface(FontsManager.getBoldTypeface(context));
        holder.areaModel.setTypeface(FontsManager.getRegularTypeface(context));
        holder.openTime.setTypeface(FontsManager.getRegularTypeface(context));
        holder.scheduledTime.setTypeface(FontsManager.getRegularTypeface(context));

        if (((String) requestMap.get("status")).equals("Accepted")) {
            holder.accept.setVisibility(View.INVISIBLE);
            holder.chat.setTypeface(FontsManager.getRegularTypeface(context));
        } else if (((String) requestMap.get("status")).equals("Completed")) {
            holder.accept.setVisibility(View.INVISIBLE);
            holder.chat.setVisibility(View.INVISIBLE);

        } else {
            holder.chat.setTypeface(FontsManager.getRegularTypeface(context));
            holder.accept.setTypeface(FontsManager.getRegularTypeface(context));
        }
    }

    private void setPic(String path, final CircularImageView im) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");

        storageRef.child("items/cars/" + path + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
        return this.requestRow.size();
    }
}

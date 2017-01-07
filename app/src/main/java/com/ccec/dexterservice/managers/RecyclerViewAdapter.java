package com.ccec.dexterservice.managers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.ccec.dexterservice.NewOrderDetail;
import com.ccec.dexterservice.R;
import com.ccec.dexterservice.ServiceFragment;
import com.ccec.dexterservice.entities.RequestRow;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
    private List<RequestRow> requestRow;
    protected Context context;
    private ServiceFragment fragment;
    private CircularImageView img;
    private Spinner sp;
    private int pos;

    public RecyclerViewAdapter(Context context, List<RequestRow> requestRow, ServiceFragment fragment, Spinner sp) {
        this.requestRow = requestRow;
        this.context = context;
        this.fragment = fragment;
        this.sp = sp;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolders viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_service_row, parent, false);
        viewHolder = new RecyclerViewHolders(layoutView, requestRow);

        if (AppData.setOne == true) {
            fragment.selectSpinnerItemByValue(sp, 0);
            AppData.setOne = false;
        }
        fragment.stopLoading();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        final Map<String, Object> requestMap = requestRow.get(position).getRequestMap();
        Map<String, Object> itemMap = requestRow.get(position).getItemMap();

        holder.requestID.setText((String) requestMap.get("key"));
        holder.areaModel.setText((String) itemMap.get("make") + " " + (String) itemMap.get("model"));
        if (AppData.currentStatus == "Accepted")
            holder.scheduledTime.setText("Scheduled on: " + (String) requestMap.get("scheduleTime"));
        else if (AppData.currentStatus == "Completed")
            holder.scheduledTime.setText("Processed on: " + (String) requestMap.get("scheduleTime"));
        else
            holder.scheduledTime.setText("Placed on: " + (String) requestMap.get("openTime"));

        img = holder.RVCircle;
        String temp = (String) requestMap.get("item");
        setPic(temp, img);

        holder.requestID.setTypeface(FontsManager.getBoldTypeface(context));
        holder.areaModel.setTypeface(FontsManager.getRegularTypeface(context));
        holder.openTime.setTypeface(FontsManager.getRegularTypeface(context));
        holder.scheduledTime.setTypeface(FontsManager.getRegularTypeface(context));

        holder.openTime.setText("Fetching name..");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/Customer/" + (String) requestMap.get("issuedBy"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                holder.openTime.setText("Raised by: " + (String) itemMap.get("name"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.currentPath = (String) requestMap.get("key");
                AppData.currentSelectedUser = (String) requestMap.get("issuedBy");
                fragment.showInfo();
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = position;
                getProductDetails();
            }
        });
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

    public void getProductDetails() {
        Intent intent = new Intent(context, NewOrderDetail.class);

        AppData.currentVeh = requestRow.get(pos).getRequestMap();
        AppData.currentVehCust = requestRow.get(pos).getItemMap();

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return this.requestRow.size();
    }
}

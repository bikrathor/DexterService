package com.ccec.dexterservice.managers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ccec.dexterservice.CompletedFragment;
import com.ccec.dexterservice.NewCompletedOrderDetail;
import com.ccec.dexterservice.NewOrderDetail;
import com.ccec.dexterservice.R;
import com.ccec.dexterservice.entities.RequestRow;
import com.ccec.dexterservice.entities.Requests;
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

public class CompletedRecyclerViewAdapter extends RecyclerView.Adapter<ComRecyclerViewHolders> {
    private List<Requests> requestRow;
    protected Context context;
    private CompletedFragment fragment;
    private CircularImageView img;
    private int pos;

    public CompletedRecyclerViewAdapter(Context context, List<Requests> requestRow, CompletedFragment fragment) {
        this.requestRow = requestRow;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public ComRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        ComRecyclerViewHolders viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_service_row2, parent, false);
        viewHolder = new ComRecyclerViewHolders(layoutView, requestRow);

        if (fragment != null)
            fragment.stopLoading();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ComRecyclerViewHolders holder, final int position) {
        Requests req = requestRow.get(position);

        holder.requestID.setText(req.getKey());
        holder.scheduledTime.setText("Processed on: " + req.getScheduleTime());

        img = holder.RVCircle;
        String temp = req.getItem();
        setPic(temp, img);

        holder.requestID.setTypeface(FontsManager.getBoldTypeface(context));
        holder.areaModel.setTypeface(FontsManager.getRegularTypeface(context));
        holder.openTime.setTypeface(FontsManager.getRegularTypeface(context));
        holder.scheduledTime.setTypeface(FontsManager.getRegularTypeface(context));

        holder.areaModel.setText("Fetching car details..");
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("items/" + AppData.serviceType + "/" + req.getItem());
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                holder.areaModel.setText((String) itemMap.get("make") + " " + (String) itemMap.get("model"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.openTime.setText("Fetching name..");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/Customer/" + req.getIssuedBy());
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetwork()) {
                    pos = position;
                    getProductDetails();
                } else
                    Toast.makeText(context, "Please connect to internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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
        Intent intent = new Intent(context, NewCompletedOrderDetail.class);

        AppData.currentReq = requestRow.get(pos);
        AppData.currentStatus = "Completed";

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return this.requestRow.size();
    }
}

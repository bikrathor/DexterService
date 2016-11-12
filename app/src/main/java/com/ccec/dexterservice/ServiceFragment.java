package com.ccec.dexterservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ccec.dexterservice.entities.RequestRow;
import com.ccec.dexterservice.managers.AppData;
import com.ccec.dexterservice.managers.RecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private DatabaseReference databaseReference, databaseReference2;
    private List<Map<String, Object>> requestsMap;
    private List<RequestRow> recyclerViewList;
    private ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);

        if (!isNetwork()) {
            ((HomePage) getActivity()).showHelperNoConnection();
        } else {

            requestsMap = new ArrayList<Map<String, Object>>();
            recyclerViewList = new ArrayList<RequestRow>();
            databaseReference = FirebaseDatabase.getInstance().getReference("/requests/" + AppData.serviceType);
            recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Updating...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getAllRequests(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            });
        }

        return view;
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void getAllRequests(DataSnapshot dataSnapshot) {
        for (final DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            Map<String, Object> requestMap = (HashMap<String, Object>) singleSnapshot.getValue();
            requestsMap.add(requestMap);
        }
        getRequestDetails();
    }

    private void getRequestDetails() {
        for (int i = 0; i < requestsMap.size(); i++) {
            final int temp = i;
            databaseReference2 = FirebaseDatabase.getInstance().getReference("/items/" + AppData.serviceType + "/");
            databaseReference2.child((String) requestsMap.get(i).get("item"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot2.getValue();
                            recyclerViewList.add(new RequestRow(requestsMap.get(temp), itemMap));

                            recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), recyclerViewList, ServiceFragment.this);
                            recyclerView.setAdapter(recyclerViewAdapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError2) {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            return;
                        }
                    });
        }
    }

    public void stopLoading() {
        pDialog.dismiss();
    }
}
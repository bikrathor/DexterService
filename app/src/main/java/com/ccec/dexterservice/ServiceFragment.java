package com.ccec.dexterservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterservice.entities.RequestRow;
import com.ccec.dexterservice.managers.AppData;
import com.ccec.dexterservice.managers.FontsManager;
import com.ccec.dexterservice.managers.RecyclerViewAdapter;
import com.ccec.dexterservice.managers.UserSessionManager;
import com.google.firebase.database.ChildEventListener;
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
    private UserSessionManager session;
    private String id, loc;
    private int selectedId = 0;
    private DataSnapshot mySnap;
    private RelativeLayout errorSec;
    private ImageView erImg;
    private TextView erTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);

        if (!isNetwork()) {
            ((HomePage) getActivity()).showHelperNoConnection();
        } else {
            session = new UserSessionManager(getActivity());
            HashMap<String, String> user = session.getUserDetails();
            loc = user.get(UserSessionManager.TAG_location);
            id = user.get(UserSessionManager.TAG_id);

            recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);

            requestsMap = new ArrayList<Map<String, Object>>();
            recyclerViewList = new ArrayList<RequestRow>();

            errorSec = (RelativeLayout) view.findViewById(R.id.errorSec);

            TextView txtStatus = (TextView) view.findViewById(R.id.textStatus);
            txtStatus.setTypeface(FontsManager.getBoldTypeface(getActivity()));

            ImageView imgStatus = (ImageView) view.findViewById(R.id.imageView2);
            imgStatus.setOnClickListener(null);

            erTxt = (TextView) view.findViewById(R.id.errorHeader);
            erImg = (ImageView) view.findViewById(R.id.errorImage);

            databaseReference = FirebaseDatabase.getInstance().getReference("/requests/" + AppData.serviceType);
            Query query = databaseReference.orderByChild("issuedTo").equalTo(id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    requestsMap = new ArrayList<Map<String, Object>>();
                    recyclerViewList = new ArrayList<RequestRow>();

                    mySnap = dataSnapshot;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            databaseReference.keepSynced(true);

            Spinner spinnerLoc = (Spinner) view.findViewById(R.id.statusSpinner);
            List<String> categories = new ArrayList<String>();
            categories.add("Open");
            categories.add("Accepted");
            categories.add("Completed");

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, categories) {
                @Override
                public boolean isEnabled(int position) {
                    return true;
                }

                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    ((TextView) v).setTypeface(FontsManager.getRegularTypeface(getContext()));

                    return v;
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    tv.setTextColor(Color.BLACK);

                    ((TextView) view).setTypeface(FontsManager.getRegularTypeface(getContext()));
                    return view;
                }
            };
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLoc.setAdapter(dataAdapter);
            spinnerLoc.setSelection(0, false);
            spinnerLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedId = position;

                    if (mySnap != null) {
                        showDialog();
                        getAllRequests(mySnap);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        return view;
    }

    private void showDialog() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Updating...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void getAllRequests(DataSnapshot dataSnapshot) {
        requestsMap = new ArrayList<Map<String, Object>>();
        recyclerViewList = new ArrayList<RequestRow>();

        for (final DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            Map<String, Object> requestMap = (HashMap<String, Object>) singleSnapshot.getValue();

            switch (selectedId) {
                case 0:
                    if (((String) requestMap.get("status")).equals("Open"))
                        requestsMap.add(requestMap);
                    break;
                case 1:
                    if (((String) requestMap.get("status")).equals("Accepted"))
                        requestsMap.add(requestMap);
                    break;
                case 2:
                    if (((String) requestMap.get("status")).equals("Completed"))
                        requestsMap.add(requestMap);
                    break;
            }
        }

        getRequestDetails();
    }

    private void getRequestDetails() {
        if (requestsMap.size() == 0) {
            errorSec.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);

            if (loc.equals("na")) {
                erTxt.setText("Profile not updated");
                erImg.setImageResource(R.drawable.icon_not_verified);
            } else {
                erTxt.setText("No Request Found");
                erImg.setImageResource(R.drawable.icon_bin_empty);
            }

            stopLoading();
        } else {
            errorSec.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

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
                databaseReference2.keepSynced(true);
            }
        }
    }

    public void stopLoading() {
        pDialog.dismiss();
    }
}
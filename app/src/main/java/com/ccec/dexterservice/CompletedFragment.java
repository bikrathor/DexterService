package com.ccec.dexterservice;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterservice.entities.RequestRow;
import com.ccec.dexterservice.managers.AppData;
import com.ccec.dexterservice.managers.CompletedRecyclerViewAdapter;
import com.ccec.dexterservice.managers.UserSessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CompletedFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CompletedRecyclerViewAdapter recyclerViewAdapter;
    private DatabaseReference databaseReference, databaseReference2;
    private List<Map<String, Object>> requestsMap;
    private List<RequestRow> recyclerViewList;
    private ProgressDialog pDialog;
    private UserSessionManager session;
    private String id, loc;
    private int selectedId = 2;
    private DataSnapshot mySnap;
    private RelativeLayout errorSec;
    private ImageView erImg;
    private TextView erTxt;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private String dayFire, monthFire, yearFire;
    private Button subButton, calButton;
    public static boolean DESC = false;
    private Map<String, Object> itemSortedMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);

        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        loc = user.get(UserSessionManager.TAG_location);
        id = user.get(UserSessionManager.TAG_id);

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCal();
            }
        };

        recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        requestsMap = new ArrayList<Map<String, Object>>();
        recyclerViewList = new ArrayList<RequestRow>();

        errorSec = (RelativeLayout) view.findViewById(R.id.errorSec);

        erTxt = (TextView) view.findViewById(R.id.errorHeader);
        erImg = (ImageView) view.findViewById(R.id.errorImage);

        databaseReference = FirebaseDatabase.getInstance().getReference("/requests/Completed" + AppData.serviceType);
        Query query = databaseReference.orderByChild("issuedTo").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requestsMap = new ArrayList<Map<String, Object>>();
                recyclerViewList = new ArrayList<RequestRow>();

                mySnap = dataSnapshot;
                if (mySnap != null) {
                    showDialog();
                    getAllRequests(mySnap);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.keepSynced(true);

        return view;
    }

    private void updateCal() {
        SimpleDateFormat format = new SimpleDateFormat("d");
        String date = format.format(myCalendar.getTime());
        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("EE MMM d'st', yyyy");
        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("EE MMM d'nd', yyyy");
        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("EE MMM d'rd', yyyy");
        else
            format = new SimpleDateFormat("EE MMM d'th', yyyy");
        String yourDate = format.format(myCalendar.getTime());

        format = new SimpleDateFormat("d");
        dayFire = format.format(myCalendar.getTime());
        format = new SimpleDateFormat("MMM");
        monthFire = format.format(myCalendar.getTime());
        format = new SimpleDateFormat("yyyy");
        yearFire = format.format(myCalendar.getTime());

        calButton.setText(yourDate);
        subButton.setVisibility(View.VISIBLE);

        myCalendar = Calendar.getInstance();
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void getAllRequests(DataSnapshot dataSnapshot) {
        requestsMap = new ArrayList<Map<String, Object>>();
        recyclerViewList = new ArrayList<RequestRow>();

        Map<String, Object> rMap = (HashMap<String, Object>) dataSnapshot.getValue();
        itemSortedMap = new HashMap<>();
        if (rMap != null)
            itemSortedMap = sortByComparator(rMap, DESC);

        for (int i = 0; i < itemSortedMap.keySet().size(); i++) {
            Map<String, Object> requestMap = (HashMap<String, Object>) itemSortedMap.get(itemSortedMap.keySet().toArray()[i]);

            switch (selectedId) {
                case 0:
                    if (((String) requestMap.get("status")).equals("Open")) {
                        AppData.currentStatus = "Open";
                        requestsMap.add(requestMap);
                    }
                    break;
                case 1:
                    if (((String) requestMap.get("status")).equals("Accepted")) {
                        AppData.currentStatus = "Accepted";
                        requestsMap.add(requestMap);
                    }
                    break;
                case 2:
                    if (((String) requestMap.get("status")).equals("Completed")) {
                        AppData.currentStatus = "Completed";
                        requestsMap.add(requestMap);
                    }
                    break;
            }
        }

        getRequestDetails();
    }

    private void showDialog() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Updating...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
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

                                    recyclerViewAdapter = new CompletedRecyclerViewAdapter(getActivity(), recyclerViewList, CompletedFragment.this);
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

    private static Map<String, Object> sortByComparator(Map<String, Object> unsortMap, final boolean order) {
        List<Map.Entry<String, Object>> list = new LinkedList<>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {
            public int compare(Map.Entry<String, Object> o1,
                               Map.Entry<String, Object> o2) {
                if (order)
                    return ((String) ((HashMap) o1.getValue()).get("key")).compareTo((String) ((HashMap) o2.getValue()).get("key"));
                else
                    return ((String) ((HashMap) o2.getValue()).get("key")).compareTo((String) ((HashMap) o1.getValue()).get("key"));
            }
        });

        Map<String, Object> sortedMap = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, Object> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public void stopLoading() {
        try {
            pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
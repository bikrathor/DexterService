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

import com.ccec.dexterservice.entities.Notif;
import com.ccec.dexterservice.entities.RequestRow;
import com.ccec.dexterservice.entities.Requests;
import com.ccec.dexterservice.managers.AcceptedRecyclerViewAdapter;
import com.ccec.dexterservice.managers.AppData;
import com.ccec.dexterservice.managers.FontsManager;
import com.ccec.dexterservice.managers.OpenRecyclerViewAdapter;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcceptedFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AcceptedRecyclerViewAdapter recyclerViewAdapter;
    private DatabaseReference databaseReference, databaseReference2;
    private List<Map<String, Object>> requestsMap;
    private List<RequestRow> recyclerViewList;
    private ProgressDialog pDialog;
    private UserSessionManager session;
    private String id, loc;
    private int selectedId = 1;
    private DataSnapshot mySnap;
    private RelativeLayout errorSec;
    private ImageView erImg;
    private TextView erTxt;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private String dayFire, monthFire, yearFire;
    private Button subButton, calButton;

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

        databaseReference = FirebaseDatabase.getInstance().getReference("/requests/" + AppData.serviceType);
        Query query = databaseReference.orderByChild("issuedTo").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requestsMap = new ArrayList<Map<String, Object>>();
                recyclerViewList = new ArrayList<RequestRow>();

                mySnap = dataSnapshot;
                if (mySnap != null) {
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

    public void showInfo() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.dialog_accept_order, null);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setView(dialoglayout);
        final android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();

        TextView txtButton = (TextView) dialoglayout.findViewById(R.id.headerTextCloudCreated);
        txtButton.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        calButton = (Button) dialoglayout.findViewById(R.id.btn_pollutioncheckdate);
        subButton = (Button) dialoglayout.findViewById(R.id.btn_pollutioncheckdate2);
        calButton.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        subButton.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                long now = System.currentTimeMillis() - 1000;
                dialog.getDatePicker().setMinDate(now);
                dialog.show();
            }
        });

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Processing..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();

                SimpleDateFormat format = new SimpleDateFormat("d");
                Date da = new Date();
                da.setDate(Integer.parseInt(dayFire));
                int mon = 0;
                switch (monthFire) {
                    case "Jan":
                        mon = 0;
                        break;
                    case "Feb":
                        mon = 1;
                        break;
                    case "Mar":
                        mon = 2;
                        break;
                    case "Apr":
                        mon = 3;
                        break;
                    case "May":
                        mon = 4;
                        break;
                    case "Jun":
                        mon = 5;
                        break;
                    case "Jul":
                        mon = 6;
                        break;
                    case "Aug":
                        mon = 7;
                        break;
                    case "Sep":
                        mon = 8;
                        break;
                    case "Oct":
                        mon = 9;
                        break;
                    case "Nov":
                        mon = 10;
                        break;
                    case "Dec":
                        mon = 11;
                        break;
                }
                da.setMonth(mon);
                String date = format.format(da);
                if (date.endsWith("1") && !date.endsWith("11"))
                    format = new SimpleDateFormat("EE, MMM d'st'");
                else if (date.endsWith("2") && !date.endsWith("12"))
                    format = new SimpleDateFormat("EE, MMM d'nd'");
                else if (date.endsWith("3") && !date.endsWith("13"))
                    format = new SimpleDateFormat("EE, MMM d'rd'");
                else
                    format = new SimpleDateFormat("EE, MMM d'th'");
                final String yourDate = format.format(da);

                final DatabaseReference firebasedbref = FirebaseDatabase.getInstance().getReference().child("requests/" + AppData.serviceType + "/" + AppData.currentPath);
                firebasedbref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Requests requests = dataSnapshot.getValue(Requests.class);
                        requests.setScheduleTime(yourDate);
                        requests.setStatus("Accepted");

                        DatabaseReference firebasedbref2 = FirebaseDatabase.getInstance().getReference().child("requests/" + AppData.serviceType + "/" + AppData.currentPath);
                        firebasedbref2.setValue(requests, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                pDialog.dismiss();
                                dialog.dismiss();
                                sendNotification();

                                HomeFragment profileFragment = new HomeFragment();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, profileFragment).commit();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void sendNotification() {
        DatabaseReference firebasedbrefproduct = FirebaseDatabase.getInstance().getReference("users/Customer/" + AppData.currentSelectedUser);
        firebasedbrefproduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference firebasedbrefproduc = FirebaseDatabase.getInstance().getReference();
                Notif notif = new Notif();
                notif.setUsername((String) ((HashMap) dataSnapshot.getValue()).get("fcm"));
                notif.setMessage("Order Accepted");
                firebasedbrefproduc.child("notifs").push().setValue(notif);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showDialog() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Updating...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
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

                                recyclerViewAdapter = new AcceptedRecyclerViewAdapter(getActivity(), recyclerViewList, null);
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
        try {
            pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
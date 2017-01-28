package com.ccec.dexterservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterservice.entities.FlowRecord;
import com.ccec.dexterservice.entities.Notif;
import com.ccec.dexterservice.entities.Requests;
import com.ccec.dexterservice.managers.AppData;
import com.ccec.dexterservice.managers.AttachmentsViewAdapter;
import com.ccec.dexterservice.managers.FontsManager;
import com.ccec.dexterservice.managers.ProcessFlowViewAdapter;
import com.ccec.dexterservice.managers.QueryviewAdapter;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewOrderDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private TextView location, name, company, locationD, nameD, companyD, locMoreD, carMoreD;
    private Object obj;
    private Object custobj;
    private GoogleMap mMap;
    private LatLng sydney;
    private ImageView navImg;
    private TextView CarCametv, CarWorkStartedtv, CarWorkCompletedtv, CarWorkPricePaidtv, CarWorkRequestCompletedtv;
    private SwitchCompat CarCamesv, CarWorkStartedsv, CarWorkCompletedsv, CarWorkPricePaidsv, CarWorkRequestCompletedsv;
    private String ProcessFlowUpdateText, key;
    private DatabaseReference firebaseprocessflowref;
    private boolean sCheckable = true;
    private android.app.AlertDialog.Builder builder;
    private TextView CarMake, CarModel, CarManufacturingYear, CarRegNumber, CarChessisNumber, CarAvgKilometer, CarKilometer, CarPollutionChkDt, CarNxtPollutionChkDt, CarInsurancePurchaseDt, CarNxtInsurancePurchaseDt, company1, companyD1;
    private View linview1;
    private LinearLayoutManager linearLayoutManagerProcess;
    private RecyclerView processList;
    private CardView cardProcessList, cardAttachList;
    private LinearLayout lrecyclerView, lrecyclerView2, lin, lincompany1;
    private RecyclerView recyclerView, recyclerView2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            obj = AppData.currentVeh;
            custobj = AppData.currentVehCust;

            appBarLayout.setTitle(FontsManager.actionBarTypeface(getActivity(), (String) ((HashMap) obj).get("key")));
        }

        firebaseprocessflowref = FirebaseDatabase.getInstance().getReference("processFlow/" + (String) ((HashMap) AppData.currentVeh).get("key"));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.getLocation(((String) ((HashMap) obj).get("issuedBy")), new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    sydney = new LatLng(location.latitude, location.longitude);
                    System.out.println(String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));
                } else {
                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.neworder_detail, container, false);

        name = (TextView) view.findViewById(R.id.fullNameTitle);
        location = (TextView) view.findViewById(R.id.skypeNameTitle);
        company = (TextView) view.findViewById(R.id.companyNameTitle);
        company1 = (TextView) view.findViewById(R.id.companyNameTitle1);

        name.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        location.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        company.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        company1.setTypeface(FontsManager.getBoldTypeface(getActivity()));

        nameD = (TextView) view.findViewById(R.id.fullNameDetail);
        locationD = (TextView) view.findViewById(R.id.skypeNameDetail);
        companyD = (TextView) view.findViewById(R.id.companyNameDetail);
        companyD1 = (TextView) view.findViewById(R.id.companyNameDetail1);

        nameD.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        locationD.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        companyD.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        companyD1.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        lincompany1 = (LinearLayout) view.findViewById(R.id.linProf11111);
        linview1 = (View) view.findViewById(R.id.viewne);

        locMoreD = (TextView) view.findViewById(R.id.fullNameMore);
        SpannableString content = new SpannableString(locMoreD.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        locMoreD.setText(content);
        locMoreD.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        carMoreD = (TextView) view.findViewById(R.id.skypeNameMore);
        SpannableString content2 = new SpannableString(carMoreD.getText());
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        carMoreD.setText(content2);
        carMoreD.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        if (AppData.currentStatus == "Accepted" || AppData.currentStatus == "Completed") {
            if (AppData.currentStatus == "Completed")
                company1.setText("Completed on: ");
            linview1.setVisibility(View.VISIBLE);
            lincompany1.setVisibility(View.VISIBLE);
            companyD1.setText((String) ((HashMap) obj).get("scheduleTime"));
        }

        locMoreD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + sydney.latitude + ", " + sydney.longitude));
                startActivity(intent);
            }
        });

        carMoreD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCarDetails();
            }
        });

        lin = (LinearLayout) view.findViewById(R.id.linProf11);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/Customer/" + (String) ((HashMap) AppData.currentVeh).get("issuedBy"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                nameD.setText(((String) itemMap.get("name")) + "\n"
                        + ((String) itemMap.get("contact")) + "\n"
                        + ((String) itemMap.get("location")));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        locationD.setText((String) ((HashMap) custobj).get("make") + " " + (String) ((HashMap) custobj).get("model"));
        companyD.setText((String) ((HashMap) obj).get("openTime"));

        CarCametv = (TextView) view.findViewById(R.id.carcamestep);
        CarWorkStartedtv = (TextView) view.findViewById(R.id.carworkstartedstep);
        CarWorkCompletedtv = (TextView) view.findViewById(R.id.carworkcompletedstep);
        CarWorkPricePaidtv = (TextView) view.findViewById(R.id.workpricepaidstep);
        CarWorkRequestCompletedtv = (TextView) view.findViewById(R.id.workrequestcompletestep);
        TextView sFlowtv = (TextView) view.findViewById(R.id.sFlow);
        TextView aFlowtv = (TextView) view.findViewById(R.id.aFlow);

        CarCametv.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarWorkStartedtv.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarWorkCompletedtv.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarWorkPricePaidtv.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarWorkRequestCompletedtv.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        sFlowtv.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        aFlowtv.setTypeface(FontsManager.getBoldTypeface(getActivity()));

        CarCamesv = (SwitchCompat) view.findViewById(R.id.carcameswitchButton);
        CarWorkStartedsv = (SwitchCompat) view.findViewById(R.id.carworkstartedswitchButton);
        CarWorkCompletedsv = (SwitchCompat) view.findViewById(R.id.carworkcompletedswitchButton);
        CarWorkPricePaidsv = (SwitchCompat) view.findViewById(R.id.carworkpricepaidswitchButton);
        CarWorkRequestCompletedsv = (SwitchCompat) view.findViewById(R.id.carworkrequestcompleteswitchButton);

        if (AppData.currentStatus == "Accepted") {
            final DatabaseReference firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("processFlow/" + (String) ((HashMap) obj).get("key"));
            firebasedbrefproducts.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    String temp = null;
                    try {
                        temp = (String) itemMap.get("switches");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        sCheckable = false;
//                        CarCamesv.setClickable(false);
                        CarWorkStartedsv.setClickable(false);
                        CarWorkCompletedsv.setClickable(false);
                        CarWorkPricePaidsv.setClickable(false);
                        CarWorkRequestCompletedsv.setClickable(false);
                    }

                    if (temp == null)
                        temp = "00000";

                    if (temp.charAt(0) == '1') {
                        CarCamesv.setChecked(true);
                        CarCamesv.setClickable(false);
                    }

                    if (temp.charAt(1) == '1') {
                        CarWorkStartedsv.setChecked(true);
                        CarWorkStartedsv.setClickable(false);
                    }

                    if (temp.charAt(2) == '1') {
                        CarWorkCompletedsv.setChecked(true);
                        CarWorkCompletedsv.setClickable(false);
                    }

                    if (temp.charAt(3) == '1') {
                        CarWorkPricePaidsv.setChecked(true);
                        CarWorkPricePaidsv.setClickable(false);
                    }

                    if (temp.charAt(4) == '1') {
                        CarWorkRequestCompletedsv.setChecked(true);
                        CarWorkRequestCompletedsv.setClickable(false);
                    }

                    final String finalTemp = temp;
                    CarCamesv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (sCheckable == true) {
                                if (b) {
                                    if (finalTemp.charAt(0) != '1') {
                                        ProcessFlowUpdateText = CarCametv.getText().toString();
                                        FlowRecord flowRecord = new FlowRecord();
                                        flowRecord.setTitle(ProcessFlowUpdateText);

                                        Calendar c = Calendar.getInstance();
                                        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM hh:mm a");
                                        String formattedDate = df.format(c.getTime());
                                        flowRecord.setTimestamp(formattedDate);

                                        firebasedbrefproducts.push().setValue(flowRecord, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                firebasedbrefproducts.child("switches").setValue("10000", new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        firebasedbrefproducts.child("status").setValue("Car came to service center");
                                                        CarCamesv.setClickable(false);
                                                        showProcessFlowCard();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            } else {
                                showDialog();
                                CarCamesv.setChecked(false);
                            }
                        }
                    });

                    CarWorkStartedsv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                if (CarCamesv.isChecked()) {
                                    if (finalTemp.charAt(1) != '1') {
                                        ProcessFlowUpdateText = CarWorkStartedtv.getText().toString();
                                        FlowRecord flowRecord = new FlowRecord();
                                        flowRecord.setTitle(ProcessFlowUpdateText);

                                        Calendar c = Calendar.getInstance();
                                        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM hh:mm a");
                                        String formattedDate = df.format(c.getTime());
                                        flowRecord.setTimestamp(formattedDate);

                                        firebasedbrefproducts.push().setValue(flowRecord, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                firebasedbrefproducts.child("switches").setValue("11000", new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        firebasedbrefproducts.child("status").setValue("Work started");
                                                        CarWorkStartedsv.setClickable(false);
                                                        showProcessFlowCard();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                                if (!CarCamesv.isChecked()) {
                                    Toast.makeText(getContext(), "Please make sure that car came to service center.", Toast.LENGTH_SHORT).show();
                                    CarWorkStartedsv.setChecked(false);
                                }
                            }
                        }
                    });

                    CarWorkCompletedsv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                if (CarCamesv.isChecked() && CarWorkStartedsv.isChecked()) {
                                    if (finalTemp.charAt(2) != '1') {
                                        ProcessFlowUpdateText = CarWorkCompletedtv.getText().toString();
                                        FlowRecord flowRecord = new FlowRecord();
                                        flowRecord.setTitle(ProcessFlowUpdateText);

                                        Calendar c = Calendar.getInstance();
                                        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM hh:mm a");
                                        String formattedDate = df.format(c.getTime());
                                        flowRecord.setTimestamp(formattedDate);

                                        firebasedbrefproducts.push().setValue(flowRecord, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                firebasedbrefproducts.child("switches").setValue("11100", new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        firebasedbrefproducts.child("status").setValue("Work completed");
                                                        CarWorkCompletedsv.setClickable(false);
                                                        showProcessFlowCard();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                                if (!CarWorkStartedsv.isChecked()) {
                                    Toast.makeText(getContext(), "Please make sure that work on the car has started.", Toast.LENGTH_SHORT).show();
                                    CarWorkCompletedsv.setChecked(false);
                                }
                            }
                        }
                    });

                    CarWorkPricePaidsv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                if (CarCamesv.isChecked() && CarWorkStartedsv.isChecked() && CarWorkCompletedsv.isChecked()) {
                                    if (finalTemp.charAt(3) != '1') {
                                        ProcessFlowUpdateText = CarWorkPricePaidtv.getText().toString();
                                        FlowRecord flowRecord = new FlowRecord();
                                        flowRecord.setTitle(ProcessFlowUpdateText);

                                        Calendar c = Calendar.getInstance();
                                        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM hh:mm a");
                                        String formattedDate = df.format(c.getTime());
                                        flowRecord.setTimestamp(formattedDate);

                                        firebasedbrefproducts.push().setValue(flowRecord, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                firebasedbrefproducts.child("switches").setValue("11110", new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        firebasedbrefproducts.child("status").setValue("Payment completed");
                                                        CarWorkPricePaidsv.setClickable(false);
                                                        showProcessFlowCard();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                                if (!CarWorkCompletedsv.isChecked()) {
                                    Toast.makeText(getContext(), "Please make sure that work on the car has completed.", Toast.LENGTH_SHORT).show();
                                    CarWorkPricePaidsv.setChecked(false);
                                }

                            }
                        }
                    });

                    CarWorkRequestCompletedsv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                if (CarCamesv.isChecked() && CarWorkStartedsv.isChecked() && CarWorkCompletedsv.isChecked() && CarWorkPricePaidsv.isChecked()) {
                                    if (finalTemp.charAt(4) != '1') {
                                        final ProgressDialog pDialog = new ProgressDialog(getActivity());
                                        pDialog.setMessage("Processing..");
                                        pDialog.setIndeterminate(false);
                                        pDialog.setCancelable(false);
                                        pDialog.show();

                                        ProcessFlowUpdateText = CarWorkRequestCompletedtv.getText().toString();
                                        FlowRecord flowRecord = new FlowRecord();
                                        flowRecord.setTitle(ProcessFlowUpdateText);

                                        Calendar c = Calendar.getInstance();
                                        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM hh:mm a");
                                        String formattedDate = df.format(c.getTime());
                                        flowRecord.setTimestamp(formattedDate);

                                        firebasedbrefproducts.push().setValue(flowRecord, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                firebasedbrefproducts.child("switches").setValue("11111", new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        firebasedbrefproducts.child("status").setValue("Completed", new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                final DatabaseReference firebasedbref = FirebaseDatabase.getInstance().getReference().child("requests/" + AppData.serviceType + "/" + (String) ((HashMap) obj).get("key"));
                                                                firebasedbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        Requests requests = dataSnapshot.getValue(Requests.class);
                                                                        requests.setStatus("Completed");

                                                                        DatabaseReference firebasedbref2 = FirebaseDatabase.getInstance().getReference().child("requests/" + AppData.serviceType + "/" + (String) ((HashMap) obj).get("key"));
                                                                        firebasedbref2.setValue(requests, new DatabaseReference.CompletionListener() {
                                                                            @Override
                                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                                final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("attachments/" + (String) ((HashMap) AppData.currentVeh).get("key"));
                                                                                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        if (dataSnapshot.getChildrenCount() > 0) {
                                                                                            Map<String, String> itemMap = (HashMap<String, String>) dataSnapshot.getValue();
                                                                                            ArrayList<String> itemMap2 = new ArrayList<String>(itemMap.values());

                                                                                            retainStorage(itemMap2);
                                                                                            databaseReference2.removeValue();
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });

                                                                                pDialog.dismiss();
                                                                                sendNotification();

                                                                                Toast.makeText(getActivity(), "Service completed", Toast.LENGTH_SHORT).show();
                                                                                ((NewOrderDetail) getActivity()).goBack();
                                                                                AppData.selectedTab = 2;
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
                                                });
                                            }
                                        });
                                    }
                                }
                                if (!CarWorkPricePaidsv.isChecked()) {
                                    Toast.makeText(getContext(), "Please make sure that customer has paid.", Toast.LENGTH_SHORT).show();
                                    CarWorkRequestCompletedsv.setChecked(false);
                                }
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            firebasedbrefproducts.keepSynced(true);
        } else {
            if (AppData.currentStatus == "Completed" || AppData.currentStatus == "Open") {
                CardView cardView = (CardView) view.findViewById(R.id.card_view);
                cardView.setVisibility(View.GONE);
            }

            if (AppData.currentStatus == "Completed") {
                CardView cardView = (CardView) view.findViewById(R.id.card_view5);
                cardView.setVisibility(View.GONE);
            }

            CarCamesv.setClickable(false);
            CarWorkStartedsv.setClickable(false);
            CarWorkCompletedsv.setClickable(false);
            CarWorkPricePaidsv.setClickable(false);
            CarWorkRequestCompletedsv.setClickable(false);
        }

        CarNxtInsurancePurchaseDt = (TextView) view.findViewById(R.id.queriesHeader);
        CarInsurancePurchaseDt = (TextView) view.findViewById(R.id.queriesHeader2);
        CarNxtInsurancePurchaseDt.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        CarInsurancePurchaseDt.setTypeface(FontsManager.getBoldTypeface(getActivity()));

        recyclerView = (RecyclerView) view.findViewById(R.id.queriesList);
        lrecyclerView = (LinearLayout) view.findViewById(R.id.queries_steps);
        showQueriesCard();

        recyclerView2 = (RecyclerView) view.findViewById(R.id.attachList);
        lrecyclerView2 = (LinearLayout) view.findViewById(R.id.attach_steps);
        cardAttachList = (CardView) view.findViewById(R.id.card_view5);
        showAttachmentsCard();

        cardProcessList = (CardView) view.findViewById(R.id.card_view4);
        processList = (RecyclerView) view.findViewById(R.id.processList);
        showProcessFlowCard();

        return view;
    }

    private void retainStorage(ArrayList<String> itemMap2) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");

        for (String item : itemMap2) {
            storageRef.child("attachments/" + item).delete().addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        }
    }

    private void showCarDetails() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.custom_show_cardetails, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setView(dialoglayout);

        CarMake = (TextView) dialoglayout.findViewById(R.id.car_make_tv);
        CarModel = (TextView) dialoglayout.findViewById(R.id.car_model_tv);
        CarManufacturingYear = (TextView) dialoglayout.findViewById(R.id.car_manufactured_tv);
        CarRegNumber = (TextView) dialoglayout.findViewById(R.id.car_regnum_tv);
        CarChessisNumber = (TextView) dialoglayout.findViewById(R.id.car_chessisnum_tv);
        CarKilometer = (TextView) dialoglayout.findViewById(R.id.car_kilometer_tv);
        CarAvgKilometer = (TextView) dialoglayout.findViewById(R.id.car_avgkilometer_tv);
        CarPollutionChkDt = (TextView) dialoglayout.findViewById(R.id.car_pollution_tv);
//                CarNxtPollutionChkDt = (TextView) dialoglayout.findViewById(R.id.car_pollutionnxt_tv);
        CarInsurancePurchaseDt = (TextView) dialoglayout.findViewById(R.id.car_insurancedt_tv);
//                CarNxtInsurancePurchaseDt = (TextView) dialoglayout.findViewById(R.id.car_insurancedtnxt_tv);

        CarMake.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarModel.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarManufacturingYear.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarRegNumber.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarChessisNumber.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarPollutionChkDt.setTypeface(FontsManager.getRegularTypeface(getActivity()));
//                CarNxtPollutionChkDt.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarInsurancePurchaseDt.setTypeface(FontsManager.getRegularTypeface(getActivity()));
//                CarNxtInsurancePurchaseDt.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarKilometer.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarAvgKilometer.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        CarMake.setText((String) ((HashMap) AppData.currentVehCust).get("make"));
        CarModel.setText((String) ((HashMap) AppData.currentVehCust).get("model"));
        CarManufacturingYear.setText((String) ((HashMap) AppData.currentVehCust).get("manufacturedin"));
        CarRegNumber.setText((String) ((HashMap) AppData.currentVehCust).get("registrationnumber"));
        CarChessisNumber.setText((String) ((HashMap) AppData.currentVehCust).get("chessisnumber"));
        CarKilometer.setText((String) ((HashMap) AppData.currentVehCust).get("kilometer"));
        CarAvgKilometer.setText((String) ((HashMap) AppData.currentVehCust).get("avgrunning"));
        CarPollutionChkDt.setText((String) ((HashMap) AppData.currentVehCust).get("polluctionchkdate"));
//                CarNxtPollutionChkDt.setText((String) ((HashMap) AppData.currentVehCust).get("nextpolluctionchkdate"));
        CarInsurancePurchaseDt.setText((String) ((HashMap) AppData.currentVehCust).get("insurancepurchasedate"));
//                CarNxtInsurancePurchaseDt.setText((String) ((HashMap) AppData.currentVehCust).get("insuranceduedate"));

        CarMake = (TextView) dialoglayout.findViewById(R.id.car_make_tvtxt);
        CarModel = (TextView) dialoglayout.findViewById(R.id.car_model_tvtxt);
        CarManufacturingYear = (TextView) dialoglayout.findViewById(R.id.car_manufactured_tvtxt);
        CarRegNumber = (TextView) dialoglayout.findViewById(R.id.car_regnum_tvtxt);
        CarChessisNumber = (TextView) dialoglayout.findViewById(R.id.car_chessisnum_tvtxt);
        CarKilometer = (TextView) dialoglayout.findViewById(R.id.car_kilometer_tvtxt);
        CarAvgKilometer = (TextView) dialoglayout.findViewById(R.id.car_avgkilometer_tvtxt);
        CarPollutionChkDt = (TextView) dialoglayout.findViewById(R.id.car_pollution_tvtxt);
//                CarNxtPollutionChkDt = (TextView) dialoglayout.findViewById(R.id.car_pollutionnxt_tvtxt);
        CarInsurancePurchaseDt = (TextView) dialoglayout.findViewById(R.id.car_insurancedt_tvtxt);
//                CarNxtInsurancePurchaseDt = (TextView) dialoglayout.findViewById(R.id.car_insurancedtnxt_tvtxt);

        CarMake.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarModel.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarManufacturingYear.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarRegNumber.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarChessisNumber.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarPollutionChkDt.setTypeface(FontsManager.getRegularTypeface(getActivity()));
//                CarNxtPollutionChkDt.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarInsurancePurchaseDt.setTypeface(FontsManager.getRegularTypeface(getActivity()));
//                CarNxtInsurancePurchaseDt.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarKilometer.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        CarAvgKilometer.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAttachmentsCard() {
        recyclerView2.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(layoutManager);

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("attachments/" + (String) ((HashMap) AppData.currentVeh).get("key"));
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    try {
                        Map<String, String> itemMap = (HashMap<String, String>) dataSnapshot.getValue();
                        ArrayList<String> itemMap2 = new ArrayList<String>(itemMap.values());

                        if (itemMap2.size() > 0) {
                            AttachmentsViewAdapter adapter = new AttachmentsViewAdapter(itemMap2, getActivity());
                            recyclerView2.setAdapter(adapter);
                        } else {
                            lrecyclerView2.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        lrecyclerView2.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else
                    cardAttachList.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference2.keepSynced(true);
    }

    private void showQueriesCard() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("queries");
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    try {
                        List<String> itemMap = (ArrayList) dataSnapshot.getValue();
                        String temp = (String) ((HashMap) AppData.currentVeh).get("queries");

                        ArrayList<String> itemMap2 = new ArrayList<String>();
                        for (int i = 0; i < itemMap.size(); i++) {
                            if (temp.charAt(i) == '1')
                                itemMap2.add(itemMap.get(i));
                        }

                        if (itemMap2.size() > 0) {
                            RecyclerView.Adapter adapter = new QueryviewAdapter(itemMap2, getActivity());
                            recyclerView.setAdapter(adapter);
                        } else
                            lrecyclerView.setVisibility(View.GONE);
                    } catch (Exception e) {
                        lrecyclerView.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference2.keepSynced(true);
    }

    private void showProcessFlowCard() {
        if (AppData.currentStatus == "Open")
            cardProcessList.setVisibility(View.GONE);
        else {
            linearLayoutManagerProcess = new LinearLayoutManager(getActivity());
            processList.setLayoutManager(linearLayoutManagerProcess);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/processFlow/" + (String) ((HashMap) obj).get("key"));
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        List<FlowRecord> list = new ArrayList<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            try {
                                Map<String, Object> itemMap = (HashMap<String, Object>) postSnapshot.getValue();
                                FlowRecord f = new FlowRecord();
                                f.setTitle((String) itemMap.get("title"));
                                f.setTimestamp((String) itemMap.get("timestamp"));
                                list.add(f);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        ProcessFlowViewAdapter recyclerViewAdapter = new ProcessFlowViewAdapter(getActivity(), list);
                        processList.setAdapter(recyclerViewAdapter);
                    } else
                        cardProcessList.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            databaseReference.keepSynced(true);
        }
    }

    private void sendNotification() {
        DatabaseReference firebasedbrefproduct = FirebaseDatabase.getInstance().getReference("users/Customer/" + AppData.currentSelectedUser);
        firebasedbrefproduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference firebasedbrefproduc = FirebaseDatabase.getInstance().getReference();
                Notif notif = new Notif();
                notif.setUsername((String) ((HashMap) dataSnapshot.getValue()).get("fcm"));
                notif.setMessage("Thanks for choosing us. We look forward to serve you in future.");
                notif.setTitle("Service Completed");
                firebasedbrefproduc.child("notifs").push().setValue(notif);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showDialog() {
        builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage("User has not accepted scheduled date until now.");
        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

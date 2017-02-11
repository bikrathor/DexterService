package com.ccec.dexterservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.ccec.dexterservice.managers.JSONObjectParser;
import com.ccec.dexterservice.managers.JSONStringParser;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewCompletedOrderDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private TextView location, name, company, locationD, nameD, companyD, locMoreD, carMoreD;
    private Requests obj;
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
    private CardView cardProcessList, cardAttachList, cardPrice;
    private LinearLayout lrecyclerView, lrecyclerView2, lin, lincompany1, linSkype;
    private RecyclerView recyclerView, recyclerView2;
    private String finalTemp;
    private String estPrice;
    private TextView esPriceF, esPrice;
    private ProgressDialog pDialog;
    private String titleParam;
    private static final String BASE_URL = "http://188.166.245.67/html/phpscript/";
    private Requests requestsCom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            obj = AppData.currentReq;

            appBarLayout.setTitle(FontsManager.actionBarTypeface(getActivity(), AppData.currentReq.getKey()));
        }

        estPrice = AppData.currentReq.getEstPrice();
        firebaseprocessflowref = FirebaseDatabase.getInstance().getReference("processFlow/" + AppData.currentReq.getKey());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.getLocation(AppData.currentReq.getIssuedBy(), new LocationCallback() {
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
        linSkype = (LinearLayout) view.findViewById(R.id.linProf2D);
        linSkype.setVisibility(View.GONE);

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

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("items/" + AppData.serviceType + "/" + AppData.currentReq.getItem());
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                locationD.setText((String) itemMap.get("make") + " " + (String) itemMap.get("model"));
                linSkype.setVisibility(View.VISIBLE);
                AppData.currentVehCust = itemMap;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (AppData.currentStatus == "Accepted" || AppData.currentStatus == "Completed") {
            if (AppData.currentStatus == "Completed")
                company1.setText("Completed on: ");
            linview1.setVisibility(View.VISIBLE);
            lincompany1.setVisibility(View.VISIBLE);
            companyD1.setText(AppData.currentReq.getScheduleTime());
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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/Customer/" + AppData.currentReq.getIssuedBy());
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

        companyD.setText(AppData.currentReq.getOpenTime());

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

        cardPrice = (CardView) view.findViewById(R.id.card_view6);
        esPrice = (TextView) view.findViewById(R.id.estPrice);
        esPriceF = (TextView) view.findViewById(R.id.estPriceFlow);
        esPriceF.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        esPrice.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        if (!estPrice.equals("")) {
            showPriceCard();
        } else
            cardPrice.setVisibility(View.GONE);

        cardProcessList = (CardView) view.findViewById(R.id.card_view4);
        processList = (RecyclerView) view.findViewById(R.id.processList);
        showProcessFlowCard();

        return view;
    }

    private void showPriceCard() {
        cardPrice.setVisibility(View.VISIBLE);
        esPrice.setText("\u20B9 " + estPrice);
    }

    private void getPrice() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.custom_edit_item, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setView(dialoglayout);
        final android.app.AlertDialog dialog = builder.create();
        dialog.show();

        final EditText textView = (EditText) dialoglayout.findViewById(R.id.newDetail);
        Button btnView = (Button) dialoglayout.findViewById(R.id.submitButton);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textView.getText().toString().equals(""))
                    Toast.makeText(getActivity(), "Please provide a valid value", Toast.LENGTH_SHORT).show();
                else if (isNetwork()) {
                    dialog.dismiss();
                    final ProgressDialog pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Updating..");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                    final DatabaseReference firebasedbref = FirebaseDatabase.getInstance().getReference("requests/" + AppData.serviceType + "/" + AppData.currentReq.getKey());
                    firebasedbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                Requests v = dataSnapshot.getValue(Requests.class);
                                v.setEstPrice(textView.getText().toString());

                                final DatabaseReference firebasedbref = FirebaseDatabase.getInstance().getReference("requests/" + AppData.serviceType + "/" + AppData.currentReq.getKey());
                                firebasedbref.setValue(v);
                                pDialog.dismiss();
                                Toast.makeText(getActivity(), "Price updated", Toast.LENGTH_SHORT).show();

                                //show placeholder
                                estPrice = textView.getText().toString();
                                CarWorkStartedsv.setChecked(true);
                                sendPriceNotification();
                                showPriceCard();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("attachments/" + AppData.currentReq.getKey());
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
                        String temp = AppData.currentReq.getQueries();

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

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/processFlow/" + AppData.currentReq.getKey());
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
                titleParam = "Thanks for choosing us. We look forward to serve you in future.";
                notif.setMessage(titleParam);
                notif.setTitle("Service Completed");
                firebasedbrefproduc.child("notifs").push().setValue(notif);

                titleParam = "Thanks for choosing us, we look forward to serve you in future";
                new GetData().execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendPriceNotification() {
        DatabaseReference firebasedbrefproduct = FirebaseDatabase.getInstance().getReference("users/Customer/" + AppData.currentSelectedUser);
        firebasedbrefproduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference firebasedbrefproduc = FirebaseDatabase.getInstance().getReference();
                Notif notif = new Notif();
                notif.setUsername((String) ((HashMap) dataSnapshot.getValue()).get("fcm"));
                titleParam = "The estimated price for " + AppData.currentReq.getKey() + " is " + estPrice + " rupees.";
                notif.setMessage(titleParam);
                notif.setTitle("Estimated Price");
                firebasedbrefproduc.child("notifs").push().setValue(notif);

                new PostData().execute();
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

    private void showPDialog() {
        try {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Working...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PostCompletedData extends AsyncTask<String, String, String> {
        private static final String url = BASE_URL + "insertCompleted.php";
        private static final String TAG_DATA = "data";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            JSONObjectParser jsonObjectParser = new JSONObjectParser();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("estPrice", requestsCom.getEstPrice()));
            params.add(new BasicNameValuePair("issuedBy", requestsCom.getIssuedBy()));
            params.add(new BasicNameValuePair("issuedTo", requestsCom.getIssuedTo()));
            params.add(new BasicNameValuePair("item", requestsCom.getItem()));
            params.add(new BasicNameValuePair("keyCar", requestsCom.getKey()));
            params.add(new BasicNameValuePair("openTime", requestsCom.getOpenTime()));
            params.add(new BasicNameValuePair("queries", requestsCom.getQueries()));
            params.add(new BasicNameValuePair("scheduleTime", requestsCom.getScheduleTime()));
            params.add(new BasicNameValuePair("status", requestsCom.getStatus()));

            JSONObject json = jsonObjectParser.makeHttpRequest(url, "GET", params);

            return null;
        }

        protected void onPostExecute(String file_url) {
            try {
                sendNotification();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class PostData extends AsyncTask<String, String, String> {
        private static final String url = BASE_URL + "insertdata.php";
        private static final String TAG_DATA = "data";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showPDialog();
        }

        protected String doInBackground(String... args) {
            JSONObjectParser jsonObjectParser = new JSONObjectParser();

            SimpleDateFormat format = new SimpleDateFormat("d");
            String date = format.format(new Date());
            if (date.endsWith("1") && !date.endsWith("11"))
                format = new SimpleDateFormat("EE, MMM d'st'");
            else if (date.endsWith("2") && !date.endsWith("12"))
                format = new SimpleDateFormat("EE, MMM d'nd'");
            else if (date.endsWith("3") && !date.endsWith("13"))
                format = new SimpleDateFormat("EE, MMM d'rd'");
            else
                format = new SimpleDateFormat("EE, MMM d'th'");
            final String yourDate = format.format(new Date());

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", AppData.currentSelectedUser));
            params.add(new BasicNameValuePair("title", titleParam));
            params.add(new BasicNameValuePair("date", yourDate));
            params.add(new BasicNameValuePair("extra", ""));

            JSONObject json = jsonObjectParser.makeHttpRequest(url, "GET", params);

            return null;
        }

        protected void onPostExecute(String file_url) {
            try {
                pDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class GetData extends AsyncTask<String, String, String> {
        private static final String url = BASE_URL + "insertdata.php";
        private static final String TAG_DATA = "data";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            JSONStringParser jsonObjectParser = new JSONStringParser();

            SimpleDateFormat format = new SimpleDateFormat("d");
            String date = format.format(new Date());
            if (date.endsWith("1") && !date.endsWith("11"))
                format = new SimpleDateFormat("EE, MMM d'st'");
            else if (date.endsWith("2") && !date.endsWith("12"))
                format = new SimpleDateFormat("EE, MMM d'nd'");
            else if (date.endsWith("3") && !date.endsWith("13"))
                format = new SimpleDateFormat("EE, MMM d'rd'");
            else
                format = new SimpleDateFormat("EE, MMM d'th'");
            final String yourDate = format.format(new Date());

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("uid", AppData.currentSelectedUser));
            params.add(new BasicNameValuePair("title", titleParam));
            params.add(new BasicNameValuePair("date", yourDate));
            params.add(new BasicNameValuePair("extra", ""));

            String json = jsonObjectParser.makeServiceCall(url, 1, params);

            return null;
        }

        protected void onPostExecute(String file_url) {
            try {
                pDialog.dismiss();

                Toast.makeText(getActivity(), "Service completed", Toast.LENGTH_SHORT).show();
                ((NewOrderDetail) getActivity()).goBack();
                AppData.selectedTab = 2;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

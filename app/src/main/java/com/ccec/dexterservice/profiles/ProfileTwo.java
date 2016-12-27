package com.ccec.dexterservice.profiles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterservice.ProfileFragment;
import com.ccec.dexterservice.R;
import com.ccec.dexterservice.managers.AppData;
import com.ccec.dexterservice.managers.FontsManager;
import com.ccec.dexterservice.managers.UserSessionManager;
import com.ccec.dexterservice.maps.UpdateMe;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ProfileTwo extends Fragment {
    private EditText fName, website, contact, location, makes;
    private Button btn;
    private UserSessionManager session;
    private String fNameE, websiteE, contactE, locationE, uid, makeE;
    private String fNameS, websiteS, contactS, locationS, makeS;
    private DatabaseReference databaseReference;
    private ProgressDialog pDialog;
    private SwitchCompat switchCompat, switchCompat2, switchCompat3, switchCompat4;
    private boolean sw1 = false, sw2 = false, sw3 = false, sw4 = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_prof_two, container, false);
        setupUI(view.findViewById(R.id.parent));

        session = new UserSessionManager(getContext());
        HashMap<String, String> user = session.getUserDetails();
        uid = user.get(UserSessionManager.TAG_id);
        fNameS = user.get(UserSessionManager.TAG_fullname);
        locationS = user.get(UserSessionManager.TAG_location);
        contactS = user.get(UserSessionManager.TAG_contact);
        websiteS = user.get(UserSessionManager.TAG_website);
        makeS = user.get(UserSessionManager.TAG_makes);

        fName = (EditText) view.findViewById(R.id.profile_full_name);
        location = (EditText) view.findViewById(R.id.profile_location);
        website = (EditText) view.findViewById(R.id.profile_website);
        contact = (EditText) view.findViewById(R.id.profile_contact);
        makes = (EditText) view.findViewById(R.id.profile_make);

        fName.setTypeface(FontsManager.getRegularTypeface(getContext()));
        location.setTypeface(FontsManager.getRegularTypeface(getContext()));
        website.setTypeface(FontsManager.getRegularTypeface(getContext()));
        contact.setTypeface(FontsManager.getRegularTypeface(getContext()));
        makes.setTypeface(FontsManager.getRegularTypeface(getContext()));

        makes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMakeData();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), UpdateMe.class);
                startActivity(in);
            }
        });

        btn = (Button) view.findViewById(R.id.updateProfButton);
        btn.setTypeface(FontsManager.getBoldTypeface(getContext()));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fNameE = fName.getText().toString();
                locationE = location.getText().toString();
                websiteE = website.getText().toString();
                contactE = contact.getText().toString();
                makeE = makes.getText().toString();

                if (validate() && isNetwork()) {
                    updateProfile();

                    fName.setText("");
                    location.setText("");
                    website.setText("");
                    contact.setText("");
                    makes.setText("");
                } else if (!isNetwork()) {
                    Toast.makeText(getContext(), "Please connect to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void showMakeData() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.custom_dialog_reqeuest, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(dialoglayout);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final TextView rpmm = (TextView) dialoglayout.findViewById(R.id.fullNameTitle);
        rpmm.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        final TextView throttle = (TextView) dialoglayout.findViewById(R.id.skypeNameTitle);
        throttle.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        final TextView temp = (TextView) dialoglayout.findViewById(R.id.companyNameTitle);
        temp.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        final TextView airflow = (TextView) dialoglayout.findViewById(R.id.contactNameTitle);
        airflow.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        switchCompat = (SwitchCompat) dialoglayout.findViewById(R.id.switchButton1);
        switchCompat2 = (SwitchCompat) dialoglayout.findViewById(R.id.switchButton2);
        switchCompat3 = (SwitchCompat) dialoglayout.findViewById(R.id.switchButton3);
        switchCompat4 = (SwitchCompat) dialoglayout.findViewById(R.id.switchButton4);

        if (makeS.contains("Maruti")) {
            switchCompat.setChecked(true);
            sw1 = true;
        }

        if (makeS.contains("Honda")) {
            switchCompat2.setChecked(true);
            sw2 = true;
        }

        if (makeS.contains("Hyundai")) {
            switchCompat3.setChecked(true);
            sw3 = true;
        }

        if (makeS.contains("Ford")) {
            switchCompat4.setChecked(true);
            sw4 = true;
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sw1 = isChecked;
            }
        });

        switchCompat2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sw2 = isChecked;
            }
        });

        switchCompat3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sw3 = isChecked;
            }
        });

        switchCompat4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sw4 = isChecked;
            }
        });

        final Button cancel = (Button) dialoglayout.findViewById(R.id.cancelButton);
        cancel.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final Button submit = (Button) dialoglayout.findViewById(R.id.submitButton);
        submit.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String temp = "";
                if (sw1)
                    temp += "Maruti ";
                if (sw2)
                    temp += "Honda ";
                if (sw3)
                    temp += "Hyundai ";
                if (sw4)
                    temp += "Ford ";
                makes.setText(temp);
            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        if (fNameE.isEmpty() && websiteE.isEmpty() && contactE.isEmpty() && locationE.isEmpty() && makeE.isEmpty()) {
            Toast.makeText(getActivity(), "Enter atleast one field", Toast.LENGTH_SHORT).show();
            fName.setError("Enter atleast one field");
            valid = false;
        } else {
            fName.setError(null);
        }

        return valid;
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void updateProfile() {
        if (!fNameE.isEmpty()) {
            fNameS = fNameE;
        }
        if (!websiteE.isEmpty()) {
            websiteS = websiteE;
        }
        if (!contactE.isEmpty()) {
            contactS = contactE;
        }
        if (!locationE.isEmpty()) {
            locationS = locationE;
        }
        if (!makeE.isEmpty()) {
            makeS = makeE;
        }

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Updating..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("/users/ServiceCenter/" + uid);
        databaseReference.child("name").setValue(fNameS);
        databaseReference.child("website").setValue(websiteS);
        databaseReference.child("contact").setValue(contactS);
        databaseReference.child("location").setValue(locationS);
        databaseReference.child("makes").setValue(makeS);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        GeoFire geoFire = new GeoFire(ref);
        if (AppData.selectedLoc != null && !AppData.selectedLoc.isEmpty())
            geoFire.setLocation(uid, new GeoLocation(AppData.selectedCordLoc.getLatitude(), AppData.selectedCordLoc.getLongitude()));

        AppData.selectedLoc = "";
        AppData.selectedCordLoc = null;

        pDialog.dismiss();

        session.createUserLoginSession(fNameS, "", websiteS, contactS, locationS, makeS);

        ProfileFragment profileFragment = new ProfileFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, profileFragment).commit();
//        ((HomePage) getActivity()).setVerificationMethod(5, "Profile");
//        CloudletData.setSelectedItem(5);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (AppData.selectedLoc != null)
            location.setText(AppData.selectedLoc);
    }
}
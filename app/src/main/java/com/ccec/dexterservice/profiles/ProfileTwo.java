package com.ccec.dexterservice.profiles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ccec.dexterservice.ProfileFragment;
import com.ccec.dexterservice.R;
import com.ccec.dexterservice.managers.FontsManager;
import com.ccec.dexterservice.managers.UserSessionManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ProfileTwo extends Fragment {
    private EditText fName, website, contact, location;
    private Button btn;
    private UserSessionManager session;
    private String fNameE, websiteE, contactE, locationE, uid;
    private String fNameS, websiteS, contactS, locationS;
    private DatabaseReference databaseReference;
    private ProgressDialog pDialog;

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

        fName = (EditText) view.findViewById(R.id.profile_full_name);
        location = (EditText) view.findViewById(R.id.profile_location);
        website = (EditText) view.findViewById(R.id.profile_website);
        contact = (EditText) view.findViewById(R.id.profile_contact);

        fName.setTypeface(FontsManager.getRegularTypeface(getContext()));
        location.setTypeface(FontsManager.getRegularTypeface(getContext()));
        website.setTypeface(FontsManager.getRegularTypeface(getContext()));
        contact.setTypeface(FontsManager.getRegularTypeface(getContext()));

        btn = (Button) view.findViewById(R.id.updateProfButton);
        btn.setTypeface(FontsManager.getBoldTypeface(getContext()));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fNameE = fName.getText().toString();
                locationE = location.getText().toString();
                websiteE = website.getText().toString();
                contactE = contact.getText().toString();

                if (validate() && isNetwork()) {
                    updateProfile();

                    fName.setText("");
                    location.setText("");
                    website.setText("");
                    contact.setText("");
                } else if (!isNetwork()) {
                    Toast.makeText(getContext(), "Please connect to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean validate() {
        boolean valid = true;

        if (fNameE.isEmpty() && websiteE.isEmpty() && contactE.isEmpty() && locationE.isEmpty()) {
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

        pDialog.dismiss();

        session.createUserLoginSession(fNameS, "", websiteS, contactS, locationS);

        ProfileFragment profileFragment = new ProfileFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, profileFragment).commit();
//        ((HomePage) getActivity()).setVerificationMethod(5, "Profile");
//        CloudletData.setSelectedItem(5);
    }
}
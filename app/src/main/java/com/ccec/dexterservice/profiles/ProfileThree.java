package com.ccec.dexterservice.profiles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class ProfileThree extends Fragment {
    private Button btn;
    private EditText current, nw, confirm;
    private String currentP, nwP, confirmP, oPwd, uid;
    private UserSessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_prof_three, container, false);
        setupUI(view.findViewById(R.id.parent));

        session = new UserSessionManager(getContext());
        HashMap<String, String> user = session.getUserDetails();
        oPwd = user.get(UserSessionManager.TAG_pwd);
        uid = user.get(UserSessionManager.TAG_id);

        current = (EditText) view.findViewById(R.id.profile_curr_pass);
        nw = (EditText) view.findViewById(R.id.profile_new_pass);
        confirm = (EditText) view.findViewById(R.id.profile_conf_pass);

        current.setTypeface(FontsManager.getRegularTypeface(getContext()));
        nw.setTypeface(FontsManager.getRegularTypeface(getContext()));
        confirm.setTypeface(FontsManager.getRegularTypeface(getContext()));

        btn = (Button) view.findViewById(R.id.updatePassButton);
        btn.setTypeface(FontsManager.getBoldTypeface(getContext()));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentP = current.getText().toString();
                nwP = nw.getText().toString();
                confirmP = confirm.getText().toString();

                if (validate() && isNetwork()) {
                    changePass();
                    current.setText("");
                    nw.setText("");
                    confirm.setText("");
                } else if (!isNetwork()) {
                    Toast.makeText(getContext(), "Please connect to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean validate() {
        boolean valid = true;

        if (currentP.isEmpty() || currentP.length() < 6 || currentP.length() > 12) {
            current.setError("between 6 and 12 alphanumeric characters");
            valid = false;
        } else {
            current.setError(null);
        }

        if (nwP.isEmpty() || nwP.length() < 6 || nwP.length() > 12) {
            nw.setError("between 6 and 12 alphanumeric characters");
            valid = false;
        } else {
            nw.setError(null);
        }

        if (confirmP.isEmpty() || confirmP.length() < 6 || confirmP.length() > 12) {
            confirm.setError("between 6 and 12 alphanumeric characters");
            valid = false;
        } else {
            confirm.setError(null);
        }

        if (!nwP.equals(confirmP)) {
            confirm.setError("Password do not match");
            nw.setError("Password do not match");
            valid = false;
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

    private void changePass() {
        if (!currentP.equals(oPwd)) {
            Toast.makeText(getActivity(), "Old password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        } else {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Toast.makeText(getActivity(), "Processing..", Toast.LENGTH_SHORT).show();

            user.updatePassword(nwP)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Password updated", Toast.LENGTH_SHORT).show();
                                session.createUserLoginSession(user.getUid(), user.getEmail(), nwP);

                                ProfileFragment profileFragment = new ProfileFragment();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, profileFragment).commit();
                                //        ((HomePage) getActivity()).setVerificationMethod(5, "Profile");
                                //        CloudletData.setSelectedItem(5);
                            }
                        }
                    });
        }
    }
}
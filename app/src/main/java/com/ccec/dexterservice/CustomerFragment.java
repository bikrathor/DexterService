package com.ccec.dexterservice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccec.dexterservice.managers.UserSessionManager;

import java.util.HashMap;

public class CustomerFragment extends Fragment {
    private UserSessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);

        session = new UserSessionManager(getContext());
        HashMap<String, String> user = session.getUserDetails();

        return view;
    }
}
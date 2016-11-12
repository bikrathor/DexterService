package com.ccec.dexterservice.profiles;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccec.dexterservice.R;
import com.ccec.dexterservice.managers.FontsManager;
import com.ccec.dexterservice.managers.UserSessionManager;

import java.util.HashMap;

public class ProfileOne extends Fragment {
    private TextView title, website, contact, name;
    private TextView titleD, websiteD, contactD, nameD;
    private UserSessionManager session;
    private String pcontact, pwebsite, pname, plocation;
    private TextView location, locationD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_prof_one, container, false);

        session = new UserSessionManager(getContext());
        HashMap<String, String> user = session.getUserDetails();
        pcontact = user.get(UserSessionManager.TAG_contact);
        pwebsite = user.get(UserSessionManager.TAG_website);
        pname = user.get(UserSessionManager.TAG_fullname);
        plocation = user.get(UserSessionManager.TAG_location);

        name = (TextView) view.findViewById(R.id.fullNameTitle);
        website = (TextView) view.findViewById(R.id.websiteNameTitle);
        contact = (TextView) view.findViewById(R.id.contactNameTitle);
        location = (TextView) view.findViewById(R.id.locationNameTitle);

        nameD = (TextView) view.findViewById(R.id.fullNameDetail);
        websiteD = (TextView) view.findViewById(R.id.websiteNameDetail);
        contactD = (TextView) view.findViewById(R.id.contactNameDetail);
        locationD = (TextView) view.findViewById(R.id.locationNameDetail);

        if (pname.equals("") || pname == null || pname.equals("na")) {
            nameD.setText("Not Mentioned");
        } else {
            nameD.setText(pname);
        }

        if (pwebsite.equals("") || pwebsite == null || pwebsite.equals("na")) {
            websiteD.setText("Not Mentioned");
        } else {
            websiteD.setText(pwebsite);
        }

        if (pcontact.equals("") || pcontact == null || pcontact.equals("na")) {
            contactD.setText("Not Mentioned");
        } else {
            contactD.setText(pcontact);
        }

        if (plocation.equals("") || plocation == null || plocation.equals("na")) {
            locationD.setText("Not Mentioned");
        } else {
            locationD.setText(plocation);
        }

        name.setTypeface(FontsManager.getRegularTypeface(getContext()));
        website.setTypeface(FontsManager.getRegularTypeface(getContext()));
        contact.setTypeface(FontsManager.getRegularTypeface(getContext()));
        location.setTypeface(FontsManager.getRegularTypeface(getContext()));

        nameD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        websiteD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        contactD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        locationD.setTypeface(FontsManager.getRegularTypeface(getContext()));

        return view;
    }
}
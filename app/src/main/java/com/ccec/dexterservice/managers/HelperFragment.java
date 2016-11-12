package com.ccec.dexterservice.managers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterservice.HomeFragment;
import com.ccec.dexterservice.HomePage;
import com.ccec.dexterservice.ProfileFragment;
import com.ccec.dexterservice.R;

public class HelperFragment extends Fragment {
    private TextView content, header;
    private ImageView img;
    private Button btn;
    private String helper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_helper, container, false);

        header = (TextView) view.findViewById(R.id.errorHeader);
        content = (TextView) view.findViewById(R.id.errorContent);
        btn = (Button) view.findViewById(R.id.tryAgainButton);
        img = (ImageView) view.findViewById(R.id.errorImage);

        header.setTypeface(FontsManager.getBoldTypeface(getContext()));
        content.setTypeface(FontsManager.getRegularTypeface(getContext()));
        btn.setTypeface(FontsManager.getBoldTypeface(getContext()));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryAgain();
            }
        });

        helper = getArguments().getString("helper");

        if (helper.equals("no_requests")) {
            btn.setVisibility(View.GONE);
            header.setText("No requests found");
            content.setText("");
            img.setImageResource(R.drawable.icon_bin_empty);
        } else if (helper.equals("not_verified")) {
            btn.setText("Visit Profile");
            header.setText("Profile not updated");
            content.setText("Please update your profile to start receiving service requests.");
            img.setImageResource(R.drawable.icon_not_verified);
        }

        return view;
    }

    private void tryAgain() {
        if (helper.equals("not_verified")) {
            ProfileFragment fragment = new ProfileFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

            ((HomePage) getActivity()).updatedActionBar(1, "Profile");
        } else if (isNetwork()) {
            HomeFragment fragment = new HomeFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else {
            Toast.makeText(getContext(), "Connection not available", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
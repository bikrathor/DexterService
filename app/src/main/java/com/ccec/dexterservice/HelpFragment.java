package com.ccec.dexterservice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ccec.dexterservice.managers.UserSessionManager;

import java.util.HashMap;

public class HelpFragment extends Fragment {
    private String id;
    private UserSessionManager session;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        listView = (ListView) view.findViewById(R.id.list);

        String[] values = new String[]{
                "Mail us",
                "Legal"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int itemPosition = position;
                if (itemPosition == 0) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto","dexterhelp@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Dexter");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } else if (itemPosition == 1) {
                    Intent intent = new Intent(getActivity(), Legal.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }
}
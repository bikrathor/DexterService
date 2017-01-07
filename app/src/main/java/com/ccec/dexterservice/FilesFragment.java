package com.ccec.dexterservice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccec.dexterservice.managers.FilesRecyclerViewAdapter;
import com.ccec.dexterservice.managers.UserSessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilesFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    UserSessionManager session;
    private List<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);

        session = new UserSessionManager(getContext());
        HashMap<String, String> user = session.getUserDetails();
//        uid = user.get(UserSessionManager.TAG_id);
//        profilePic = user.get(UserSessionManager.TAG_profilepic);
//        email = user.get(UserSessionManager.TAG_email);

        recyclerView = (RecyclerView) view.findViewById(R.id.fileslist);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        list=new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("obdFiles");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren())
                    list.add((String) data.getValue());

                FilesRecyclerViewAdapter recyclerViewAdapter = new FilesRecyclerViewAdapter(getActivity(), list);
                recyclerView.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}
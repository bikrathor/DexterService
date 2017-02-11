package com.ccec.dexterservice;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterservice.entities.RequestRow;
import com.ccec.dexterservice.entities.Requests;
import com.ccec.dexterservice.managers.AppData;
import com.ccec.dexterservice.managers.CompletedRecyclerViewAdapter;
import com.ccec.dexterservice.managers.JSONArrayParser;
import com.ccec.dexterservice.managers.UserSessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CompletedFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ProgressDialog pDialog;
    private UserSessionManager session;
    private String id, loc;
    private RelativeLayout errorSec;
    private TextView erTxt;
    private List<Requests> items;
    private ImageView erImgTxt;
    private boolean swiper = false;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);

        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        loc = user.get(UserSessionManager.TAG_location);
        id = user.get(UserSessionManager.TAG_id);

        recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        errorSec = (RelativeLayout) view.findViewById(R.id.errorSec);
        erTxt = (TextView) view.findViewById(R.id.errorHeader);
        erImgTxt = (ImageView) view.findViewById(R.id.errorImage);

        items = new ArrayList<>();

        fetchData();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshClouds);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swiper = true;
                        items = new ArrayList<>();
                        fetchData();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mySwipeRefreshLayout.setRefreshing(false);
                            }
                        }, 2000);
                    }
                }
        );
    }

    private void fetchData() {
        if (isNetwork())
            new GetData().execute();
        else {
            erImgTxt.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_no_connection));
            errorSec.setVisibility(View.VISIBLE);
            erTxt.setText("Please connect to internet");
        }
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void stopLoading() {
        try {
            pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class GetData extends AsyncTask<String, String, String> {
        private static final String url = "http://188.166.245.67/html/phpscript/getCompletedListTwo.php";
        private static final String TAG_DATA = "data";
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (swiper == false) {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Updating...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        protected String doInBackground(String... args) {
            JSONArrayParser jsonObjectParser = new JSONArrayParser();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));

            JSONArray response = jsonObjectParser.makeHttpRequest(url, "GET", params);

            try {
                for (int i = 0; i < response.length(); i++) {
                    Requests item = new Requests();
                    JSONObject noti = response.getJSONObject(i);

                    item.setEstPrice(noti.getString("estPrice").toString());
                    item.setIssuedBy(noti.getString("issuedBy").toString());
                    item.setIssuedTo(noti.getString("issuedTo").toString());
                    item.setItem(noti.getString("item").toString());
                    item.setKey(noti.getString("keyCar").toString());
                    item.setOpenTime(noti.getString("openTime").toString());
                    item.setQueries(noti.getString("queries").toString());
                    item.setScheduleTime(noti.getString("scheduleTime").toString());
                    item.setStatus(noti.getString("status").toString());

                    items.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            try {
                if (swiper == false) {
                    pDialog.dismiss();
                }
                if (items.size() > 0) {
                    Collections.sort(items, new Comparator<Requests>() {
                        public int compare(Requests emp1, Requests emp2) {
                            return emp2.getKey().compareToIgnoreCase(emp1.getKey());
                        }
                    });

                    CompletedRecyclerViewAdapter recyclerViewAdapter = new CompletedRecyclerViewAdapter(getActivity(), items, CompletedFragment.this);
                    recyclerView.setAdapter(recyclerViewAdapter);
                } else {
                    errorSec.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
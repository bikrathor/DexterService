package com.ccec.dexterservice.managers;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;


public class FirebaseInstanceID extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private DatabaseReference databaseReference;
    private UserSessionManager session;
    private String uid;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        uid = user.get(UserSessionManager.TAG_id);

        if (uid != null)
            sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        databaseReference = FirebaseDatabase.getInstance().getReference("/users/ServiceCenter/" + uid);
        databaseReference.child("fcm").setValue(token);
    }
}

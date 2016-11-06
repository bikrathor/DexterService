package com.ccec.dexterservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import org.w3c.dom.Comment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("addddddddd", "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list

                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("chhhhhhhhhh", "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("reeeeeeee", "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("moooooo", "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("caaaaaaa", "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        myRef.addChildEventListener(childEventListener);

        String key = myRef.child("Service Centres").push().getKey();
        Vehicle v = new Vehicle("ford","figo","3344","1122","11/11","1111","22,11","22/11","11/10","12/11");
        Map<String, Object> postValues = v.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Service Centres/" + "Maruti" + "/" + "-KVseMVJrF_4Pt3Q3gix", postValues);

        myRef.updateChildren(childUpdates);
    }

    public class Vehicle {
        private String make, model, registrationnumber, chessisnumber, manufacturedin, kilometer,
                polluctionchkdate, nextpolluctionchkdate, insurancepurchasedate, insuranceduedate, addvehicle;

        public Vehicle() {
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("model", model);
            result.put("make", make);
            result.put("registrationnumber", registrationnumber);
            result.put("chessisnumber", chessisnumber);
            result.put("manufacturedin", manufacturedin);
            result.put("kilometer", kilometer);
            result.put("polluctionchkdate", polluctionchkdate);
            result.put("nextpolluctionchkdate", nextpolluctionchkdate);
            result.put("insurancepurchasedate", insurancepurchasedate);
            result.put("insuranceduedate", insuranceduedate);
            return result;
        }

        public Vehicle(String model, String make, String registrationnumber, String chessisnumber, String manufacturedin, String kilometer, String polluctionchkdate, String nextpolluctionchkdate, String insurancepurchasedate, String insuranceduedate) {
            this.model = model;
            this.make = make;
            this.registrationnumber = registrationnumber;
            this.chessisnumber = chessisnumber;
            this.manufacturedin = manufacturedin;
            this.kilometer = kilometer;
            this.polluctionchkdate = polluctionchkdate;
            this.nextpolluctionchkdate = nextpolluctionchkdate;
            this.insurancepurchasedate = insurancepurchasedate;
            this.insuranceduedate = insuranceduedate;
        }

        public String getMake() {
            return make;
        }

        public void setMake(String make) {
            this.make = make;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getRegistrationnumber() {
            return registrationnumber;
        }

        public void setRegistrationnumber(String registrationnumber) {
            this.registrationnumber = registrationnumber;
        }

        public String getChessisnumber() {
            return chessisnumber;
        }

        public void setChessisnumber(String chessisnumber) {
            this.chessisnumber = chessisnumber;
        }

        public String getManufacturedin() {
            return manufacturedin;
        }

        public void setManufacturedin(String manufacturedin) {
            this.manufacturedin = manufacturedin;
        }

        public String getKilometer() {
            return kilometer;
        }

        public void setKilometer(String kilometer) {
            this.kilometer = kilometer;
        }

        public String getPolluctionchkdate() {
            return polluctionchkdate;
        }

        public void setPolluctionchkdate(String polluctionchkdate) {
            this.polluctionchkdate = polluctionchkdate;
        }

        public String getNextpolluctionchkdate() {
            return nextpolluctionchkdate;
        }

        public void setNextpolluctionchkdate(String nextpolluctionchkdate) {
            this.nextpolluctionchkdate = nextpolluctionchkdate;
        }

        public String getInsurancepurchasedate() {
            return insurancepurchasedate;
        }

        public void setInsurancepurchasedate(String insurancepurchasedate) {
            this.insurancepurchasedate = insurancepurchasedate;
        }

        public String getInsuranceduedate() {
            return insuranceduedate;
        }

        public void setInsuranceduedate(String insuranceduedate) {
            this.insuranceduedate = insuranceduedate;
        }

        public String getAddvehicle() {
            return addvehicle;
        }

        public void setAddvehicle(String addvehicle) {
            this.addvehicle = addvehicle;
        }
    }
}
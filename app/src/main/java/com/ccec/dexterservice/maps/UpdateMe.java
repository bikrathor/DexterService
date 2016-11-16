package com.ccec.dexterservice.maps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterservice.R;
import com.ccec.dexterservice.managers.AppData;
import com.ccec.dexterservice.managers.FontsManager;
import com.ccec.dexterservice.managers.UserSessionManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UpdateMe extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    UserSessionManager session;
    private LocationManager mLocationManager;
    private Marker myMarker;
    private Location location;
    private ImageView img;
    private TextView searchLoc;
    private EditText enterLoc;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String result = null, id;
    private FloatingActionButton fab;
    private String source = "normal";
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (!isNetwork()) {
            Toast.makeText(UpdateMe.this, "Please connect to internet", Toast.LENGTH_LONG).show();
            UpdateMe.this.finish();
        }

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);

        img = (ImageView) findViewById(R.id.imageView);
        enterLoc = (EditText) findViewById(R.id.input_location);
        searchLoc = (TextView) findViewById(R.id.textSearch);
        enterLoc.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        searchLoc.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));

        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("PLace", "Placeeeeeeeeeee: " + place.getName());
                source = "auto";
            }

            @Override
            public void onError(Status status) {
            }
        });

        enterLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(UpdateMe.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.selectedLoc = enterLoc.getText().toString();
                AppData.selectedCordLoc = location;
                UpdateMe.this.finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);

                LatLng latLong;
                latLong = place.getLatLng();
                Location loc = new Location("Sample");
                loc.setLatitude(latLong.latitude);
                loc.setLongitude(latLong.longitude);

                updateMyLocation(mMap, loc);
                getCurrentName();

//                enterLoc.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("status", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateMe.this);
            dialog.setMessage("Location not enabled");
            dialog.setPositiveButton("Enable location", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    UpdateMe.this.finish();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } else {
            Toast.makeText(UpdateMe.this, "Getting location..", Toast.LENGTH_SHORT).show();
        }

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(UpdateMe.this, "Make sure location is on.", Toast.LENGTH_LONG).show();

            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });

        if (gps_enabled != false) {
            location = mLocationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location!=null) {
                getCurrentName();
                updateMyLocation(googleMap, location);
            }
        }

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                source = "normal";
                if (ActivityCompat.checkSelfPermission(UpdateMe.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UpdateMe.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                location = mLocationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                updateMyLocation(googleMap, location);

                if (result != null)
                    enterLoc.setText(result);
                else
                    enterLoc.setText("My location");
            }
        });
    }

    private void getCurrentName() {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
        }
        if (addressList != null && addressList.size() > 0) {
            Address address = addressList.get(0);
            StringBuilder sb = new StringBuilder();
            if (source.equals("auto"))
                sb.append(place.getName()).append("\n");
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                sb.append(address.getAddressLine(i)).append("\n");
            }
            sb.append(address.getLocality()).append("");
            result = sb.toString();
        }

        if (result != null)
            enterLoc.setText(result);
        else
            enterLoc.setText("My location");
    }

    private void updateMyLocation(final GoogleMap googleMap, Location location) {
        this.location = location;

        LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());

        if (myMarker == null)
            myMarker = mMap.addMarker(new MarkerOptions().position(myLoc).
                    title("My location")
                    .icon(getMarkerIcon("#8b3e58")));
        else
            myMarker.setPosition(myLoc);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 16));
    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }
}
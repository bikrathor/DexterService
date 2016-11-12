package com.ccec.dexterservice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterservice.managers.CustomTypefaceSpan;
import com.ccec.dexterservice.managers.FontsManager;
import com.ccec.dexterservice.managers.HelperFragment;
import com.ccec.dexterservice.managers.UserSessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    UserSessionManager session;
    private ProgressDialog pDialog;
    AlertDialog.Builder builder;
    private boolean doubleBackToExitPressedOnce = false;
    private NavigationView navigationView;
    private CircularImageView view1;
    private String id, email, location;
    private TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorGreen));
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        changeDrawerFont();

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        id = user.get(UserSessionManager.TAG_id);
        email = user.get(UserSessionManager.TAG_email);
        location = user.get(UserSessionManager.TAG_location);

        View hView = navigationView.inflateHeaderView(R.layout.nav_header_home_page);
        view1 = (CircularImageView) hView.findViewById(R.id.circularImage2);
        getPic();

        header = (TextView) hView.findViewById(R.id.headerText);
        header.setText(email);

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = new ProfileFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, profileFragment).commit();
                getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Profile"));

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                navigationView.getMenu().getItem(1).setChecked(true);
//                CloudletData.setSelectedItem(1);
            }
        });

        if (!location.equals("na")) {
            if (isNetwork()) {
                HomeFragment homeFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, homeFragment).commit();
                getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Home"));
            } else {
                showHelperNoConnection();
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("helper", "not_verified");

            HelperFragment helpFragment = new HelperFragment();
            helpFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, helpFragment).commit();
            getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Home"));
        }
    }

    public void showHelperNoConnection() {
        Bundle bundle = new Bundle();
        bundle.putString("helper", "no_conn");

        HelperFragment helpFragment = new HelperFragment();
        helpFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, helpFragment).commit();
        getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Home"));
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void getPic() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");
        storageRef.child("profilePics/" + id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri).noPlaceholder().into(view1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                storageRef.child("profilePics/" + id + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getApplicationContext()).load(uri).noPlaceholder().into(view1);
                    }
                });
            }
        });
    }

    public void updatedActionBar(int pos, String tit) {
        navigationView.getMenu().getItem(pos).setChecked(true);
        getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), tit));
    }

    private void changeDrawerFont() {
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(mi);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Click back again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            //check for verification
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeFragment).commit();
            getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Home"));
        } else if (id == R.id.profile) {
            //check for verification
            ProfileFragment profileFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, profileFragment).commit();
            getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Profile"));
            // CloudletData.setSelectedItem(5);
        } else if (id == R.id.bla) {

        } else if (id == R.id.blabla) {

        } else if (id == R.id.logout) {
            builder = new AlertDialog.Builder(HomePage.this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");
            builder.setCancelable(false);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pDialog = new ProgressDialog(HomePage.this);
                    pDialog.setMessage("Logging Out...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();

                    session.logoutUser();
                    pDialog.dismiss();
                    dialog.dismiss();

                    HomePage.this.finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//                    navigationView.getMenu().getItem(CloudletData.getSelectedItem()).setChecked(true);
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
            textView.setTextSize(14);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
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
import android.support.design.widget.TabLayout;
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

import com.ccec.dexterservice.managers.AppData;
import com.ccec.dexterservice.managers.CustomTypefaceSpan;
import com.ccec.dexterservice.managers.FontsManager;
import com.ccec.dexterservice.managers.HelperFragment;
import com.ccec.dexterservice.managers.UserSessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
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
    private TabLayout tabLayout;

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

        //sample noti push
//        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("/notifs");
//
//        Map notification = new HashMap<>();
//        notification.put("username", "fEMp5APLeVc:APA91bGURclfd_VwOE3LZRlaN5n7eIyhD23Dhwh6v0BcpxfXSItBpMadiSO-k3m80pNOT79m32VgBJRsUbhhJqycJDiKiVusEi7akjDISD-HS0mgXzPPWW-AUKML6kiDSc92WuLRFAod");
//        notification.put("message", "helloo");
//
//        databaseReference2.push().setValue(notification);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        changeDrawerFont();

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        id = user.get(UserSessionManager.TAG_id);
        email = user.get(UserSessionManager.TAG_email);
        location = user.get(UserSessionManager.TAG_location);

        FirebaseMessaging.getInstance().subscribeToTopic("news");

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
                tabLayout.setVisibility(View.GONE);
                AppData.setSelectedItem(1);
            }
        });

        if (!location.equals("na")) {
            if (AppData.selectedTab != 0) {
                CompletedFragment completedFragment = new CompletedFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, completedFragment).commit();
                getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Completed Services"));
                tabLayout.setVisibility(View.GONE);
                AppData.setSelectedItem(2);
                AppData.selectedTab = 0;
                navigationView.getMenu().getItem(2).setChecked(true);
            } else {
                HomeFragment homeFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, homeFragment).commit();
                getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Services"));
                tabLayout.setVisibility(View.VISIBLE);
                AppData.setSelectedItem(0);
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("helper", "not_verified");

            HelperFragment helpFragment = new HelperFragment();
            helpFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, helpFragment).commit();
            getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Home"));
            tabLayout.setVisibility(View.GONE);
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

        try {
            storageRef.child("profilePics/" + id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getApplicationContext()).load(uri).noPlaceholder().into(view1);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            AppData.setOne = true;
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeFragment).commit();
            getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Services"));
            tabLayout.setVisibility(View.VISIBLE);
            AppData.setSelectedItem(0);
        } else if (id == R.id.profile) {
            //check for verification
            ProfileFragment profileFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, profileFragment).commit();
            getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Profile"));
            AppData.setSelectedItem(1);
            tabLayout.setVisibility(View.GONE);
        } else if (id == R.id.completedServices) {
            CompletedFragment completedFragment = new CompletedFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, completedFragment).commit();
            getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Completed Services"));
            AppData.setSelectedItem(2);
            tabLayout.setVisibility(View.GONE);
        } else if (id == R.id.about) {
            HelpFragment ordersFragment = new HelpFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ordersFragment).commit();
            AppData.setSelectedItem(3);
            getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Help"));
            tabLayout.setVisibility(View.GONE);
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
                    navigationView.getMenu().getItem(AppData.getSelectedItem()).setChecked(true);
                    if (AppData.getSelectedItem() != 0)
                        tabLayout.setVisibility(View.GONE);
                    else
                        tabLayout.setVisibility(View.VISIBLE);
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
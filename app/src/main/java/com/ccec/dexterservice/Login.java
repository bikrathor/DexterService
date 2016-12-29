package com.ccec.dexterservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterservice.managers.FontsManager;
import com.ccec.dexterservice.managers.UserSessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private Button loginBtn;
    private TextView forgot, signUp;
    private UserSessionManager session;
    private EditText LoginEmail, LoginPassword;
    private String Login_Email, Login_Password;
    private ProgressDialog pDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setupUI(findViewById(R.id.parent));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorGreen));
        }

        mAuth = FirebaseAuth.getInstance();

        session = new UserSessionManager(getApplicationContext());

        LoginEmail = (EditText) findViewById(R.id.input_email_login);
        LoginPassword = (EditText) findViewById(R.id.input_password_login);
        LoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        loginBtn = (Button) findViewById(R.id.loginButton);
        forgot = (TextView) findViewById(R.id.forgotPassTxt);
        signUp = (TextView) findViewById(R.id.signUpTxt);

        LoginEmail.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        LoginPassword.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        loginBtn.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));

        forgot.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        signUp.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Login.this, ForgotPass.class);
                startActivity(in);
                Login.this.finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Login.this, SignUp.class);
                startActivity(in);
                Login.this.finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login_Email = LoginEmail.getText().toString();
                Login_Password = LoginPassword.getText().toString();

                if (isNetwork() && validate()) {
                    processSignIn();
                } else if (!isNetwork()) {
                    Toast.makeText(Login.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        if (Login_Email.isEmpty() || (!android.util.Patterns.EMAIL_ADDRESS.matcher(Login_Email).matches())) {
            LoginEmail.setError("enter a valid email");
            valid = false;
        } else {
            LoginEmail.setError(null);
        }

        if (Login_Password.isEmpty() || Login_Password.length() < 6 || Login_Password.length() > 12) {
            LoginPassword.setError("between 6 and 12 alphanumeric characters");
            valid = false;
        } else {
            LoginPassword.setError(null);
        }

        return valid;
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Login.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void processSignIn() {
        pDialog = new ProgressDialog(Login.this);
        pDialog.setMessage("Signing in...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        mAuth.signInWithEmailAndPassword(Login_Email, Login_Password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(Login.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        } else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            final String uid = user.getUid();
                            session.createUserLoginSession(uid, Login_Email, Login_Password);

                            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/users/ServiceCenter/" + uid);
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                        session.createUserLoginSession((String) itemMap.get("name"), "",
                                                (String) itemMap.get("website"), (String) itemMap.get("contact"),
                                                (String) itemMap.get("location"), (String) itemMap.get("makes"));

                                        Toast.makeText(getApplicationContext(), "Welcome",
                                                Toast.LENGTH_LONG).show();

                                        Intent in = new Intent(Login.this, HomePage.class);
                                        startActivity(in);

                                        pDialog.dismiss();
                                        Login.this.finish();
                                        String token = FirebaseInstanceId.getInstance().getToken();
                                        databaseReference2 = FirebaseDatabase.getInstance().getReference("/users/ServiceCenter/" + uid);
                                        databaseReference2.child("fcm").setValue(token);
                                    } else {
                                        pDialog.dismiss();
                                        Toast.makeText(Login.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                                        session.clearData();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    pDialog.dismiss();
                                }
                            });

                        }
                    }
                });
    }
}
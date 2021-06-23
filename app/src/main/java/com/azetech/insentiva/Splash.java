package com.azetech.insentiva;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Splash extends Activity {

    private ImageView logo_left, logo_right;
    private ImageView text;
    Animation startLeftAnim, endLeftAnim, fade_in;
    Animation startRightAnim, endRightAnim;
    private FirebaseAuth mAuth;
    private RelativeLayout splashLayout, serverError;
    private DatabaseReference mAdminRef, mPayRollRef, mEmpRef, mUpdateRef;

    Convert convert = new Convert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOption = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOption);
        setContentView(R.layout.activity_splash);

        validate();

        checkStatus();

        setAnimation();


    }

    private void checkStatus() {
        mUpdateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String status = convert.ObjectToString(dataSnapshot.child("app_status").getValue());
                    if (status.equals("false")){
                        init();
                    }
                    else {
                        serverError.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    private void init() {

        FirebaseApp.initializeApp(this);

        try{
            final FirebaseUser user = mAuth.getCurrentUser();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (user != null) {
                        String uid = mAuth.getCurrentUser().getUid();
                        checkUser(uid);
                        if (isOnline()){
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Snackbar snackbar = Snackbar
                                            .make(splashLayout, "Network Error, Please try again", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            },5000, 8000);
                        }
                    }
                    else {
                        startActivity(new Intent(Splash.this, MainActivity.class));
                        finish();
                    }
                }
            },1000);
        }
        catch (Exception e){
            Toast.makeText(Splash.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void checkUser(final String uid) {
        final List userList = new ArrayList();
        mAdminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String uid = convert.ObjectToString(snapshot.getKey());
                    userList.add(uid);
                }

                if (userList.contains(uid)){
                    if (!checkAttendance()) {
                        Intent intent = new Intent(Splash.this, AdminHome.class);
                        intent.putExtra("mobile", "mobile");
                        intent.putExtra("who", "admin");
                        intent.putExtra("from", "home");
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent = new Intent(Splash.this, AdminAttendance.class);
                        intent.putExtra("mobile", "mobile");
                        intent.putExtra("who", "admin");
                        intent.putExtra("from", "home");
                        startActivity(intent);
                        finish();
                    }
                }
                else {
                    getEmployeeDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getEmployeeDetails() {
        String uid = mAuth.getCurrentUser().getUid();
        mEmpRef.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String mobile = convert.ObjectToString(dataSnapshot.child("mobile").getValue());
                    Intent intent = new Intent(Splash.this, EmployeeHome.class);
                    intent.putExtra("mobile", mobile);
                    intent.putExtra("who", "employee");
                    intent.putExtra("from", "home");
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(Splash.this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    Intent intent = new Intent(Splash.this, EmployeeHome.class);
                    intent.putExtra("mobile", "");
                    intent.putExtra("who", "employee");
                    intent.putExtra("from", "home");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkAttendance() {
        SharedPreferences prefs = getSharedPreferences("attendance", MODE_PRIVATE);
        String date = prefs.getString("date", "");
        if (date.equals(convert.getDate())){
            return false;
        }
        else {
            return true;
        }
    }

    private void setAnimation() {
        logo_left.startAnimation(startLeftAnim);
        logo_right.startAnimation(startRightAnim);
        text.startAnimation(fade_in);

        startLeftAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logo_left.startAnimation(endLeftAnim);
                logo_right.startAnimation(endRightAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        endRightAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logo_left.startAnimation(startLeftAnim);
                logo_right.startAnimation(startRightAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void validate() {
        logo_left = (ImageView) findViewById(R.id.logo_left);
        logo_right = (ImageView) findViewById(R.id.logo_right);
        text = (ImageView) findViewById(R.id.splash_text1);

        splashLayout = (RelativeLayout) findViewById(R.id.splash_screen_layout);
        serverError = (RelativeLayout) findViewById(R.id.splash_error_text_layout);
        serverError.setVisibility(View.GONE);

        startLeftAnim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.start_left);
        startRightAnim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.start_right);
        endLeftAnim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.end_left);
        endRightAnim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.end_right);
        fade_in = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);

        mAuth = FirebaseAuth.getInstance();
        mAdminRef = FirebaseDatabase.getInstance().getReference().child("Admin");
        mAdminRef.keepSynced(true);
        mPayRollRef = FirebaseDatabase.getInstance().getReference().child("PayRoll");
        mPayRollRef.keepSynced(true);
        mEmpRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mEmpRef.keepSynced(true);
        mUpdateRef = FirebaseDatabase.getInstance().getReference().child("Updates");
        mUpdateRef.keepSynced(true);
    }
}

package com.azetech.insentiva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Sign_up extends AppCompatActivity {

    private EditText email_editText, name_editText, mobile_editText, password_editText, confirm_pass_editText, referral_editText;
    private Button signup_button;
    private DatabaseReference mUserRef, mPayRollRef, mUpdateRef;
    private FirebaseAuth mAuth;
    private ProgressBar progress;

    Convert convert = new Convert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        validate();

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                initialize();
            }
        });
    }

    private void initialize() {
        String username = convert.ObjectToString(email_editText.getText()).trim();
        String password = convert.ObjectToString(password_editText.getText()).trim();
        String name = convert.ObjectToString(name_editText.getText()).trim();
        String mobile= convert.ObjectToString(mobile_editText.getText()).trim();
        String confirm_password = convert.ObjectToString(confirm_pass_editText.getText()).trim();
        String referral = convert.ObjectToString(referral_editText.getText()).trim();

        if (username.equals("")){
            progress.setVisibility(View.GONE);
            email_editText.setError("Please Fill This Field");
            email_editText.setFocusable(true);
        }
        else if (password.equals("")){
            progress.setVisibility(View.GONE);
            password_editText.setError("Please Fill This Field");
            password_editText.setFocusable(true);
        }
        else if (name.equals("")){
            progress.setVisibility(View.GONE);
            name_editText.setError("Please Fill This Field");
            name_editText.setFocusable(true);
        }
        else if (mobile.equals("")){
            progress.setVisibility(View.GONE);
            mobile_editText.setError("Please Fill This Field");
            mobile_editText.setFocusable(true);
        }
        else if (confirm_password.equals("")){
            progress.setVisibility(View.GONE);
            confirm_pass_editText.setError("Please Fill This Field");
            confirm_pass_editText.setFocusable(true);
        }
        else if (!password.equals(confirm_password)){
            progress.setVisibility(View.GONE);
            confirm_pass_editText.setError("Password does not match!!");
        }
        else if (referral.equals("")){
            progress.setVisibility(View.GONE);
            referral_editText.setError("Please Enter Referral Code");
        }
        else {
            checkReferral(username, password, name, mobile, referral);
        }
    }

    private void checkReferral(final String username, final String password, final String name, final String mobile, final String referral) {
        mUpdateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String referralCode = convert.ObjectToString(dataSnapshot.child("referral").getValue());
                    if (referral.equals(referralCode)){
                        signup(username, password, name, mobile);
                    }
                    else {
                        progress.setVisibility(View.GONE);
                        referral_editText.setError("Invalid Referral Code");
                        referral_editText.setText("");
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
        startActivity(new Intent(Sign_up.this, MainActivity.class));
        finish();
    }

    private void signup(final String username, String password, final String name, final String mobile) {

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            addUserDetails(username, name, mobile);
                        }
                        else {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                password_editText.setError("Weak Password");
                                progress.setVisibility(View.GONE);
                                password_editText.requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                email_editText.setError(getString(R.string.Email_error));
                                progress.setVisibility(View.GONE);
                                email_editText.requestFocus();
                            } catch(FirebaseAuthUserCollisionException e) {
                                email_editText.setError(getString(R.string.user_exist));
                                progress.setVisibility(View.GONE);
                                email_editText.requestFocus();
                            } catch (FirebaseNetworkException e){
                                Toast.makeText(Sign_up.this, "Poor Internet connection", Toast.LENGTH_SHORT).show();
                                progress.setVisibility(View.GONE);
                            } catch(Exception e) {
                                Toast.makeText(Sign_up.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                progress.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    private void addUserDetails(String username, final String name, final String mobile) {
        try {
            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("name", name);
            userMap.put("salary", "0");
            userMap.put("mobile", mobile);

            String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mUserRef.child(currentUser)
                    .setValue(userMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                addToAttendance(name, mobile);
                            }
                        }
                    });
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addToAttendance(final String name, final String mobile) {
        try {
            HashMap map = new HashMap();
            map.put("name", name);
            map.put("isPresent", "yes");
            map.put("attendance", "30");

            mPayRollRef.child(mobile)
                    .setValue(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(Sign_up.this, EmployeeHome.class);
                                intent.putExtra("mobile", mobile);
                                intent.putExtra("who", "employee");
                                intent.putExtra("from", "home");
                                startActivity(intent);
                                finish();
                                progress.setVisibility(View.GONE);
                                Toast.makeText(Sign_up.this, "Congratulations!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
        }
    }

    private void validate() {
        email_editText = (EditText) findViewById(R.id.sign_up_email);
        password_editText = (EditText) findViewById(R.id.sign_up_password);
        name_editText = (EditText) findViewById(R.id.sign_up_name);
        mobile_editText = (EditText) findViewById(R.id.sign_up_mobile);
        confirm_pass_editText = (EditText) findViewById(R.id.sign_up_confirm_password);
        referral_editText = (EditText) findViewById(R.id.sign_up_referral);

        signup_button= (Button) findViewById(R.id.sign_up_button);

        progress = (ProgressBar) findViewById(R.id.sign_up_progress);
        progress.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserRef.keepSynced(true);
        mPayRollRef = FirebaseDatabase.getInstance().getReference().child("PayRoll");
        mPayRollRef.keepSynced(true);
        mUpdateRef = FirebaseDatabase.getInstance().getReference().child("Updates");
        mUpdateRef.keepSynced(true);
    }
}

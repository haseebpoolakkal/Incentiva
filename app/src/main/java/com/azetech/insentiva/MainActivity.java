package com.azetech.insentiva;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText email_editText, password_editText;
    private Button login_button;
    private TextView register_button, forgot_button;
    private FirebaseAuth mAuth;
    private DatabaseReference mAdminRef, mEmpRef;
    private ProgressBar progressBar;

    Convert convert = new Convert();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        validate();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String username = convert.ObjectToString(email_editText.getText()).trim();
                String password = convert.ObjectToString(password_editText.getText()).trim();

                if(username.isEmpty()){
                    email_editText.setError("Enter Email Address");
                    progressBar.setVisibility(View.GONE);
                }
                else if (password.isEmpty()){
                    password_editText.setError("Enter Password");
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    if (!username.endsWith("@gmail.com")){
                        username= username+"@gmail.com";
                        login(username, password);
                    }
                    else {
                        login(username, password);
                    }
                }
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Sign_up.class));
                finish();
            }
        });

        forgot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Forgot.class));
                finish();
            }
        });
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
                        Intent intent = new Intent(MainActivity.this, AdminHome.class);
                        intent.putExtra("mobile", "mobile");
                        intent.putExtra("who", "admin");
                        intent.putExtra("from", "home");
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent = new Intent(MainActivity.this, AdminAttendance.class);
                        intent.putExtra("mobile", "mobile");
                        intent.putExtra("who", "admin");
                        intent.putExtra("from", "home");
                        startActivity(intent);
                        finish();
                    }
                }
                else {
                    getEmplyeeDetails(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getEmplyeeDetails(String uid) {
        mEmpRef.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String mobile = convert.ObjectToString(dataSnapshot.child("mobile").getValue());
                            Intent intent = new Intent(MainActivity.this, EmployeeHome.class);
                            intent.putExtra("mobile", mobile);
                            intent.putExtra("who", "employee");
                            intent.putExtra("from", "home");
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
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

    private void login(final String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    checkUser(mAuth.getCurrentUser().getUid());
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(MainActivity.this, "Please Check username and password!!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    email_editText.setText("");
                    password_editText.setText("");
                }
            }
        });
    }

    private void validate() {
        email_editText = (EditText) findViewById(R.id.login_email);
        password_editText = (EditText) findViewById(R.id.login_password);
        login_button = (Button) findViewById(R.id.login_button);
        register_button = (TextView) findViewById(R.id.login_register);
        forgot_button= (TextView) findViewById(R.id.login_forgot);

        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mAdminRef = FirebaseDatabase.getInstance().getReference().child("Admin");
        mAdminRef.keepSynced(true);
        mEmpRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mEmpRef.keepSynced(true);
    }
}

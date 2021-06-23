package com.azetech.insentiva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminChangeSalary extends AppCompatActivity {

    private TextView oldSalary;
    private Spinner employee_spinner;
    private EditText newSalary;
    private Button change_button;
    private ProgressBar progress;
    private ImageView back_button;
    private DatabaseReference mUsersRef;

    String mobile, who, from;

    Convert convert = new Convert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_change_salary);
        validate();

        mobile = getIntent().getStringExtra("mobile");
        who = getIntent().getStringExtra("who");
        from = getIntent().getStringExtra("from");

        populateSpinner();

        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                String new_salary = convert.ObjectToString(newSalary.getText());
                if (new_salary.equals("")){
                    newSalary.setError("Please Enter a Salary");
                }
                else {
                    changeSalary(new_salary);
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminChangeSalary.this, AdminHome.class);
                intent.putExtra("mobile", "");
                intent.putExtra("from", from);
                intent.putExtra("who", who);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminChangeSalary.this, AdminHome.class);
        intent.putExtra("mobile", "");
        intent.putExtra("from", from);
        intent.putExtra("who", who);
        startActivity(intent);
        finish();
    }

    private void changeSalary(final String new_salary) {
        final String name = convert.ObjectToString(employee_spinner.getSelectedItem());
        Query query = mUsersRef.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    snapshot.child("salary").getRef().setValue(new_salary);
                    Toast.makeText(AdminChangeSalary.this, "Salary Changed Successfully", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    newSalary.setText("");
                    showSalary(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateSpinner() {
        final List employeeList = new ArrayList();
        mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String name = convert.ObjectToString(snapshot.child("name").getValue());
                    if (!employeeList.contains(name)){
                        employeeList.add(name);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, employeeList);
                employee_spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        employee_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = convert.ObjectToString(parent.getSelectedItem());
                showSalary(name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showSalary(String name) {
        Query query = mUsersRef.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String salary = convert.ObjectToString(snapshot.child("salary").getValue());
                    oldSalary.setText("₹"+salary);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validate() {
        oldSalary = (TextView) findViewById(R.id.salary_old_text);

        employee_spinner = (Spinner) findViewById(R.id.salary_spinner);

        newSalary  = (EditText) findViewById(R.id.salary_new);

        change_button = (Button) findViewById(R.id.salary_change_button);

        progress = (ProgressBar) findViewById(R.id.salary_progress);
        progress.setVisibility(View.GONE);

        back_button = (ImageView) findViewById(R.id.salary_back_button);

        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersRef.keepSynced(true);
    }
}

package com.azetech.insentiva;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminAddInsentive extends AppCompatActivity {
    private Spinner inct_name_spinner, employee_name_spinner;
    private DatabaseReference mInctTypeRef, mInctRef, mAdminInctRef, mUsersRef;
    private TextView nameText, toolbar_amount, unit_value;
    private EditText bill_value, bill_number;
    private ImageView back_button;
    private Button add_button;
    private ProgressBar add_progress;
    String mobile = "", name = "", who, from;
    private boolean isHome = true;

    Convert convert = new Convert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_insentive);

        validate();

        if (name.equals("")) {
            nameText.setText("");
            isHome = true;
            getMobile();
            employee_name_spinner.setVisibility(View.VISIBLE);
            populateEmployeeSpinner();
        } else {
            isHome = false;
            employee_name_spinner.setVisibility(View.GONE);
            nameText.setText(name);
            mobile = getIntent().getStringExtra("mobile");
        }

        populateSpinner();

        inct_name_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getType(convert.ObjectToString(inct_name_spinner.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bill_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("") && StringUtils.isNumeric(s)) {
                    calculateIncentive(convert.ObjectToString(s));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_progress.setVisibility(View.VISIBLE);
                final String bill_amount = convert.ObjectToString(bill_value.getText()).replaceAll("[^a-zA-Z0-9]", "").trim();
                final String incentive = convert.ObjectToString(toolbar_amount.getText()).replaceAll("[^a-zA-Z0-9]", "").trim();
                final String bill_no = convert.ObjectToString(bill_number.getText()).replaceAll("[^a-zA-Z0-9]", "").trim();
                final String type = convert.ObjectToString(inct_name_spinner.getSelectedItem());

                if (isHome) {
                    name = convert.ObjectToString(employee_name_spinner.getSelectedItem());
                }

                if (bill_amount.equals("")) {
                    bill_value.setError("Enter an Amount");
                    add_progress.setVisibility(View.GONE);
                } else if (bill_no.equals("")) {
                    bill_number.setError("Enter Bill Invoice Number");
                    add_progress.setVisibility(View.GONE);
                }
                else {
                    mAdminInctRef.child(convert.getYear())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List billList = new ArrayList();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String key = convert.ObjectToString(snapshot.getKey());
                                        billList.add(key);
                                    }
                                    if (!billList.contains(bill_no)) {
                                        addIncentive(bill_no, incentive, type);
                                    } else {
                                        bill_number.setError("Bill Number Already Exists. Please give valid Bill Number");
                                        add_progress.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equals("incentive")){
                    Intent intent = new Intent(AdminAddInsentive.this, AdminInsentives.class);
                    intent.putExtra("mobile", mobile);
                    intent.putExtra("from", from);
                    intent.putExtra("who", who);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(AdminAddInsentive.this, AdminHome.class);
                    intent.putExtra("mobile", mobile);
                    intent.putExtra("from", from);
                    intent.putExtra("who", who);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void addIncentive(final String bill_no,  final String incentive, final String type) {
        final HashMap inctMap = new HashMap();
        inctMap.put("name", name);
        inctMap.put("bill_number", bill_no);
        inctMap.put("incentive", incentive);
        inctMap.put("inct_type", type);
        inctMap.put("date", convert.getDate());
        inctMap.put("month", convert.getMonth());

        mInctRef.child(convert.getYear())
                .child(mobile)
                .child(bill_no)
                .setValue(inctMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mAdminInctRef.child(convert.getYear())
                                    .child(bill_no)
                                    .setValue(inctMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                add_progress.setVisibility(View.GONE);
                                                Toast.makeText(AdminAddInsentive.this, "Incentive Added Successfully", Toast.LENGTH_SHORT).show();
                                                bill_value.setText("");
                                                bill_number.setText("");
                                                toolbar_amount.setText("");
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void getMobile() {
        employee_name_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String empName = convert.ObjectToString(employee_name_spinner.getSelectedItem());
                nameText.setText(empName);
                Query query = mUsersRef.orderByChild("name").equalTo(empName);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String empMobile = convert.ObjectToString(snapshot.child("mobile").getValue());
                                mobile = empMobile;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void populateEmployeeSpinner() {
        Query query = mUsersRef.orderByChild("name");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> nameList = new ArrayList<String>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String titlename = dataSnapshot1.child("name").getValue(String.class);
                    nameList.add(titlename);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AdminAddInsentive.this, android.R.layout.simple_spinner_item, nameList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                employee_name_spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (from.equals("incentive")){
            Intent intent = new Intent(AdminAddInsentive.this, AdminInsentives.class);
            intent.putExtra("mobile", mobile);
            intent.putExtra("from", from);
            intent.putExtra("who", who);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(AdminAddInsentive.this, AdminHome.class);
            intent.putExtra("mobile", mobile);
            intent.putExtra("from", from);
            intent.putExtra("who", who);
            startActivity(intent);
            finish();
        }
    }

    private void getType(final String item) {
        mInctTypeRef.child(item)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String type_unit = convert.ObjectToString(dataSnapshot.child("inct_type").getValue());
                            setView(type_unit, item);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setView(String type_unit, String item) {
        if (type_unit.equals("Percentage")) {
            setPercentageView(item);
        } else if (type_unit.equals("Gram")) {
            setGramView(item);
        } else if (type_unit.equals("Quantity")) {
            setQuantityView(item);
        } else if (type_unit.equals("Custom")) {
            setCustomView();
        }
    }

    private void setCustomView() {
        unit_value.setVisibility(View.GONE);
        bill_value.setHint("Enter Incentive Amount");
    }

    private void setQuantityView(String item) {
        unit_value.setVisibility(View.VISIBLE);
        bill_value.setHint("Enter Number of Units Sold");
        getUnitValue(item, "Quantity");
    }

    private void setGramView(String item) {
        unit_value.setVisibility(View.VISIBLE);
        bill_value.setHint("Enter Total Gram Sold");
        getUnitValue(item, "Gram");
    }

    private void setPercentageView(String item) {
        unit_value.setVisibility(View.VISIBLE);
        bill_value.setHint("Enter Bill Amount");
        getUnitValue(item, "Percentage");
    }

    private void getUnitValue(String item, final String type_unit) {
        mInctTypeRef.child(item)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String unitValue = convert.ObjectToString(dataSnapshot.child("inct_value").getValue());
                            if (type_unit.equals("Percentage")) {
                                unit_value.setText(unitValue + "%");
                            } else if (type_unit.equals("Gram")) {
                                unit_value.setText(unitValue + " per/g");
                            } else if (type_unit.equals("Quantity")) {
                                unit_value.setText(unitValue + " per/unit");
                            } else if (type_unit.equals("Custom")) {
                                unit_value.setText("");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void calculateIncentive(final String s) {
        String item = convert.ObjectToString(inct_name_spinner.getSelectedItem());
        mInctTypeRef.child(item)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String type = convert.ObjectToString(dataSnapshot.child("inct_type").getValue());
                            String type_value = convert.ObjectToString(dataSnapshot.child("inct_value").getValue());
                            if (type.equals("Percentage")) {
                                String incentive = convert.percentage(type_value, s);
                                toolbar_amount.setText(incentive);
                            } else if (type.equals("Gram")) {
                                String incentive = convert.multiply(s, type_value);
                                toolbar_amount.setText(incentive);
                            } else if (type.equals("Quantity")) {
                                String incentive = convert.multiply(type_value, s);
                                toolbar_amount.setText(incentive);
                            } else if (type.equals("Custom")) {
                                toolbar_amount.setText(s);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void populateSpinner() {
        Query query = mInctTypeRef.orderByChild("inct_name");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> typeList = new ArrayList<String>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String titlename = dataSnapshot1.child("inct_name").getValue(String.class);
                    typeList.add(titlename);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AdminAddInsentive.this, android.R.layout.simple_spinner_item, typeList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                inct_name_spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void validate() {
        who = getIntent().getStringExtra("who");
        from = getIntent().getStringExtra("from");
        name = getIntent().getStringExtra("name");
        mobile = getIntent().getStringExtra("mobile");

        nameText = (TextView) findViewById(R.id.add_inst_name);
        toolbar_amount = (TextView) findViewById(R.id.add_inst_toolbar_amount);
        bill_value = (EditText) findViewById(R.id.sell_amount);
        unit_value = (TextView) findViewById(R.id.add_inct_percent_text);
        bill_number = (EditText) findViewById(R.id.add_inct_bill_number);

        back_button = (ImageView) findViewById(R.id.add_inct_back_button);

        add_button = (Button) findViewById(R.id.add_inct_add_button);

        add_progress = (ProgressBar) findViewById(R.id.add_inct_progress);
        add_progress.setVisibility(View.GONE);

        inct_name_spinner = (Spinner) findViewById(R.id.add_inst_type_spinner);
        employee_name_spinner = (Spinner) findViewById(R.id.admin_add_incentive_name_spinner);

        mInctTypeRef = FirebaseDatabase.getInstance().getReference().child("Incentive_Types");
        mInctRef = FirebaseDatabase.getInstance().getReference().child("Incentives");
        mAdminInctRef = FirebaseDatabase.getInstance().getReference().child("AdminIncentives");
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersRef.keepSynced(true);
        mAdminInctRef.keepSynced(true);
        mInctRef.keepSynced(true);
        mInctTypeRef.keepSynced(true);
    }
}

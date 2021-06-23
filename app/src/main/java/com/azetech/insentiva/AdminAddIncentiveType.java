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
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminAddIncentiveType extends AppCompatActivity {

    private EditText type_name, type_value;
    private Spinner type_spinner;
    private Button add_button;
    private ProgressBar progress;
    private RelativeLayout value_layout;
    private ImageView back_button;
    private DatabaseReference mInctTypeRef;

    String mobile, who, from;
    Convert convert = new Convert();

    @Override
    protected void onStart() {
        super.onStart();
        populateSpinner();
    }

    private void populateSpinner() {
        List typeList = new ArrayList();
        typeList.add("Percentage");
        typeList.add("Gram");
        typeList.add("Quantity");
        typeList.add("Custom");

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(AdminAddIncentiveType.this, android.R.layout.simple_spinner_item, typeList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(typeAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(new Intent(AdminAddIncentiveType.this, AdminHome.class));
        intent.putExtra("mobile", mobile);
        intent.putExtra("from", from);
        intent.putExtra("who", who);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_incentive_type);
        validate();

        mobile = getIntent().getStringExtra("mobile");

        who = getIntent().getStringExtra("who");
        from = getIntent().getStringExtra("from");

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new Intent(AdminAddIncentiveType.this, AdminHome.class));
                intent.putExtra("mobile", mobile);
                intent.putExtra("from", from);
                intent.putExtra("who", who);
                startActivity(intent);
                finish();
            }
        });

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (type_spinner.getSelectedItemPosition() == 3){
                    value_layout.setVisibility(View.GONE);
                }
                else {
                    value_layout.setVisibility(View.VISIBLE);
                }

                if (type_spinner.getSelectedItemPosition() == 0){
                    type_value.setHint("Enter Percentage Value");
                }
                else if (type_spinner.getSelectedItemPosition() == 1){
                    type_value.setHint("Enter Amount for 1 Gram");
                }
                else if (type_spinner.getSelectedItemPosition() == 2){
                    type_value.setHint("Enter Amount for 1 Unit");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                addType();
            }
        });
    }

    private void addType() {
        String name = convert.ObjectToString(type_name.getText()).trim();
        String type = convert.ObjectToString(type_spinner.getSelectedItem());
        String value = convert.ObjectToString(type_value.getText()).trim();

        if (!(type_spinner.getSelectedItemPosition() == 3)){
            if (name.equals("")){
                type_name.setError("Please provide type name");
                progress.setVisibility(View.GONE);
            }
            else if (value.equals("")){
                type_value.setError("Please provide type value");
                progress.setVisibility(View.GONE);
            }
            else {
                HashMap inctHash = new HashMap();
                inctHash.put("inct_name", name);
                inctHash.put("inct_value", value);
                inctHash.put("content_type", "value");
                inctHash.put("inct_type", type);

                mInctTypeRef.child(name)
                        .setValue(inctHash)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progress.setVisibility(View.GONE);
                                    type_name.setText("");
                                    type_spinner.setSelection(0);
                                    type_value.setText("");
                                }
                            }
                        });
            }
        }
        else {
            if (name.equals("")){
                type_name.setError("Please provide type name");
                progress.setVisibility(View.GONE);
            }
            else {

                HashMap inctHash = new HashMap();
                inctHash.put("inct_name", name);
                inctHash.put("inct_type", type);
                inctHash.put("content_type", "nonvalue");

                mInctTypeRef.child(name)
                        .setValue(inctHash)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progress.setVisibility(View.GONE);
                                    type_name.setText("");
                                    type_spinner.setSelection(0);
                                }
                            }
                        });
            }
        }
    }

    private void validate() {
        type_name = (EditText) findViewById(R.id.add_type_name);
        type_value = (EditText) findViewById(R.id.add_type_value);

        type_spinner = (Spinner) findViewById(R.id.add_type_spinner);

        add_button = (Button) findViewById(R.id.add_type_button);

        back_button = (ImageView) findViewById(R.id.add_type_back_button);

        progress = (ProgressBar) findViewById(R.id.add_type_progress);
        progress.setVisibility(View.GONE);

        value_layout = (RelativeLayout) findViewById(R.id.add_type_value_layout);

        mInctTypeRef = FirebaseDatabase.getInstance().getReference().child("Incentive_Types");
        mInctTypeRef.keepSynced(true);
    }
}

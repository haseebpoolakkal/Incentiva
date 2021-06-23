package com.azetech.insentiva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminEditIncentiveType extends AppCompatActivity {

    private Spinner type_spinner;
    private TextView type_old_value;
    private EditText type_new_value;
    private Button edit_button;
    private ImageView delete_button, back_button;
    private ProgressBar progress;
    private DatabaseReference mInctRef;

    String mobile, who, from;
    Convert convert = new Convert();

    @Override
    protected void onStart() {
        super.onStart();
        populateSpinner();
    }

    private void populateSpinner() {
        Query query = mInctRef.orderByChild("inct_name");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> typeList = new ArrayList<String>();
                for (DataSnapshot typeShot : dataSnapshot.getChildren()) {
                    String type = convert.ObjectToString(typeShot.child("inct_name").getValue());
                    typeList.add(type);
                }

                ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(AdminEditIncentiveType.this, android.R.layout.simple_spinner_item, typeList);
                typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                type_spinner.setAdapter(typeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(new Intent(AdminEditIncentiveType.this, AdminHome.class));
        intent.putExtra("mobile", "");
        intent.putExtra("from", from);
        intent.putExtra("who", who);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_incentive_type);
        validate();

        mobile = getIntent().getStringExtra("mobile");

        who = getIntent().getStringExtra("who");
        from = getIntent().getStringExtra("from");

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final List<String> dbList = new ArrayList<String>();
                Query query = mInctRef.orderByChild("content_type").equalTo("value");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot typeShot : dataSnapshot.getChildren()) {
                            String type = convert.ObjectToString(typeShot.child("inct_name").getValue());
                            dbList.add(type);
                        }
                        if (!dbList.contains(type_spinner.getSelectedItem())){
                            type_new_value.setVisibility(View.GONE);
                            edit_button.setVisibility(View.GONE);
                            type_old_value.setVisibility(View.GONE);
                        }
                        else {
                            type_new_value.setVisibility(View.VISIBLE);
                            edit_button.setVisibility(View.VISIBLE);
                            type_old_value.setVisibility(View.VISIBLE);
                            populateTypeValue(convert.ObjectToString(type_spinner.getSelectedItem()));
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

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new Intent(AdminEditIncentiveType.this, AdminHome.class));
                intent.putExtra("mobile", "");
                intent.putExtra("from", from);
                intent.putExtra("who", who);
                startActivity(intent);
                finish();
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AdminEditIncentiveType.this);
                builder.setTitle("Delete");
                builder.setMessage("Do you really want to delete this type?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String type = convert.ObjectToString(type_spinner.getSelectedItem());
                                mInctRef.child(type)
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(AdminEditIncentiveType.this, "Type Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                    }
                });
                builder.setNegativeButton("No", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String type = convert.ObjectToString(type_spinner.getSelectedItem());
                final int type_pos = type_spinner.getSelectedItemPosition();
                final String value = convert.ObjectToString(type_new_value.getText());
                if (value.equals("")){
                    type_new_value.setError("Please enter value");
                    progress.setVisibility(View.GONE);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminEditIncentiveType.this);
                    builder.setTitle("Delete");
                    builder.setMessage("Do you really want to delete this type?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editType(type, value);
                            progress.setVisibility(View.VISIBLE);
                            type_spinner.setSelection(type_pos);
                        }
                    });
                    builder.setNegativeButton("No", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    private void editType(String type, final String value) {
        mInctRef.child(type)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            dataSnapshot.child("inct_value").getRef().setValue(value);

                            type_new_value.setText("");
                            progress.setVisibility(View.GONE);
                            Toast.makeText(AdminEditIncentiveType.this, "Successfully Edited!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void populateTypeValue(String selectedItem) {
        mInctRef.child(selectedItem)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String value = convert.ObjectToString(dataSnapshot.child("inct_value").getValue());
                    type_old_value.setText(value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validate() {
        type_spinner = (Spinner) findViewById(R.id.edit_type_spinner);

        type_old_value = (TextView) findViewById(R.id.edit_type_old_value);

        type_new_value = (EditText) findViewById(R.id.edit_type_new_value);

        edit_button = (Button) findViewById(R.id.edit_type_button);

        delete_button = (ImageView) findViewById(R.id.edit_type_delete_button);
        back_button = (ImageView) findViewById(R.id.edit_type_back_button);

        progress = (ProgressBar) findViewById(R.id.edit_type_progress);
        progress.setVisibility(View.GONE);

        mInctRef = FirebaseDatabase.getInstance().getReference().child("Incentive_Types");
        mInctRef.keepSynced(true);

     }
}

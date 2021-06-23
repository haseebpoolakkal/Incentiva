package com.azetech.insentiva;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class EmployeeSettingsFragment extends Fragment {

    private RelativeLayout logout, resetName, resetPassword, about, privacy;
    private FirebaseAuth mAuth;
    private DatabaseReference mEmpRef;

    String mobile;

    Convert convert = new Convert();

    public EmployeeSettingsFragment(String mobile) {
        // Required empty public constructor
        this.mobile = mobile;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_employee_settings, container, false);

        validate(view);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

        resetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText nameText = new EditText(v.getContext());
                nameText.setHint("Enter Name");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Change Name");
                builder.setMessage("Enter new Name");
                builder.setView(nameText);
                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = convert.ObjectToString(nameText.getText());
                        mEmpRef.child(mAuth.getCurrentUser().getUid())
                                .child("name")
                                .getRef()
                                .setValue(name)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(getContext(), "Something went wrong, Please try later", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EmployeeChangePassword.class);
                intent.putExtra("mobile", mobile);
                intent.putExtra("who", "employee");
                intent.putExtra("from", "settings");
                startActivity(intent);
                getActivity().finish();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutBottomSheet sheet = new AboutBottomSheet();
                sheet.show(getChildFragmentManager(), "About");
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpClass popUpClass = new PopUpClass();
                popUpClass.showPopupWindow(v);
            }
        });

        privacy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    privacy.setBackgroundColor(getResources().getColor(R.color.touch_pressed));
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    privacy.setBackgroundColor(getResources().getColor(R.color.white));
                }
                return false;
            }
        });

        resetName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    resetName.setBackgroundColor(getResources().getColor(R.color.touch_pressed));
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    resetName.setBackgroundColor(getResources().getColor(R.color.white));
                }
                return false;
            }
        });

        resetPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    resetPassword.setBackgroundColor(getResources().getColor(R.color.touch_pressed));
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    resetPassword.setBackgroundColor(getResources().getColor(R.color.white));
                }
                return false;
            }
        });

        logout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    logout.setBackgroundColor(getResources().getColor(R.color.touch_pressed));
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    logout.setBackgroundColor(getResources().getColor(R.color.white));
                }
                return false;
            }
        });

        about.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    about.setBackgroundColor(getResources().getColor(R.color.touch_pressed));
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    about.setBackgroundColor(getResources().getColor(R.color.white));
                }
                return false;
            }
        });

        return view;
    }

    private void validate(View view) {
        logout = (RelativeLayout) view.findViewById(R.id.employee_setting_logout);
        about = (RelativeLayout) view.findViewById(R.id.employee_setting_about);
        resetName = (RelativeLayout) view.findViewById(R.id.employee_setting_name_reset);
        resetPassword = (RelativeLayout) view.findViewById(R.id.employee_setting_pass_reset);
        privacy = (RelativeLayout) view.findViewById(R.id.employee_setting_privacy);

        mAuth = FirebaseAuth.getInstance();

        mEmpRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mEmpRef.keepSynced(true);
    }

}

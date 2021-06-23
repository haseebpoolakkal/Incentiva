package com.azetech.insentiva;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class AdminSettingFragment extends Fragment {

    private RelativeLayout add_inct_type_layout, logout, edit_inct_type_layout, privacy,
            add_salary, about_layout, change_password, referral_layout;
    private FirebaseAuth firebaseAuth;

    Convert convert = new Convert();

    public AdminSettingFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_setting, container, false);

        validate(view);

        add_inct_type_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminAddIncentiveType.class);
                intent.putExtra("mobile", "");
                intent.putExtra("who", "admin");
                intent.putExtra("from", "settings");
                startActivity(intent);
                getActivity().finish();
            }
        });

        edit_inct_type_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminEditIncentiveType.class);
                intent.putExtra("mobile", "");
                intent.putExtra("who", "admin");
                intent.putExtra("from", "settings");
                startActivity(intent);
                getActivity().finish();
            }
        });

        add_salary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminChangeSalary.class);
                intent.putExtra("mobile", "");
                intent.putExtra("who", "admin");
                intent.putExtra("from", "settings");
                startActivity(intent);
                getActivity().finish();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

        about_layout.setOnClickListener(new View.OnClickListener() {
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

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EmployeeChangePassword.class);
                intent.putExtra("mobile", "");
                intent.putExtra("who", "admin");
                intent.putExtra("from", "settings");
                startActivity(intent);
                getActivity().finish();
            }
        });

        referral_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpReferral popUpReferral = new PopUpReferral();
                popUpReferral.showPopupWindow(v);
            }
        });

        referral_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    referral_layout.setBackgroundColor(getResources().getColor(R.color.touch_pressed));
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    referral_layout.setBackgroundColor(getResources().getColor(R.color.white));
                }
                return false;
            }
        });

        change_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    change_password.setBackgroundColor(getResources().getColor(R.color.touch_pressed));
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    change_password.setBackgroundColor(getResources().getColor(R.color.white));
                }
                return false;
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


        edit_inct_type_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    edit_inct_type_layout.setBackgroundColor(getResources().getColor(R.color.touch_pressed));
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    edit_inct_type_layout.setBackgroundColor(getResources().getColor(R.color.white));
                }
                return false;
            }
        });

        add_inct_type_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    add_inct_type_layout.setBackgroundColor(getResources().getColor(R.color.touch_pressed));
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    add_inct_type_layout.setBackgroundColor(getResources().getColor(R.color.white));
                }
                return false;
            }
        });

        add_salary.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    add_salary.setBackgroundColor(getResources().getColor(R.color.touch_pressed));
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    add_salary.setBackgroundColor(getResources().getColor(R.color.white));
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

        about_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    about_layout.setBackgroundColor(getResources().getColor(R.color.touch_pressed));
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    about_layout.setBackgroundColor(getResources().getColor(R.color.white));
                }
                return false;
            }
        });

        return view;
    }

    private void validate(View view) {
        add_inct_type_layout = (RelativeLayout) view.findViewById(R.id.setting_add_inst);
        logout = (RelativeLayout) view.findViewById(R.id.setting_logout);
        edit_inct_type_layout = (RelativeLayout) view.findViewById(R.id.setting_change_inst);
        about_layout = (RelativeLayout) view.findViewById(R.id.admin_setting_about);
        add_salary = (RelativeLayout) view.findViewById(R.id.setting_add_salary);
        privacy = (RelativeLayout) view.findViewById(R.id.admin_setting_privacy);
        change_password = (RelativeLayout) view.findViewById(R.id.setting_change_password);
        referral_layout = (RelativeLayout) view.findViewById(R.id.setting_referral_layout);

        firebaseAuth = FirebaseAuth.getInstance();
    }

}

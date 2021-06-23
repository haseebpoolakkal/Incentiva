package com.azetech.insentiva;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class AdminHomeFragment extends Fragment {

    private DatabaseReference mInctRef, mEmpRef, mAdminInctRef, mPayRollRef, mUpdateRef;
    private TextView main_total, employee_total, employee_absence, month_total, chart_heading;
    private RelativeLayout attendanceLayout, incentiveLayout, typeLayout, salaryLayout;

    private LineChartView lineChartView;

    Convert convert = new Convert();


    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        validate(view);

        checkStatus();

        attendanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminAttendanceBottomSheet sheet = new AdminAttendanceBottomSheet();
                sheet.show(getFragmentManager(), "Bottom Sheet");
            }
        });

        incentiveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminAddInsentive.class);
                intent.putExtra("name", "");
                intent.putExtra("mobile", "");
                intent.putExtra("who", "admin");
                intent.putExtra("from", "home");
                startActivity(intent);
            }
        });

        typeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminEditIncentiveType.class);
                intent.putExtra("mobile", "");
                intent.putExtra("who", "admin");
                intent.putExtra("from", "home");
                startActivity(intent);
                getActivity().finish();
            }
        });

        salaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), AdminChangeSalary.class);
                intent.putExtra("mobile", "");
                intent.putExtra("who", "admin");
                intent.putExtra("from", "home");
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
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
                        startActivity(new Intent(getActivity(), Splash.class));
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void init() {
        getTotal();
        getEmployeeCount();
        getMonthTotal();

        showChart();
    }

    private void showChart() {
        final List valueList = new ArrayList();
        final List nameList = new ArrayList();
        mEmpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String count = convert.ObjectToString(dataSnapshot.getChildrenCount());
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        String name = convert.ObjectToString(snapshot.child("name").getValue());
                        if (!nameList.contains(name)){
                            nameList.add(name);
                        }
                    }
                    for (int i = 0; i < convert.StringToInt(count); i++) {
                        valueList.add(i,"0");
                    }
                    getTotalIncentive(nameList, valueList);
                }
            }

            private void getTotalIncentive(final List nameList, final List valueList) {
                Query query = mAdminInctRef.child(convert.getYear()).orderByChild("month").equalTo(convert.getMonth());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if (snapshot.exists()){
                                String name = convert.ObjectToString(snapshot.child("name").getValue());
                                String value = convert.ObjectToString(snapshot.child("incentive").getValue());
                                for (int i = 0; i < nameList.size(); i++){
                                    if (name.equals(nameList.get(i))){
                                        String val1 = convert.ObjectToString(valueList.get(i));
                                        String sum = convert.addition(value, val1);
                                        valueList.set(i, sum);
                                    }
                                }
                                initializeChart(nameList, valueList);
                            }
                        }
                    }

                    private void initializeChart(final List nameList, final List valueList) {
                        List values = new ArrayList();
                        List names = new ArrayList();


                        for (int i = 0; i < valueList.size(); i++) {
                            values.add(new PointValue(i, convert.StringToInt(convert.ObjectToString(valueList.get(i)))));
                        }

                        for (int i = 0; i < nameList.size(); i++) {
                            names.add(i, new AxisValue(i).setLabel(convert.ObjectToString(nameList.get(i))));
                        }

                        Line line = new Line(values);
                        List lines = new ArrayList();
                        lines.add(line);

                        LineChartData data = new LineChartData();
                        data.setLines(lines);

                        lineChartView.setLineChartData(data);
                        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
                        viewport.top = 50;
                        lineChartView.setMaximumViewport(viewport);
                        lineChartView.setCurrentViewport(viewport);

                        lineChartView.setInteractive(true);
                        lineChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);

                        lineChartView.setLineChartData(data);
                        lineChartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
                            @Override
                            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                                Toast.makeText(getContext(),
                                        "Incentive of ₹"+convert.ObjectToString((int) value.getY())+
                                                " is earned by "+ nameList.get((int) value.getX()),
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onValueDeselected() {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMonthTotal() {
        final List totalList = new ArrayList();
        Query query = mAdminInctRef.child(convert.getYear()).orderByChild("month").equalTo(convert.getMonth());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    for (DataSnapshot inctShot : dataSnapshot.getChildren()) {
                        if (inctShot.exists()) {
                            String inct_amount = convert.ObjectToString(inctShot.child("incentive").getValue());
                            totalList.add(inct_amount);
                        } else {
                            Toast.makeText(getContext(), "Something went Wrong!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (!totalList.isEmpty()) {
                        int total = 0;
                        for (Object value : totalList) {
                            total = convert.StringToInt(convert.addition(convert.IntToString(total), convert.ObjectToString(value)));
                        }
                        month_total.setText("₹" + convert.IntToString(total));
                    } else {
                        month_total.setText("0");
                    }
                } catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getEmployeeCount() {
        try {
            mEmpRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String count = convert.ObjectToString(dataSnapshot.getChildrenCount());
                    employee_total.setText(count);
                    getAttendence(count);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getAttendence(final String count) {
        try {
            mPayRollRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (convert.ObjectToString(snapshot.child("isPresent").getValue()).equals("yes")) {
                            i += 1;
                        }
                    }
                    String days = convert.IntToString(i);
                    String attendance = count;
                    employee_total.setText(attendance);
                    employee_absence.setText(days);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getTotal() {
        final List totalList = new ArrayList();
        mAdminInctRef.child(convert.getYear()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot mainShot: dataSnapshot.getChildren()){
                    String date = convert.ObjectToString(mainShot.child("date").getValue());
                    if (date.equals(convert.getDate())){
                        String inct = convert.ObjectToString(mainShot.child("incentive").getValue());
                        totalList.add(inct);
                    }
                }
                if (!totalList.isEmpty()){
                    int total = 0;
                    for (Object value: totalList) {
                        total = convert.StringToInt(convert.addition(convert.IntToString(total), convert.ObjectToString(value)));
                    }
                    main_total.setText("₹"+convert.IntToString(total));
                }
                else {
                    main_total.setText("₹0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validate(View view) {
        mInctRef = FirebaseDatabase.getInstance().getReference().child("Incentives");
        mInctRef.keepSynced(true);
        mEmpRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mEmpRef.keepSynced(true);
        mAdminInctRef = FirebaseDatabase.getInstance().getReference().child("AdminIncentives");
        mAdminInctRef.keepSynced(true);
        mPayRollRef = FirebaseDatabase.getInstance().getReference().child("PayRoll");
        mPayRollRef.keepSynced(true);
        mUpdateRef = FirebaseDatabase.getInstance().getReference().child("Updates");
        mUpdateRef.keepSynced(true);

        attendanceLayout = (RelativeLayout) view.findViewById(R.id.admjn_home_attendance);
        incentiveLayout = (RelativeLayout) view.findViewById(R.id.admjn_home_add_incentive);
        typeLayout = (RelativeLayout) view.findViewById(R.id.admjn_home_edit_incentive);
        salaryLayout = (RelativeLayout) view.findViewById(R.id.admjn_home_change_salary);


        main_total = (TextView) view.findViewById(R.id.admin_home_main_today);
        employee_total = (TextView) view.findViewById(R.id.admin_home_main_employee_total);
        employee_absence = (TextView) view.findViewById(R.id.admin_home_main_employee_absence);
        month_total = (TextView) view.findViewById(R.id.admin_home_main_month);
        chart_heading = (TextView) view.findViewById(R.id.admin_home_chart_heading);
        chart_heading.setText("Incentives of "+convert.getMonth());

        lineChartView = (LineChartView) view.findViewById(R.id.admin_home_chart);
    }
}

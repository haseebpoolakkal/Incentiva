package com.azetech.insentiva;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;


public class EmployeeHomeFragment extends Fragment {

    private String mobile;
    private FirebaseAuth mAuth;
    private DatabaseReference mEmpRef, mEmpInctRef, mPayRollRef, mAdminInctRef, mUpdateRef;
    private TextView nameText, inctText, salaryText, attendanceText, grandTotaltext, chartHeading;

    private LineChartView lineChartView;
    private PieChart pieChart;


    Convert convert = new Convert();

    public EmployeeHomeFragment(String mobile) {
        // Required empty public constructor
        this.mobile = mobile;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_home, container, false);

        validate(view);
        checkStatus();

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

    private void init(){
        String currentUser = mAuth.getCurrentUser().getUid();

        getEmployeeName(currentUser);

        showPieChart();
    }


    private void showPieChart() {
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
                        List colors = new ArrayList();

                        pieChart.setUsePercentValues(true);
                        pieChart.setHoleColor(getResources().getColor(R.color.colorPrimaryDark));
                        pieChart.setDrawHoleEnabled(true);
                        pieChart.setHoleRadius(30);
                        pieChart.setTransparentCircleRadius(10);
                        pieChart.setRotationAngle(0);
                        pieChart.setRotationEnabled(true);

                        ArrayList<Entry> values = new ArrayList<>();

                        for (int i = 0; i< valueList.size(); i++){
                            values.add(new BarEntry(Float.parseFloat(convert.ObjectToString(valueList.get(i))), i));
                        }

                        PieDataSet dataSet = new PieDataSet(values, "Entries");
                        dataSet.setSliceSpace(3);
                        dataSet.setSelectionShift(5);

                        colors.add(ColorTemplate.getHoloBlue());
                        dataSet.setColors(colors);

                        PieData data = new PieData(nameList, dataSet);
                        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

                        dataSet.setSliceSpace(3f);
                        dataSet.setSelectionShift(5f);



                        pieChart.setData(data);

                        pieChart.setDescription("Incentives of Employees");

                        data.setValueFormatter(new PercentFormatter());
                        data.setValueTextSize(11f);
                        data.setValueTextColor(Color.DKGRAY);
                        pieChart.animateXY(1400, 1400);

                        pieChart.setData(data);

                        pieChart.highlightValues(null);

                        pieChart.invalidate();

                        Legend l = pieChart.getLegend();
                        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
                        l.setXEntrySpace(7);
                        l.setYEntrySpace(5);
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

    private void getAttendance(final String salary) {
        mPayRollRef.child(mobile)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String attendance = convert.ObjectToString(dataSnapshot.child("attendance").getValue());
                            calculateAbsence(attendance, salary);

                            attendanceText.setText(attendance);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void calculateAbsence(String attendance, String salary) {
        String daySalary = convert.divide(salary, "30");
        String payableSalary = convert.multiply(daySalary, attendance);
        getIncentive(payableSalary);
    }

    private void getIncentive(final String payableSalary) {
        final List totalList = new ArrayList();
        mEmpInctRef.child(convert.getYear())
                .child(mobile)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            if (snapshot.exists()){
                                String value = convert.ObjectToString(snapshot.child("incentive").getValue());
                                totalList.add(value);
                            }
                        }
                        if (!totalList.isEmpty()){
                            int total = 0;
                            for (Object value: totalList){
                                total = convert.StringToInt(convert.addition(convert.IntToString(total), convert.ObjectToString(value)));
                            }
                            inctText.setText("₹"+convert.IntToString(total));
                            getGrandTotal(payableSalary, convert.IntToString(total));
                        }
                        else {
                            inctText.setText("₹0");
                            getGrandTotal(payableSalary, "0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getGrandTotal(String payableSalary, String totalIncentive) {
        String grandTotal = convert.addition(payableSalary, totalIncentive);
        grandTotaltext.setText("₹"+grandTotal);
        showChart();
    }

    private void showChart() {
        final List monthInctList = new ArrayList();
        for (int i = 0; i < 12; i++) {
            monthInctList.add(i, "0");
        }
        mEmpInctRef.child(convert.getYear())
                .child(mobile)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            String month = convert.ObjectToString(snapshot.child("month").getValue());
                            String value = convert.ObjectToString(snapshot.child("incentive").getValue());
                            if (month.equals("January")){
                                getMonthTotal("0", value);
                            }
                            else if (month.equals("February")){
                                getMonthTotal("1", value);
                            }
                            else if (month.equals("March")){
                                getMonthTotal("2", value);
                            }
                            else if (month.equals("April")){
                                getMonthTotal("3", value);
                            }
                            else if (month.equals("May")){
                                getMonthTotal("4", value);
                            }
                            else if (month.equals("June")){
                                getMonthTotal("5", value);
                            }
                            else if (month.equals("July")){
                                getMonthTotal("6", value);
                            }
                            else if (month.equals("August")){
                                getMonthTotal("7", value);
                            }
                            else if (month.equals("September")){
                                getMonthTotal("8", value);
                            }
                            else if (month.equals("October")){
                                getMonthTotal("9", value);
                            }
                            else if (month.equals("November")){
                                getMonthTotal("10", value);
                            }
                            else if (month.equals("December")){
                                getMonthTotal("11", value);
                            }
                            drawChart(monthInctList);
                        }
                    }

                    private void getMonthTotal(String pos, String value) {
                        String val = convert.ObjectToString(monthInctList.get(convert.StringToInt(pos)));
                        String sum = convert.addition(value, val);
                        monthInctList.set(convert.StringToInt(pos), sum);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void drawChart(List monthInctList) {
        String[] axisData = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
                "Oct", "Nov", "Dec"};
        final String[] axisMonthData = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        Line line = new Line(yAxisValues);

        for (int i = 0; i < axisData.length; i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < monthInctList.size(); i++) {
            yAxisValues.add(new PointValue(i, convert.StringToInt(convert.ObjectToString(monthInctList.get(i)))));
        }
        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(getResources().getColor(R.color.text_color_accent));
        data.setAxisXBottom(axis);

        lineChartView.setLineChartData(data);

        lineChartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getContext(),
                        "Total of ₹"+convert.ObjectToString((int) value.getY())+
                                " earned in "+ axisMonthData[(int) value.getX()],
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    private void getEmployeeName(String currentUser) {
        mEmpRef.child(currentUser)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = convert.ObjectToString(dataSnapshot.child("name").getValue());
                    String salary = convert.ObjectToString(dataSnapshot.child("salary").getValue());
                    nameText.setText(name);
                    salaryText.setText("₹"+salary);


                    getAttendance(salary);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validate(View view) {
        mAuth = FirebaseAuth.getInstance();

        mEmpRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mEmpRef.keepSynced(true);
        mPayRollRef = FirebaseDatabase.getInstance().getReference().child("PayRoll");
        mPayRollRef.keepSynced(true);
        mEmpInctRef = FirebaseDatabase.getInstance().getReference().child("Incentives");
        mEmpInctRef.keepSynced(true);
        mAdminInctRef = FirebaseDatabase.getInstance().getReference().child("AdminIncentives");
        mAdminInctRef.keepSynced(true);
        mUpdateRef = FirebaseDatabase.getInstance().getReference().child("Updates");
        mUpdateRef.keepSynced(true);


        nameText = (TextView) view.findViewById(R.id.employee_home_person_name);

        inctText = (TextView) view.findViewById(R.id.employee_home_total_inct);
        salaryText = (TextView) view.findViewById(R.id.employee_home_total_salary);
        grandTotaltext = (TextView) view.findViewById(R.id.employee_home_grand_total);
        attendanceText = (TextView) view.findViewById(R.id.employee_home_attendance);
        chartHeading = (TextView) view.findViewById(R.id.employee_inct_chart_heading);
        chartHeading.setText("Incentives of "+convert.getYear());

        lineChartView = (LineChartView) view.findViewById(R.id.employee_inct_chart);
        pieChart = view.findViewById(R.id.employee_pie_chart);
    }

}

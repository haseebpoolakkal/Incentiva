package com.azetech.insentiva;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminEmployeePayRoll extends Fragment {

    private String mobile, name, salary;
    private TextView heading, salaryText, incentiveText, attendanceText, totalText, totalPayable, dayText;
    private TextView grandTotalText, balanceText, monthText, paidText, totalPaidText;
    private ImageView error, check;
    private Button payButton;
    private ProgressBar progressBar;
    private DatabaseReference mPayRollRef, mInctRef, mPaymentRef;

    private RelativeLayout progress, data_container;
    private ImageView s1, s2, s3, s4, s5, s6, s7, s8, s9;
    Animation shinAnim;

    Convert convert = new Convert();

    public AdminEmployeePayRoll(String mobile, String name, String salary) {
        // Required empty public constructor
        this.mobile = mobile;
        this.name = name;
        this.salary = salary;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_employee_pay_roll, container, false);
        validate(view);

        init();

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPayDialog();
            }
        });
        return view;
    }

    private void init() {
        heading.setText(name);
        payButton.setVisibility(View.VISIBLE);
        monthText.setText("("+convert.getMonth()+")");
        if (salary.equals("0")){
            data_container.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            payButton.setEnabled(false);
        }
        else {
            salaryText.setText(salary);
            getIncentive();
        }
    }

    private void getIncentive() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            final List totalList = new ArrayList();
            mInctRef.child(convert.getYear())
                    .child(mobile)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot mainShot: dataSnapshot.getChildren()){
                                String inct = convert.ObjectToString(mainShot.child("incentive").getValue());
                                totalList.add(inct);
                            }
                            if (!totalList.isEmpty()){
                                int total = 0;
                                for (Object value: totalList) {
                                    total = convert.StringToInt(convert.addition(convert.IntToString(total), convert.ObjectToString(value)));
                                }
                                incentiveText.setText(convert.IntToString(total));
                                getTotal();
                            }
                            else {
                                incentiveText.setText("0");
                                getTotal();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } catch(Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getTotal() {
        try {
            String inct = convert.ObjectToString(incentiveText.getText()).replaceAll("[^0-9]","");
            String total = convert.addition(salary, inct);
            totalText.setText(total);
            getAttendance();
        } catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getAttendance() {
        try {
            mPayRollRef.child(mobile)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String daysAttended = convert.ObjectToString(dataSnapshot.child("attendance").getValue());
                                String absences = convert.subtract("30", daysAttended);
                                calculateAbsence(absences);
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Error, Please try later", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void calculateAbsence(String days) {
        try {
            if (!salary.equals("0")){
                String dailyWage = convert.divide(salary, "30");
                dayText.setText("("+dailyWage+" x "+days+")");
                String absenceWage = convert.multiply(days, dailyWage);
                attendanceText.setText(absenceWage);
                getTotalPayable();
            }
            else {
                progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getTotalPayable() {
        String totalPayabale = convert.ObjectToString(totalText.getText()).replaceAll("[^0-9]","");
        String absenceWage = convert.ObjectToString(attendanceText.getText()).replaceAll("[^0-9]","");
        if (absenceWage.equals("0")){
            totalPayable.setText(totalPayabale);
            progressBar.setVisibility(View.GONE);
            getPaid();
        }
        else {
            String total = convert.subtract(totalPayabale, absenceWage);
            totalPayable.setText(total);
            progressBar.setVisibility(View.GONE);
            getPaid();
        }
    }

    private void getPaid() {
        mPaymentRef.child(mobile)
                .child(convert.getMonth())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String paid = convert.ObjectToString(dataSnapshot.child("paid").getValue());
                            paidText.setText(paid);
                            getTotalPaid(paid);
                        }
                        else {
                            paidText.setText("0");
                            getTotalPaid("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getTotalPaid(String paid) {
        String payable = convert.ObjectToString(totalPayable.getText());
        if (!paid.equals("0")){
            payable = convert.subtract(payable, paid);
            totalPaidText.setText(payable);
            getBalance();
        }
        else {
            totalPaidText.setText(payable);
            getBalance();
        }
    }

    private void getBalance() {
        final List list = new ArrayList();
        mPaymentRef.child(mobile)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            String month = convert.ObjectToString(snapshot.getKey());
                            if (!month.equals(convert.getMonth())){
                                if (snapshot.exists()){
                                    String balance = convert.ObjectToString(snapshot.child("balance").getValue());
                                    list.add(balance);
                                }
                                else {
                                    balanceText.setText("0");
                                    getGrandTotal();
                                }
                            }
                            else {
                                list.add("0");
                            }
                        }
                        if (!list.isEmpty()){
                            int total = 0;
                            for (Object value: list) {
                                total = convert.StringToInt(convert.addition(convert.IntToString(total), convert.ObjectToString(value)));
                            }
                            balanceText.setText(convert.IntToString(total));
                            getGrandTotal();
                        }
                        else {
                            balanceText.setText("0");
                            getGrandTotal();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getGrandTotal() {
        String totalPayable = convert.ObjectToString(totalPaidText.getText());
        String balance = convert.ObjectToString(balanceText.getText());
        if (!balance.equals("0")){
            String total = convert.addition(totalPayable, balance);
            grandTotalText.setText("₹"+total);
            error.setVisibility(View.VISIBLE);
            check.setVisibility(View.GONE);
        }
        else {
            grandTotalText.setText("₹"+totalPayable);
            if (totalPayable.equals("0")) {
                check.setVisibility(View.VISIBLE);
                error.setVisibility(View.GONE);
                payButton.setVisibility(View.GONE);
            }
            else {
                error.setVisibility(View.VISIBLE);
                check.setVisibility(View.GONE);
            }
        }

        data_container.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void openPayDialog() {
        final String payable = convert.ObjectToString(grandTotalText.getText()).replaceAll("[^0-9]","");
        final EditText input = new EditText(getContext());
        input.setGravity(Gravity.CENTER);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter Amount");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Pay");
        builder.setMessage("Total Amount to Pay is ₹"+payable);
        builder.setView(input);
        builder.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amount = convert.ObjectToString(input.getText());
                String balance = convert.subtract(payable, amount);
                if (convert.StringToInt(balance) < 0){
                    Toast.makeText(getContext(), "Please check entered amount !!", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadPayments(amount, balance);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void uploadPayments(final String newPayed, final String balance) {
        mPaymentRef.child(mobile)
                .child(convert.getMonth())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String oldPaid = "0";
                        if (dataSnapshot.exists()){
                            oldPaid = convert.ObjectToString(dataSnapshot.child("paid").getValue());
                        }
                        oldPaid = convert.addition(newPayed, oldPaid);
                        dataSnapshot.child("paid").getRef().setValue(oldPaid);
                        dataSnapshot.child("balance").getRef().setValue(balance);
                        resetAllMonthBalance();
                        init();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void resetAllMonthBalance() {
        mPaymentRef.child(mobile)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot mainShot: dataSnapshot.getChildren()){
                            if (mainShot.exists()) {
                                String month = convert.ObjectToString(mainShot.getKey());
                                if (!month.equals(convert.getMonth())) {
                                    mainShot.child("balance").getRef().setValue("0");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void validate(View view) {
        heading = (TextView) view.findViewById(R.id.ledger_heading);
        salaryText = (TextView) view.findViewById(R.id.ledger_salary);
        incentiveText = (TextView) view.findViewById(R.id.ledger_incentive);
        balanceText = (TextView) view.findViewById(R.id.ledger_balance);
        grandTotalText = (TextView) view.findViewById(R.id.ledger_grand_total);
        attendanceText = (TextView) view.findViewById(R.id.ledger_attendance);
        totalText = (TextView) view.findViewById(R.id.ledger_total_payable);
        totalPayable = (TextView) view.findViewById(R.id.ledger_total);
        dayText = (TextView) view.findViewById(R.id.ledger_attendance_day);
        monthText = (TextView) view.findViewById(R.id.ledger_month);
        paidText = (TextView) view.findViewById(R.id.ledger_payed);
        totalPaidText = (TextView) view.findViewById(R.id.ledger_total_balance);

        error = (ImageView) view.findViewById(R.id.ledger_money_error);
        error.setVisibility(View.GONE);
        check = (ImageView) view.findViewById(R.id.ledger_money_ok);
        check.setVisibility(View.GONE);

        payButton = (Button) view.findViewById(R.id.ledger_pay_button);

        progressBar = (ProgressBar) view.findViewById(R.id.ledger_progress);
        progressBar.setVisibility(View.GONE);

        mInctRef = FirebaseDatabase.getInstance().getReference().child("Incentives");
        mInctRef.keepSynced(true);
        mPayRollRef = FirebaseDatabase.getInstance().getReference().child("PayRoll");
        mPayRollRef.keepSynced(true);
        mPaymentRef = FirebaseDatabase.getInstance().getReference().child("Payment").child(convert.getYear());
        mPaymentRef.keepSynced(true);


        //Animation
        progress = (RelativeLayout) view.findViewById(R.id.ledger_progress_container);
        data_container = (RelativeLayout) view.findViewById(R.id.ledger_data_container);
        data_container.setVisibility(View.GONE);

        shinAnim = AnimationUtils.loadAnimation(getContext(), R.anim.shine_transform);

        s1 = (ImageView) view.findViewById(R.id.payroll_shine_progress_1);
        s2 = (ImageView) view.findViewById(R.id.payroll_shine_progress_2);
        s3 = (ImageView) view.findViewById(R.id.payroll_shine_progress_3);
        s4 = (ImageView) view.findViewById(R.id.payroll_shine_progress_4);
        s5 = (ImageView) view.findViewById(R.id.payroll_shine_progress_5);
        s6 = (ImageView) view.findViewById(R.id.payroll_shine_progress_6);
        s7 = (ImageView) view.findViewById(R.id.payroll_shine_progress_7);
        s8 = (ImageView) view.findViewById(R.id.payroll_shine_progress_8);
        s9 = (ImageView) view.findViewById(R.id.payroll_shine_progress_9);

        s1.startAnimation(shinAnim);
        s2.startAnimation(shinAnim);
        s3.startAnimation(shinAnim);
        s4.startAnimation(shinAnim);
        s5.startAnimation(shinAnim);
        s6.startAnimation(shinAnim);
        s7.startAnimation(shinAnim);
        s8.startAnimation(shinAnim);
        s9.startAnimation(shinAnim);

    }

}

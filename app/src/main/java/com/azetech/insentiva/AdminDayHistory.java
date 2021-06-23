package com.azetech.insentiva;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AdminDayHistory extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference mAdminInctRef, mUsersRef, mInctRef;
    private RelativeLayout datePicker;
    final Calendar calendar = Calendar.getInstance();

    Convert convert = new Convert();

    public AdminDayHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_day_history, container, false);
        validate(view);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDate();
            }

        };

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Objects.requireNonNull(getContext()), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        return view;
    }

    private void showRecycler(String date) {
        try {
            Query query = mAdminInctRef.child(convert.getYear()).orderByChild("date").equalTo(date);
            FirebaseRecyclerOptions Option = new FirebaseRecyclerOptions.Builder<incetive_model>()
                    .setQuery(query, incetive_model.class)
                    .build();


            FirebaseRecyclerAdapter<incetive_model, AdminHistoryDayViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<incetive_model, AdminHistoryDayViewHolder>(Option) {
                @SuppressLint("SetTextI18n")
                @Override
                protected void onBindViewHolder(@NonNull AdminHistoryDayViewHolder holder, final int position, @NonNull incetive_model model) {

                    try {
                        final String name = model.getName();
                        final String type = model.getInct_type();
                        String datetext = model.getDate();
                        final String bill_no = model.getBill_number();
                        String incentive = model.getIncentive();

                        holder.inct_type.setText(type);
                        holder.bill_number.setText(bill_no);
                        holder.name.setText(name);
                        holder.inct_amount.setText("₹" + incentive);
                        holder.date.setText(datetext);
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                alert.setTitle("Delete");
                                alert.setMessage("Do you want to delete this bill?");
                                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getMobileNumber(name, bill_no);
                                    }
                                });
                                alert.setNegativeButton("No", null);
                                AlertDialog dialog = alert.create();
                                dialog.show();
                                return false;
                            }
                        });


                    } catch (NullPointerException n) {
                        Toast.makeText(getContext(), "Null", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                    }

                }

                @NonNull
                @Override
                public AdminHistoryDayViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.employee_incentive_list, viewGroup, false);
                    AdminHistoryDayViewHolder viewHolder = new AdminHistoryDayViewHolder(view);

                    return viewHolder;
                }
            };

            recyclerView.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void getMobileNumber(String name, final String bill_no) {
        Query query = mUsersRef.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if (dataSnapshot.exists()){
                        String mobile = convert.ObjectToString(snapshot.child("mobile").getValue());
                        deleteBill(mobile, bill_no);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteBill(final String mobile, final String bill_no) {
        mInctRef.child(convert.getYear())
                .child(mobile)
                .child(bill_no)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mAdminInctRef.child(convert.getYear())
                            .child(bill_no)
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void setDate() {
        String myFormat = "dd-MMM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        String date = convert.ObjectToString(sdf.format(calendar.getTime()));
        showRecycler(date);
    }

    public static class AdminHistoryDayViewHolder extends RecyclerView.ViewHolder {
        TextView bill_number, inct_amount, name, inct_type, date;

        public AdminHistoryDayViewHolder(@NonNull View itemView) {
            super(itemView);
            bill_number = itemView.findViewById(R.id.employee_history_list_bill_no);
            inct_amount = itemView.findViewById(R.id.employee_history_list_inct);
            name = itemView.findViewById(R.id.employee_history_list_name);
            inct_type = itemView.findViewById(R.id.employee_history_list_type);
            date  = itemView.findViewById(R.id.employee_history_list_date);

        }
    }

    private void validate(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.admin_history_day_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdminInctRef = FirebaseDatabase.getInstance().getReference().child("AdminIncentives");
        mAdminInctRef.keepSynced(true);
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersRef.keepSynced(true);
        mInctRef = FirebaseDatabase.getInstance().getReference().child("Incentives");
        mInctRef.keepSynced(true);

        datePicker = (RelativeLayout) view.findViewById(R.id.admin_history_day_datePicker_layout);
    }

}

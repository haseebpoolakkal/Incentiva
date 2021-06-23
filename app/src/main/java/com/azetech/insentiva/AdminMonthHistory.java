package com.azetech.insentiva;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

public class AdminMonthHistory extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference mAdminInctRef, mInctRef;
    private Spinner month_spinner;

    private RelativeLayout progress;
    private ImageView s1, s2, s3, s4, s5, s6, s7;
    Animation shinAnim;

    Convert convert = new Convert();

    public AdminMonthHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_month_history, container, false);
        validate(view);

        populateSpinner();

        month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String month = convert.ObjectToString(parent.getSelectedItem());
                showRecycler(month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    private void populateSpinner() {
        mAdminInctRef.child(convert.getYear())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            final List<String> monthList = new ArrayList<String>();
                            monthList.add("Select a month");
                            for (DataSnapshot monthShot : dataSnapshot.getChildren()) {
                                String month = convert.ObjectToString(monthShot.child("month").getValue());
                                if (!monthList.contains(month)) {
                                    monthList.add(month);
                                }
                            }

                            ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, monthList);
                            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            month_spinner.setAdapter(typeAdapter);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showRecycler(String month) {
        try {
            Query query = mAdminInctRef.child(convert.getYear()).orderByChild("month").equalTo(month);
            FirebaseRecyclerOptions Option = new FirebaseRecyclerOptions.Builder<incetive_model>()
                    .setQuery(query, incetive_model.class)
                    .build();


            FirebaseRecyclerAdapter<incetive_model, AdminMonthViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<incetive_model, AdminMonthViewHolder>(Option) {
                @Override
                protected void onBindViewHolder(@NonNull AdminMonthViewHolder holder, final int position, @NonNull incetive_model model) {

                    try {
                        final String type = model.getInct_type();
                        final String bill_no = model.getBill_number();
                        final String name = model.getName();
                        String incentive = model.getIncentive();
                        String date = model.getDate();

                        holder.name.setText(name);
                        holder.inct_type.setText(type);
                        holder.bill_number.setText(bill_no);
                        holder.date.setText(date);
                        holder.inct_amount.setText("₹" + incentive);

                        progress.setVisibility(View.GONE);

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
                public AdminMonthViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.employee_incentive_list, viewGroup, false);
                    AdminMonthViewHolder viewHolder = new AdminMonthViewHolder(view);

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
        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = mUserRef.orderByChild("name").equalTo(name);
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

    public static class AdminMonthViewHolder extends RecyclerView.ViewHolder {
        TextView bill_number, inct_amount, name, inct_type, date;

        public AdminMonthViewHolder(@NonNull View itemView) {
            super(itemView);
            bill_number = itemView.findViewById(R.id.employee_history_list_bill_no);
            inct_amount = itemView.findViewById(R.id.employee_history_list_inct);
            name = itemView.findViewById(R.id.employee_history_list_name);
            inct_type = itemView.findViewById(R.id.employee_history_list_type);
            date = itemView.findViewById(R.id.employee_history_list_date);


        }
    }

    private void validate(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.admin_history_month_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        month_spinner = (Spinner) view.findViewById(R.id.admin_history_month_pick_spinner);

        mAdminInctRef = FirebaseDatabase.getInstance().getReference().child("AdminIncentives");
        mAdminInctRef.keepSynced(true);
        mInctRef = FirebaseDatabase.getInstance().getReference().child("Incentives");
        mInctRef.keepSynced(true);

        //Animation
        progress = (RelativeLayout) view.findViewById(R.id.admin_history_month_progress_animation_layout);

        shinAnim = AnimationUtils.loadAnimation(getContext(), R.anim.shine_transform);

        s1 = (ImageView) view.findViewById(R.id.admin_history_month_shine1);
        s2 = (ImageView) view.findViewById(R.id.admin_history_month_shine2);
        s3 = (ImageView) view.findViewById(R.id.admin_history_month_shine3);
        s4 = (ImageView) view.findViewById(R.id.admin_history_month_shine4);
        s5 = (ImageView) view.findViewById(R.id.admin_history_month_shine5);
        s6 = (ImageView) view.findViewById(R.id.admin_history_month_shine6);
        s7 = (ImageView) view.findViewById(R.id.admin_history_month_shine7);

        s1.startAnimation(shinAnim);
        s2.startAnimation(shinAnim);
        s3.startAnimation(shinAnim);
        s4.startAnimation(shinAnim);
        s5.startAnimation(shinAnim);
        s6.startAnimation(shinAnim);
        s7.startAnimation(shinAnim);
    }

}

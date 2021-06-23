package com.azetech.insentiva;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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

public class AdminTodayHistory extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference mAdminInctRef, mInctRef, mUsersRef;

    private RelativeLayout progress;
    private ImageView s1, s2, s3, s4, s5, s6, s7;
    Animation shinAnim;

    Convert convert = new Convert();

    public AdminTodayHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_today_history, container, false);
        validate(view);

        try {


            Query query = mAdminInctRef.child(convert.getYear()).orderByChild("date").equalTo(convert.getDate());
            FirebaseRecyclerOptions Option = new FirebaseRecyclerOptions.Builder<incetive_model>()
                    .setQuery(query, incetive_model.class)
                    .build();


            FirebaseRecyclerAdapter<incetive_model, AdminHistoryTodayViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<incetive_model, AdminHistoryTodayViewHolder>(Option) {
                @Override
                protected void onBindViewHolder(@NonNull AdminHistoryTodayViewHolder holder, final int position, @NonNull incetive_model model) {

                    try {
                        final String name = model.getName();
                        final String type = model.getInct_type();
                        final String bill_no = model.getBill_number();
                        String incentive = model.getIncentive();

                        holder.inct_type.setText(type);
                        holder.bill_number.setText(bill_no);
                        holder.name.setText(name);
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
                public AdminHistoryTodayViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.employee_incentive_list, viewGroup, false);
                    AdminHistoryTodayViewHolder viewHolder = new AdminHistoryTodayViewHolder(view);

                    return viewHolder;
                }
            };

            recyclerView.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }
        return view;
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
                    else{
                        Toast.makeText(getContext(), "Something went wrong, Please try again later.", Toast.LENGTH_SHORT).show();
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
                                    else {
                                        Toast.makeText(getContext(), convert.ObjectToString(task.getException()), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(getContext(), convert.ObjectToString(task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public static class AdminHistoryTodayViewHolder extends RecyclerView.ViewHolder {
        TextView bill_number, inct_amount, name, inct_type;

        public AdminHistoryTodayViewHolder(@NonNull View itemView) {
            super(itemView);
            bill_number = itemView.findViewById(R.id.employee_history_list_bill_no);
            inct_amount = itemView.findViewById(R.id.employee_history_list_inct);
            name = itemView.findViewById(R.id.employee_history_list_name);
            inct_type = itemView.findViewById(R.id.employee_history_list_type);


        }
    }

    private void validate(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.admin_history_today_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdminInctRef = FirebaseDatabase.getInstance().getReference().child("AdminIncentives");
        mAdminInctRef.keepSynced(true);
        mInctRef = FirebaseDatabase.getInstance().getReference().child("Incentives");
        mInctRef.keepSynced(true);
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersRef.keepSynced(true);

        //Animation
        progress = (RelativeLayout) view.findViewById(R.id.admin_today_history_progress_animation_layout);

        shinAnim = AnimationUtils.loadAnimation(getContext(), R.anim.shine_transform);

        s1 = (ImageView) view.findViewById(R.id.admin_today_history_shine1);
        s2 = (ImageView) view.findViewById(R.id.admin_today_history_shine2);
        s3 = (ImageView) view.findViewById(R.id.admin_today_history_shine3);
        s4 = (ImageView) view.findViewById(R.id.admin_today_history_shine4);
        s5 = (ImageView) view.findViewById(R.id.admin_today_history_shine5);
        s6 = (ImageView) view.findViewById(R.id.admin_today_history_shine6);
        s7 = (ImageView) view.findViewById(R.id.admin_today_history_shine7);

        s1.startAnimation(shinAnim);
        s2.startAnimation(shinAnim);
        s3.startAnimation(shinAnim);
        s4.startAnimation(shinAnim);
        s5.startAnimation(shinAnim);
        s6.startAnimation(shinAnim);
        s7.startAnimation(shinAnim);
    }

}

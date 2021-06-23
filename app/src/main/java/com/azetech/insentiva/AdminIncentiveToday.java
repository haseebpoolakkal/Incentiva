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

//Created by Aseeb-P(+91 8606459264).

public class AdminIncentiveToday extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference mInctRef, mAdminInctRef;

    private String mobile;
    private String name;

    private RelativeLayout progress;
    private ImageView s1, s2, s3, s4, s5, s6, s7;
    Animation shinAnim;

    Convert convert = new Convert();

    public AdminIncentiveToday(String mobile, String name) {
        // Required empty public constructor
        this.mobile = mobile;
        this.name = name;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_incentive_today, container, false);
        validate(view);

        try {
            Query query = mInctRef.child(convert.getYear()).child(mobile).orderByChild("date").equalTo(convert.getDate());
            FirebaseRecyclerOptions Option = new FirebaseRecyclerOptions.Builder<incetive_model>()
                    .setQuery(query, incetive_model.class)
                    .build();


            FirebaseRecyclerAdapter<incetive_model, InctTodayViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<incetive_model, InctTodayViewHolder>(Option) {

                @Override
                protected void onBindViewHolder(@NonNull InctTodayViewHolder holder, final int position, @NonNull incetive_model model) {

                    try {
                        final String name = model.getName();
                        final String type = model.getInct_type();
                        final String bill_no = model.getBill_number();
                        String incentive = model.getIncentive();
                        String date = model.getDate();

                        holder.inct_type.setText(type);
                        holder.bill_number.setText(bill_no);
                        holder.date.setText(date);
                        holder.inct_amount.setText("₹"+incentive);

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
                                        deleteBill(mobile, bill_no);
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
                public InctTodayViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.today_incentive_list, viewGroup, false);
                    InctTodayViewHolder viewHolder = new InctTodayViewHolder(view);

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


    private void deleteBill(String mobile, final String bill_no) {
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
                                        Toast.makeText(getContext(), "Failed to delete. Try again later", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    public static class InctTodayViewHolder extends RecyclerView.ViewHolder {
        TextView bill_number, inct_amount, date, inct_type;

        public InctTodayViewHolder(@NonNull View itemView) {
            super(itemView);
            bill_number = itemView.findViewById(R.id.user_inct_list_bill_no);
            inct_amount = itemView.findViewById(R.id.user_inct_list_inct);
            date = itemView.findViewById(R.id.user_inct_list_date);
            inct_type = itemView.findViewById(R.id.user_inct_list_type);


        }
    }

    private void validate(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.inct_today_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mInctRef = FirebaseDatabase.getInstance().getReference().child("Incentives");
        mInctRef.keepSynced(true);
        mAdminInctRef = FirebaseDatabase.getInstance().getReference().child("AdminIncentives");
        mAdminInctRef.keepSynced(true);

        //Animation
        progress = (RelativeLayout) view.findViewById(R.id.inct_today_progress_animation_layout);

        shinAnim = AnimationUtils.loadAnimation(getContext(), R.anim.shine_transform);

        s2 = (ImageView) view.findViewById(R.id.inct_today_shine2);
        s1 = (ImageView) view.findViewById(R.id.inct_today_shine1);
        s3 = (ImageView) view.findViewById(R.id.inct_today_shine3);
        s4 = (ImageView) view.findViewById(R.id.inct_today_shine4);
        s5 = (ImageView) view.findViewById(R.id.inct_today_shine5);
        s6 = (ImageView) view.findViewById(R.id.inct_today_shine6);
        s7 = (ImageView) view.findViewById(R.id.inct_today_shine7);

        s1.startAnimation(shinAnim);
        s2.startAnimation(shinAnim);
        s3.startAnimation(shinAnim);
        s4.startAnimation(shinAnim);
        s5.startAnimation(shinAnim);
        s6.startAnimation(shinAnim);
        s7.startAnimation(shinAnim);
    }

}

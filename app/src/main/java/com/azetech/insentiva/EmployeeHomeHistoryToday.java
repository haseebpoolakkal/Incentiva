package com.azetech.insentiva;


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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

//Created by Aseeb-P(+91 8606459264).
public class EmployeeHomeHistoryToday extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference mInctRef;
    private TextView errorText;

    private String mobile;

    private RelativeLayout progress;
    private ImageView s1, s2, s3, s4, s5, s6, s7;
    Animation shinAnim;

    Convert convert = new Convert();


    public EmployeeHomeHistoryToday(String mobile) {
        // Required empty public constructor
        this.mobile = mobile;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_home_history_today, container, false);

        validate(view);

        checkData();

        try {
            Query query = mInctRef.child(convert.getYear()).child(mobile).orderByChild("date").equalTo(convert.getDate());
            FirebaseRecyclerOptions Option = new FirebaseRecyclerOptions.Builder<incetive_model>()
                    .setQuery(query, incetive_model.class)
                    .build();


            FirebaseRecyclerAdapter<incetive_model, EmpTodayViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<incetive_model, EmpTodayViewHolder>(Option) {
                @Override
                protected void onBindViewHolder(@NonNull EmpTodayViewHolder holder, final int position, @NonNull incetive_model model) {

                    try {
                        final String name = model.getName();
                        final String type = model.getInct_type();
                        String bill_no = model.getBill_number();
                        String incentive = model.getIncentive();
                        String date = model.getDate();

                        holder.inct_type.setText(type);
                        holder.bill_number.setText(bill_no);
                        holder.date.setText(date);
                        holder.inct_amount.setText("₹"+incentive);

                        progress.setVisibility(View.GONE);


                    } catch (NullPointerException n) {
                        Toast.makeText(getContext(), "Null", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                    }

                }

                @NonNull
                @Override
                public EmpTodayViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.today_incentive_list, viewGroup, false);
                    EmpTodayViewHolder viewHolder = new EmpTodayViewHolder(view);

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

    private void checkData() {
        Query query = mInctRef.child(convert.getYear()).child(mobile).orderByChild("date").equalTo(convert.getDate());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    errorText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class EmpTodayViewHolder extends RecyclerView.ViewHolder {
        TextView bill_number, inct_amount, date, inct_type;

        public EmpTodayViewHolder(@NonNull View itemView) {
            super(itemView);
            bill_number = itemView.findViewById(R.id.user_inct_list_bill_no);
            inct_amount = itemView.findViewById(R.id.user_inct_list_inct);
            date = itemView.findViewById(R.id.user_inct_list_date);
            inct_type = itemView.findViewById(R.id.user_inct_list_type);


        }
    }

    private void validate(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.employee_home_history_today_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        errorText = (TextView) view.findViewById(R.id.employee_home_today_history_text);
        errorText.setVisibility(View.GONE);

        mInctRef = FirebaseDatabase.getInstance().getReference().child("Incentives");
        mInctRef.keepSynced(true);

        //Animation
        progress = (RelativeLayout) view.findViewById(R.id.employee_history_today_progress_animation_layout);

        shinAnim = AnimationUtils.loadAnimation(getContext(), R.anim.shine_transform);

        s1 = (ImageView) view.findViewById(R.id.employee_history_today_shine1);
        s2 = (ImageView) view.findViewById(R.id.employee_history_today_shine2);
        s3 = (ImageView) view.findViewById(R.id.employee_history_today_shine3);
        s4 = (ImageView) view.findViewById(R.id.employee_history_today_shine4);
        s5 = (ImageView) view.findViewById(R.id.employee_history_today_shine5);
        s6 = (ImageView) view.findViewById(R.id.employee_history_today_shine6);
        s7 = (ImageView) view.findViewById(R.id.employee_history_today_shine7);

        s1.startAnimation(shinAnim);
        s2.startAnimation(shinAnim);
        s3.startAnimation(shinAnim);
        s4.startAnimation(shinAnim);
        s5.startAnimation(shinAnim);
        s6.startAnimation(shinAnim);
        s7.startAnimation(shinAnim);
    }

}

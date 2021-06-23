package com.azetech.insentiva;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminAttendanceBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private DatabaseReference mAttendanceRef;
    private ImageView closeButton;

    Convert convert = new Convert();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_attendance_bottom_sheet, container, false);
        validate(v);

        showAttendance();
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }


    private void showAttendance() {
        try {

            FirebaseRecyclerOptions Option = new FirebaseRecyclerOptions.Builder<Users>()
                    .setQuery(mAttendanceRef, Users.class)
                    .build();


            FirebaseRecyclerAdapter<Users, AttendanceViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, AttendanceViewHolder>(Option) {
                @SuppressLint("SetTextI18n")
                @Override
                protected void onBindViewHolder(@NonNull AttendanceViewHolder holder, final int position, @NonNull Users model) {

                    try {
                        final String name = model.getName();
                        final String attendance = model.getIsPresent();
                        if (attendance.equals("yes")){
                            holder.attendence.setText("Present");
                            holder.attendence.setTextColor(Color.BLUE);
                        }
                        else {
                            holder.attendence.setText("Absent");
                            holder.attendence.setTextColor(Color.RED);
                        }

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (attendance.equals("yes")){
                                    mAttendanceRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                                snapshot.child("isPresent").getRef().setValue("no");
                                                String value =convert.ObjectToString(snapshot.child("attendance").getValue());
                                                String newValue = convert.subtract(value, convert.IntToString(1));
                                                snapshot.child("attendance").getRef().setValue(newValue);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                else {
                                    mAttendanceRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                                snapshot.child("isPresent").getRef().setValue("yes");
                                                String value =convert.ObjectToString(snapshot.child("attendance").getValue());
                                                String newValue = convert.addition(value, convert.IntToString(1));
                                                snapshot.child("attendance").getRef().setValue(newValue);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        });


                        holder.Name.setText(name);


                    } catch (NullPointerException n) {
                        Toast.makeText(getContext(), n.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                @NonNull
                @Override
                public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.employee_name_list, viewGroup, false);
                    AttendanceViewHolder viewHolder = new AttendanceViewHolder(view);

                    return viewHolder;
                }
            };

            recyclerView.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();

        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {

        TextView Name, attendence;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.employee_list_name);
            attendence = itemView.findViewById(R.id.employee_list_absent);


        }
    }

    private void validate(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.attendance_bottom_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        closeButton = (ImageView) v.findViewById(R.id.attendance_bottom_close);

        mAttendanceRef = FirebaseDatabase.getInstance().getReference().child("PayRoll");
        mAttendanceRef.keepSynced(true);
    }
}

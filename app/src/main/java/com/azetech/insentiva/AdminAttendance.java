package com.azetech.insentiva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminAttendance extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button nextButton;
    private DatabaseReference mPayRollRef;

    String mobile, who, from;

    Convert convert = new Convert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance);
        validate();

        mobile = getIntent().getStringExtra("mobile");

        who = getIntent().getStringExtra("who");
        from = getIntent().getStringExtra("from");

        showEmployee();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttended();
                Intent intent = new Intent(AdminAttendance.this, AdminHome.class);
                intent.putExtra("mobile", "");
                intent.putExtra("who", "admin");
                intent.putExtra("from", "home");
                startActivity(intent);
                finish();
            }
        });
    }

    private void setAttended() {
        SharedPreferences prefs = getSharedPreferences("attendance", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("date", convert.getDate());
        editor.commit();
    }

    private void showEmployee() {
        try {

            FirebaseRecyclerOptions Option = new FirebaseRecyclerOptions.Builder<Users>()
                    .setQuery(mPayRollRef, Users.class)
                    .build();


            FirebaseRecyclerAdapter<Users, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(Option) {
                @SuppressLint("SetTextI18n")
                @Override
                protected void onBindViewHolder(@NonNull UserViewHolder holder, final int position, @NonNull Users model) {

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
                                    mPayRollRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    mPayRollRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }

                }

                @NonNull
                @Override
                public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.employee_name_list, viewGroup, false);
                    UserViewHolder viewHolder = new UserViewHolder(view);

                    return viewHolder;
                }
            };

            recyclerView.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView Name, attendence;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.employee_list_name);
            attendence = itemView.findViewById(R.id.employee_list_absent);


        }
    }

    private void validate() {
        recyclerView = (RecyclerView) findViewById(R.id.attendance_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        nextButton = (Button) findViewById(R.id.attendance_button);

        mPayRollRef = FirebaseDatabase.getInstance().getReference().child("PayRoll");
        mPayRollRef.keepSynced(true);
    }
}

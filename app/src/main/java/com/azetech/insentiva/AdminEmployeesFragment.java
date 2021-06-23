package com.azetech.insentiva;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class AdminEmployeesFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference mUsersRef;
    EmployeeListAdapter adapter;

    private RelativeLayout progress;
    private ImageView s1, s2, s3, s4, s5, s6, s7;
    Animation shinAnim;

    Convert convert = new Convert();

    public AdminEmployeesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_employees_list, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.users_toolbar);
        toolbar.setTitle("Employee");
        toolbar.setTitleTextColor(Color.WHITE);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        validate(view);
        populateSearch();

        return view;
    }

    private void populateSearch() {
        final HashMap employeeList = new HashMap();
        final HashMap mobileList = new HashMap();
        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    String name = convert.ObjectToString(shot.child("name").getValue());
                    String salary = convert.ObjectToString(shot.child("salary").getValue());
                    String mobile = convert.ObjectToString(shot.child("mobile").getValue());
                    if (!employeeList.containsKey(name)) {
                        employeeList.put(name, salary);
                        mobileList.put(name, mobile);
                    }
                }

                if (!employeeList.isEmpty()){
                    progress.setVisibility(View.GONE);
                    adapter = new EmployeeListAdapter(employeeList, mobileList, getActivity());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_item);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void validate(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.home_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersRef.keepSynced(true);

        //Animation
        progress = (RelativeLayout) view.findViewById(R.id.employee_progress_animation_layout);

        shinAnim = AnimationUtils.loadAnimation(getContext(), R.anim.shine_transform);

        s2 = (ImageView) view.findViewById(R.id.employee_shine2);
        s1 = (ImageView) view.findViewById(R.id.employee_shine1);
        s3 = (ImageView) view.findViewById(R.id.employee_shine3);
        s4 = (ImageView) view.findViewById(R.id.employee_shine4);
        s5 = (ImageView) view.findViewById(R.id.employee_shine5);
        s6 = (ImageView) view.findViewById(R.id.employee_shine6);
        s7 = (ImageView) view.findViewById(R.id.employee_shine7);

        s1.startAnimation(shinAnim);
        s2.startAnimation(shinAnim);
        s3.startAnimation(shinAnim);
        s4.startAnimation(shinAnim);
        s5.startAnimation(shinAnim);
        s6.startAnimation(shinAnim);
        s7.startAnimation(shinAnim);
    }

}

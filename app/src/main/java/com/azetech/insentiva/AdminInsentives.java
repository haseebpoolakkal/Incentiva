package com.azetech.insentiva;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminInsentives extends AppCompatActivity {

    private DatabaseReference mInctRef;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    String name, mobile, salary, who, from;

    Convert convert = new Convert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_insentives);
        name = getIntent().getStringExtra("name");
        mobile = getIntent().getStringExtra("mobile");
        salary = getIntent().getStringExtra("salary");
        from = getIntent().getStringExtra("from");
        who = getIntent().getStringExtra("who");

        validate();

        showHistory(mobile, name, salary);

        FloatingActionButton fab = findViewById(R.id.inst_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminAddInsentive.class);
                intent.putExtra("name", name);
                intent.putExtra("mobile", mobile);
                intent.putExtra("who", "admin");
                intent.putExtra("from", "employee");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminInsentives.this, AdminHome.class);
        intent.putExtra("mobile", mobile);
        intent.putExtra("who", "admin");
        intent.putExtra("from", "employee");
        startActivity(intent);
        finish();
    }

    private void showHistory(final String mobile, final String name, final String salary) {
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0 :
                        AdminEmployeePayRoll f1 = new AdminEmployeePayRoll(mobile, name, salary);
                        return f1;
                    case 1:
                        AdminIncentiveToday f2 = new AdminIncentiveToday(mobile, name);
                        return f2;
                    case 2 :
                        AdminIncentiveMonth f3 = new AdminIncentiveMonth(mobile, name);
                        return f3;
                    default: return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };

        viewPager.setAdapter(adapter);
    }


    private void validate(){
        viewPager = (ViewPager) findViewById(R.id.inct_viewPager);
        tabLayout = (TabLayout) findViewById(R.id.inct_tab_layout);

        mInctRef = FirebaseDatabase.getInstance().getReference().child("Incentives");
        mInctRef.keepSynced(true);
    }
}

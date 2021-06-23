package com.azetech.insentiva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmployeeHome extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private BottomNavigationView bottomNavigationView;
    private String mobile, from, who;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);

        validate();

        mobile = getIntent().getStringExtra("mobile");

        who = getIntent().getStringExtra("who");
        from = getIntent().getStringExtra("from");

        if (who.equals("employee")){
            if (from.equals("settings")){

                loadEmployeeFragment(new EmployeeSettingsFragment(mobile));
            }
            else {

                loadEmployeeFragment(new EmployeeHomeFragment(mobile));
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) this);


    }

    private boolean loadEmployeeFragment(Fragment fragment1) {
        //switching fragment
        if (fragment1 != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.employee_container, fragment1)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment1 = null;

        switch (menuItem.getItemId()) {
            case R.id.employee_navigation_home:
                fragment1 = new EmployeeHomeFragment(mobile);
                break;

            case R.id.employee_navigation_history:
                fragment1 = new EmployeeHistoryFragment(mobile);
                break;

            case R.id.employee_navigation_settings:
                fragment1 = new EmployeeSettingsFragment(mobile);
                break;
        }
        return loadEmployeeFragment(fragment1);
    }

    private void validate() {
        bottomNavigationView = findViewById(R.id.employee_bottom_container);
    }
}

package com.azetech.insentiva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHome extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    String mobile, who, from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        mobile = getIntent().getStringExtra("mobile");
        who = getIntent().getStringExtra("who");
        from = getIntent().getStringExtra("from");

        validate();

        if (from.equals("home")) {
            loadFragment(new AdminHomeFragment());
        }
        else if (from.equals("employee")){
            loadFragment(new AdminEmployeesFragment());
        }
        else if (from.equals("settings")){
            loadFragment(new AdminSettingFragment());
        }
        else {
            loadFragment(new AdminHomeFragment());
        }
    }

    private void validate() {
        BottomNavigationView bottom_view = findViewById(R.id.bottom_container);
        bottom_view.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) this);
        if (from.equals("home")) {
            bottom_view.setSelectedItemId(R.id.navigation_home);
        }
        else if (from.equals("employee")){
            bottom_view.setSelectedItemId(R.id.navigation_employee);
        }
        else if (from.equals("settings")){
            bottom_view.setSelectedItemId(R.id.navigation_settings);
        }
        else if (from.equals("history")){
            bottom_view.setSelectedItemId(R.id.navigation_history);
        }
        else {
            bottom_view.setSelectedItemId(R.id.navigation_home);
        }
    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                fragment = new AdminHomeFragment();
                break;

            case R.id.navigation_history:
                fragment = new AdminHistory();
                break;

            case R.id.navigation_employee:
                fragment = new AdminEmployeesFragment();
                break;

            case R.id.navigation_settings:
                fragment = new AdminSettingFragment();
                break;
        }
        return loadFragment(fragment);
    }
}

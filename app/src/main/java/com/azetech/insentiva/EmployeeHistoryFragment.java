package com.azetech.insentiva;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;


public class EmployeeHistoryFragment extends Fragment {

    private String mobile;

    private ViewPager viewPager1;
    private TabLayout tabLayout1;
    private TextView heading;
    private RelativeLayout rightSwipe, leftSwipe;
    private Animation rightAnim, leftAnim;

    public EmployeeHistoryFragment(String mobile) {
        // Required empty public constructor
        this.mobile = mobile;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_history, container, false);
        validate(view);

        rightSwipe.startAnimation(rightAnim);

        viewPager1.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout1));
        tabLayout1.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager1));

        tabLayout1.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabpos = tab.getPosition();
                if (tabpos == 0) {
                    heading.setText(getText(R.string.today));
                    leftSwipe.clearAnimation();
                    leftSwipe.setVisibility(View.GONE);
                    rightSwipe.startAnimation(rightAnim);
                    rightSwipe.setVisibility(View.VISIBLE);
                } else if (tabpos == 1) {
                    heading.setText(getText(R.string.month));
                    rightSwipe.clearAnimation();
                    rightSwipe.setVisibility(View.GONE);
                    leftSwipe.setVisibility(View.VISIBLE);
                    leftSwipe.startAnimation(leftAnim);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        EmployeeHomeHistoryToday f1 = new EmployeeHomeHistoryToday(mobile);
                        return f1;
                    case 1:
                        EmployeeHomeHistoryMonth f2 = new EmployeeHomeHistoryMonth(mobile);
                        return f2;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        viewPager1.setAdapter(adapter);

        return view;
    }

    private void validate(View view) {
        viewPager1 = view.findViewById(R.id.employee_history_viewPager);

        tabLayout1 = view.findViewById(R.id.employee_history_Tab_layout);

        heading = (TextView) view.findViewById(R.id.employee_history_heading);
        heading.setText(getText(R.string.today));
        rightSwipe = (RelativeLayout) view.findViewById(R.id.employee_history_right_swipe_layout);
        rightAnim = AnimationUtils.loadAnimation(getContext(), R.anim.swipe_right);
        leftSwipe = (RelativeLayout) view.findViewById(R.id.employee_history_left_swipe_layout);
        leftSwipe.setVisibility(View.GONE);
        leftAnim = AnimationUtils.loadAnimation(getContext(), R.anim.swipe_left);
    }

}

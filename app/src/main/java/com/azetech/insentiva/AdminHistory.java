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

public class AdminHistory extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView heading;
    private RelativeLayout rightSwipe, leftSwipe;
    private Animation rightAnim, leftAnim;

    Convert convert = new Convert();

    public AdminHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_history, container, false);
        validate(view);

        rightSwipe.startAnimation(rightAnim);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabpos = tab.getPosition();
                if (tabpos == 0) {
                    heading.setText(getText(R.string.today));
                    leftSwipe.clearAnimation();
                    leftSwipe.setVisibility(View.GONE);
                } else if (tabpos == 1) {
                    heading.setText(getText(R.string.month));
                    leftSwipe.setVisibility(View.VISIBLE);
                    leftSwipe.startAnimation(leftAnim);
                    rightSwipe.startAnimation(rightAnim);
                    rightSwipe.setVisibility(View.VISIBLE);
                } else if (tabpos == 2){
                    heading.setText(getText(R.string.datePicker));
                    rightSwipe.clearAnimation();
                    rightSwipe.setVisibility(View.GONE);
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
                        AdminTodayHistory f1 = new AdminTodayHistory();
                        return f1;
                    case 1:
                        AdminMonthHistory f2 = new AdminMonthHistory();
                        return f2;
                    case 2:
                        AdminDayHistory f3 = new AdminDayHistory();
                        return f3;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };

        viewPager.setAdapter(adapter);

        return view;
    }

    private void validate(View view) {
        viewPager = view.findViewById(R.id.admin_history_viewPager);

        tabLayout = view.findViewById(R.id.admin_history_Tab_layout);

        heading = (TextView) view.findViewById(R.id.admin_history_heading);
        heading.setText(getText(R.string.today));
        rightSwipe = (RelativeLayout) view.findViewById(R.id.admin_home_history_right_swipe_layout);
        rightAnim = AnimationUtils.loadAnimation(getContext(), R.anim.swipe_right);
        leftSwipe = (RelativeLayout) view.findViewById(R.id.admin_home_history_left_swipe_layout);
        leftSwipe.setVisibility(View.GONE);
        leftAnim = AnimationUtils.loadAnimation(getContext(), R.anim.swipe_left);
    }

}

package com.affable.smartbills.report;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.affable.smartbills.R;
import com.affable.smartbills.invoice.adapter.ViewPagerAdapter;
import com.affable.smartbills.invoice.order_fragments.CanceledOrderFragment;
import com.affable.smartbills.invoice.order_fragments.CompletedOrderFragment;
import com.affable.smartbills.invoice.order_fragments.PendingOrderFragment;
import com.affable.smartbills.report.report_fragments.ExpenseReportFragment;
import com.affable.smartbills.report.report_fragments.SalesReportFragment;
import com.google.android.material.tabs.TabLayout;

public class ReportsFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find views by id
        ViewPager2 viewPager = view.findViewById(R.id.viewpager);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);

        // attach tablayout with viewpager

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), getLifecycle());

        // add your fragments
        adapter.addFrag(new SalesReportFragment());
        adapter.addFrag(new ExpenseReportFragment());

        // set adapter on viewpager
        viewPager.setAdapter(adapter);
        tabLayout.addTab(tabLayout.newTab().setText("Sales Report"));
        tabLayout.addTab(tabLayout.newTab().setText("Expense Report"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //type your code here
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //type your code here
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }

}
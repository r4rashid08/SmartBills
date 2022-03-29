package com.affable.smartbills.report.report_fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.affable.smartbills.R;
import com.affable.smartbills.clients.AddClientActivity;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.retrunGoods.ReturnAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.twigsntwines.daterangepicker.DatePickerSpinner;
import com.twigsntwines.daterangepicker.DateRangePickedListener;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class SalesReportFragment extends Fragment {


    private int mFyear = 2021,mFmonth =1,mFday =1, mEyear = 2021,mEmonth =1,mEday =1;

    private TextView txtTotalSales;
    private DatabaseAccess databaseAccess;
    private RecyclerView recyclerView;
    private Button mStartDate,mEndDate,mSearchButton;
    private AutoCompleteTextView mSpinner;
    private List<HashMap<String, String>> itemsList;
    private ReportAdapter adapter;
    private View noDataIndicator;
    private LinearLayout mView;
    private float totalSales;

    DecimalFormat f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_vat_info, container, false);

        f = new DecimalFormat("#0.00");



        txtTotalSales = view.findViewById(R.id.report_price);
        mStartDate = view.findViewById(R.id.report_start_date);
        mView = view.findViewById(R.id.report_cart_view);
        mSearchButton = view.findViewById(R.id.report_button);
        mSpinner = view. findViewById(R.id.report_text);
        noDataIndicator = view.findViewById(R.id.no_data);
        recyclerView = view.findViewById(R.id.report_recyclerView);
        mEndDate = view.findViewById(R.id.report_end_date);



        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        databaseAccess = DatabaseAccess.getInstance(getContext());

        Calendar calendar = Calendar.getInstance();
        mFday = calendar.get(Calendar.DAY_OF_MONTH);
        mFyear = calendar.get(Calendar.YEAR);
        mFmonth = calendar.get(Calendar.MONTH)+1;

        mEday = calendar.get(Calendar.DAY_OF_MONTH);
        mEyear = calendar.get(Calendar.YEAR);
        mEmonth = calendar.get(Calendar.MONTH)+1;

    //    mStartDate.setText(mDay+"-"+mMonth+"-"+mYear);



        return view;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mEndDate.setOnClickListener(v -> {


            Calendar c=Calendar.getInstance();
            Integer month=c.get(Calendar.MONTH);
            Integer day=c.get(Calendar.DAY_OF_MONTH);
            Integer year=c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog =new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    mEday = dayOfMonth;
                    mEmonth = month+1;
                    mEyear = year;

                    mEndDate.setText(mEday+"-"+mEmonth+"-"+mEyear);
                }
            },year,month,day);
            datePickerDialog.show();





        });


        mStartDate.setOnClickListener(v -> {


            Calendar c=Calendar.getInstance();
            Integer month=c.get(Calendar.MONTH);
            Integer day=c.get(Calendar.DAY_OF_MONTH);
            Integer year=c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog =new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    mFday = dayOfMonth;
                    mFmonth = month+1;
                    mFyear = year;

                    mStartDate.setText(mFday+"-"+mFmonth+"-"+mFyear);
                }
            },year,month,day);
            datePickerDialog.show();





        });



        mSearchButton.setOnClickListener(v -> {


            String userType = mSpinner.getText().toString().toLowerCase().trim();

            getQuery(userType);

        });


        ArrayList<String> customerList = getCustomerList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, customerList);

        mSpinner.setAdapter(adapter);

        mSpinner.setOnTouchListener((v, event) -> {
            InputMethodManager in = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        });





    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void getQuery(String userType) {


        Log.d("day", "first month : "+mFmonth );
        Log.d("day", "first year : "+mFyear );
        Log.d("day", userType);


        int v = String.valueOf(mFmonth).length();
        String fmonth = "";
        if(v ==1){
             fmonth = "0"+mFmonth;
        }else {
            fmonth = mFmonth+"";
        }

        int v2 = String.valueOf(mEmonth).length();
        String emonth = "";
        if(v2 ==1){
             emonth = "0"+mEmonth;
        }else {
            emonth = mEmonth+"";
        }
        Log.d("day", "final Month : "+emonth );

        databaseAccess.open();

        itemsList =    databaseAccess.getReportInfo(fmonth,String.valueOf(mFyear),String.valueOf(mFday),emonth,String.valueOf(mEyear),String.valueOf(mEday),userType);


        Log.i("data", "" + itemsList.size());

        if (itemsList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noDataIndicator.setVisibility(View.VISIBLE);
        } else {

            double total_vat =0, total_amount=0;
            for(int i = 0; i<itemsList.size() ; i++){

                String vat  =  itemsList.get(i).get("tax");
                String total  =  itemsList.get(i).get("total_price");

                assert vat != null;
                double b =  Double.parseDouble(vat);
                   total_vat = total_vat + b;

                assert total != null;
                double d =  Double.parseDouble(total);
                   total_amount = total_amount + d;
            }




            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
                mView.setVisibility(View.VISIBLE);
                txtTotalSales.setVisibility(View.VISIBLE);

            }
            if (noDataIndicator.getVisibility() == View.VISIBLE) {
                noDataIndicator.setVisibility(View.GONE);
                mView.setVisibility(View.GONE);
                txtTotalSales.setVisibility(View.GONE);
            }
            mView.setVisibility(View.VISIBLE);
            txtTotalSales.setVisibility(View.VISIBLE);

            txtTotalSales.setText( "Total Amount :"+total_amount + "\n Total vat : "+ total_vat);
            adapter = new ReportAdapter(getContext(), itemsList, recyclerView,noDataIndicator);
            recyclerView.setAdapter(adapter);

            adapter.notifyDataSetChanged();

        }
       //   Toast.makeText(getContext(), "Already items are return against this invoice", Toast.LENGTH_SHORT).show();

    }





    private ArrayList<String> getCustomerList()
    {
        ArrayList<String> customers = new ArrayList<>();
       // customers.add("VAT");
        customers.add("SALE");
        customers.add("PURCHASE");
        customers.add("PURCHASE_RETURN");
        customers.add("SALE_RETURN");
        return customers;
    }


}
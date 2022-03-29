package com.affable.smartbills.retrunGoods;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.Inventory.AddInventoryActivity;
import com.affable.smartbills.R;
import com.affable.smartbills.cart.CartActivity;
import com.affable.smartbills.clients.AddClientActivity;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.database.DatabaseOpenHelper;
import com.affable.smartbills.utils.Constant;
import com.ajts.androidmads.library.SQLiteToExcel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;


public class ReturnFragment extends Fragment {


    private RecyclerView recyclerView;
    private ReturnAdapter adapter;
    private List<HashMap<String, String>> itemsList;
    private ProgressDialog loading;
    private boolean clicked = true;

    private TextView  textCount;
    private FloatingActionButton fab;

    private View noDataIndicator;
    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private EditText mTextView;
    private Button searchButton;
    public static  String   type = "", mCInvoiceId="emp";
    private DatabaseAccess databaseAccess;


    public ReturnFragment(String localTYpe) {
        type = localTYpe;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retrun, container, false);


        databaseAccess = DatabaseAccess.getInstance(getContext());


        //instantiating UI components
         initView(view);

         databaseAccess.open();
         databaseAccess.clearAllItemFromCart(type);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);



        rotateOpen =  AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_close_anim);
        fromBottom =  AnimationUtils.loadAnimation(getContext(), R.anim.fab_from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getContext(), R.anim.fab_to_bottom_anim);








        //set recyclerview



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getItemsDataFromDB();
            }
        });

        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), CartActivity.class)
                .putExtra("type", type)));


      //  setSearchOption();

        return view;
    }

    private void initView(View view) {

        recyclerView = view.findViewById(R.id.retrun_recyclerView);

        mTextView = view.findViewById(R.id.return_invoice_number);
        searchButton = view.findViewById(R.id.return_search);
        noDataIndicator = view.findViewById(R.id.no_data);
        textCount = view.findViewById(R.id.retrun_text_count);
        fab = view.findViewById(R.id.return_fab);

    }

    @Override
    public void onStart() {
        super.onStart();
        //show cart items count
        databaseAccess.open();
        int count = databaseAccess.getCartItemCount(type);
        if (count == 0) {
            textCount.setVisibility(View.INVISIBLE);
        } else {
            textCount.setVisibility(View.VISIBLE);
            textCount.setText(String.valueOf(count));
        }
    }

    private void getItemsDataFromDB() {

         mCInvoiceId = mTextView.getText().toString();
        String v ="";
        if(type.equals("purchase_return")){

             v = "purchase";
        }else if(type.equals("sale_return")){
            v = "sale";
        }

        if (mCInvoiceId.equals("emp")) {
            mTextView.setError(getString(R.string.item_name_cannot_be_empty));
            mTextView.requestFocus();
        }else {

            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getContext());
            databaseAccess.open();

            int count = 0;
            String status = "emp";


            status = databaseAccess.getOderStatus(mCInvoiceId);
            databaseAccess.open();
            count = databaseAccess.isReturn(mCInvoiceId, type);

          /*  if (type.equals("purchase_return")) {
                count = databaseAccess.isReturn(mCInvoiceId, type);

            } else if (type.equals("sale_return")) {


            }*/

            //get data from local database

            databaseAccess.open();

            if (count == 0 && status.equals(Constant.COMPLETED)) {
                itemsList = databaseAccess.getOrderDetailsList(mCInvoiceId, v);

                Log.i("data", "" + itemsList.size());

                if (itemsList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    noDataIndicator.setVisibility(View.VISIBLE);
                } else {

                    if (recyclerView.getVisibility() == View.GONE) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    if (noDataIndicator.getVisibility() == View.VISIBLE) {
                        noDataIndicator.setVisibility(View.GONE);
                    }

                    adapter = new ReturnAdapter(getContext(), itemsList, recyclerView, type, textCount);
                    recyclerView.setAdapter(adapter);

                }
            }else {
                recyclerView.setVisibility(View.GONE);
                noDataIndicator.setVisibility(View.VISIBLE);
             //   Toast.makeText(getContext(), "Already items are return against this invoice", Toast.LENGTH_SHORT).show();

            }
        }

    }



}
package com.affable.smartbills.cart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.MainActivity;
import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.retrunGoods.ReturnFragment;
import com.affable.smartbills.utils.Admob;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Constant;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class CartActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private LinearLayout no_data;
    private Button btn_submit_order;
    private TextView txt_total_price;
    private CartItemAdapter adapter;
    Double   calculate_discount = 0.0;

    private DatabaseAccess databaseAccess;
    private ArrayList<String> customerNameList, orderTypesList, paymentMethodList;
    private double calculated_total_cost, due_money;
    private String  type;

     double calculated_tax=0.0;

     AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        type = getIntent().getStringExtra("type");

        switch (type) {
            case "sale":
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.sale_cart);
                break;
            case "purchase":
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.pur_cart);
                break;
            case "sale_return":
                getSupportActionBar().setTitle(R.string.nav_drawer_menu_sale_return);
                break;
            case "purchase_return":
                getSupportActionBar().setTitle(R.string.nav_drawer_menu_purchase_return);

                break;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        databaseAccess = DatabaseAccess.getInstance(CartActivity.this);

        recyclerView = findViewById(R.id.recyclerView);
        no_data = findViewById(R.id.no_data);
        btn_submit_order = findViewById(R.id.btn_submit_order);
        txt_total_price = findViewById(R.id.txt_sub_total);


        MobileAds.initialize(CartActivity.this, initializationStatus -> {});


        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("ABCDEF012345"))
                        .build());

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
          adView = findViewById(R.id.adView);

        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d("error_add", "google banner ad loaded"); }
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {Log.d("error_add", "google banner ad failed to load : "+ adError.getMessage());}
            @Override
            public void onAdOpened() {Log.d("error_add", "google banner ad opened");}
            @Override
            public void onAdClicked() {Log.d("error_add", "google banner ad clicked");}
            @Override
            public void onAdClosed() {Log.d("error_add", "google banner ad closed");}
        });

        adView.loadAd(adRequest);




        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        //get data from local database
        databaseAccess.open();
        List<HashMap<String, String>> cartItemsList = databaseAccess.getCartItems(type);

        if (cartItemsList.size() <= 0) {
            no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            no_data.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            btn_submit_order.setVisibility(View.VISIBLE);
            txt_total_price.setVisibility(View.VISIBLE);

            adapter = new CartItemAdapter(CartActivity.this, cartItemsList,
                    txt_total_price, btn_submit_order, no_data, recyclerView,type);

            recyclerView.setAdapter(adapter);

        }

        btn_submit_order.setOnClickListener(v -> {

            //show confirmation dialog
            showDialog();

        });


    }

    @SuppressLint("SetTextI18n")
    private void showDialog() {

        DecimalFormat f = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
        f.applyPattern("#0.00");

        //create custom dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        //init dialog views
        final Button dialog_btn_submit = dialogView.findViewById(R.id.btn_submit);
        final ImageButton dialog_btn_close = dialogView.findViewById(R.id.btn_close);

        final TextView dialog_customer = dialogView.findViewById(R.id.dialog_customer);
         if(type.equals("sale") || type.equals("sale_return") ){
             dialog_customer.setHint("select Customer");
         }else if(type.equals("purchase") || type.equals("purchase_return")){
             dialog_customer.setHint("select Supplier");
         }
        final TextView dialog_order_type = dialogView.findViewById(R.id.dialog_order_type);
        final TextView dialog_order_payment_method = dialogView.findViewById(R.id.dialog_order_status);

        final TextView dialog_txt_total = dialogView.findViewById(R.id.dialog_txt_total);
        final TextView dialog_txt_total_tax = dialogView.findViewById(R.id.dialog_txt_total_tax);
        final TextView dialog_txt_level_tax = dialogView.findViewById(R.id.dialog_level_tax);
        final TextView dialog_txt_total_cost = dialogView.findViewById(R.id.dialog_txt_total_cost);
        final TextView dialog_txt_return_discount = dialogView.findViewById(R.id.etxt_dialog_return_discount);
        final EditText dialog_etxt_discount = dialogView.findViewById(R.id.etxt_dialog_discount);
        final LinearLayout dialog_discount_view = dialogView.findViewById(R.id.dialog_discount_view);
        final LinearLayout dialog_discount_given_view = dialogView.findViewById(R.id.dialog_discount_return_view);

        final EditText etxt_paid_amount = dialogView.findViewById(R.id.etxt_paid_amount);
        final TextView txt_due_amount = dialogView.findViewById(R.id.txt_due_amount);


        final ImageButton dialog_img_customer = dialogView.findViewById(R.id.img_select_customer);
        final ImageButton dialog_img_order_payment_method = dialogView.findViewById(R.id.img_order_payment_method);
        final ImageButton dialog_img_order_type = dialogView.findViewById(R.id.img_order_type);
        final TextView dialog_net_price = dialogView.findViewById(R.id.dialog_txt_net_total_cost);


        //submit won't available until paid money set
        dialog_btn_submit.setVisibility(View.INVISIBLE);


        //get data from local database
        //TODO: if multiple users added get user info with user id

        databaseAccess.open();
        HashMap<String, String> userInfo = databaseAccess.getUserInformation();

        String currency = userInfo.get("currency");

        String tax = userInfo.get("tax");
        assert tax != null;
        double getTax = Double.parseDouble(tax);

        double total_cost = CartItemAdapter.totalPrice;

         calculated_tax    = (total_cost * getTax) / 100.0;


        calculated_total_cost = (total_cost /*+ calculated_tax*/);


        //set data
        dialog_txt_level_tax.setText(getString(R.string.total_tax) + "( " + tax + "%) : ");

        dialog_txt_total.setText(f.format(total_cost) + currency);

        dialog_txt_total_tax.setText(f.format(calculated_tax) + currency);

        dialog_net_price.setText( f.format(calculated_total_cost) + currency);

        calculated_total_cost = calculated_total_cost +calculated_tax;

        dialog_txt_total_cost.setText(f.format(calculated_total_cost) + currency);

        txt_due_amount.setText(f.format(calculated_total_cost) + currency);




        //discount edit text
        dialog_etxt_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //your code here

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                calculated_total_cost = (total_cost);

                String get_discount = s.toString();
                if (get_discount.isEmpty()) {
                    get_discount = "0";
                } else if (get_discount.equals(".")) {
                    get_discount = "0.";
                }
                 calculate_discount  = Double.parseDouble(get_discount);
                if (calculate_discount > calculated_total_cost) {
                    dialog_etxt_discount.setError(getString(R.string.discount_cant_be_greater_than_total_price));
                    dialog_etxt_discount.requestFocus();
                } else {
                    //change total cost on discount change
                    calculated_total_cost = calculated_total_cost - calculate_discount;
                    Double cost = calculated_total_cost;
                    calculated_tax = (cost * getTax) / 100.0;

                    calculated_total_cost= calculated_total_cost +calculated_tax;
                    dialog_txt_total_cost.setText(f.format(calculated_total_cost) + currency);
                    dialog_net_price.setText(f.format(cost) + currency);
                    dialog_txt_total_tax.setText(f.format(calculated_tax)+ currency);

                }

                String get_paid = etxt_paid_amount.getText().toString();
                if (get_paid.isEmpty()) {
                    get_paid = "0";
                } else if (get_paid.equals(".")) {
                    get_paid = "0.";
                }
                double paid = Double.parseDouble(get_paid);




                //change due on discount change
                due_money = calculated_total_cost - paid;
                txt_due_amount.setText( f.format(due_money)+ currency );


            }

            @Override
            public void afterTextChanged(Editable s) {
                //your code here
            }
        });

        //paid money edit text
        etxt_paid_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //add your code here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                calculated_total_cost = (total_cost );

                String get_paid = s.toString();
                if (get_paid.isEmpty()) {
                    get_paid = "0";
                } else if (get_paid.equals(".")) {
                    get_paid = "0.";
                }
                double paid_amount = Double.parseDouble(get_paid);

                String get_discount = dialog_etxt_discount.getText().toString();
                if (get_discount.isEmpty()) {
                    get_discount = "0";
                } else if (get_discount.equals(".")) {
                    get_discount = "0.";
                }

                double calculate_discount = Double.parseDouble(get_discount);

                calculated_total_cost = calculated_total_cost - calculate_discount;

                Double cost = calculated_total_cost;

                calculated_tax = (cost * getTax) / 100.0;

                calculated_total_cost= calculated_total_cost +calculated_tax;

                if (paid_amount > calculated_total_cost) {
                    etxt_paid_amount.setError(getString(R.string.due_cant_be_greater_than_total_price));
                    etxt_paid_amount.requestFocus();
                } else {

                    //change due on discount change

                    due_money = calculated_total_cost - paid_amount;
                    txt_due_amount.setText(f.format(due_money) + currency);

                    dialog_txt_total_cost.setText(f.format(calculated_total_cost) + currency);
                    dialog_net_price.setText(f.format(cost) + currency);
                    dialog_txt_total_tax.setText(f.format(calculated_tax)+ currency);

                }

                //change total cost
                dialog_txt_total_cost.setText(f.format(calculated_total_cost) + currency);
                dialog_btn_submit.setVisibility(View.VISIBLE);


            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });


        //get customer name list
        databaseAccess.open();

        switch (type) {
            case "sale":
                customerNameList = databaseAccess.getClientsName("CUSTOMER");

                break;
            case "purchase":
                customerNameList = databaseAccess.getClientsName("SUPPLIER");

                break;
            case "sale_return":
            case "purchase_return":

                String invoiceId = ReturnFragment.mCInvoiceId;
                String rtype = "";
                if (type.equals("sale_return")) {
                    rtype = "sale";
                } else if (type.equals("purchase_return")) {
                    rtype = "purchase";
                }
                List<HashMap<String, String>> orderList = databaseAccess.searchOrder(invoiceId, Constant.COMPLETED, rtype);

                String cusName = orderList.get(0).get("customer_name");
                String order_type = orderList.get(0).get("order_type");
                String payment_method = orderList.get(0).get("payment_method");
                String discount = orderList.get(0).get("discount");

                dialog_customer.setText(cusName);
                dialog_order_type.setText(order_type);
                dialog_order_payment_method.setText(payment_method);
                dialog_img_order_type.setVisibility(View.GONE);
                dialog_img_customer.setVisibility(View.GONE);
                dialog_img_order_payment_method.setVisibility(View.GONE);

                dialog_discount_given_view.setVisibility(View.VISIBLE);
                dialog_txt_return_discount.setText(discount);
               // dialog_discount_view.setVisibility(View.GONE);


                break;
        }

        //show selectCustomer Dialog

            dialog_img_customer.setOnClickListener(v -> selectCustomerDialog(dialog_customer, type));

        //get payment method list
        databaseAccess.open();
        paymentMethodList = databaseAccess.getPaymentMethodNames();
        //show payment method selection dialog
        dialog_img_order_payment_method.setOnClickListener(v -> selectPaymentMethodDialog(dialog_order_payment_method));


        //get orderTypes
        databaseAccess.open();
        orderTypesList = databaseAccess.getOrderTypeNames();
        //show order type dialog
        dialog_img_order_type.setOnClickListener(v -> selectOrderTypesDialog(dialog_order_type));


        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();


        //submit button
        dialog_btn_submit.setOnClickListener(v -> {

            String order_type = dialog_order_type.getText().toString().trim();
            String order_payment_method = dialog_order_payment_method.getText().toString().trim();
            String customer_name = dialog_customer.getText().toString().trim();

            String discount = dialog_etxt_discount.getText().toString().trim();
            if (discount.isEmpty()||discount.equals(".")) {
                discount = "0.00";
            }

            String paid_amount = etxt_paid_amount.getText().toString().trim();
            if (paid_amount.isEmpty() || paid_amount.equals("0.00")||paid_amount.equals("0")) {
                paid_amount = "0.00";
            }

            if(type.equals("sale_return") || type.equals("purchase_return")){
                if ( due_money>0.00){
                    Toast.makeText(getApplicationContext(), "please enter the full return amount", Toast.LENGTH_SHORT).show();
                }else {

                    proceedOrder(order_type, order_payment_method, customer_name, f.format(calculated_total_cost),
                            total_cost, calculated_tax, discount, paid_amount, f.format(due_money),type);

                }
            }else {
                proceedOrder(order_type, order_payment_method, customer_name, f.format(calculated_total_cost),
                        total_cost, calculated_tax, discount, paid_amount, f.format(due_money),type);

            }


            alertDialog.dismiss();
        });

        dialog_btn_close.setOnClickListener(v -> alertDialog.dismiss());


    }


    private void selectCustomerDialog(TextView dialog_customer, String type ) {

        ArrayAdapter<String> customerAdapter = new ArrayAdapter<String>(CartActivity.this, android.R.layout.simple_list_item_1);
        customerAdapter.addAll(customerNameList);

        AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
        EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
        TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
        ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);

        if(type.equals("sale")|| type.equals("sale_return")) {
            dialogTitle.setText(R.string.select_customer);
        }else if(type.equals("purchase")|| type.equals("purchase_return")){
            dialogTitle.setText(R.string.select_supplier);
        }
        dialogList.setVerticalScrollBarEnabled(true);
        dialogList.setAdapter(customerAdapter);

        dialogInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                customerAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });

        final AlertDialog alertDialog = dialog.create();

        dialogButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();


        dialogList.setOnItemClickListener((parent, view, position, id) -> {

            alertDialog.dismiss();
            String selectedItem = customerAdapter.getItem(position);

            dialog_customer.setText(selectedItem);

        });

    }


    private void selectPaymentMethodDialog(TextView dialog_order_payment_method) {

        ArrayAdapter<String> paymentMethodAdapter = new ArrayAdapter<String>(CartActivity.this, android.R.layout.simple_list_item_1, paymentMethodList);

        AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
        EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
        TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
        ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);


        dialogTitle.setText(R.string.select_payment_method);
        dialogList.setVerticalScrollBarEnabled(true);
        dialogList.setAdapter(paymentMethodAdapter);

        dialogInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                paymentMethodAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });


        final AlertDialog alertDialog = dialog.create();

        dialogButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();


        dialogList.setOnItemClickListener((parent, view, position, id) -> {

            alertDialog.dismiss();
            String selectedItem = paymentMethodAdapter.getItem(position);
            dialog_order_payment_method.setText(selectedItem);

        });
    }


    private void selectOrderTypesDialog(TextView dialog_order_type) {

        ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<String>(CartActivity.this, android.R.layout.simple_list_item_1, orderTypesList);

        AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
        EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
        TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
        ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);


        dialog_title.setText(R.string.select_order_type);

        dialog_list.setVerticalScrollBarEnabled(true);
        dialog_list.setAdapter(orderTypeAdapter);

        dialog_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                orderTypeAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });


        final AlertDialog alertDialog = dialog.create();

        dialog_button.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();

        dialog_list.setOnItemClickListener((parent, view, position, id) -> {

            alertDialog.dismiss();
            String selectedItem = orderTypeAdapter.getItem(position);

            dialog_order_type.setText(selectedItem);

        });

    }


    private void proceedOrder(String orderType, String orderPaymentMethod, String customerName, String totalPrice, double subTotal,
                              double calculatedTax, String discount, String paidAmount, String dueAmount ,String type) {

        databaseAccess.open();
        int itemCount = databaseAccess.getCartItemCount(type);

        if (itemCount > 0) {

            databaseAccess.open();
            List<HashMap<String, String>> cartItemsList = databaseAccess.getCartItems(type);

            if (cartItemsList.isEmpty()) {
                Toasty.error(this, R.string.no_items_found, Toast.LENGTH_SHORT).show();
            } else {

                //ge all essential order details values

                String currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date());

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int cMonth = calendar.get(Calendar.MONTH) + 1;
                int cDay = calendar.get(Calendar.DATE);

                String day = String.valueOf(cDay);
                String month = String.valueOf(cMonth);

                if (cDay < 10)
                    day = "0" + day;

                if (cMonth < 10)
                    month = "0" + month;


                final JSONObject jObject = new JSONObject();

                try {

                    jObject.put("order_type", orderType);
                    jObject.put("payment_method", orderPaymentMethod);
                    jObject.put("customer_name", customerName);
                    jObject.put("tax", calculatedTax);
                    jObject.put("discount", discount);
                    jObject.put("sub_total", subTotal);
                    jObject.put("total_price", totalPrice);
                    jObject.put("paid_money", paidAmount);
                    jObject.put("due_money", dueAmount);
                    jObject.put("order_time", currentTime);
                    jObject.put("day", day);
                    jObject.put("month", month);
                    jObject.put("year", year);
                    jObject.put("type", type);

                    /*
                     * every order can hold multiple number of items
                     * so store all items in a json array to easily send to the db file
                     * & then retrieve it from json array and store it in order_details table
                     *
                     */
                    JSONArray jsonArray = new JSONArray();

                    for (int i = 0; i < cartItemsList.size(); i++) {

                        databaseAccess.open();
                        String itemId = cartItemsList.get(i).get("item_id");

                        databaseAccess.open();
                        String stock = databaseAccess.getItemStock(itemId);

                        databaseAccess.open();
                        String itemWeightUnit = cartItemsList.get(i).get("item_weight_unit");
                        String weightUnit = databaseAccess.getWeightUnit(itemWeightUnit);


                        //for Order details
                        JSONObject obj = new JSONObject();
                        obj.put("item_id", itemId);
                        obj.put("item_weight", cartItemsList.get(i).get("item_weight") + " " + weightUnit);
                        obj.put("item_qty", cartItemsList.get(i).get("item_qty"));
                        obj.put("item_price", cartItemsList.get(i).get("item_price"));
                        obj.put("stock", stock);

                        jsonArray.put(obj);

                    }

                    jObject.put("orderDetails", jsonArray);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                /************-------saveOrderInDB--------------***********/
                saveOrderInDB(jObject, type);


            }

        } else {
            Toasty.error(this, R.string.no_items_in_cart, Toast.LENGTH_SHORT).show();
        }

    }

    private void saveOrderInDB(JSONObject object , String type) {

        //generate current timestamp for unique invoice id
        long tsLong = System.currentTimeMillis() / 1000;

        databaseAccess.open();
        String invoiceId = databaseAccess.getLastInvoiceId(type);

        int newInvoiceId =  Integer.parseInt(invoiceId)+ 1;

        databaseAccess.open();

        //timestamp used for un sync order and make it unique id
        if (databaseAccess.insertOrder(String.valueOf(newInvoiceId), object)) {

            Toasty.success(this, R.string.order_done_successful, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            switch (type) {
                case "sale":
                    intent.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_SALE_INVOICES);

                    break;
                case "purchase":
                    intent.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_PURCHASE_INVOICES);
                    break;
                case "purchase_return":
                    intent.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_PURCHASE_RETURN_INVOICES);
                    break;
                case "sale_return":
                    intent.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_SALES_RETURN_INVOICES);
                    break;
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("type",type);
            startActivity(intent);
            finish();

        } else {
            Toasty.error(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        }


    }


    //for top back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
package com.affable.smartbills.retrunGoods;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.affable.smartbills.MainActivity;
import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.database.DatabaseOpenHelper;
import com.affable.smartbills.items.ScannerViewActivity;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Constant;
import com.ajts.androidmads.library.ExcelToSQLite;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
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

public class ReturnInventoryActivity extends BaseActivity {

    public static TextInputEditText itemCode;
    public TextInputEditText itemName, itemCategory, itemStock,
             itemDescription, supplierName, invoiceId;
    private ImageView btnScanner;
    private MaterialButton addItems;

    private String mediaPath, encodedImage = "N/A";
    private ProgressDialog loading;
    public String  returnType  ="",finalType ="";

    private List<HashMap<String, String>> itemCategoryList, itemWeightUnitList, itemMapList;
    private List<String> categoryNames, weightUnits,supplierList , itemNameList , itemIdList, itemStockList ,itemBarCodeList,titemCategoryList;
    private ArrayAdapter<String> categoryAdapter, weightUnitAdapter,supplierAdopter , itemAdopter;
    private String selectedCategoryNameID = "0", selectedWeightUnitID = "0", mCurrentItemId, mCurrentStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrun_inventory);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            returnType = extras.getString("return_type");
        }

        if(returnType.equals("1")){
            getSupportActionBar().setTitle(R.string.retrun_stock);
        }else if(returnType.equals("2"))  {
            getSupportActionBar().setTitle(R.string.inventory_return_title);
        }

        itemName = findViewById(R.id.re_itemName);
        itemCode = findViewById(R.id.re_itemCode);
        itemCategory = findViewById(R.id.re_itemCategory);

        itemStock = findViewById(R.id.re_item_stock);
        invoiceId = findViewById(R.id.re_invoice_id);

        if(returnType.equals("1")){
            invoiceId.setVisibility(View.GONE);
        }

        itemDescription = findViewById(R.id.re_item_description);
        btnScanner = findViewById(R.id.btn_scanner);

        addItems = findViewById(R.id.re_add_items);
        supplierName = findViewById(R.id.re_supplier_name);

        btnScanner.setOnClickListener(v ->

                startActivity(new Intent(ReturnInventoryActivity.this, ScannerViewActivity.class)
                .putExtra(Constant.SCANNER_VIEW_RETURN_ACTIVITY, "RetrunInventoryActivity")));



        //add data form DB to categoryNames & weightUnits ArrayList
        createCategoryAndWeightUnitList();


        itemCategory.setOnClickListener(v -> categoryChooserDialog());

        supplierName.setOnClickListener(v -> getSupplierInfo());

        itemName.setOnClickListener(v-> ItemInfo());





        addItems.setOnClickListener(v -> {

            String item_nameSt = Objects.requireNonNull(itemName.getText()).toString();
            String item_codeSt = Objects.requireNonNull(itemCode.getText()).toString();
            String item_categoryNameSt = Objects.requireNonNull(itemCategory.getText()).toString();

            String item_stockSt = Objects.requireNonNull(itemStock.getText()).toString();

            String item_descriptionSt = Objects.requireNonNull(itemDescription.getText()).toString();
            String f_supplierName = Objects.requireNonNull(supplierName.getText()).toString();
            String invoice_id = Objects.requireNonNull(invoiceId.getText()).toString();

            if (item_nameSt.isEmpty()) {
                itemName.setError(getString(R.string.item_name_cannot_be_empty));
                itemName.requestFocus();
            } else if (item_codeSt.isEmpty()) {
                itemCode.setError(getString(R.string.item_code_cannot_be_empty));
                itemCode.requestFocus();
            } else if (item_categoryNameSt.isEmpty()) {
                itemCategory.setError(getString(R.string.item_category_cannot_be_empty));
                itemCategory.requestFocus();
            } else if (item_stockSt.isEmpty()) {
                itemStock.setError(getString(R.string.item_stock_cannot_be_empty));
                itemStock.requestFocus();
            }
            else if (f_supplierName.isEmpty()) {
                supplierName.setError(getString(R.string.item_name_cannot_be_empty));
                supplierName.requestFocus();
            } else {

                final Calendar calendar = Calendar.getInstance();

                final int year = calendar.get(Calendar.YEAR);
                 int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                  month = month+1;
                 String formattedDate = day +"-"+month + "-"+year;
                 Log.d("today", formattedDate);
                 int finalStock = 0;
                 boolean res = false;

                 if(returnType.equals("1")){
                         finalType = "PURCHASE_RETURN";
                     if( Integer.parseInt(mCurrentStock) >= Integer.parseInt(item_stockSt)){

                         finalStock =    Integer.parseInt(mCurrentStock) - Integer.parseInt(item_stockSt);
                         res = true;
                     }else {
                         res =false;
                     }
                 }else  if(returnType.equals("2")){
                        res = true;
                        finalType = "SALE_RETURN";
                        finalStock =    Integer.parseInt(mCurrentStock) + Integer.parseInt(item_stockSt);

                 }


                 if(res) {

                     DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ReturnInventoryActivity.this);
                     databaseAccess.open();

                     boolean check1 = databaseAccess.goodReturn(invoice_id, mCurrentItemId, formattedDate, "1", item_stockSt, finalType, f_supplierName, String.valueOf(finalStock),item_descriptionSt);

                     if (check1) {

                         if(returnType.equals("1")) {
                             Toasty.success(ReturnInventoryActivity.this, R.string.purchase_return, Toast.LENGTH_SHORT).show();
                         }else if(returnType.equals("2")){
                             Toasty.success(ReturnInventoryActivity.this, R.string.sale_return, Toast.LENGTH_SHORT).show();

                         }
                         Intent intent = new Intent(ReturnInventoryActivity.this, MainActivity.class);
                         intent.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_RETURN);
                         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         startActivity(intent);

                     } else {
                         Toasty.error(ReturnInventoryActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                     }
                 }else  {

                     Toasty.error(ReturnInventoryActivity.this, "stock is not enough", Toast.LENGTH_SHORT).show();

                 }


            }


        });


    }


    private void createCategoryAndWeightUnitList() {

        categoryNames = new ArrayList<>();
        weightUnits = new ArrayList<>();
        supplierList = new ArrayList<>();
        itemBarCodeList = new ArrayList<>();
        titemCategoryList = new ArrayList<>();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ReturnInventoryActivity.this);

        databaseAccess.open();
        itemCategoryList = databaseAccess.getItemCategories();
        Log.i("ItemCategories", itemCategoryList.size() + "");

        for (int i = 0; i < itemCategoryList.size(); i++) {

            categoryNames.add(itemCategoryList.get(i).get("category_name"));

        }
        Log.d("categoryNames", categoryNames.size() + "");


        databaseAccess.open();
        itemWeightUnitList = databaseAccess.getItemWeightUnit();
        for (int i = 0; i < itemWeightUnitList.size(); i++) {

            weightUnits.add(itemWeightUnitList.get(i).get("unit_name"));

        }


        databaseAccess.open();
        if(returnType.equals("1")) {
            supplierList = databaseAccess.getClientsName("SUPPLIER");

        }else if(returnType.equals("2")){
            supplierList = databaseAccess.getClientsName("CUSTOMER");
        }
        Log.d("supplier_list", supplierList.size()+"");

        databaseAccess.open();
        itemMapList = databaseAccess.getItems();

        itemIdList = new ArrayList<>();
        itemStockList = new ArrayList<>();
        itemNameList = new ArrayList<>();
        for(int i = 0; i< itemMapList.size(); i++){

            String  id = itemMapList.get(i).get("item_id");
            String  name = itemMapList.get(i).get("item_name");
            String  stock = itemMapList.get(i).get("item_stock");

            String  itemCode = itemMapList.get(i).get("item_code");
            String  category111 = itemMapList.get(i).get("item_category");
            itemNameList.add(name);
            itemIdList.add(id);
            itemStockList.add(stock);
            itemBarCodeList.add(itemCode);
            titemCategoryList.add(category111);
        }




    }


    private void categoryChooserDialog() {

        categoryAdapter = new ArrayAdapter<>(ReturnInventoryActivity.this, android.R.layout.simple_list_item_1);
        categoryAdapter.addAll(categoryNames);

        AlertDialog.Builder dialog = new AlertDialog.Builder(ReturnInventoryActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null, false);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        Button dialog_button = dialogView.findViewById(R.id.dialog_button);
        EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
        TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
        ListView dialog_list = dialogView.findViewById(R.id.dialog_list);

        dialog_title.setText(R.string.product_category);
        dialog_list.setVerticalScrollBarEnabled(true);
        dialog_list.setAdapter(categoryAdapter);

        dialog_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                categoryAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });

        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        dialog_button.setOnClickListener(v -> alertDialog.dismiss());


        dialog_list.setOnItemClickListener((parent, view, position, id) -> {

            alertDialog.dismiss();

            final String selectedItem = categoryAdapter.getItem(position);
            itemCategory.setText(selectedItem);

            //get category ID
            for (int i = 0; i < categoryNames.size(); i++) {

                if (categoryNames.get(i).equalsIgnoreCase(selectedItem)) {
                    selectedCategoryNameID = itemCategoryList.get(i).get("category_id");
                }

            }


        });


    }

    private  void ItemInfo(){



        itemAdopter = new ArrayAdapter<>(ReturnInventoryActivity.this, android.R.layout.simple_list_item_1);
        itemAdopter.addAll(itemNameList);

        AlertDialog.Builder dialog = new AlertDialog.Builder(ReturnInventoryActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null, false);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        MaterialButton dialog_button = dialogView.findViewById(R.id.dialog_button);
        EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
        TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
        ListView dialog_list = dialogView.findViewById(R.id.dialog_list);

        dialog_title.setText(R.string.add_supplier);
        dialog_list.setVerticalScrollBarEnabled(true);
        dialog_list.setAdapter(itemAdopter);

        dialog_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemAdopter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });

        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        dialog_button.setOnClickListener(v -> alertDialog.dismiss());

        dialog_list.setOnItemClickListener((parent, view, position, id) -> {

            alertDialog.dismiss();

            final String selectedItem = itemAdopter.getItem(position);
            itemName.setText(selectedItem);

            mCurrentItemId  = itemIdList.get(position);
            mCurrentStock  = itemStockList.get(position);
            String barcode  = itemBarCodeList.get(position);
            String category  = titemCategoryList.get(position);

            itemCode.setText(barcode);



            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ReturnInventoryActivity.this);
            databaseAccess.open();
            String cat = databaseAccess.getCategory(category);
            itemCategory.setText(cat);




        });



    }

    private void getSupplierInfo() {

        supplierAdopter = new ArrayAdapter<>(ReturnInventoryActivity.this, android.R.layout.simple_list_item_1);
        supplierAdopter.addAll(supplierList);

        AlertDialog.Builder dialog = new AlertDialog.Builder(ReturnInventoryActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null, false);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        MaterialButton dialog_button = dialogView.findViewById(R.id.dialog_button);
        EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
        TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
        ListView dialog_list = dialogView.findViewById(R.id.dialog_list);

        if(returnType.equals("1")){
            dialog_title.setText(R.string.add_supplier);
        }else if(returnType.equals("2")){
            dialog_title.setText(R.string.add_customer);
        }

        dialog_list.setVerticalScrollBarEnabled(true);
        dialog_list.setAdapter(supplierAdopter);

        dialog_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                supplierAdopter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //type your code here
            }
        });

        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        dialog_button.setOnClickListener(v -> alertDialog.dismiss());

        dialog_list.setOnItemClickListener((parent, view, position, id) -> {

            alertDialog.dismiss();

            final String selectedItem = supplierAdopter.getItem(position);
            supplierName.setText(selectedItem);




        });

    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_items_menu, menu);
        return true;
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            case R.id.menu_import:
                fileChooser();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void fileChooser() {

        new ChooserDialog(ReturnInventoryActivity.this)

                .displayPath(true)
                .withFilter(false, false, "xls") //filter file type

                .withChosenListener((path, pathFile) -> onImport(path))
                // to handle the back key pressed or clicked outside the dialog:
                .withOnCancelListener(DialogInterface::cancel).build().show();

    }


    //import data from Excel xls file
    public void onImport(String path) {

        String directory_path = path;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ReturnInventoryActivity.this);
        databaseAccess.open();

        File file = new File(directory_path);
        if (!file.exists()) {
            Toasty.error(this, R.string.no_file_found, Toast.LENGTH_SHORT).show();
            return;
        }

        //if you want to add column in excel and import into DB, you must drop the table
        //Is used to import data from excel without dropping table
        ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, false);
        // Import EXCEL FILE to SQLite
        excelToSQLite.importFromFile(directory_path, new ExcelToSQLite.ImportListener() {

            @Override
            public void onStart() {

                loading = new ProgressDialog(ReturnInventoryActivity.this);
                loading.setMessage(getString(R.string.data_importing_please_wait));
                loading.setCancelable(false);
                loading.show();

            }

            @Override
            public void onCompleted(String dbName) {

                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(ReturnInventoryActivity.this, R.string.data_successfully_imported, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReturnInventoryActivity.this, MainActivity.class);
                    //intent.putExtra()
                    startActivity(intent);
                    finish();

                }, 5000);

            }

            @Override
            public void onError(Exception e) {
                loading.dismiss();
                Log.d("Error : ", e.getMessage());
                Toasty.error(ReturnInventoryActivity.this, R.string.data_import_fail, Toast.LENGTH_SHORT).show();
            }
        });

    }



}
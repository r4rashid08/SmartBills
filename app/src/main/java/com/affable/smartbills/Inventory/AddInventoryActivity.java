package com.affable.smartbills.Inventory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
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

import androidx.annotation.Nullable;

import com.affable.smartbills.MainActivity;
import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.database.DatabaseOpenHelper;
import com.affable.smartbills.items.ScannerViewActivity;
import com.affable.smartbills.utils.Admob;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Constant;
import com.ajts.androidmads.library.ExcelToSQLite;
import com.google.android.gms.ads.AdView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class AddInventoryActivity extends BaseActivity {

    public static TextInputEditText itemCode;
    public TextInputEditText itemName, itemCategory, itemBuyPrice,  itemStock,
             supplierName;
    private ImageView btnScanner;
    private MaterialButton addItems;

    private String mediaPath, encodedImage = "N/A";
    private ProgressDialog loading;

    private List<HashMap<String, String>> itemCategoryList, itemWeightUnitList, itemMapList;
    private List<String> categoryNames, weightUnits,supplierList , itemNameList , itemIdList, itemStockList ,itemBarCodeList,titemCategoryList;
    private ArrayAdapter<String> categoryAdapter, weightUnitAdapter,supplierAdopter , itemAdopter;
    private String selectedCategoryNameID = "0", selectedWeightUnitID = "0", mCurrentItemId, mCurrentStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase);

        getSupportActionBar().setTitle(R.string.add_inventory);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        itemName = findViewById(R.id.itemName);
        itemCode = findViewById(R.id.itemCode);
        itemCategory = findViewById(R.id.itemCategory);
        itemBuyPrice = findViewById(R.id.item_buy_price);

        itemStock = findViewById(R.id.item_stock);

        btnScanner = findViewById(R.id.btn_scanner);

        addItems = findViewById(R.id.add_items);
        supplierName = findViewById(R.id.item_supplier_name);


        AdView adView = findViewById(R.id.inv_adView);

        Admob.bannerAdd(getApplicationContext(), adView);



        btnScanner.setOnClickListener(v -> startActivity(new Intent(AddInventoryActivity.this, ScannerViewActivity.class)
                .putExtra(Constant.SCANNER_VIEW_RETURN_ACTIVITY, "AddInventoryActivity")));



        //add data form DB to categoryNames & weightUnits ArrayList
        createCategoryAndWeightUnitList();


        itemCategory.setOnClickListener(v -> categoryChooserDialog());


        supplierName.setOnClickListener(v -> getSupplierInfo());

        itemName.setOnClickListener(v-> ItemInfo());





        addItems.setOnClickListener(v -> {

            String item_nameSt = Objects.requireNonNull(itemName.getText()).toString();
            String item_codeSt = Objects.requireNonNull(itemCode.getText()).toString();
            String item_categoryNameSt = Objects.requireNonNull(itemCategory.getText()).toString();
            String item_buy_priceSt = Objects.requireNonNull(itemBuyPrice.getText()).toString();
             String item_stockSt = Objects.requireNonNull(itemStock.getText()).toString();
            String f_supplierName = Objects.requireNonNull(supplierName.getText()).toString();

            if (item_nameSt.isEmpty()) {
                itemName.setError(getString(R.string.item_name_cannot_be_empty));
                itemName.requestFocus();
            } else if (item_codeSt.isEmpty()) {
                itemCode.setError(getString(R.string.item_code_cannot_be_empty));
                itemCode.requestFocus();
            } else if (item_categoryNameSt.isEmpty()) {
                itemCategory.setError(getString(R.string.item_category_cannot_be_empty));
                itemCategory.requestFocus();
            } else if (item_buy_priceSt.isEmpty()) {
                itemBuyPrice.setError(getString(R.string.item_buy_price_cannot_be_empty));
                itemBuyPrice.requestFocus();
            }  else if (item_stockSt.isEmpty()) {
                itemStock.setError(getString(R.string.item_stock_cannot_be_empty));
                itemStock.requestFocus();
            }
            else if (f_supplierName.isEmpty()) {
                supplierName.setError(getString(R.string.item_name_cannot_be_empty));
                supplierName.requestFocus();
            } else {


                int fanalStock =  Integer.parseInt(item_stockSt) + Integer.parseInt(mCurrentStock);

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = simpleDateFormat.format(c);

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddInventoryActivity.this);
                databaseAccess.open();

                boolean check1 = databaseAccess.addPurchaseHistory(f_supplierName,"1",mCurrentItemId,item_buy_priceSt,item_stockSt,formattedDate,String.valueOf(fanalStock));

                if(check1){

                    Toasty.success(AddInventoryActivity.this, R.string.inventory_added, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddInventoryActivity.this, MainActivity.class);
                    intent.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_INVENTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    Toasty.error(AddInventoryActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddInventoryActivity.this);

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
         supplierList  = databaseAccess.getClientsName("SUPPLIER");
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

        categoryAdapter = new ArrayAdapter<>(AddInventoryActivity.this, android.R.layout.simple_list_item_1);
        categoryAdapter.addAll(categoryNames);

        AlertDialog.Builder dialog = new AlertDialog.Builder(AddInventoryActivity.this);
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



             itemAdopter = new ArrayAdapter<>(AddInventoryActivity.this, android.R.layout.simple_list_item_1);
             itemAdopter.addAll(itemNameList);

             AlertDialog.Builder dialog = new AlertDialog.Builder(AddInventoryActivity.this);
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



                 DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddInventoryActivity.this);
                 databaseAccess.open();
                 String cat = databaseAccess.getCategory(category);
                 itemCategory.setText(cat);




             });



     }
     private void getSupplierInfo() {

        supplierAdopter = new ArrayAdapter<>(AddInventoryActivity.this, android.R.layout.simple_list_item_1);
        supplierAdopter.addAll(supplierList);

        AlertDialog.Builder dialog = new AlertDialog.Builder(AddInventoryActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null, false);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        MaterialButton dialog_button = dialogView.findViewById(R.id.dialog_button);
        EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
        TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
        ListView dialog_list = dialogView.findViewById(R.id.dialog_list);

        dialog_title.setText(R.string.add_supplier);
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

        new ChooserDialog(AddInventoryActivity.this)

                .displayPath(true)
                .withFilter(false, false, "xls") //filter file type

                .withChosenListener((path, pathFile) -> onImport(path))
                // to handle the back key pressed or clicked outside the dialog:
                .withOnCancelListener(DialogInterface::cancel).build().show();

    }


    //import data from Excel xls file
    public void onImport(String path) {

        String directory_path = path;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddInventoryActivity.this);
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

                loading = new ProgressDialog(AddInventoryActivity.this);
                loading.setMessage(getString(R.string.data_importing_please_wait));
                loading.setCancelable(false);
                loading.show();

            }

            @Override
            public void onCompleted(String dbName) {

                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(AddInventoryActivity.this, R.string.data_successfully_imported, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddInventoryActivity.this, MainActivity.class);
                    //intent.putExtra()
                    startActivity(intent);
                    finish();

                }, 5000);

            }

            @Override
            public void onError(Exception e) {
                loading.dismiss();
                Log.d("Error : ", e.getMessage());
                Toasty.error(AddInventoryActivity.this, R.string.data_import_fail, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
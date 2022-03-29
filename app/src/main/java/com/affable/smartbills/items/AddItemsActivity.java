package com.affable.smartbills.items;

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
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Constant;
import com.ajts.androidmads.library.ExcelToSQLite;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class AddItemsActivity extends BaseActivity {

    public static TextInputEditText itemCode;
    private TextInputEditText itemName, itemCategory, itemBuyPrice, itemSellPrice, itemStock,
            itemWeight, itemWeightUnit, itemDescription;
    private ImageView btnScanner, addItemImage;
    private MaterialButton addItems;

    private String mediaPath, encodedImage = "N/A";
    private ProgressDialog loading;
    private List<HashMap<String, String>> itemCategoryList, itemWeightUnitList;
    private List<String> categoryNames, weightUnits;
    private ArrayAdapter<String> categoryAdapter, weightUnitAdapter;
    private String selectedCategoryNameID = "0", selectedWeightUnitID = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        getSupportActionBar().setTitle(R.string.add_items);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        itemName = findViewById(R.id.itemName);
        itemCode = findViewById(R.id.itemCode);
        itemCategory = findViewById(R.id.itemCategory);
        itemBuyPrice = findViewById(R.id.item_buy_price);
        itemSellPrice = findViewById(R.id.item_sell_price);
        itemStock = findViewById(R.id.item_stock);
        itemWeight = findViewById(R.id.item_weight);
        itemWeightUnit = findViewById(R.id.item_weight_unit);
        itemDescription = findViewById(R.id.item_description);
        btnScanner = findViewById(R.id.btn_scanner);
        addItemImage = findViewById(R.id.add_item_image);
        addItems = findViewById(R.id.add_items);

        btnScanner.setOnClickListener(v -> startActivity(new Intent(AddItemsActivity.this, ScannerViewActivity.class)
                .putExtra(Constant.SCANNER_VIEW_RETURN_ACTIVITY, "AddItemsActivity")));





        addItemImage.setOnClickListener(v -> {


            Intent intent = new Intent(AddItemsActivity.this, ImageSelectActivity.class);
            intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
            intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
            intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
            startActivityForResult(intent, 1213);


        });

        //add data form DB to categoryNames & weightUnits ArrayList
        createCategoryAndWeightUnitList();

        itemCategory.setOnClickListener(v -> categoryChooserDialog());

        itemWeightUnit.setOnClickListener(v -> weightUnitChooserDialog());


        addItems.setOnClickListener(v -> {

            String item_nameSt = itemName.getText().toString();
            String item_codeSt = itemCode.getText().toString();
            String item_categoryNameSt = itemCategory.getText().toString();
            String item_buy_priceSt = itemBuyPrice.getText().toString();
            String item_sell_priceSt = itemSellPrice.getText().toString();
            String item_stockSt = itemStock.getText().toString();
            String item_weightSt = itemWeight.getText().toString();
            String item_weight_unitSt = itemWeightUnit.getText().toString();
            String item_descriptionSt = itemDescription.getText().toString();

            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddItemsActivity.this);
            databaseAccess.open();
            String item_code =    databaseAccess.getItemCode(item_codeSt.toString());

            if(item_code.equals(item_codeSt)){
                itemCode.setError("item code is not available");
                itemCode.requestFocus();
            }else
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
            } else if (item_sell_priceSt.isEmpty()) {
                itemSellPrice.setError(getString(R.string.item_sell_price_cannot_be_empty));
                itemSellPrice.requestFocus();
            } else if (item_stockSt.isEmpty()) {
                itemStock.setError(getString(R.string.item_stock_cannot_be_empty));
                itemStock.requestFocus();
            } else if (item_weightSt.isEmpty()) {
                itemWeight.setError(getString(R.string.item_name_cannot_be_empty));
                itemWeight.requestFocus();
            } else if (item_weight_unitSt.isEmpty()) {
                itemWeightUnit.setError(getString(R.string.item_name_cannot_be_empty));
                itemWeightUnit.requestFocus();
            } else {

                databaseAccess.open();

                boolean check = databaseAccess.addItem(
                        item_nameSt, item_codeSt, selectedCategoryNameID, item_buy_priceSt,
                        item_sell_priceSt, item_stockSt, encodedImage,
                        selectedWeightUnitID, item_weightSt, item_descriptionSt
                );

                if (check) {
                    Toasty.success(AddItemsActivity.this, R.string.item_successfully_added, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddItemsActivity.this, MainActivity.class);
                    intent.putExtra(Constant.PREF_INTENT_REPLACE_FRAG, Constant.KEY_TO_ITEMS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toasty.error(AddItemsActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                }


            }


        });


    }


    private void createCategoryAndWeightUnitList() {

        categoryNames = new ArrayList<>();
        weightUnits = new ArrayList<>();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddItemsActivity.this);

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

    }


    private void categoryChooserDialog() {

        categoryAdapter = new ArrayAdapter<>(AddItemsActivity.this, android.R.layout.simple_list_item_1);
        categoryAdapter.addAll(categoryNames);

        AlertDialog.Builder dialog = new AlertDialog.Builder(AddItemsActivity.this);
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


    private void weightUnitChooserDialog() {

        weightUnitAdapter = new ArrayAdapter<>(AddItemsActivity.this, android.R.layout.simple_list_item_1);
        weightUnitAdapter.addAll(weightUnits);

        AlertDialog.Builder dialog = new AlertDialog.Builder(AddItemsActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null, false);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        MaterialButton dialog_button = dialogView.findViewById(R.id.dialog_button);
        EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
        TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
        ListView dialog_list = dialogView.findViewById(R.id.dialog_list);

        dialog_title.setText(R.string.product_weight_unit);
        dialog_list.setVerticalScrollBarEnabled(true);
        dialog_list.setAdapter(weightUnitAdapter);

        dialog_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //type your code here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                weightUnitAdapter.getFilter().filter(s);
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

            final String selectedItem = weightUnitAdapter.getItem(position);
            itemWeightUnit.setText(selectedItem);

            //get weight unit ID
            for (int i = 0; i < weightUnits.size(); i++) {

                if (weightUnits.get(i).equalsIgnoreCase(selectedItem)) {
                    selectedWeightUnitID = itemWeightUnitList.get(i).get("unit_id");
                }

            }


        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == 1213 && resultCode == RESULT_OK && data != null) {

                mediaPath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Bitmap selectedImage = BitmapFactory.decodeFile(mediaPath);
                addItemImage.setImageBitmap(selectedImage);

                encodedImage = encodeImage(selectedImage);

            }

        } catch (Exception e) {
            Toasty.error(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
        }

    }


    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
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

        new ChooserDialog(AddItemsActivity.this)

                .displayPath(true)
                .withFilter(false, false, "xls") //filter file type

                .withChosenListener((path, pathFile) -> onImport(path))
                // to handle the back key pressed or clicked outside the dialog:
                .withOnCancelListener(DialogInterface::cancel).build().show();

    }


    //import data from Excel xls file
    public void onImport(String path) {

        String directory_path = path;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddItemsActivity.this);
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

                loading = new ProgressDialog(AddItemsActivity.this);
                loading.setMessage(getString(R.string.data_importing_please_wait));
                loading.setCancelable(false);
                loading.show();

            }

            @Override
            public void onCompleted(String dbName) {

                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(AddItemsActivity.this, R.string.data_successfully_imported, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddItemsActivity.this, MainActivity.class);
                    //intent.putExtra()
                    startActivity(intent);
                    finish();

                }, 5000);

            }

            @Override
            public void onError(Exception e) {
                loading.dismiss();
                Log.d("Error : ", e.getMessage());
                Toasty.error(AddItemsActivity.this, R.string.data_import_fail, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
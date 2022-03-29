package com.affable.smartbills;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.affable.smartbills.Inventory.InventoryFragment;
import com.affable.smartbills.clients.ClientsFragment;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.expenses.ExpensesFragment;
import com.affable.smartbills.home.HomeFragment;
import com.affable.smartbills.invoice.InvoiceFragment;
import com.affable.smartbills.items.ItemsFragment;
import com.affable.smartbills.report.ReportsFragment;
import com.affable.smartbills.report.report_fragments.SalesReportFragment;
import com.affable.smartbills.retrunGoods.ReturnFragment;
import com.affable.smartbills.sales.SalesFragment;
import com.affable.smartbills.settings.SettingsActivity;
import com.affable.smartbills.startup.LoginActivity;
import com.affable.smartbills.utils.Admob;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.Constant;
import com.affable.smartbills.utils.LocaleManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AdView adView;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);


        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {}
        });


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

        adView.loadAd(adRequest);





        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //set Home Fragment in MainActivity
        if (savedInstanceState == null) {
            Fragment frag = new HomeFragment();

            replaceFragment(frag);
            updateTitleAndDrawer(frag);
        }

        //request for runtime permissions
        requestPermission();

        //set user data at nav header
        customizeNavHeader();

        //get data from intent


        String replaceKey = getIntent().getStringExtra(Constant.PREF_INTENT_REPLACE_FRAG);
        String type = getIntent().getStringExtra("type");


        if (replaceKey != null) {

            Fragment f;

            switch (replaceKey) {
                case Constant.KEY_TO_CLIENTS:
                    f = new ClientsFragment("1");
                    replaceFragment(f);
                    updateTitleAndDrawer(f);
                    break;

                case Constant.KEY_TO_EXPENSES:
                    f = new ExpensesFragment();
                    replaceFragment(f);
                    updateTitleAndDrawer(f);
                    break;

                case Constant.KEY_TO_RETURN:
                    f = new ReturnFragment(type);
                    replaceFragment(f);
                    updateTitleAndDrawer(f);
                    break;

                case Constant.KEY_TO_ITEMS:
                    f = new ItemsFragment();
                    replaceFragment(f);
                    updateTitleAndDrawer(f);
                    break;

                    case Constant.KEY_TO_SALES:

                    f = new SalesFragment(type);
                    replaceFragment(f);
                    updateTitleAndDrawer(f);
                    break;

                case Constant.KEY_TO_PURCHASE:

                    f = new SalesFragment(type);
                    replaceFragment(f);
                    updateTitleAndDrawer(f);
                    break;

                case Constant.KEY_TO_PURCHASE_INVOICES:
                     f = new InvoiceFragment(type);
                    replaceFragment(f);
                    updateTitleAndDrawer(f);
                    break;


                case Constant.KEY_TO_SALE_INVOICES:
                     f = new InvoiceFragment(type);
                    replaceFragment(f);
                    updateTitleAndDrawer(f);
                    break;

                case Constant.KEY_TO_SALES_RETURN_INVOICES:
                     f = new InvoiceFragment(type);
                    replaceFragment(f);
                    updateTitleAndDrawer(f);
                    break;

                case Constant.KEY_TO_PURCHASE_RETURN_INVOICES:
                    f = new InvoiceFragment(type);
                    replaceFragment(f);
                    updateTitleAndDrawer(f);
                    break;

                case Constant.KEY_TO_REPORTS:
                    f = new SalesReportFragment();
                    replaceFragment(f);
                    updateTitleAndDrawer(f);
                    break;

                case Constant.KEY_TO_INVENTORY:
                    f = new InventoryFragment();
                    replaceFragment(f);
                    updateTitleAndDrawer(f);
                    break;

            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void customizeNavHeader() {
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView title = headerView.findViewById(R.id.nav_header_title);
        TextView subTitle = headerView.findViewById(R.id.nav_header_subtitle);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        HashMap<String, String> map = databaseAccess.getUserInformation();
        if (map != null) {

            String ac_name = map.get("user_name");
            String ac_email = map.get("user_email");
            String ac_phone = map.get("user_phone");

            if (ac_name != null && ac_email != null && ac_phone != null) {
                title.setText(ac_name);
                subTitle.setText(ac_phone + "\n" + ac_email);
            }

        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        switch (item.getItemId()) {

            case R.id.home_menu:
                fragment = new HomeFragment();
                break;

            case R.id.clients_menu:
                fragment = new ClientsFragment("1");
                break;

            case R.id.supplier_menu:
                fragment = new ClientsFragment("2");
                break;

            case R.id.items_menu:
                fragment = new ItemsFragment();
                break;

            case R.id.purchase_menu:
                fragment = new SalesFragment("purchase");
                break;

            case R.id.sales_menu:
                fragment = new SalesFragment("sale");
                break;

            case R.id.expenses_menu:
                fragment = new ExpensesFragment();
                break;

            case R.id.return_sale_menu:
                fragment = new ReturnFragment("sale_return");
                break;
            case R.id.return_purchase_menu:
                fragment = new ReturnFragment("purchase_return");
                break;

            case R.id.invoice_sale_menu:
                fragment = new InvoiceFragment("sale");
                break;

            case R.id.invoice_purchase_menu:
                fragment = new InvoiceFragment("purchase");
                break;

            case R.id.invoice_sale_return_menu:
                fragment = new InvoiceFragment("sale_return");
                break;

            case R.id.invoice_purchase_return_menu:
                fragment = new InvoiceFragment("purchase_return");
                break;



            case R.id.report_menu:
                fragment = new SalesReportFragment();
                break;

            case R.id.setting_menu:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_logout:
                logout();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

           /* case R.id.nav_about:
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;*/

        }

        if (fragment != null) {
            replaceFragment(fragment);
            updateTitleAndDrawer(fragment);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void replaceFragment(Fragment fragment) {

        String fragmentTag = fragment.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        ft.replace(R.id.fragmentContainer, fragment, fragmentTag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

    }

    private void updateTitleAndDrawer(Fragment fragment) {
        String fragClassName = fragment.getClass().getName();

        if (fragClassName.equals(HomeFragment.class.getName())) {
            setTitle(getString(R.string.dashboard));
            navigationView.setCheckedItem(R.id.home_menu);

        } else if (fragClassName.equals(ItemsFragment.class.getName())) {
            setTitle(getString(R.string.items));
            navigationView.setCheckedItem(R.id.items_menu);

        } else if (fragClassName.equals(ClientsFragment.class.getName())) {
             setTitle(getString(R.string.clients));
            navigationView.setCheckedItem(R.id.clients_menu);

        } else if (fragClassName.equals(SalesFragment.class.getName())) {
            String type = SalesFragment.type;
            if(type.equals("sale")) {
                setTitle(getString(R.string.sales));
            }else if (type.equals("purchase")){
                setTitle("Purchase");
            }
            navigationView.setCheckedItem(R.id.sales_menu);

        } else if (fragClassName.equals(ExpensesFragment.class.getName())) {
            setTitle(getString(R.string.expenses));
            navigationView.setCheckedItem(R.id.expenses_menu);

        } else if (fragClassName.equals(InvoiceFragment.class.getName())) {
            setTitle(getString(R.string.invoices));
            navigationView.setCheckedItem(R.id.invoice_sale_menu);

        } else if (fragClassName.equals(ReportsFragment.class.getName())) {
            setTitle(getString(R.string.reports));
            navigationView.setCheckedItem(R.id.report_menu);

        }/*else if (fragClassName.equals(InventoryFragment.class.getName())) {
            setTitle(getString(R.string.inventory));
            navigationView.setCheckedItem(R.id.purchase_menu);
        }*/
        else if (fragClassName.equals(ReturnFragment.class.getName())) {
            String type = ReturnFragment.type;
            if(type.equals("sale_return")) {
                setTitle(getString(R.string.retrun_stock));
            }else if(type.equals("purchase_return")){
                setTitle(R.string.nav_drawer_menu_purchase_return);
            }
            navigationView.setCheckedItem(R.id.return_sale_menu);
        }
    }

    private void exitDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage("Do You want to close the app?");
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialog1, which) -> dialog1.dismiss());
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Close", (dialog12, which) -> MainActivity.this.finish());
        dialog.show();
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {

            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

            if (frag instanceof HomeFragment) {
                exitDialog();
            } else {
                frag = new HomeFragment();

                replaceFragment(frag);
                updateTitleAndDrawer(frag);
            }

        }
    }

    private void requestPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //do your action
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.language_menu, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.local_english:
                setNewLocale(LocaleManager.ENGLISH);
                return true;

           /* case R.id.local_spanish:
                setNewLocale(LocaleManager.SPANISH);
                return true;

            case R.id.local_bangla:
                setNewLocale(LocaleManager.BANGLE);
                return true;

            case R.id.local_hindi:
                setNewLocale(LocaleManager.HINDI);
                return true;*/

        }

        return super.onOptionsItemSelected(item);
    }


    private void setNewLocale(@LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(getApplicationContext(), language);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void logout() {

        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //remove saved login from sharedPref
                        SharedPreferences sharedPref = getSharedPreferences(Constant.PREF_REMEMBER, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(Constant.PREF_KEY_REMEMBER_LOGIN, false);
                        editor.apply();

                        //get back into login activity
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.ic_logout_outline)
                .show();

    }


    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}
package com.affable.smartbills.startup;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.affable.smartbills.Network.ServerCallback;
import com.affable.smartbills.Network.ServerError;
import com.affable.smartbills.Network.ServerTask;
import com.affable.smartbills.Network.UserInfo;
import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.utils.BaseActivity;
import com.affable.smartbills.utils.NetworkUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetAccountInfoActivity extends BaseActivity {

    private EditText etxtAcName, etxtAcEmail, etxtAcPhone,
            etxtAcAddress, etxtTax, etxtAcCurrency,mCompany,mVatNumber;
    private FloatingActionButton fabDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_account_info);

        etxtAcName = findViewById(R.id.etxt_ac_name);
        etxtAcEmail = findViewById(R.id.etxt_ac_email);
        etxtAcPhone = findViewById(R.id.etxt_ac_phone);
        etxtAcAddress = findViewById(R.id.etxt_ac_address);
        etxtTax = findViewById(R.id.etxt_tax);
        mCompany = findViewById(R.id.etxt_company_name);
        mVatNumber = findViewById(R.id.etxt_vat_number);
        etxtAcCurrency = findViewById(R.id.etxt_ac_currency);

        fabDone = findViewById(R.id.fab_done);

        fabDone.setOnClickListener(v -> {

            String name = etxtAcName.getText().toString().trim();
            String phone = etxtAcPhone.getText().toString().trim();
            String email = etxtAcEmail.getText().toString().trim();
            String address = etxtAcAddress.getText().toString().trim();
            String currency = etxtAcCurrency.getText().toString().trim();
            String company = mCompany.getText().toString().trim();
            String vat_number = mVatNumber.getText().toString().trim();
            String tax = etxtTax.getText().toString().trim().replace("%", "");

            if (name.isEmpty()) {
                etxtAcName.setError("fill up this field!");
                etxtAcName.requestFocus();
            } else if (email.isEmpty()) {
                etxtAcEmail.setError("fill up this field!");
                etxtAcEmail.requestFocus();
            }else if (phone.isEmpty()) {
                etxtAcPhone.setError("fill up this field!");
                etxtAcPhone.requestFocus();
            }  else if (address.isEmpty()) {
                etxtAcAddress.setError("fill up this field!");
                etxtAcAddress.requestFocus();
            } else if (tax.isEmpty()) {
                etxtTax.setError("fill up this field!");
                etxtTax.requestFocus();
            }  else if (currency.isEmpty()) {
                etxtAcCurrency.setError("fill up this field!");
                etxtAcCurrency.requestFocus();
            } else if (company.isEmpty()) {
                mCompany.setError("fill up this field!");
                mCompany.requestFocus();
            } else if (vat_number.isEmpty()) {
                mVatNumber.setError("fill up this field!");
                mVatNumber.requestFocus();
            }else {


                ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setProgress(0);
                progressDialog.show();

                if(NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                    Call<UserInfo> data = ServerTask.getInstance().getServices().insertUser(company, address, vat_number, phone, email, name);
                    data.enqueue(new ServerCallback<UserInfo>() {
                        @Override
                        public void onFailure(ServerError restError) {
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onSuccess(Response<UserInfo> response) {

                        }

                        @Override
                        public void onResponse(Response<UserInfo> response) {

                            progressDialog.dismiss();
                            assert response.body() != null;
                            String status = response.body().getStatus();

                            if(status.equals("Successfull")) {
                                setAccountInfo(name, phone, email, address, currency, tax, vat_number, company);
                            }else {
                                Toast.makeText(getApplicationContext(), response.body().getStatus(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    Toast.makeText(getApplicationContext(), "You do not have internet for  signUp ", Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

    private void setAccountInfo(String name, String phone, String email, String address, String currency, String tax, String vatNumber , String company) {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        if (databaseAccess.updateUserInformation("1", name, phone, email, address, currency, tax,vatNumber,company)) {

            Toasty.success(this, getString(R.string.ac_info_updated), Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, SetPasswordActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        } else {
            Toasty.error(this, getString(R.string.ac_info_not_updated), Toast.LENGTH_SHORT).show();
        }

    }
}
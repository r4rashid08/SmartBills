package com.affable.smartbills.invoice;



import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;

import android.text.Layout;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.affable.smartbills.Model.InvoiceData;
import com.affable.smartbills.Model.InvoiceDate;
import com.affable.smartbills.Model.InvoiceTaxAmount;
import com.affable.smartbills.Model.InvoiceTotalAmount;
import com.affable.smartbills.Model.QRBarcodeEncoder;
import com.affable.smartbills.Model.Seller;
import com.affable.smartbills.Model.TaxNumber;
import com.affable.smartbills.R;

import com.affable.smartbills.database.DatabaseAccess;

import com.affable.smartbills.invoice.pdf.PurchaseItemAdapter;
import com.affable.smartbills.qrcode.ZatcaQRCodeGeneration;
import com.affable.smartbills.utils.Admob;
import com.affable.smartbills.utils.BaseActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.Sys;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextStyle;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class PrintertActivity extends BaseActivity {

     public InvoiceData invoiceData;
    public  InterstitialAd mInterstitial;

      ImageView imageView;
      TextView mDate,mInvoiceNumber,mCashType,mTotalAmount,mDiscount,mNetAmount,mPaidAmount,mVatPercent,
       mTotalAmount2, mTotalItems,mVatNumber, mCompany, mCusName ;
     Button mPrint2;
     LinearLayout mRootView;
     DatabaseAccess databaseAccess;
     ArrayList<HashMap<String, String>> mList;
     PurchaseItemAdapter itemAdapter;
      RecyclerView mRecycleView;
     DriverManager mDriverManager = DriverManager.getInstance();
     Printer mPrinter;
     Sys mSys = mDriverManager.getBaseSysDevice();
     String cusName ;
    ExecutorService mSingleThreadExecutor;
    String companyName = "", vatNumber ="",type;

    @SuppressLint({"SetTextI18n", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        mDriverManager = DriverManager.getInstance();
        mPrinter = mDriverManager.getPrinter();
        mSingleThreadExecutor = mDriverManager.getSingleThreadExecutor();

        initView();

        databaseAccess = DatabaseAccess.getInstance(this);

        Bundle bundle = getIntent().getExtras();
        invoiceData = (InvoiceData) bundle.getSerializable("data");
        type = bundle.getString("type");
        cusName = bundle.getString("cus_name");
        mCusName.setText(cusName);

        mDate.setText(invoiceData.getIssueTime());
        String userName = invoiceData.getUserInfo().get("user_name");
       // mUserName.setText(userName);
        mInvoiceNumber.setText(invoiceData.getInvoiceId());
        mCashType.setText(invoiceData.getPaymentMethod());

        double val = invoiceData.getSubTotal() - invoiceData.getDiscount();
        double  valueTotal =Double.parseDouble(new DecimalFormat("0.00").format(val));

        mTotalAmount.setText(valueTotal + "");


        mNetAmount.setText(invoiceData.getTotalMoney()+"");

        mVatPercent.setText(invoiceData.getUserInfo().get("tax") + "%");

        mTotalAmount2.setText(invoiceData.getTax()+"");

        mPaidAmount.setText(invoiceData.getTotalMoney()+"");
        mDiscount.setText(invoiceData.getDiscount()+"");

        getItem();

        databaseAccess.open();
        HashMap<String, String> infoMap = databaseAccess.getUserInformation();


        if (infoMap != null) {

            vatNumber = infoMap.get("vat_number");
            companyName = infoMap.get("company_name");

        }

        mVatNumber.setText(vatNumber);
        mCompany.setText(companyName);




        ZatcaQRCodeGeneration.Builder builder = new ZatcaQRCodeGeneration.Builder();


        assert vatNumber != null;
        assert companyName != null;



           /* builder
           .sellerName(companyName.trim())
           .taxNumber(vatNumber.trim())
           .invoiceDate(invoiceData.getIssueTime().trim())
           .totalAmount(String.valueOf(invoiceData.getTotalMoney()).trim())
           .taxAmount(String.valueOf(invoiceData.getTax()).trim());
            */




        Instant instant = Instant.now();
        String time = instant.toString();

        Log.d("qr_time", time);


        String d =     QRBarcodeEncoder.encode(
                new Seller(companyName.trim()),
                new TaxNumber(vatNumber.trim()),
                new InvoiceDate(time),
                new InvoiceTotalAmount(String.valueOf(invoiceData.getTotalMoney()).trim()),
                new InvoiceTaxAmount(String.valueOf(invoiceData.getTax()).trim()));


        Bitmap qrBitmap = qrCode(d.trim().replaceAll("[\\n]",""));


        imageView.setImageBitmap(qrBitmap);


      /*  mPrint1.setOnClickListener(v -> {
            LinearLayout layout = findViewById(R.id.root_view);
            int width = layout.getWidth();
            int higt = layout.getHeight();

            Log.d("higth", "w"+width+"h"+higt);

         Bitmap bitmap =  Tools.getViewBitmap(layout,width,higt);

            mSingleThreadExecutor.execute(() -> {

                  printPic(bitmap);

            });
        });*/


        mPrint2.setOnClickListener(v -> mSingleThreadExecutor.execute(() -> printManual(qrBitmap)));







    }

    private void initView() {

        mDate = findViewById(R.id.invoice_date);
        //mUserName = findViewById(R.id.invoice_user_name);
        mInvoiceNumber = findViewById(R.id.invoice_number);
        mCashType = findViewById(R.id.invoice_cash_type);
        mTotalAmount = findViewById(R.id.invoice_total_amount);
        mDiscount = findViewById(R.id.invoice_discount);
        mNetAmount = findViewById(R.id.invoice_net_amoun);
        mPaidAmount = findViewById(R.id.invoice_paid_amount);
        mVatPercent = findViewById(R.id.invoice_vat_percent);
        mTotalAmount2 = findViewById(R.id.invoice_vat_amount);
        mCusName = findViewById(R.id.invoice_cus_name);


        mVatNumber = findViewById(R.id.vat_number);
        mCompany = findViewById(R.id.compnay_name);
        mTotalItems = findViewById(R.id.invoice_total_items);
        mRecycleView = findViewById(R.id.item_recycle_view);
      //  mPrint1 = findViewById(R.id.print_print_1);
        mPrint2 = findViewById(R.id.print_print_2);
        mRootView = findViewById(R.id.root_view);
        imageView = findViewById(R.id.print_qr_code);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setHasFixedSize(true);


        loadAd( getString(R.string.in_ad_unit_id2));


        AdView adView = findViewById(R.id.print_adView);

        Admob.bannerAdd(getApplicationContext(), adView);


    }



       @SuppressLint("SetTextI18n")
       public  void getItem(){

        String invoiceId = invoiceData.getInvoiceId();
           databaseAccess.open();
           mList = databaseAccess.getOrderDetailsList(invoiceId,type);
           itemAdapter = new PurchaseItemAdapter(getApplicationContext(), mList, mRecycleView);

           mRecycleView.setAdapter(itemAdapter);

           mTotalItems.setText(mList.size() +"");

       }


    public  void printItem(){

        String invoiceId = invoiceData.getInvoiceId();
        databaseAccess.open();
        mList = databaseAccess.getOrderDetailsList(invoiceId,type);

        PrnStrFormat format = new PrnStrFormat();

        for(int i = 0; i<mList.size(); i++){

            String item_id =  mList.get(i).get("item_id");

                   databaseAccess.open();

                    String name = databaseAccess.getItemName(item_id);
                    String qty  = mList.get(i).get("item_qty");
                    String price = mList.get(i).get("item_price");
                     assert qty != null;
                    assert price != null;
                    double total =  Double.parseDouble(qty)* Double.parseDouble(price);

                       format.setAli(Layout.Alignment.ALIGN_OPPOSITE);
                       format.setStyle(PrnTextStyle.BOLD);
                       format.setTextSize(25);
                       mPrinter.setPrintAppendString(name, format);

                     format.setAli(Layout.Alignment.ALIGN_NORMAL);
                     mPrinter.setPrintAppendString(qty  +" X " + price +" = "  +total + " ريـــــال ", format);



        }

    }




    private void printPic(Bitmap bitmap) {


                final int i = mSys.sdkInit();

                if (i == SdkResult.SDK_OK) {
                  Log.d("open","open");
                    int printStatus = mPrinter.getPrinterStatus();

                    if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                        Log.d("error","paper roll out")  ;
                    } else {

                        PrnStrFormat format = new PrnStrFormat();
                        mPrinter.setPrintAppendString(" ", format);
                        mPrinter.setPrintAppendString(" ", format);
                        mPrinter.setPrintAppendBitmap(bitmap, Layout.Alignment.ALIGN_NORMAL,382);
                        mPrinter.setPrintAppendString(" ", format);
                        mPrinter.setPrintAppendString(" ", format);
                        mPrinter.setPrintAppendString(" ", format);
                        mPrinter.setPrintAppendString(" ", format);

                        printStatus = mPrinter.setPrintStart();
                        if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {

                            Log.d("error","paper roll out");

                        }
                    }
                }else {
                   Log.d("error", i + "error");
                }

            }

  private void printManual(Bitmap qrcodeBitmap) {


                final int i = mSys.sdkInit();

                if (i == SdkResult.SDK_OK) {
                  Log.d("open","open");
                    int printStatus = mPrinter.getPrinterStatus();

                    if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                        Log.d("error","paper roll out")  ;
                    } else {

                        PrnStrFormat format = new PrnStrFormat();
                        mPrinter.setPrintAppendString(" ", format);
                        mPrinter.setPrintAppendString(" ", format);

                        format.setAli(Layout.Alignment.ALIGN_CENTER);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(40);

                        mPrinter.setPrintAppendString(companyName, format);
                        mPrinter.setPrintAppendString(" ", format);

                        format.setAli(Layout.Alignment.ALIGN_CENTER);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(35);
                        mPrinter.setPrintAppendString( " فاتورة ضريبية المبسطة "     , format);


                        format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(25);
                        mPrinter.setPrintAppendString( " رقم الضريبة        :"  + vatNumber   , format);


                        format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(25);
                        mPrinter.setPrintAppendString(" رقم الفاتورة       : " +  invoiceData.getInvoiceId()  , format);


                        format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(25);
                        mPrinter.setPrintAppendString(   " تاريخ الفاتورة            : "  +invoiceData.getIssueTime(), format);


                        format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(25);
                        mPrinter.setPrintAppendString(" اسم العميل                :" +cusName, format);



                        mPrinter.setPrintAppendString("---------------------------------", format);
                        printItem();

                        mPrinter.setPrintAppendString("----------------------------------", format);

                        format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(25);

                       double value1 =Double.parseDouble(new DecimalFormat("0.00").format(invoiceData.getSubTotal()));
                        mPrinter.setPrintAppendString( " مبلغ الإجمالي             :"+ value1  , format);


                        format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(30);

                        double disvalue =Double.parseDouble(new DecimalFormat("0.00").format(invoiceData.getDiscount()));
                        mPrinter.setPrintAppendString(   " خصم الصافي                : " + disvalue, format);

                        format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(25);

                        double val = invoiceData.getSubTotal()- invoiceData.getDiscount();
                      double  value =Double.parseDouble(new DecimalFormat("0.00").format(val));
                        mPrinter.setPrintAppendString(    " الإجمالي (غير شامل ضريبة) : "+value+""  , format);


                        double  valueTax =Double.parseDouble(new DecimalFormat("0.00").format(invoiceData.getTax()));
                        format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(25);
                        mPrinter.setPrintAppendString( " مجموع ضريبة               : " + valueTax, format);



                        format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(25);

                        mPrinter.setPrintAppendString( " إجمالي المبلغ (شامل ضريبة) :" +invoiceData.getTotalMoney()  , format);


                       format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(25);
                        mPrinter.setPrintAppendString( " المبلغ المدفوع            :" +invoiceData.getTotalMoney()  , format);



                        format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(25);
                        mPrinter.setPrintAppendString( " إجمالي المستحق             : " + invoiceData.getDueMoney() +  invoiceData.getCurrency()  , format);

                      format.setAli(Layout.Alignment.ALIGN_NORMAL);
                        format.setStyle(PrnTextStyle.BOLD);
                        format.setTextSize(25);
                        mPrinter.setPrintAppendString( ""+invoiceData.getTotalMoney()  , format);




                        mPrinter.setPrintAppendString(" ", format);
                        mPrinter.setPrintAppendString(" ", format);

                        mPrinter.setPrintAppendBitmap(qrcodeBitmap, Layout.Alignment.ALIGN_CENTER);

                        mPrinter.setPrintAppendString(" ", format);
                        mPrinter.setPrintAppendString(" ", format);
                        mPrinter.setPrintAppendString(" ", format);
                        mPrinter.setPrintAppendString(" ", format);

                        printStatus = mPrinter.setPrintStart();
                        if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {

                            Log.d("error","paper roll out");

                        }
                    }
                }else {
                   Log.d("error", i + "error");
                }

            }





    public Bitmap  qrCode(String string){

        Bitmap bitmap = null;



        QRGEncoder  qrgEncoder = new QRGEncoder(string, QRGContents.Type.TEXT,20);
        qrgEncoder.setColorBlack(Color.BLACK);
        qrgEncoder.setColorWhite(Color.WHITE);

        try {
            bitmap = qrgEncoder.getBitmap();
            bitmap = getResizedBitmap(bitmap,250,250);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return  bitmap;
    }





    public void loadAd(String id ) {

        MobileAds.initialize(this, initializationStatus -> {});

        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("ABCDEF012345"))
                        .build());

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                id,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitial = interstitialAd;
                        Log.i("error_admob", "onAdLoaded");

                           interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {

                                        mInterstitial = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {

                                        mInterstitial = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("error", loadAdError.getMessage());
                        mInterstitial = null;

                        @SuppressLint("DefaultLocale") String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Toast.makeText(
                               PrintertActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        if (mInterstitial!= null) {
            mInterstitial.show(PrintertActivity.this);
        } else {
           // loadAd(id);
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }



    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        return Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
    }

}
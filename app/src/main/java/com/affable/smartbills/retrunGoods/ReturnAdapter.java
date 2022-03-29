package com.affable.smartbills.retrunGoods;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.invoice.pdf.PurchaseItemAdapter;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ReturnAdapter extends RecyclerView.Adapter<ReturnAdapter.ReturnViewHolder> {

    private Context context;
    private final List<HashMap<String, String>> purchaseList;
    RecyclerView recyclerView;
    public  DatabaseAccess databaseAccess ;
    public  String type ="";
    public  String totalPrice ="";
    private TextView textCount ;


    public ReturnAdapter(Context context, List<HashMap<String, String>> clientDataList, RecyclerView recyclerView, String type , TextView textCount) {
        this.context = context;
        this.purchaseList = clientDataList;
        this.recyclerView = recyclerView;
        this.type = type;
        this.textCount = textCount;

    }

    @NonNull
    @Override
    public ReturnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.return_item_list, parent, false);
        return new ReturnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReturnViewHolder holder, int position) {



        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();



        final String item_id = purchaseList.get(position).get("item_id");
        String  itemName =    databaseAccess.getItemName(item_id);


        databaseAccess.open();
        String weight_unit_id =    databaseAccess.getItemWeightUnitID(item_id);

        String invoiceId = purchaseList.get(position).get("invoice_id");
        String item_weight = purchaseList.get(position).get("item_weight");
      //  String  currentStock = purchaseList.get(position).get("stock");

        String qty = purchaseList.get(position).get("item_qty");

        String price = purchaseList.get(position).get("item_price");


        holder.mName.setText(itemName);
        holder.mQty.setText(qty);
        holder.mPrice.setText(price);

    //    holder.mStock.setText(currentStock);


        holder.txt_plus.setOnClickListener(v -> {

           int  returnGood = Integer.parseInt(holder.txt_number.getText().toString());
           int   fstock = Integer.parseInt(qty);


                if (returnGood < fstock) {

                    returnGood = returnGood + 1;

                    holder.txt_number.setText(returnGood + "");


                    Toasty.info(context, "item is available", Toast.LENGTH_SHORT).show();


                } else {

                    Toasty.error(context, context.getString(R.string.stock_limit_crossed), Toast.LENGTH_SHORT).show();

                }


        });

         holder.mSubmit.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {


                 String returnGood = holder.txt_number.getText().toString();

                     databaseAccess.open();
                 int check = databaseAccess.addToCart(
                         item_id,
                         item_weight,
                         weight_unit_id,
                         price,
                         Integer.parseInt(returnGood),
                         qty,
                         type
                 );


                 if (check == 1) {

                     Toasty.success(context, R.string.item_added_to_cart, Toast.LENGTH_SHORT).show();

                 } else if (check == 2) {

                     Toasty.info(context, R.string.already_added_to_cart, Toast.LENGTH_SHORT).show();

                 } else {

                     Toasty.error(context, R.string.item_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                 }

                 databaseAccess.open();
                 int count = databaseAccess.getCartItemCount(type);

                 if (textCount != null) {
                     if (count == 0) {
                         textCount.setVisibility(View.INVISIBLE);
                     } else {
                         textCount.setVisibility(View.VISIBLE);
                         textCount.setText(String.valueOf(count));
                     }
                 }
             }
         });

        holder.txt_minus.setOnClickListener(v -> {

          int   quantity = Integer.parseInt(holder.txt_number.getText().toString());


                if (quantity > 0) {
                    quantity--;


                    holder.txt_number.setText(quantity + "");



                } else {
                    Toasty.error(context, context.getString(R.string.stock_limit_crossed), Toast.LENGTH_SHORT).show();
                }











        });



        //   int total =  Integer.valueOf(qty) * Integer.valueOf(price);
     //   holder.mTotal.setText(total+"");





    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    class ReturnViewHolder extends RecyclerView.ViewHolder  {

        TextView mName, mQty,mPrice,txt_number;
         ImageButton txt_minus, txt_plus;
         Button mSubmit;



        public ReturnViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.return_item_name_item_view);
            mPrice = itemView.findViewById(R.id.return_item_price_item_veiw);
      //      mStock = itemView.findViewById(R.id.return_item_stock_item_view);
            mQty = itemView.findViewById(R.id.return_item_qty_item_view);
            txt_minus = itemView.findViewById(R.id.txt_minus);
            txt_plus = itemView.findViewById(R.id.txt_plus);
            txt_number = itemView.findViewById(R.id.txt_number);
            mSubmit = itemView.findViewById(R.id.add_return_stock);



        }


    }

}

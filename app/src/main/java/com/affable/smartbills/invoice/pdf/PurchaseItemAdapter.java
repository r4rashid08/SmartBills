package com.affable.smartbills.invoice.pdf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.R;
import com.affable.smartbills.clients.EditClientActivity;
import com.affable.smartbills.database.DatabaseAccess;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PurchaseItemAdapter extends RecyclerView.Adapter<PurchaseItemAdapter.PurViewHolder> {

    private Context context;
    private final List<HashMap<String, String>> purchaseList;
     RecyclerView recyclerView;
     public  DatabaseAccess databaseAccess ;


    public PurchaseItemAdapter(Context context, List<HashMap<String, String>> clientDataList, RecyclerView recyclerView ) {
        this.context = context;
        this.purchaseList = clientDataList;
        this.recyclerView = recyclerView;

    }

    @NonNull
    @Override
    public PurViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.print_item_view, parent, false);
        return new PurViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurViewHolder holder, int position) {

        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();

        final String item_id = purchaseList.get(position).get("item_id");

        String  itemName =    databaseAccess.getItemName(item_id);

        String invoiceId = purchaseList.get(position).get("invoice_id");

        String qty = purchaseList.get(position).get("item_qty");
        String price = purchaseList.get(position).get("item_price");


        holder.mName.setText(itemName);
        holder.mQty.setText(qty);
        holder.mPrice.setText(price);
        int total =  Integer.valueOf(qty) * Integer.valueOf(price);
        holder.mTotal.setText(total+"");





    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

     class PurViewHolder extends RecyclerView.ViewHolder  {

        TextView mName, mQty,mPrice,mTotal;


        public PurViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.print_item_name);
            mPrice = itemView.findViewById(R.id.print_item_price);
            mTotal = itemView.findViewById(R.id.print_item_total);
            mQty = itemView.findViewById(R.id.print_item_qty);




        }


    }

}

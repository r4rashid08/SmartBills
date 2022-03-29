package com.affable.smartbills.report.report_fragments;

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

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private Context context;
    private final List<HashMap<String, String>> clientDataList;
    private RecyclerView recyclerView;
    private View noData;

    public ReportAdapter(Context context, List<HashMap<String, String>> clientDataList, RecyclerView recyclerView, View noData) {
        this.context = context;
        this.clientDataList = clientDataList;
        this.recyclerView = recyclerView;
        this.noData = noData;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.report_item_view, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {

        final String invoice_id = clientDataList.get(position).get("invoice_id");
        String discount = clientDataList.get(position).get("discount");
        String tax = clientDataList.get(position).get("tax");
        String price = clientDataList.get(position).get("total_price");
        String time = clientDataList.get(position).get("order_time");
        String date = clientDataList.get(position).get("order_date");






        holder.invoice.setText(invoice_id);
        holder.discount.setText(discount);
        holder.tax.setText(tax);
        holder.time.setText(date + " :"+ time);
        holder.price.setText(price);



    }

    @Override
    public int getItemCount() {
        return clientDataList.size();
    }

    class ReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView invoice, tax, price, discount, time;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            invoice = itemView.findViewById(R.id.report_invoice_item_view);
            tax = itemView.findViewById(R.id.report_vat_item_view);
            price = itemView.findViewById(R.id.report_price_item_view);
            discount = itemView.findViewById(R.id.report_discount_item_view);
            time = itemView.findViewById(R.id.report_date_item_view);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {



        }

    }

}

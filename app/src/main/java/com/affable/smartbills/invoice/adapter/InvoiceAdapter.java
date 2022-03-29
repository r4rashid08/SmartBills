package com.affable.smartbills.invoice.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;
import com.affable.smartbills.invoice.OrderDetailsActivity;
import com.affable.smartbills.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private Context context;
    private List<HashMap<String, String>> orderList;

    private RecyclerView recyclerView;
    private LinearLayout noData;
    private DatabaseAccess databaseAccess;
     static  String  type ;


    public InvoiceAdapter(Context context, List<HashMap<String, String>> orderList, RecyclerView recyclerView, LinearLayout noData,String type) {
        this.context = context;
        this.orderList = orderList;
        this.recyclerView = recyclerView;
        this.noData = noData;
        this.type = type;

    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_orderlist, parent, false);

        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {

        databaseAccess = DatabaseAccess.getInstance(context);

        HashMap<String, String> order = orderList.get(position);

        final String invoice_id = order.get("invoice_id");
        String order_time = order.get("order_time");
        String order_date = order.get("order_date");
        String order_type = order.get("order_type");
        String order_status = order.get("order_status");
        String customer_name = order.get("customer_name");
        String order_discount = order.get("discount");
        String order_tax = order.get("tax");
        String order_price = order.get("total_price");





        holder.txtCustomerName.setText(customer_name);
        holder.txtOrderId.setText(String.format("Invoice Number: %s", invoice_id));
        holder.txtPaymentMethod.setText(String.format("Date: %s",order_date +" : " + order_time));
        holder.txtOrderType.setText(String.format("Order Type: %s", order_type));
        holder.txtDate.setText(String.format("Total Price: %s", order_price));

        holder.tax.setText(String.format("Tax: %s", order_tax));
        holder.amount.setText(String.format("Discount Amount: %s", order_discount));

        holder.txtOrderStatus.setText(order_status);

        if (order_status != null) {

            if (order_status.equals(Constant.CANCELED)) {
                holder.txtOrderStatus.setBackgroundResource(R.color.red);
            } else if (order_status.equals(Constant.COMPLETED)) {
                holder.txtOrderStatus.setBackgroundResource(R.color.green);
            }


            if (order_status.equals(Constant.PENDING)) {

                holder.txtOrderStatus.setOnClickListener(v -> {

                    //create custom dialog
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView = li.inflate(R.layout.dialog_order_status, null);
                    dialog.setView(dialogView);

                    final TextView txtComplete = dialogView.findViewById(R.id.txtComplete);
                    final TextView txtCancel = dialogView.findViewById(R.id.txtCancel);

                    final AlertDialog alertDialog = dialog.create();
                    alertDialog.show();

                    txtComplete.setOnClickListener(v1 -> {

                        changeStatus(holder, invoice_id, Constant.COMPLETED);
                        alertDialog.dismiss();

                    });

                    txtCancel.setOnClickListener(v12 -> {

                        changeStatus(holder, invoice_id, Constant.CANCELED);

                        //on cancel restore item stock
                        
                        restoreItemStock(invoice_id);
                        alertDialog.dismiss();
                    });


                });
            }
        }

        holder.itemView.setOnClickListener(v -> {

            assert order_status != null;
            if (order_status.equals(Constant.PENDING) || order_status.equals(Constant.COMPLETED)) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("invoice_id", invoice_id);
                intent.putExtra("type", type);
                intent.putExtra("status", order_status);

                context.startActivity(intent);
            }
        });


    }



    private void restoreItemStock(String invoice_id) {

        /*
         * 1.get order details
         * 2.get stock & item_id
         * 3. loop though the updateStock() method
         */

        databaseAccess.open();
        ArrayList<HashMap<String, String>> orderDetailsList = databaseAccess.getOrderDetailsList(invoice_id,type);

        if (orderDetailsList.isEmpty()) {

            for (HashMap<String, String> map : orderDetailsList) {

                String item_id = map.get("item_id");
                String stock = map.get("stock");
                String item_qty = map.get("item_qty");

                int reset_stock = 0;
                if (item_qty != null && stock != null)
                    reset_stock = Integer.parseInt(stock) + Integer.parseInt(item_qty);

                databaseAccess.open();
                databaseAccess.updateItemStock(item_id, String.valueOf(reset_stock));

            }
            databaseAccess.close();

        }

    }



    @SuppressLint("NotifyDataSetChanged")
    private void changeStatus(InvoiceViewHolder holder, String invoice_id, String status) {


        if(status.equals(Constant.COMPLETED)) {
            if (type.equals("sale") || type.equals("purchase") || type.equals("sale_return") || type.equals("purchase_return")) {
                databaseAccess.open();
                int paymentCount = databaseAccess.getPaymentCount(invoice_id,type);

                //get payment info
                databaseAccess.open();
                HashMap<String, String> paymentMap = databaseAccess.getSinglePayment(invoice_id, String.valueOf(paymentCount),type);

                if (paymentMap.size()>0) {

                    String total_money = paymentMap.get("total_money");
                    String total_pay = paymentMap.get("total_payed");
                    String due = paymentMap.get("due_money");


                    if (Double.parseDouble(due) == 0) {

                        databaseAccess.open();
                        if (databaseAccess.updateOrderStatus(invoice_id, status)) {

                            orderList.remove(holder.getAdapterPosition());
                            notifyDataSetChanged();
                            if (orderList.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                noData.setVisibility(View.VISIBLE);
                            }

                        }
                    } else {

                        Toast.makeText(context, "please pay the remaining  bill amount :  " + due, Toast.LENGTH_SHORT).show();

                    }
                }
            }
        }else  if(status.equals(Constant.CANCELED)){

            databaseAccess.open();
            int paymentCount = databaseAccess.getPaymentCount(invoice_id,type);
            Log.d("total_pay",paymentCount+"");

            databaseAccess.open();
            HashMap<String, String> paymentMap = databaseAccess.getSinglePayment(invoice_id, paymentCount + "",type);

            if (paymentMap.size()>0) {


                String due = paymentMap.get("due_money");
                String total_pay = paymentMap.get("total_payed");


                if (Double.parseDouble(total_pay) == 0) {
                    databaseAccess.open();
                    if (databaseAccess.updateOrderStatus(invoice_id, status)) {

                        orderList.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                        if (orderList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            noData.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    Toast.makeText(context, "you already take a  " + total_pay + " amount so you can't delete it ", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class InvoiceViewHolder extends RecyclerView.ViewHolder {

        TextView txtCustomerName, txtOrderId, txtOrderStatus, txtOrderType, txtPaymentMethod, txtDate, amount, tax;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCustomerName = itemView.findViewById(R.id.txt_customer_name);
            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            txtOrderType = itemView.findViewById(R.id.txt_order_type);
            txtPaymentMethod = itemView.findViewById(R.id.txt_payment_method);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtOrderStatus = itemView.findViewById(R.id.txt_order_status);
            amount = itemView.findViewById(R.id.txt_amount);
            tax = itemView.findViewById(R.id.txt_tax);

        }
    }


}

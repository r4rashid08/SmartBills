package com.affable.smartbills.Inventory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.affable.smartbills.R;
import com.affable.smartbills.database.DatabaseAccess;

import java.util.HashMap;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {


    private Context context;
    private List<HashMap<String, String>> itemsList;
     HashMap<String, String> dataList = new HashMap<>();
    private RecyclerView recyclerView;
    private View noData;

    public InventoryAdapter(Context context, List<HashMap<String, String>> itemsList, RecyclerView recyclerView, View noData) {
        this.context = context;
        this.itemsList = itemsList;
        this.recyclerView = recyclerView;
        this.noData = noData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);

        final String purchase_id = itemsList.get(position).get("purchase_id");
        final String item_id = itemsList.get(position).get("item_id");
        String client_name = itemsList.get(position).get("client_name");
        String item_price = itemsList.get(position).get("item_price");
        String item_qty = itemsList.get(position).get("item_qty");
        String user_id = itemsList.get(position).get("user_id");
        String date = itemsList.get(position).get("date");





        //get Currency user selected
        databaseAccess.open();
        //TODO: (if multi user feature added then) change username for getCurrency
        String currency = databaseAccess.getCurrency();

        databaseAccess.open();
        dataList = databaseAccess.getItemInfo(item_id);

        String item_name = dataList.get("item_name");
        String base64Image = dataList.get("item_image");


        //set the values in recyclerItem
        holder.txtItemName.setText(item_name);
        holder.txtCategory.setText("Supplier : " + client_name);
        holder.txtPrice.setText("Purchase Price: " + item_price + currency);
        holder.txtWeight.setText("quantity: " + item_qty);

        //item image set
        if (base64Image != null) {
            if (base64Image.length() < 6) {
                Log.d("64Base", base64Image);
                holder.itemImage.setImageResource(R.drawable.image_placeholder);
            } else {

                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.itemImage.setImageBitmap(bitmap);

            }
        }

        //delete btn
    /*    holder.imgDelete.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.want_to_delete_item)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {


                        databaseAccess.open();
                        boolean deleteProduct = databaseAccess.deleteItem(item_id);

                        if (deleteProduct) {
                            Toasty.error(context, R.string.item_deleted, Toast.LENGTH_SHORT).show();

                            itemsList.remove(holder.getAdapterPosition());

                            // Notify that item at position has been removed
                            notifyItemRemoved(holder.getAdapterPosition());

                            //show no data indicator when list in empty
                            if (itemsList.size() <= 0) {
                                recyclerView.setVisibility(View.GONE);
                                noData.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();

                    })
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();

        });
*/
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView txtItemName, txtCategory, txtPrice, txtWeight;
        ImageView imgDelete, itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtItemName = itemView.findViewById(R.id.txt_item_name);
            txtCategory = itemView.findViewById(R.id.txt_category);
            txtPrice = itemView.findViewById(R.id.txt_price);
            txtWeight = itemView.findViewById(R.id.txt_weight);

            imgDelete = itemView.findViewById(R.id.img_delete);
            itemImage = itemView.findViewById(R.id.item_image);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

         /*   final String item_code = itemsList.get(getAdapterPosition()).get("item_id");
            context.startActivity(new Intent(context, RetrunInventoryActivity.class)
                    .putExtra("item_id", item_code));

*/
        }


    }

}

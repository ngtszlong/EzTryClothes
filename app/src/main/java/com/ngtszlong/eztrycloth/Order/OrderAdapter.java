package com.ngtszlong.eztrycloth.Order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ngtszlong.eztrycloth.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Order> orderArrayList;
    private double Total = 0;

    public OrderAdapter(Context context, ArrayList<Order> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_orderdetail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderArrayList.get(position);
        for (int i = 0; i < orderArrayList.size(); i++) {
            Total = Total + Double.parseDouble(orderArrayList.get(i).getPrice()) * Double.parseDouble(orderArrayList.get(i).getQuantity());
        }
        if (position == 0) {
            holder.txt_date.setText(order.getDate());
            holder.txt_address.setText(order.getAddress());
            holder.txt_price.setText(String.valueOf(Total));
        } else if (position>0){
            holder.txt_date.setVisibility(View.GONE);
            holder.txt_address.setVisibility(View.GONE);
            holder.txt_price.setVisibility(View.GONE);
            holder.date.setVisibility(View.GONE);
            holder.address.setVisibility(View.GONE);
            holder.total.setVisibility(View.GONE);
        }
        if (!order.getImage().equals("")) {
            Picasso.get().load(order.getImage()).into(holder.img_order_image);
        }
        holder.size.setText(order.getSize());
        holder.txt_order_name.setText(order.getName());
        holder.txt_order_itemprice.setText(order.getPrice());
        holder.quantity.setText(order.getQuantity());
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_date;
        TextView txt_price;
        TextView txt_address;
        ImageView img_order_image;
        TextView txt_order_name;
        TextView txt_order_itemprice;
        TextView date;
        TextView total;
        TextView address;
        TextView quantity;
        TextView size;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_date = itemView.findViewById(R.id.txt_order_date);
            txt_price = itemView.findViewById(R.id.txt_order_price);
            txt_address = itemView.findViewById(R.id.txt_order_address);
            img_order_image = itemView.findViewById(R.id.img_order_image);
            txt_order_name = itemView.findViewById(R.id.txt_order_name);
            txt_order_itemprice = itemView.findViewById(R.id.txt_order_itemprice);
            size = itemView.findViewById(R.id.txt_order_size);
            date = itemView.findViewById(R.id.txt_date);
            total = itemView.findViewById(R.id.txt_total);
            address = itemView.findViewById(R.id.txt_address);
            quantity = itemView.findViewById(R.id.txt_order_quantity);
        }
    }
}

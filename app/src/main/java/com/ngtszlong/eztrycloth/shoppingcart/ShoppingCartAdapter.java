package com.ngtszlong.eztrycloth.shoppingcart;

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

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ShoppingCart> shoppingCartArrayList;
    private ShoppingCartAdapter.OnItemClickListener onItemClickListener;

    public ShoppingCartAdapter(Context context, ArrayList<ShoppingCart> shoppingCartArrayList) {
        this.context = context;
        this.shoppingCartArrayList = shoppingCartArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingCart shoppingCart = shoppingCartArrayList.get(position);
        holder.gender.setText(shoppingCart.getGender());
        holder.name.setText(shoppingCart.getName());
        holder.color.setText(shoppingCart.getColor());
        holder.price.setText(shoppingCart.getPrice());
        Picasso.get().load(shoppingCart.getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return shoppingCartArrayList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ShoppingCartFragment listener) {
        onItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView gender;
        TextView name;
        TextView color;
        TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.shopcart_image);
            gender = itemView.findViewById(R.id.shopcart_gender);
            name = itemView.findViewById(R.id.shopcart_name);
            color = itemView.findViewById(R.id.shopcart_color);
            price = itemView.findViewById(R.id.shopcart_price);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

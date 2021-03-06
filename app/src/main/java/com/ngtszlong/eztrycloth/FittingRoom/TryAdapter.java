package com.ngtszlong.eztrycloth.FittingRoom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ngtszlong.eztrycloth.R;
import com.ngtszlong.eztrycloth.shoppingcart.ShoppingCart;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TryAdapter extends RecyclerView.Adapter<TryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ShoppingCart> shoppingCartArrayList;
    private TryAdapter.OnItemClickListener onItemClickListener;

    public TryAdapter(Context context, ArrayList<ShoppingCart> shoppingCartArrayList) {
        this.context = context;
        this.shoppingCartArrayList = shoppingCartArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_body, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingCart shoppingCart = shoppingCartArrayList.get(position);
        Picasso.get().load(shoppingCart.getImage()).into(holder.imageView);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(FittingRoomFragment listener) {
        onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return shoppingCartArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_body);
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

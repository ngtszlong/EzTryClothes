package com.ngtszlong.eztrycloth.wishlist;

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

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Wishlist> wishlistArrayList;
    private WishlistAdapter.OnItemClickListener onItemClickListener;

    public WishlistAdapter(Context context, ArrayList<Wishlist> wishlistArrayList) {
        this.context = context;
        this.wishlistArrayList = wishlistArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wishlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wishlist wishlist = wishlistArrayList.get(position);
        holder.gender.setText(wishlist.getGender());
        holder.name.setText(wishlist.getName());
        holder.color.setText(wishlist.getColor());
        holder.price.setText(wishlist.getPrice());
        Picasso.get().load(wishlist.getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return wishlistArrayList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(WishListFragment listener) {
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
            image = itemView.findViewById(R.id.wishlist_image);
            gender = itemView.findViewById(R.id.wishlist_gender);
            name = itemView.findViewById(R.id.wishlist_name);
            color = itemView.findViewById(R.id.wishlist_color);
            price = itemView.findViewById(R.id.wishlist_price);
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

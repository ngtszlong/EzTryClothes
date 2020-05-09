package com.ngtszlong.eztrycloth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ngtszlong.eztrycloth.wishlist.Wishlist;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TryAdapter extends RecyclerView.Adapter<TryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Wishlist> wishlistArrayList;

    public TryAdapter(Context context, ArrayList<Wishlist> wishlistArrayList) {
        this.context = context;
        this.wishlistArrayList = wishlistArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.body_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wishlist wishlist = wishlistArrayList.get(position);
        Picasso.get().load(wishlist.getImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return wishlistArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_body);
        }
    }
}

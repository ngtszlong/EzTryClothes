package com.ngtszlong.eztrycloth.menu;

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

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MenuItem> menuItemArrayList;
    private MenuAdapter.OnItemClickListener mListener;

    public MenuAdapter(Context context, ArrayList<MenuItem> menuItemArrayList) {
        this.context = context;
        this.menuItemArrayList = menuItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        MenuItem menuItem = menuItemArrayList.get(position);
        viewHolder.textView.setText(menuItem.getName());
        Picasso.get().load(menuItem.getThumbnail()).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return menuItemArrayList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MenuFragment listener) {
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.txt_menu);
            imageView = view.findViewById(R.id.img_menu);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

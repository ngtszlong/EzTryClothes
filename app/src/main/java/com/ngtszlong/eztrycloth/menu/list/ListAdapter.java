package com.ngtszlong.eztrycloth.menu.list;

import android.content.Context;
import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ListItem> listItemArrayList;
    private ListAdapter.OnItemClickListener mListener;
    SharedPreferences preferences;

    public ListAdapter(Context context, ArrayList<ListItem> listItemArrayList) {
        this.context = context;
        this.listItemArrayList = listItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        preferences = context.getSharedPreferences("Setting", MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        String language = preferences.getString("My_Lang", "");
        ListItem listItem = listItemArrayList.get(position);
        if (language.equals("en")) {
            holder.list_name.setText(listItem.getName_Eng());
            holder.list_gender.setText(listItem.getGender());
        } else if (language.equals("zh")) {
            holder.list_name.setText(listItem.getName_Chi());
            if (listItem.getGender().equals("MEN")){
                holder.list_gender.setText("男裝");
            }else if (listItem.getGender().equals("WOMEN")) {
                holder.list_gender.setText("女裝");
            }
        }
        if (!listItem.getImage().isEmpty()) {
            Picasso.get().load(listItem.getImage()).into(holder.list_image);
        }
        holder.list_price.setText(listItem.getPrice());
    }

    @Override
    public int getItemCount() {
        return listItemArrayList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ListItemActivity listener) {
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView list_image;
        TextView list_name;
        TextView list_price;
        TextView list_gender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            list_image = itemView.findViewById(R.id.item_image);
            list_name = itemView.findViewById(R.id.item_name);
            list_price = itemView.findViewById(R.id.item_price);
            list_gender = itemView.findViewById(R.id.item_gender);
            itemView.setOnClickListener(new View.OnClickListener() {
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

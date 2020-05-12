package com.ngtszlong.eztrycloth.shoppingcart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ShoppingCart shoppingCart = shoppingCartArrayList.get(position);
        holder.gender.setText(shoppingCart.getGender());
        holder.name.setText(shoppingCart.getName());
        holder.color.setText(shoppingCart.getColor());
        holder.price.setText(shoppingCart.getPrice());
        if (!shoppingCart.getImage().equals("")) {
            Picasso.get().load(shoppingCart.getImage()).into(holder.image);
        }
        holder.elegantNumberButton.setNumber(shoppingCart.getQuantity());
        holder.elegantNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                shoppingCart.setQuantity(String.valueOf(newValue));
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                FirebaseUser user = fAuth.getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("ShoppingCart");
                reference.child(user.getUid()).child(shoppingCart.getStr()).setValue(shoppingCart);
            }
        });
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                FirebaseUser user = fAuth.getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ShoppingCart").child(user.getUid()).child(shoppingCart.getStr());
                reference.removeValue();
            }
        });
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
        ElegantNumberButton elegantNumberButton;
        ImageButton imageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.shopcart_image);
            gender = itemView.findViewById(R.id.shopcart_gender);
            name = itemView.findViewById(R.id.shopcart_name);
            color = itemView.findViewById(R.id.shopcart_color);
            price = itemView.findViewById(R.id.shopcart_price);
            imageButton = itemView.findViewById(R.id.shopcart_delete);
            elegantNumberButton = itemView.findViewById(R.id.txt_count);
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

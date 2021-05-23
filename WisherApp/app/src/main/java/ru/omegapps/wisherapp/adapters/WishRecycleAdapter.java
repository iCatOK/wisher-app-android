package ru.omegapps.wisherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.dto.Wish;
import ru.omegapps.wisherapp.interfaces.OnMyItemClickListener;
import ru.omegapps.wisherapp.interfaces.OnMyItemLongClickListener;

public class WishRecycleAdapter extends RecyclerView.Adapter<WishRecycleAdapter.WishViewHolder> {

    Context context;
    private ArrayList<Wish> wishes;
    OnMyItemClickListener onMyItemClickListener;
    OnMyItemLongClickListener onMyItemLongClickListener;

    @NonNull
    @Override
    public WishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.wish_layout, parent, false);
        return new WishViewHolder(v, onMyItemClickListener, onMyItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WishViewHolder holder, int position) {
        holder.wishText.setText(wishes.get(position).getText());
        holder.wishTitle.setText(wishes.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return wishes.size();
    }

    public WishRecycleAdapter(Context context, ArrayList<Wish> wishes, OnMyItemClickListener clickListener, OnMyItemLongClickListener longClickListener) {
        this.context = context;
        this.wishes = wishes;
        this.onMyItemClickListener = clickListener;
        this.onMyItemLongClickListener = longClickListener;
    }

    public static class WishViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        TextView wishTitle;
        TextView wishText;
        OnMyItemClickListener onMyItemClickListener;
        OnMyItemLongClickListener onMyItemLongClickListener;

        WishViewHolder(View itemView) {
            super(itemView);
            wishTitle = itemView.findViewById(R.id.wishTitle);
            wishText = itemView.findViewById(R.id.wishText);
        }

        public WishViewHolder(View v, OnMyItemClickListener onMyItemClickListener, OnMyItemLongClickListener onMyItemLongClickListener) {
            super(v);
            wishTitle = itemView.findViewById(R.id.wishTitle);
            wishText = itemView.findViewById(R.id.wishText);
            this.onMyItemClickListener = onMyItemClickListener;
            this.onMyItemLongClickListener = onMyItemLongClickListener;
            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onMyItemClickListener != null){
                onMyItemClickListener.onMyItemClick((getAdapterPosition()), v, false);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(onMyItemLongClickListener != null){
                onMyItemLongClickListener.onMyItemLongClick((getAdapterPosition()), v);
            }
            return true;
        }
    }
}

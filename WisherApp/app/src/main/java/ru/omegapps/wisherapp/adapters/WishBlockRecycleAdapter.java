package ru.omegapps.wisherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adroitandroid.chipcloud.ChipCloud;

import java.util.ArrayList;

import me.gujun.android.taggroup.TagGroup;
import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.dto.WishBlock;
import ru.omegapps.wisherapp.interfaces.OnMyItemClickListener;
import ru.omegapps.wisherapp.interfaces.OnMyItemLongClickListener;

public class WishBlockRecycleAdapter extends RecyclerView.Adapter<WishBlockRecycleAdapter.WishBlockViewHolder> {

    Context context;
    int wishBlockTypeId;
    private ArrayList<WishBlock> wishBlocks;
    OnMyItemClickListener onMyItemClickListener;
    OnMyItemLongClickListener onMyItemLongClickListener;

    @NonNull
    @Override
    public WishBlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(wishBlockTypeId, parent, false);
        return new WishBlockViewHolder(v, onMyItemClickListener, onMyItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WishBlockViewHolder holder, int position) {
        holder.wishText.setText(wishBlocks.get(position).getWishText());
        if(holder.privacyTag != null){
            holder.privacyTag.setText(wishBlocks.get(position).isPublic() ? "публичный" : "приватный");
            holder.privacyTag.setBackgroundResource(wishBlocks.get(position).isPublic() ?
                    R.drawable.privacy_tag_public : R.drawable.privacy_tag_private);
        }
        String[] chips = new String[wishBlocks.get(position).getTags().size()];
        chips = wishBlocks.get(position).getTags().toArray(chips);
        holder.tagGroup.setTags(chips);

    }

    @Override
    public int getItemCount() {
        return wishBlocks.size();
    }

    public static class WishBlockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView wishText;
        TagGroup tagGroup;
        TextView privacyTag;
        boolean isChosen = false;
        OnMyItemClickListener onMyItemClickListener;
        OnMyItemLongClickListener onMyItemLongClickListener;

        WishBlockViewHolder(View itemView, OnMyItemClickListener onMyItemClickListener) {
            super(itemView);
            wishText = itemView.findViewById(R.id.wishText);
            tagGroup = itemView.findViewById(R.id.tag_group_wish_blocks);
            privacyTag = itemView.findViewById(R.id.privacy_text_view);
            this.onMyItemClickListener = onMyItemClickListener;
            itemView.setOnClickListener(this);
        }

        WishBlockViewHolder(View itemView, OnMyItemClickListener onMyItemClickListener, OnMyItemLongClickListener onMyItemLongClickListener) {
            super(itemView);
            wishText = itemView.findViewById(R.id.wishText);
            tagGroup = itemView.findViewById(R.id.tag_group_wish_blocks);
            privacyTag = itemView.findViewById(R.id.privacy_text_view);
            this.onMyItemClickListener = onMyItemClickListener;
            this.onMyItemLongClickListener = onMyItemLongClickListener;
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            isChosen = !isChosen;
            if(onMyItemClickListener != null){
                onMyItemClickListener.onMyItemClick((getAdapterPosition()), v, isChosen);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(onMyItemLongClickListener != null)
                onMyItemLongClickListener.onMyItemLongClick((getAdapterPosition()), v);
            return true;
        }
    }

    public WishBlockRecycleAdapter(Context context, ArrayList<WishBlock> wishBlocks, int wishBlockTypeId, OnMyItemClickListener onMyItemClickListener, OnMyItemLongClickListener onMyItemLongClickListener) {
        this.context = context;
        this.wishBlocks = wishBlocks;
        this.wishBlockTypeId = wishBlockTypeId;
        this.onMyItemClickListener = onMyItemClickListener;
        this.onMyItemLongClickListener = onMyItemLongClickListener;
    }

    public WishBlockRecycleAdapter(Context context, ArrayList<WishBlock> wishBlocks, int wishBlockTypeId){
        this.context = context;
        this.wishBlocks = wishBlocks;
        this.wishBlockTypeId = wishBlockTypeId;
    }
}

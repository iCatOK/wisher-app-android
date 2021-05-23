package ru.omegapps.wisherapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.adapters.WishBlockRecycleAdapter;
import ru.omegapps.wisherapp.dto.WishBlock;
import ru.omegapps.wisherapp.fragments.edit.CreateWishBlock;
import ru.omegapps.wisherapp.fragments.edit.EditWishBlockFragment;
import ru.omegapps.wisherapp.interfaces.OnMyItemClickListener;
import ru.omegapps.wisherapp.interfaces.OnMyItemLongClickListener;
import ru.omegapps.wisherapp.utils.MainUtils;

public class WishBlocksFragment extends Fragment {
    private static final String WBLOCK_KEY = "Wishblocks";
    private static final String PUBLIC_WB_KEY = "PublicWishblocks";
    ArrayList<WishBlock> wishBlocks;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference wishBlockRef;
    private DatabaseReference publicWishBlockRef;
    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;

    RelativeLayout progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wishblocks, container, false);
        wishBlocks = new ArrayList<>();

        progressBar = v.findViewById(R.id.wishblocks_pb);
        progressBar.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerViewWishBlocks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

        OnMyItemClickListener onMyItemClickListener = (position, v1, isChosen) -> goToEditWB(position);

        OnMyItemLongClickListener onMyItemLongClickListener = (position, v12) ->
                showAlertDialogToDeleteWishBlock(position);


        recyclerView.setAdapter(new WishBlockRecycleAdapter(
                v.getContext(),
                wishBlocks,
                R.layout.wish_block_layout,
                onMyItemClickListener,
                onMyItemLongClickListener));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        if(currentUser != null){
            wishBlockRef = FirebaseDatabase.getInstance().getReference(WBLOCK_KEY);
            publicWishBlockRef = FirebaseDatabase.getInstance().getReference(PUBLIC_WB_KEY);

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    wishBlocks.clear();
                    DataSnapshot userWb = snapshot.child(currentUser.getUid());
                    for(DataSnapshot ds: userWb.getChildren()) {
                        WishBlock newWishBlock = ds.getValue(WishBlock.class);
                        if(newWishBlock != null){
                            newWishBlock.setUuid(ds.getKey());
                            if(newWishBlock.getTags() == null)
                                newWishBlock.setTags(new ArrayList<>());
                            wishBlocks.add(newWishBlock);
                        }
                    }
                    recyclerView.getAdapter().notifyDataSetChanged();
                    MainUtils.hideProgressBar(progressBar, getActivity());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(),
                            "Не удалось загрузить данные",
                            Toast.LENGTH_SHORT
                    ).show();
                    MainUtils.hideProgressBar(progressBar, getActivity());
                }
            };

            wishBlockRef.addValueEventListener(valueEventListener);
        }

        FloatingActionButton plusButton = v.findViewById(R.id.addWishBlockButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_wisher, new CreateWishBlock())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }

    private void goToEditWB(int position) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_wisher,
                        EditWishBlockFragment.newInstance(
                                wishBlocks.get(position).getWishText(),
                                wishBlocks.get(position).getUuid(),
                                wishBlocks.get(position).getTags(),
                                wishBlocks.get(position).getFilters(),
                                wishBlocks.get(position).isPublic()))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(wishBlockRef != null){
            wishBlockRef.removeEventListener(valueEventListener);
        }
    }

    private void showAlertDialogToDeleteWishBlock(int position){
        String wishText = wishBlocks.get(position).getWishText().substring(0, 20) + "...";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Удаление блока поздравления");
        builder.setMessage("Хотите удалить блок поздравления '" + wishText + "'?");

        builder.setPositiveButton("Да!", (dialog, which) -> deleteWishBlockByPosition(position));
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteWishBlockByPosition(int position) {
        wishBlockRef
                .child(currentUser.getUid())
                .child(wishBlocks.get(position).getUuid())
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(getContext(), "Удалено успешно!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), "Не удалось удалить", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

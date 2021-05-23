package ru.omegapps.wisherapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.adapters.WishRecycleAdapter;
import ru.omegapps.wisherapp.data_agents.FireBaseDataAgent;
import ru.omegapps.wisherapp.data_agents.LocalDataAgent;
import ru.omegapps.wisherapp.dto.Wish;
import ru.omegapps.wisherapp.enums.WishEnum;
import ru.omegapps.wisherapp.fragments.auth.AuthorizatonFragment;
import ru.omegapps.wisherapp.fragments.edit.EditWishBlockFragment;
import ru.omegapps.wisherapp.fragments.edit.EditWishFragment;
import ru.omegapps.wisherapp.fragments.wishgen.AdresseeConfigFragment;
import ru.omegapps.wisherapp.interfaces.DataAgent;
import ru.omegapps.wisherapp.interfaces.OnMyItemClickListener;
import ru.omegapps.wisherapp.interfaces.OnMyItemLongClickListener;
import ru.omegapps.wisherapp.utils.MainUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMyItemClickListener, OnMyItemLongClickListener {

    ArrayList<Wish> wishes;
    FloatingActionButton plusButton;
    DatabaseReference wishRef;
    private static final String WISH_KEY = "Wishes";
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    RecyclerView recyclerView;
    RelativeLayout progressBar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //dataAgent = new LocalDataAgent();
        wishes = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = v.findViewById(R.id.recyclerViewWishes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setAdapter(new WishRecycleAdapter(v.getContext(), wishes, this, this));

        progressBar = v.findViewById(R.id.wish_pb);
        progressBar.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            wishRef = FirebaseDatabase.getInstance().getReference(WISH_KEY);
            wishRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    wishes.clear();
                    DataSnapshot userWishes = snapshot.child(currentUser.getUid());
                    for(DataSnapshot ds: userWishes.getChildren()) {
                        Wish newWish = ds.getValue(Wish.class);
                        if(newWish != null){
                            newWish.setUuid(ds.getKey());
                            wishes.add(newWish);
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
            });
        }

        recyclerView.getAdapter().notifyDataSetChanged();

        plusButton = v.findViewById(R.id.addWishButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_wisher, new AdresseeConfigFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(currentUser == null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_wisher, new AuthorizatonFragment())
                    .commit();
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onMyItemClick(int position, View v, boolean isChosen) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_wisher,
                        EditWishFragment.newInstance(
                                wishes.get(position).getTitle(),
                                wishes.get(position).getText(),
                                wishes.get(position).getUuid()
                        ))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMyItemLongClick(int position, View v) {
        String wishText = wishes.get(position).getText().substring(0, 20) + "...";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Удаление поздравления");
        builder.setMessage("Хотите удалить поздравление '" + wishText + "'?");

        builder.setPositiveButton("Да!", (dialog, which) -> deleteWishByPosition(position));
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteWishByPosition(int position) {
        wishRef
                .child(currentUser.getUid())
                .child(wishes.get(position).getUuid())
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(getContext(), "Удалено успешно!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), "Не удалось удалить", Toast.LENGTH_SHORT).show();
                });
    }
}
package ru.omegapps.wisherapp.fragments.wishgen;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.adapters.WishBlockRecycleAdapter;
import ru.omegapps.wisherapp.data_agents.LocalDataAgent;
import ru.omegapps.wisherapp.dto.WishBlock;
import ru.omegapps.wisherapp.fragments.HomeFragment;
import ru.omegapps.wisherapp.interfaces.DataAgent;
import ru.omegapps.wisherapp.interfaces.OnMyItemClickListener;
import ru.omegapps.wisherapp.managers.WishManager;

public class EndSetupFragment extends Fragment implements OnMyItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String STEP_NAME = "end";

    ArrayList<WishBlock> wishBlocks;
    ArrayList<Integer> chosenIndexes;
    RecyclerView recyclerView;
    Button nextStepButton;

    public EndSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wishBlocks = WishManager.getWishBlocksOfStep(STEP_NAME);
        chosenIndexes = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_end_setup, container, false);
        recyclerView = v.findViewById(R.id.wishgen_step_3_RecyclerViewWishBlocks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setAdapter(new WishBlockRecycleAdapter(v.getContext(), wishBlocks, R.layout.wish_block_choose_layout, this, null));

        nextStepButton = v.findViewById(R.id.step_3_nextStepButton);
        nextStepButton.setOnClickListener(v1 -> {
            WishManager.resetStepStack(STEP_NAME);
            for(int chosenIndex: chosenIndexes)
                WishManager.pushToStepStack(wishBlocks.get(chosenIndex), STEP_NAME);
            String finalWish = WishManager.generateCurrentWish(getContext());
            WishManager.resetSession();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_wisher, new HomeFragment())
                    .add(R.id.nav_host_fragment_wisher, WishGenSessionEndFragment.newInstance(finalWish))
                    .addToBackStack(null)
                    .commit();
        });
        return v;
    }

    //клик по элементу в recycler view
    @Override
    public void onMyItemClick(int position, View v, boolean isChosen) {
        TextView counterTextView = v.findViewById(R.id.choosing_counter);
        if(isChosen){
            chosenIndexes.add(position);
            counterTextView.setText(chosenIndexes.size()+"");
            counterTextView.setTextColor(Color.WHITE);
            counterTextView.setBackgroundResource(R.drawable.counter_choosed_bg);
        } else {
            int removingChoosingIndexPosition = chosenIndexes.indexOf(position);
            chosenIndexes.remove(removingChoosingIndexPosition);
            counterTextView.setText("");
            counterTextView.setBackgroundResource(R.drawable.counter_empty_bg);

            for(int i = removingChoosingIndexPosition; i < chosenIndexes.size(); i++){
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(chosenIndexes.get(i));
                if(viewHolder != null){
                    TextView currentCounterText = viewHolder.itemView.findViewById(R.id.choosing_counter);
                    currentCounterText.setText(String.format("%d", i+1));
                }
            }
        }
    }
}
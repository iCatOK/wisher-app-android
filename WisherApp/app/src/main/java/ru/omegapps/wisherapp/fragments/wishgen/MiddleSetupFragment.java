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
import ru.omegapps.wisherapp.interfaces.DataAgent;
import ru.omegapps.wisherapp.interfaces.OnMyItemClickListener;
import ru.omegapps.wisherapp.managers.WishManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MiddleSetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MiddleSetupFragment extends Fragment implements OnMyItemClickListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String STEP_NAME = "mid";

    ArrayList<WishBlock> wishBlocks;
    ArrayList<Integer> chosenIndexes;
    RecyclerView recyclerView;
    Button nextStepButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MiddleSetupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WishGenChooseBegining.
     */
    // TODO: Rename and change types and number of parameters
    public static MiddleSetupFragment newInstance(String param1, String param2) {
        MiddleSetupFragment fragment = new MiddleSetupFragment();
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

        wishBlocks = WishManager.getWishBlocksOfStep(STEP_NAME);
        chosenIndexes = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_middle_setup, container, false);
        recyclerView = v.findViewById(R.id.wishgen_step_2_RecyclerViewWishBlocks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setAdapter(new WishBlockRecycleAdapter(v.getContext(), wishBlocks, R.layout.wish_block_choose_layout, this, null));

        nextStepButton = v.findViewById(R.id.step_2_nextStepButton);
        nextStepButton.setOnClickListener(v1 -> {
            WishManager.resetStepStack(STEP_NAME);
            for(int chosenIndex: chosenIndexes)
                WishManager.pushToStepStack(wishBlocks.get(chosenIndex), STEP_NAME);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nav_host_fragment_wisher, new EndSetupFragment())
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
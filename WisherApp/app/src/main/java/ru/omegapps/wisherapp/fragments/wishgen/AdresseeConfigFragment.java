package ru.omegapps.wisherapp.fragments.wishgen;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.dto.Wish;
import ru.omegapps.wisherapp.managers.WishManager;
import ru.omegapps.wisherapp.utils.MainUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdresseeConfigFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdresseeConfigFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText tagText;
    private TagGroup tagGroup;
    private Button setTagButton;
    private Button nextStepButton;
    private List<String> tagList;
    private EditText addresseeName;
    private String sex = "";
    private RadioGroup sexRadioGroup;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdresseeConfigFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WishGenStepZero.
     */
    // TODO: Rename and change types and number of parameters
    public static AdresseeConfigFragment newInstance(String param1, String param2) {
        AdresseeConfigFragment fragment = new AdresseeConfigFragment();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wish_gen_step_zero, container, false);
        tagText = v.findViewById(R.id.wishgen_tag_name);
        tagText.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));
        tagGroup = v.findViewById(R.id.tag_group_wishgen_zero);
        setTagButton = v.findViewById(R.id.add_tag_button);
        nextStepButton = v.findViewById(R.id.step_0_nextStepButton);
        sexRadioGroup = v.findViewById(R.id.sex_radio_group);
        addresseeName = v.findViewById(R.id.wishgen_adressee_config_name);
        addresseeName.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));

        sexRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.radioButtonMan:
                    sex = "man";
                    break;
                case R.id.radioButtonWoman:
                    sex = "woman";
                    break;
                case R.id.radioButtonDosesntMatter:
                    sex = "";
                    break;
            }
        });

        nextStepButton.setOnClickListener(listen ->{

            if(!sex.isEmpty())
                WishManager.sessionSex = sex;

            if(!addresseeName.getText().toString().isEmpty()) {
                WishManager.sessionAddresseeName = addresseeName.getText().toString();
                WishManager.sessionNameState = "";
            } else {
                WishManager.sessionNameState = "unnamed";
            }

            WishManager.sessionTags = (ArrayList<String>) tagList;

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nav_host_fragment_wisher, new BeginSetupFragment())
                    .addToBackStack(null)
                    .commit();
        });

        setTagButton.setOnClickListener(a -> {
            if(!tagList.contains(tagText.getText().toString())){
                tagList.add(tagText.getText().toString());
                tagGroup.setTags(tagList);
                tagText.setText("");
            }
        });

        tagList = new ArrayList<>();
        tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                tagList.remove(tag);
                tagGroup.setTags(tagList);
            }
        });

        return v;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
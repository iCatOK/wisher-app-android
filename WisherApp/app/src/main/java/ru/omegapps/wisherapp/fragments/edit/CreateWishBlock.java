package ru.omegapps.wisherapp.fragments.edit;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.data_agents.FireBaseDataAgent;
import ru.omegapps.wisherapp.dto.WishBlock;
import ru.omegapps.wisherapp.fragments.WishBlocksFragment;
import ru.omegapps.wisherapp.utils.MainUtils;

public class CreateWishBlock extends Fragment {

    private String wishText;
    private String wBlockUuid;
    private boolean isPublic;

    private static final String WB_KEY = "Wishblocks";
    private static final CharSequence ENTER_NAME_TAG = "{имя}";

    private static final String BEGIN_STEP = "begin";
    private static final String MID_STEP = "mid";
    private static final String END_STEP = "end";

    DatabaseReference wishBlockRef;
    FirebaseAuth mAuth;

    EditText wishTextView;
    private TagGroup tagGroup;

    private CheckBox isPublicCheck;

    private Button addNameTagButton;

    private CheckBox beginCheck;
    private CheckBox midCheck;
    private CheckBox endCheck;

    private Button setTagButton;
    private RadioGroup sexRadioGroup;
    private List<String> tagList;
    private EditText tagText;
    Button okButton;
    private FirebaseUser user;
    private ArrayList<String> filters;
    private ArrayList<String> sex;
    private ArrayList<String> steps;

    public CreateWishBlock() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_create_wish_block, container, false);

        wishTextView = v.findViewById(R.id.create_block_text);
        tagGroup = v.findViewById(R.id.tag_group_create_block);
        sexRadioGroup = v.findViewById(R.id.create_block_sex_radio_group);
        isPublicCheck = v.findViewById(R.id.create_block_public_checkbox);
        addNameTagButton = v.findViewById(R.id.addNameButtonCreateBlock);
        beginCheck = v.findViewById(R.id.create_block_begin_check);
        midCheck = v.findViewById(R.id.create_block_mid_check);
        endCheck = v.findViewById(R.id.create_block_end_check);
        tagText = v.findViewById(R.id.create_block_tag_name);
        setTagButton = v.findViewById(R.id.create_block_add_tag_button);
        okButton = v.findViewById(R.id.create_block_ok_button);

        wishTextView.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));
        tagText.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));

        sex = new ArrayList<>();
        steps = new ArrayList<>();
        tagList = new ArrayList<>();

        tagGroup.setOnTagClickListener(tag -> {
            tagList.remove(tag);
            tagGroup.setTags(tagList);
        });

        sexRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.radioButtonManCreateBlock:
                    sex = new ArrayList<>(Collections.singletonList("man"));
                    break;
                case R.id.radioButtonWomanCreateBlock:
                    sex = new ArrayList<>(Collections.singletonList("woman"));
                    break;
                case R.id.radioButtonDosesntMatterCreateBlock:
                    sex = new ArrayList<>(Arrays.asList("man", "woman"));
                    break;
            }
        });

        isPublicCheck.setOnCheckedChangeListener((buttonView, isChecked) -> isPublic = isChecked);

        beginCheck.setOnCheckedChangeListener((b, isChecked) -> {
            if(isChecked)
                steps.add(BEGIN_STEP);
            else
                steps.remove(BEGIN_STEP);
        });

        midCheck.setOnCheckedChangeListener((b, isChecked) -> {
            if(isChecked)
                steps.add(MID_STEP);
            else
                steps.remove(MID_STEP);
        });

        endCheck.setOnCheckedChangeListener((b, isChecked) -> {
            if(isChecked)
                steps.add(END_STEP);
            else
                steps.remove(END_STEP);
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        wishBlockRef = FirebaseDatabase.getInstance().getReference(WB_KEY);

        okButton.setOnClickListener(v1 -> {
            if(user == null){
                Toast.makeText(getContext(), "Пользователь не авторизован!", Toast.LENGTH_SHORT).show();
                return;
            }
            String newWishText = wishTextView.getText().toString();
            filters = new ArrayList<>();
            filters.addAll(sex);
            if(!newWishText.contains(ENTER_NAME_TAG))
                filters.add("unnamed");
            filters.addAll(steps);
            WishBlock newWishBlock = new WishBlock(wBlockUuid, user.getUid(), newWishText, (ArrayList<String>) tagList, filters, isPublic);

            createWishBlock(newWishBlock);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_wisher, new WishBlocksFragment())
                    .commit();
        });

        setTagButton.setOnClickListener(a -> {
            if(!tagList.contains(tagText.getText().toString())){
                tagList.add(tagText.getText().toString());
                tagGroup.setTags(tagList);
                tagText.setText("");
            }
        });

        addNameTagButton.setOnClickListener(e -> {
            if(wishTextView.getText().toString().isEmpty())
                return;
            String newString = wishTextView.getText().toString() + ENTER_NAME_TAG;
            wishTextView.setText(newString);
            int position = wishTextView.length();
            Editable etext = wishTextView.getText();
            Selection.setSelection(etext, position);
        });

        return v;
    }

    private void createWishBlock(WishBlock newWishBlock) {
        String uuid = wishBlockRef
                .child(user.getUid())
                .push()
                .getKey();
        newWishBlock.setUuid(uuid);
        wishBlockRef.child(user.getUid()).child(uuid).setValue(newWishBlock);
        FireBaseDataAgent.updatePublicWishBlock(newWishBlock);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
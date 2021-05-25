package ru.omegapps.wisherapp.fragments.edit;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.data_agents.FireBaseDataAgent;
import ru.omegapps.wisherapp.dto.WishBlock;
import ru.omegapps.wisherapp.enums.WishEnum;
import ru.omegapps.wisherapp.fragments.WishBlocksFragment;
import ru.omegapps.wisherapp.utils.MainUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditWishBlockFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditWishBlockFragment extends Fragment {

    private static final String WISH_TEXT = "wishtext";
    private static final String WB_UUID = "uuid";
    private static final String TAGS = "tags";
    private static final String FILTERS = "filters";
    private static final String PUBLIC = "public";
    private static final CharSequence ENTER_NAME_TAG = "{имя}";

    private String wishText;
    private String wBlockUuid;
    private boolean isPublic;

    private static final String WB_KEY = "Wishblocks";
    private static final String PUBLIC_WB_KEY = "PublicWishblocks";
    private static final String WISH_TEXT_KEY = "wishText";

    private static final String BEGIN_STEP = "begin";
    private static final String MID_STEP = "mid";
    private static final String END_STEP = "end";


    DatabaseReference wishBlockRef;
    DatabaseReference publicWishBlockRef;
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

    public EditWishBlockFragment() {
        // Required empty public constructor
    }


    public static EditWishBlockFragment newInstance(String wishText, String wBlockUuid, ArrayList<String> tags, ArrayList<String> filters, boolean isPublic) {
        EditWishBlockFragment fragment = new EditWishBlockFragment();
        Bundle args = new Bundle();
        args.putString(WISH_TEXT, wishText);
        args.putStringArrayList(TAGS, tags);
        args.putStringArrayList(FILTERS, filters);
        args.putString(WB_UUID, wBlockUuid);
        args.putBoolean(PUBLIC, isPublic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            wishText = getArguments().getString(WISH_TEXT);
            wBlockUuid = getArguments().getString(WB_UUID);
            tagList = getArguments().getStringArrayList(TAGS);
            filters = getArguments().getStringArrayList(FILTERS);
            isPublic = getArguments().getBoolean(PUBLIC);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_wish_block, container, false);
        wishTextView = v.findViewById(R.id.edit_block_text);
        wishTextView.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));

        tagGroup = v.findViewById(R.id.tag_group_edit_block);
        sexRadioGroup = v.findViewById(R.id.edit_block_sex_radio_group);
        isPublicCheck = v.findViewById(R.id.edit_block_public_checkbox);
        addNameTagButton = v.findViewById(R.id.addNameButtonEditBlock);
        beginCheck = v.findViewById(R.id.edit_block_begin_check);
        midCheck = v.findViewById(R.id.edit_block_mid_check);
        endCheck = v.findViewById(R.id.edit_block_end_check);

        tagText = v.findViewById(R.id.edit_block_tag_name);
        tagText.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));

        setTagButton = v.findViewById(R.id.edit_block_add_tag_button);
        okButton = v.findViewById(R.id.edit_block_ok_button);

        sex = new ArrayList<>();
        steps = new ArrayList<>();

        isPublicCheck.setChecked(isPublic);

        if(filters.contains(BEGIN_STEP)) {
            beginCheck.setChecked(true);
            steps.add(BEGIN_STEP);
        }
        if(filters.contains(MID_STEP)) {
            midCheck.setChecked(true);
            steps.add(MID_STEP);
        }
        if(filters.contains(END_STEP)) {
            endCheck.setChecked(true);
            steps.add(END_STEP);
        }

        tagGroup.setTags(tagList);

        tagGroup.setOnTagClickListener(tag -> {
            tagList.remove(tag);
            tagGroup.setTags(tagList);
        });

        wishTextView.setText(wishText);

        if(filters.containsAll(Arrays.asList("man", "woman"))){
            RadioButton radioButtonNone = v.findViewById(R.id.radioButtonDosesntMatterEditBlock);
            sex.add("man");
            sex.add("woman");
            radioButtonNone.toggle();
        } else if(filters.contains("man")){
            RadioButton radioButtonMan = v.findViewById(R.id.radioButtonManEditBlock);
            sex.add("man");
            radioButtonMan.toggle();
        } else if(filters.contains("woman")){
            RadioButton radioButtonWoman = v.findViewById(R.id.radioButtonWomanEditBlock);
            sex.add("woman");
            radioButtonWoman.toggle();
        }

        sexRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.radioButtonManEditBlock:
                    sex = new ArrayList<>(Collections.singletonList("man"));
                    break;
                case R.id.radioButtonWomanEditBlock:
                    sex = new ArrayList<>(Collections.singletonList("woman"));
                    break;
                case R.id.radioButtonDosesntMatterEditBlock:
                    sex = new ArrayList<>(Arrays.asList("man", "woman"));
                    break;
            }
        });

//        isNameUsed.setOnCheckedChangeListener((buttonView, isChecked) -> nameState = isChecked ? "" : "unnamed");
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
        publicWishBlockRef = FirebaseDatabase.getInstance().getReference(PUBLIC_WB_KEY);

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

            updateWishBlock(newWishBlock);

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

    private void updateWishBlock(WishBlock newWishBlock) {
        wishBlockRef
                .child(user.getUid())
                .child(wBlockUuid)
                .setValue(newWishBlock);
        FireBaseDataAgent.updatePublicWishBlock(newWishBlock);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
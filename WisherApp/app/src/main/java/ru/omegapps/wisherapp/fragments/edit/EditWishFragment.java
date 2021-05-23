package ru.omegapps.wisherapp.fragments.edit;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.dto.Wish;
import ru.omegapps.wisherapp.dto.WishBlock;
import ru.omegapps.wisherapp.fragments.HomeFragment;
import ru.omegapps.wisherapp.fragments.WishBlocksFragment;
import ru.omegapps.wisherapp.utils.MainUtils;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditWishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditWishFragment extends Fragment {
    
    private static final String TITLE = "title";
    private static final String TEXT = "text";
    private static final String UUID = "uuid";
    private static final String WISH_KEY = "Wishes";
    private static final String WISH_TEXT_KEY = "text";
    private static final String WISH_TITLE_KEY = "title";

    private String text;
    private String title;
    private String uuid;

    DatabaseReference wishRef;
    FirebaseAuth mAuth;
    
    EditText wishTitleView;
    EditText wishTextView;
    Button okButton;
    private FirebaseUser user;

    public EditWishFragment() {
        // Required empty public constructor
    }

    public static EditWishFragment newInstance(String title, String text, String uuid) {
        EditWishFragment fragment = new EditWishFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(TEXT, text);
        args.putString(UUID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            text = getArguments().getString(TEXT);
            title = getArguments().getString(TITLE);
            uuid = getArguments().getString(UUID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_wish, container, false);
        
        wishTextView = v.findViewById(R.id.edit_wish_text);
        wishTextView.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));

        wishTitleView = v.findViewById(R.id.edit_wish_title);
        wishTitleView.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));

        okButton = v.findViewById(R.id.edit_wish_ok_button);
        wishTextView.setText(text);
        wishTitleView.setText(title);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        wishRef = FirebaseDatabase.getInstance().getReference(WISH_KEY);

        Button copyButton = v.findViewById(R.id.edit_wish_copy_wish);
        copyButton.setOnClickListener(v1 -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(getContext(), ClipboardManager.class);
            ClipData clip = ClipData.newPlainText("Поздравление", wishTextView.getText());
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Cкопировано", Toast.LENGTH_SHORT).show();
        });
        
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == null){
                    Toast.makeText(getContext(), "Пользователь не авторизован!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String newWishText = wishTextView.getText().toString();
                String newWishTitle = wishTitleView.getText().toString();

                wishRef.child(user.getUid()).child(uuid).child(WISH_TEXT_KEY).setValue(newWishText);
                wishRef.child(user.getUid()).child(uuid).child(WISH_TITLE_KEY).setValue(newWishTitle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_wisher, new HomeFragment())
                        .commit();
            }
        });
        
        return v;
    }
}
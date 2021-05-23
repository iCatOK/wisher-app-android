package ru.omegapps.wisherapp.fragments.wishgen;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.fragments.HomeFragment;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WishGenSessionEndFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WishGenSessionEndFragment extends Fragment {

    private static final String WISH_TEXT = "wishText";

    private String mWishText;

    public WishGenSessionEndFragment() {
        // Required empty public constructor
    }

    public static WishGenSessionEndFragment newInstance(String wishText) {
        WishGenSessionEndFragment fragment = new WishGenSessionEndFragment();
        Bundle args = new Bundle();
        args.putString(WISH_TEXT, wishText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWishText = getArguments().getString(WISH_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_successful_operation, container, false);
        TextView wishText = v.findViewById(R.id.wishgen_end_wish_text);
        Button copyButton = v.findViewById(R.id.wishgen_copy_wish);
        Button exitButton = v.findViewById(R.id.wishgen_exit);
        wishText.setText(mWishText);
        copyButton.setOnClickListener(v1 -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(getContext(), ClipboardManager.class);
            ClipData clip = ClipData.newPlainText("Поздравление", mWishText);
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Cкопировано", Toast.LENGTH_SHORT).show();
        });
        exitButton.setOnClickListener(v2 -> getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_wisher, new HomeFragment())
                .commit());
        return v;
    }
}
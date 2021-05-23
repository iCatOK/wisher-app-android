package ru.omegapps.wisherapp.fragments.auth;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.data_agents.FireBaseDataAgent;
import ru.omegapps.wisherapp.fragments.HomeFragment;
import ru.omegapps.wisherapp.fragments.wishgen.AdresseeConfigFragment;
import ru.omegapps.wisherapp.utils.MainUtils;

public class AuthorizatonFragment extends Fragment {

    private FirebaseAuth mAuth;
    EditText loginText;
    EditText passText;
    Button enterButton;
    Button regButton;

    public AuthorizatonFragment() {
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
        View v = inflater.inflate(R.layout.fragment_authorizaton, container, false);
        loginText = v.findViewById(R.id.auth_username);
        loginText.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));
        passText = v.findViewById(R.id.auth_password);
        passText.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));
        enterButton = v.findViewById(R.id.login_button);
        regButton = v.findViewById(R.id.to_registration_button);

        regButton.setOnClickListener(v1 ->
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nav_host_fragment_wisher, new RegistrationFragment())
                    .addToBackStack(null)
                    .commit());

        enterButton.setOnClickListener(v2 -> {
            mAuth = FirebaseAuth.getInstance();
            String email = loginText.getText().toString();
            String password = passText.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Добро пожаловать!", Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.nav_host_fragment_wisher, new HomeFragment())
                                        .addToBackStack(null)
                                        .commit();
                            }
                            else
                                Toast.makeText(getContext(), "Не удалось войти", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        return v;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
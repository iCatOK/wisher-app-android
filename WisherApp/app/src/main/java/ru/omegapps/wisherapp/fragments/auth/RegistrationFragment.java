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

import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.utils.MainUtils;

public class RegistrationFragment extends Fragment {

    FirebaseAuth mAuth;
    EditText loginText;
    EditText passText;
    EditText passText2;
    Button registerButton;

    public RegistrationFragment() {
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
        View v = inflater.inflate(R.layout.fragment_registration, container, false);
        loginText = v.findViewById(R.id.register_username);
        loginText.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));
        passText = v.findViewById(R.id.register_password);
        passText.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));
        passText2 = v.findViewById(R.id.register_password2);
        passText2.setOnFocusChangeListener(MainUtils.hideKeyboardOnUnfocused(getActivity()));
        registerButton = v.findViewById(R.id.button_registration);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = loginText.getText().toString();
                String pass1 = passText.getText().toString();
                String pass2 = passText2.getText().toString();
                if(pass1.length() == 0)
                    Toast.makeText(getContext(), "Введите пароль!", Toast.LENGTH_SHORT).show();
                else if(pass1.length() < 8)
                    Toast.makeText(getContext(), "Пароль должен быть от 8 символов!", Toast.LENGTH_SHORT).show();
                else if(!pass1.equals(pass2))
                    Toast.makeText(getContext(), "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
                else{
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(login, pass1)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(), "Успешно зарегистрировались!", Toast.LENGTH_SHORT).show();
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.nav_host_fragment_wisher, new AuthorizatonFragment())
                                                .commit();
                                    } else {
                                        Toast.makeText(getContext(), "Регистрация не удалась...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        return v;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
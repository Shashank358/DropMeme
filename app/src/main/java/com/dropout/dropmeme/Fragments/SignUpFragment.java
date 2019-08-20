package com.dropout.dropmeme.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dropout.dropmeme.MainActivity;
import com.dropout.dropmeme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private EditText userName, userMobile, userEmail, userPassword;
    private TextView jumpToLoginText;
    private Button registerBtn;
    private ProgressDialog dialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;
    View view;
    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        dialog = new ProgressDialog(getActivity());
        mAuth = FirebaseAuth.getInstance();

        userName = view.findViewById(R.id.user_name_input);
        userMobile = view.findViewById(R.id.user_mobile_no_input);
        userEmail = view.findViewById(R.id.user_email_input_registration);
        userPassword = view.findViewById(R.id.user_pass_registration);
        registerBtn = view.findViewById(R.id.register_user_btn);
        jumpToLoginText = view.findViewById(R.id.jump_to_login);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(v);
            }
        });

        jumpToLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToLogin(v);
            }
        });

        return view;
    }

    private void registerUser(View v) {

        String name = userName.getText().toString();
        String phone = userMobile.getText().toString();
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)
                && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            dialog.setMessage("Registering user");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            proceedToRegistration(name, phone, email, password);
        }else {
            Snackbar.make(getActivity().findViewById(R.id.reg_container), "Please fill all the above fields", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void jumpToLogin(View v) {
        Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragment);

    }

    private void proceedToRegistration(final String name, final String phone, final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    currentUserId = mAuth.getCurrentUser().getUid();
                    HashMap<String, Object> regMap = new HashMap<>();
                    regMap.put("user_name", name);
                    regMap.put("user_id", "user id");
                    regMap.put("user_phone", phone);
                    regMap.put("user_email", email);
                    regMap.put("uId", currentUserId);
                    regMap.put("user_thumbImage", "default");
                    regMap.put("user_image", "default");
                    regMap.put("user_about", "About yourself");
                    db = FirebaseFirestore.getInstance();
                    db.collection("Users").document(currentUserId).set(regMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        dialog.dismiss();
                                        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(mainIntent);
                                    }else {
                                        dialog.hide();
                                        Snackbar.make(getActivity().findViewById(R.id.reg_container), "Error please try again", Snackbar.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.hide();
                Snackbar.make(getActivity().findViewById(R.id.reg_container), "Please check your internet and try again", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

    }

}

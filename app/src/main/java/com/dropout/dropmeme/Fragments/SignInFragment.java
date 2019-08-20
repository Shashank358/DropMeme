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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private TextView jumpToReg;
    private EditText userEmail, userPass;
    private ProgressDialog dialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;
    private Button loginBtn;
    View view;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        dialog = new ProgressDialog(getActivity());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        jumpToReg = view.findViewById(R.id.jump_to_registration);
        userEmail = view.findViewById(R.id.user_email_input_login);
        userPass = view.findViewById(R.id.user_pass_login);
        loginBtn = view.findViewById(R.id.login_user_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAccount(v);
            }
        });

        jumpToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToRegistration(v);
            }
        });


        return view;
    }
    private void loginAccount(View v) {

        String email = userEmail.getText().toString();
        String password = userPass.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            dialog.setMessage("Signing In");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            proceedToLogin(email, password);
        }else {
            Snackbar.make(getActivity().findViewById(R.id.login_container), "Please fill all the above fields", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void jumpToRegistration(View v) {

        Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_signUpFragment);

    }

    private void proceedToLogin(String email, String password) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        dialog.dismiss();
                        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }else {
                        dialog.hide();
                        Snackbar.make(getActivity().findViewById(R.id.login_container), "Error please try again", Snackbar.LENGTH_SHORT)
                                .show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.hide();
                    Snackbar.make(getActivity().findViewById(R.id.login_container), "Please check your internet and try again", Snackbar.LENGTH_SHORT)
                            .show();
                }
            });
        }else {
            dialog.hide();
            Snackbar.make(getActivity().findViewById(R.id.login_container), "Account doesn't exist please register first", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

}

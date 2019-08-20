package com.dropout.dropmeme.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dropout.dropmeme.MainActivity;
import com.dropout.dropmeme.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuthFragment extends Fragment {

    private EditText phoneNum;
    private Button nextBtn, signInFacebook, signInGoogle, signInEmail;


    private ProgressDialog dialog;
    private static final String TAG = "simplifiedcoding";

    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 234;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public AuthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_auth, container, false);

        phoneNum = view.findViewById(R.id.user_phone_number_edittext);
        nextBtn = view.findViewById(R.id.login_by_phone_number_btn);
        signInEmail = view.findViewById(R.id.sign_in_with_email);
        signInGoogle = view.findViewById(R.id.sign_with_google);

        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(getActivity());

        dialog.setMessage("Loading..");
        dialog.setCanceledOnTouchOutside(false);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNum.getText().toString();
                if (!phoneNumber.isEmpty()){
                    Bundle bundle = new Bundle();
                    bundle.putString("mobile", phoneNumber);
                    Navigation.findNavController(view).navigate(R.id.action_authFragment_to_verifyFragment, bundle);
                }else {
                    phoneNum.setError("Please type your mobile number");
                    phoneNum.requestFocus();
                }

            }
        });

        signInEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_authFragment_to_signInFragment);
            }
        });

        //--------------google signing method
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }
        });


        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {
            dialog.setMessage("Creating acount");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            //Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            //authenticating with firebase
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            dialog.hide();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String profile_url = String.valueOf(acct.getPhotoUrl());
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("first_name", acct.getGivenName());
                    userMap.put("last_name", acct.getFamilyName());
                    userMap.put("full_name", acct.getDisplayName());
                    userMap.put("profile_url", profile_url);

                    db = FirebaseFirestore.getInstance();
                    String currentUserId = mAuth.getCurrentUser().getUid();
                    db.collection("Users").document(currentUserId).set(userMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            dialog.hide();
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Toast.makeText(getActivity(), "User Signed In", Toast.LENGTH_SHORT).show();
                } else {
                    // If sign in fails, display a message to the user.
                    dialog.hide();
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(getActivity(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}

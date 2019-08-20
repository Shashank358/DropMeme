package com.dropout.dropmeme.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dropout.dropmeme.EditProfileActivity;
import com.dropout.dropmeme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Button editProfileBtn;
    private CircleImageView userImage;
    private TextView userName, userId;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String mCurrentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();

        editProfileBtn = view.findViewById(R.id.edit_profile_btn);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
//                intent.putExtra("path", "default");
                startActivity(intent);
            }
        });
        userImage = view.findViewById(R.id.profile_frag_user_image);
        userName = view.findViewById(R.id.profile_frag_user_name);
        userId = view.findViewById(R.id.profile_frag_user_id);

        getUserInfo();

        return view;
    }

    private void getUserInfo() {
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(mCurrentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String user_name = task.getResult().get("user_name").toString();
                    String user_id = task.getResult().get("user_id").toString();
                    String user_profile =  task.getResult().get("user_thumbImage").toString();

                    userName.setText(user_name);
                    userId.setText(user_id);
                    Picasso picasso = Picasso.get();
                    picasso.setIndicatorsEnabled(false);
                    picasso.load(user_profile).placeholder(R.drawable.avatar).into(userImage);


                }
            }
        });
    }

}

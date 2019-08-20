package com.dropout.dropmeme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dropout.dropmeme.Adapters.AllUsersAdapter;
import com.dropout.dropmeme.Adapters.ProfileMemesAdapter;
import com.dropout.dropmeme.Models.HomeMeme;
import com.dropout.dropmeme.Models.Users;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String otherUserId;
    private Button followProfileBtn;
    private CircleImageView userImage;
    private TextView userName, userId;
    private TextView followers, following, memes;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private Toolbar toolbar;

    private RecyclerView allMemesList;
    private LinearLayoutManager layoutManager;
    private ProfileMemesAdapter adapter;
    private String follow_state = "UnFollowing";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        otherUserId = getIntent().getStringExtra("other_userId");

        toolbar = findViewById(R.id.profile_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        followProfileBtn = findViewById(R.id.follow_profile_activity_btn);
        followers = findViewById(R.id.profile_activity_followers_count);
        following = findViewById(R.id.profile_activity_following_count);
        memes = findViewById(R.id.profile_activity_memes_count);

        //counting following and followers
        countFollowers();
        countFollowing();

        //checking following information
        checkFollowDetails();
        followProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToFollow();
            }
        });

        userImage = findViewById(R.id.profile_activity_user_image);
        userName = findViewById(R.id.profile_activity_user_name);
        userId = findViewById(R.id.profile_activity_user_id);

        getUserInfo();

        allMemesList = findViewById(R.id.profile_activity_memes_list);
        layoutManager = new GridLayoutManager(this, 2);
        allMemesList.setLayoutManager(layoutManager);
        allMemesList.setItemAnimator(new DefaultItemAnimator());

        Query query = db.collection("HomeMeme");
        final FirestoreRecyclerOptions<HomeMeme> options = new FirestoreRecyclerOptions.Builder<HomeMeme>()
                .setQuery(query, HomeMeme.class)
                .build();

        adapter = new ProfileMemesAdapter(getApplicationContext(), options);
        allMemesList.setAdapter(adapter);


    }

    private void countFollowing() {
        db.collection("Users").document(otherUserId).collection("Following")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int count = 0;
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        count++;
                    }
                    String cou = String.valueOf(count);
                    following.setText(cou);
                }
            }
        });
    }

    private void countFollowers() {
        db.collection("Users").document(otherUserId).collection("Followers")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int count = 0;
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        count++;
                    }
                    String cou = String.valueOf(count);
                    followers.setText(cou);
                }
            }
        });
    }

    private void checkFollowDetails() {
        db.collection("Users").document(currentUserId).collection("Following")
                .document(otherUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()){
                        followProfileBtn.setText("Following");
                        followProfileBtn.setTextColor(Color.BLACK);
                        followProfileBtn.setBackground(getResources().getDrawable(R.drawable.create_meme_text_box));
                        follow_state = "Following";

                    }else {
                        followProfileBtn.setText("+ Follow");
                        followProfileBtn.setTextColor(Color.WHITE);
                        followProfileBtn.setBackground(getResources().getDrawable(R.drawable.edit_profile_btn_back));
                        follow_state = "UnFollowing";

                    }
                }
            }
        });
    }

    private void proceedToFollow() {
        if (follow_state.equals("UnFollowing")){
            follow_state = "Following";
            followProfileBtn.setText("Following");
            followProfileBtn.setTextColor(Color.BLACK);
            followProfileBtn.setBackground(getResources().getDrawable(R.drawable.create_meme_text_box));

            HashMap<String, Object> followingMap = new HashMap<>();
            followingMap.put("other_uid", otherUserId);
            followingMap.put("my_uid", currentUserId);

            final HashMap<String, Object> followerMap = new HashMap<>();
            followerMap.put("other_uid", currentUserId);
            followerMap.put("my_uid", otherUserId);

            db.collection("Users").document(currentUserId).collection("Following")
                    .document(otherUserId).set(followingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        db.collection("Users").document(otherUserId).collection("Followers")
                                .document(currentUserId).set(followerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ProfileActivity.this, "Following", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

        }else if (follow_state.equals("Following")){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileActivity.this);
            builder1.setTitle("Unfollow this person");
            builder1.setMessage("Click on yes if you want to unfollow this person");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int id) {
                            follow_state = "UnFollowing";
                            followProfileBtn.setText("+ Follow");
                            followProfileBtn.setTextColor(Color.WHITE);
                            followProfileBtn.setBackground(getResources().getDrawable(R.drawable.edit_profile_btn_back));

                            db.collection("Users").document(currentUserId).collection("Following")
                                    .document(otherUserId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        db.collection("Users").document(otherUserId).collection("Followers")
                                                .document(currentUserId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(ProfileActivity.this, "UnFollow", Toast.LENGTH_SHORT).show();
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            builder1.show();
        }
    }

    private void getUserInfo() {
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(otherUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

package com.dropout.dropmeme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropout.dropmeme.Adapters.CommentAdapter;
import com.dropout.dropmeme.Adapters.HomeContentAdapter;
import com.dropout.dropmeme.Models.Comments;
import com.dropout.dropmeme.Models.HomeMeme;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    Toolbar toolbar;
    private RecyclerView recycler_comments;
    private CircleImageView userImage, commenterImage;
    private TextView userName, timeAgo, commenterName, sendText, likeCount, commentCount, caption;
    private ImageView likeIcon, shareIcon, saveIcon, memeImage;
    private EditText commentBox;

    private LinearLayoutManager layoutManager;
    private CommentAdapter adapter;

    private String meme_id, currentUserId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        meme_id = getIntent().getStringExtra("meme_id");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.comment_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userImage = (CircleImageView) findViewById(R.id.comment_activity_user_image);
        commenterImage = (CircleImageView) findViewById(R.id.commenter_user_image);
        userName = (TextView) findViewById(R.id.comment_activity_user_name);
        timeAgo = (TextView) findViewById(R.id.comment_activity_time_ago);
        commenterName = (TextView) findViewById(R.id.commenter_user_name);
        sendText = (TextView) findViewById(R.id.commenter_send_text);
        likeCount = (TextView) findViewById(R.id.comment_activity_likes_count);
        commentCount = (TextView) findViewById(R.id.comment_activity_comments_count);
        caption = (TextView) findViewById(R.id.comment_activity_caption);
        likeIcon = (ImageView) findViewById(R.id.comment_activity_like_icon);
        shareIcon = (ImageView) findViewById(R.id.comment_activity_share_icon);
        saveIcon = (ImageView) findViewById(R.id.comment_activity_save_to_gallery_icon);
        memeImage = (ImageView) findViewById(R.id.comment_activity_meme_image);
        commentBox = (EditText) findViewById(R.id.commenter_edit_text);

        recycler_comments = (RecyclerView) findViewById(R.id.recycler_comments);

        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentBox.getText().toString();
                if (!TextUtils.isEmpty(comment))
                {
                    postComment(comment);
                }else
                {
                    Toast.makeText(CommentActivity.this, "comment can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //retrieving comments

        layoutManager = new LinearLayoutManager(this);
        recycler_comments.setHasFixedSize(true);
        recycler_comments.setLayoutManager(layoutManager);

        Query query = db.collection("HomeMeme").document(meme_id).collection("Comments");
        final FirestoreRecyclerOptions<Comments> options = new FirestoreRecyclerOptions.Builder<Comments>()
                .setQuery(query, Comments.class)
                .build();

        adapter = new CommentAdapter(getApplicationContext(), options);
        recycler_comments.setAdapter(adapter);

        //fetchUserData
        fetchUserData();
        fetchCommenterData();
        commentBox.requestFocus();

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
    private void fetchCommenterData() {

        db.collection("Users").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String name = documentSnapshot.get("user_name").toString();
                String image = documentSnapshot.get("user_thumbImage").toString();

                commenterName.setText(name);
                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(image).placeholder(R.drawable.avatar).into(commenterImage);

            }
        });
    }

    private void fetchUserData() {

        db.collection("HomeMeme").document(meme_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String meme_image = documentSnapshot.get("meme_image").toString();
                String cap = documentSnapshot.get("caption").toString();
                String user_id = documentSnapshot.get("user_uid").toString();
                String time_ago = documentSnapshot.get("time_ago").toString();

                caption.setText(cap);
                timeAgo.setText(time_ago);
                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(meme_image).placeholder(R.drawable.default_image).into(memeImage);

                db.collection("Users").document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        String user_image = documentSnapshot.get("user_thumbImage").toString();
                        String user_name = documentSnapshot.get("user_name").toString();

                        userName.setText(user_name);
                        Picasso picasso = Picasso.get();
                        picasso.setIndicatorsEnabled(false);
                        picasso.load(user_image).placeholder(R.drawable.avatar).into(userImage);


                    }
                });
            }
        });
    }

    private void postComment(String comment) {
        commentBox.setText("");
        String comment_id = db.collection("HomeMeme").document(meme_id)
                .collection("Comments").document().getId();

        HashMap<String, Object> commentMap = new HashMap<>();
        commentMap.put("commenter_uid", currentUserId);
        commentMap.put("comment", comment);
        commentMap.put("meme_id", meme_id);
        commentMap.put("time_ago", FieldValue.serverTimestamp());
        commentMap.put("comment_id", comment_id);

        db.collection("HomeMeme").document(meme_id).collection("Comments")
                .document(comment_id).set(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(CommentActivity.this, "comment sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

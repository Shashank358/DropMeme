package com.dropout.dropmeme.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dropout.dropmeme.Models.Users;
import com.dropout.dropmeme.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersAdapter extends FirestoreRecyclerAdapter<Users, AllUsersAdapter.ViewHolder> {

    private Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    OnItemClick onItemClick;
    OnFollowClick onFollowClick;

    public AllUsersAdapter(Context mContext, FirestoreRecyclerOptions<Users> options) {
        super(options);
        this.mContext = mContext;
        this.notifyDataSetChanged();
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {

        void getPosition(String userId);

    }

    public void setOnFollowClick(OnFollowClick onFollowClick) {
        this.onFollowClick = onFollowClick;
    }

    public interface OnFollowClick {

        void getConf(String userId, Button conf);

    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Users model) {

        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        holder.userName.setText(model.getUser_name());
        holder.userId.setText(model.getUser_id());

        Picasso.get().load(model.getUser_thumbImage()).placeholder(R.drawable.avatar)
                .into(holder.profile);

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.getPosition(model.getuId());
            }
        });

        db.collection("Users").document(currentUserId).collection("Following")
                .document(model.getuId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    holder.followText.setText("- Following");
                    holder.followText.setTextColor(Color.DKGRAY);

                }else {
                    holder.followText.setText("+ Follow");
                }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_single_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView userName, userId, followText;
        ConstraintLayout main_layout;


        //AppCompatRatingBar ratingBar;
        Button seeAll,confirm;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_list_single_name);
            userId = itemView.findViewById(R.id.user_list_single_id);
            profile = itemView.findViewById(R.id.user_list_single_image);
            main_layout = itemView.findViewById(R.id.user_list_main_layout);
            followText = itemView.findViewById(R.id.user_single_follow_text);
        }


    }
}

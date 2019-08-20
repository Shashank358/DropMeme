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

import com.dropout.dropmeme.Models.Comments;
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

import org.w3c.dom.Comment;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comments, CommentAdapter.ViewHolder> {

    private Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    OnItemClick onItemClick;

    public CommentAdapter(Context mContext, FirestoreRecyclerOptions<Comments> options) {
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

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Comments model) {

        holder.comment.setText(model.getComment());
//        holder.timeAgo.setText(model.getTime_ago().toString());

        db.collection("Users").document(model.getCommenter_uid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    String user_name = documentSnapshot.get("user_name").toString();
                    String image = documentSnapshot.get("user_thumbImage").toString();

                    Picasso picasso = Picasso.get();
                    picasso.setIndicatorsEnabled(false);
                    picasso.load(image).placeholder(R.drawable.avatar).into(holder.profile);
                    holder.userName.setText(user_name);

                }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_single_comment_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView userName, comment, timeAgo;
        ConstraintLayout main_layout;


        //AppCompatRatingBar ratingBar;
        Button seeAll,confirm;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.single_comment_user_name);
            profile = itemView.findViewById(R.id.single_comment_user_image);
            main_layout = itemView.findViewById(R.id.single_comment_main_layout);
            timeAgo = itemView.findViewById(R.id.single_comment_time_ago);
            comment = itemView.findViewById(R.id.single_comment_user_comment);
        }


    }
}

package com.dropout.dropmeme.Adapters;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dropout.dropmeme.CommentActivity;
import com.dropout.dropmeme.Models.HomeMeme;
import com.dropout.dropmeme.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeContentAdapter extends FirestoreRecyclerAdapter<HomeMeme, HomeContentAdapter.ViewHolder> {

    private Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    OnItemClick onItemClick;
    OnConfClick onConfClick;

    public HomeContentAdapter(Context mContext, FirestoreRecyclerOptions<HomeMeme> options) {
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

    public void setOnConfClick(OnConfClick onConfClick) {
        this.onConfClick = onConfClick;
    }

    public interface OnConfClick {

        void getConf(String userId, Button conf);

    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final HomeMeme model) {

        holder.caption.setText(model.getCaption());
        holder.timeAgo.setText(model.getTime_ago().toString());

        Picasso.get().load(model.getMeme_image()).placeholder(R.drawable.default_image)
                .into(holder.memeImage);

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("meme_id", model.getMeme_id());
                mContext.startActivity(intent);
            }
        });

        //fetch personal data from here
        db.collection("Users").document(model.getUser_uid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String user_name = documentSnapshot.get("user_name").toString();
                String user_thumbImage = documentSnapshot.get("user_thumbImage").toString();

                holder.userName.setText(user_name);

                Picasso.get().load(user_thumbImage).placeholder(R.drawable.avatar)
                        .into(holder.profile);
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_home_meme_frame_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        ImageView memeImage, comment;
        TextView userName, timeAgo, caption;
        CardView card_layout;


        //AppCompatRatingBar ratingBar;
        Button seeAll,confirm;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_list_single_name);
            caption = itemView.findViewById(R.id.caption_single_meme_frame);
            timeAgo = itemView.findViewById(R.id.post_sigle_time_ago_text);
            profile = itemView.findViewById(R.id.user_list_single_image);
            memeImage = itemView.findViewById(R.id.meme_single_imageview);
            comment = itemView.findViewById(R.id.single_home_content_comment_icon);
        }


    }
}

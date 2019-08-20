package com.dropout.dropmeme.Adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dropout.dropmeme.Models.HomeMeme;
import com.dropout.dropmeme.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileMemesAdapter extends FirestoreRecyclerAdapter<HomeMeme, ProfileMemesAdapter.ViewHolder> {

    private Context mContext;

    private FirebaseFirestore db;

    OnItemClick onItemClick;
    OnConfClick onConfClick;

    public ProfileMemesAdapter(Context mContext, FirestoreRecyclerOptions<HomeMeme> options) {
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


        Picasso.get().load(model.getMeme_image()).placeholder(R.drawable.default_image)
                .into(holder.memeImage);


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_meme_single_frame_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView memeImage;

        //AppCompatRatingBar ratingBar;
        Button seeAll,confirm;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            memeImage = itemView.findViewById(R.id.user_single_meme_image);
        }


    }
}

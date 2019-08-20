package com.dropout.dropmeme.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dropout.dropmeme.Fragments.BrushFragment;
import com.dropout.dropmeme.R;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    Context context;
    List<Integer> colorList;
    colorAdapterListener listener;

    public ColorAdapter(Context context, colorAdapterListener listener) {
        this.context = context;
        this.colorList = genColorList();
        this.listener = listener;
    }

    public interface colorAdapterListener{
        void onColorSelected(int color);
    }

    @NonNull
    @Override
    public ColorAdapter.ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.color_item, parent,false);
        return new ColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorAdapter.ColorViewHolder holder, int position) {

        holder.color_section.setCardBackgroundColor(colorList.get(position));
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }
    
    public class ColorViewHolder extends RecyclerView.ViewHolder {

        CardView color_section;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);

            color_section = (CardView) itemView.findViewById(R.id.color_section);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onColorSelected(colorList.get(getAdapterPosition()));
                }
            });
        }
    }

    private List<Integer> genColorList() {
        List<Integer> colorList = new ArrayList<>();

        colorList.add(Color.parseColor("#4d5252"));
        colorList.add(Color.parseColor("#c1cdcd"));
        colorList.add(Color.parseColor("#eaeaec"));
        colorList.add(Color.parseColor("#332e49"));
        colorList.add(Color.parseColor("#44492e"));
        colorList.add(Color.parseColor("#c4f3e7"));
        colorList.add(Color.parseColor("#ffffff"));
        colorList.add(Color.parseColor("#e0e0e0"));
        colorList.add(Color.parseColor("#b9b9b9"));
        colorList.add(Color.parseColor("#b7b7b7"));
        colorList.add(Color.parseColor("#2e2e2e"));
        colorList.add(Color.parseColor("#272727"));
        colorList.add(Color.parseColor("#0b0b0b"));
        colorList.add(Color.parseColor("#000000"));
        colorList.add(Color.parseColor("#fdd7ed"));
        colorList.add(Color.parseColor("#fcbadf"));
        colorList.add(Color.parseColor("#eac1d5"));
        colorList.add(Color.parseColor("#d5e9fb"));
        colorList.add(Color.parseColor("#6f263d"));
        colorList.add(Color.parseColor("#263354"));
        colorList.add(Color.parseColor("#c18d44"));
        colorList.add(Color.parseColor("#7555da"));
        colorList.add(Color.parseColor("#ace246"));
        colorList.add(Color.parseColor("#0c0a45"));
        colorList.add(Color.parseColor("#414053"));
        colorList.add(Color.parseColor("#eac1d5"));
        colorList.add(Color.parseColor("#afbfe3"));
        colorList.add(Color.parseColor("#b6688b"));
        colorList.add(Color.parseColor("#7efdff"));
        colorList.add(Color.parseColor("#c6c5eb"));


        return colorList;
    }
}

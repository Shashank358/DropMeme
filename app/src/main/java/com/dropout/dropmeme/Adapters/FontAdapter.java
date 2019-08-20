package com.dropout.dropmeme.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropout.dropmeme.R;

import java.util.ArrayList;
import java.util.List;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.FontViewHolder> {

    Context context;
    FontAdapterClickListener listener;
    List<String> fontList;

    int row_selected = -1;

    public FontAdapter(Context context, FontAdapterClickListener listener) {
        this.context = context;
        this.listener = listener;
        fontList = loadFontList();
    }

    private List<String> loadFontList(){

        List<String> result = new ArrayList<>();
        result.add("Aller_Rg.ttf");
        result.add("Lato-Regular.ttf");
        result.add("OpenSans-Regular.ttf");
        result.add("ostrich-regular.ttf");
        result.add("Raleway-Regular.ttf");
        result.add("Roboto-Regular.ttf");

        return result;
    }

    @NonNull
    @Override
    public FontAdapter.FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.font_item, parent, false);
        return new FontViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FontAdapter.FontViewHolder holder, int position) {

        if (row_selected == position)
            holder.img_check.setVisibility(View.VISIBLE);
        else
            holder.img_check.setVisibility(View.INVISIBLE);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), new StringBuilder("fonts/")
        .append(fontList.get(position)).toString());

        holder.text_font_name.setText(fontList.get(position));
        holder.text_font_demo.setTypeface(typeface);
    }

    @Override
    public int getItemCount() {
        return fontList.size();
    }

    public class FontViewHolder extends RecyclerView.ViewHolder {
        TextView text_font_name, text_font_demo;
        ImageView img_check;
        public FontViewHolder(@NonNull View itemView) {
            super(itemView);

            text_font_demo = (TextView) itemView.findViewById(R.id.text_font_demo);
            text_font_name = (TextView) itemView.findViewById(R.id.text_font_name);
            img_check = (ImageView) itemView.findViewById(R.id.img_check);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFontSelected(fontList.get(getAdapterPosition()));

                    row_selected = getAdapterPosition();
                }
            });
        }
    }

    public interface FontAdapterClickListener{
        void onFontSelected(String fontName);
    }
}
